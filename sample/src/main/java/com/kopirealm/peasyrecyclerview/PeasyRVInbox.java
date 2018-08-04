package com.kopirealm.peasyrecyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PeasyRVInbox extends PeasyRecyclerView<PeasyRVInbox.ModelInbox> {

    private final PeasyHeaderContent<ModelInbox> headerContent = new PeasyHeaderContent<ModelInbox>(InboxHeaderViewHolder.VIEWTYPE_HEADER, null) {
        @Override
        PeasyHeaderViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return new InboxHeaderViewHolder(InboxHeaderViewHolder.inflateView(inflater, parent, InboxHeaderViewHolder.LAYOUT_ID));
        }

        @Override
        void onBindViewHolder(Context context, PeasyHeaderViewHolder holder, int position, ModelInbox item) {
            if (holder.isInstance(InboxHeaderViewHolder.class)) {
                ((InboxHeaderViewHolder) holder).createView(item);
            }
        }
    };

    private final PeasyFooterContent<ModelInbox> footerContent = new PeasyFooterContent<ModelInbox>(InboxFooterViewHolder.VIEWTYPE_FOOTER, null) {
        @Override
        PeasyFooterViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return new InboxFooterViewHolder(InboxFooterViewHolder.inflateView(inflater, parent, InboxFooterViewHolder.LAYOUT_ID));
        }

        @Override
        void onBindViewHolder(Context context, PeasyFooterViewHolder holder, int position, ModelInbox item) {
            if (holder.isInstance(InboxFooterViewHolder.class)) {
                ((InboxFooterViewHolder) holder).createView(item);
            }
        }
    };

    public PeasyRVInbox(@NonNull Context context, RecyclerView recyclerView, FloatingActionButton fab, ArrayList<ModelInbox> arrayList) {
        super(context, recyclerView, arrayList);
        this.setFAB(fab, true);
    }

    @Override
    public void setContent(ArrayList<ModelInbox> arrayList) {
        this.setHeaderContent(getPresentation().equals(Presentation.VerticalList) ? headerContent : null);
        this.setFooterContent(getPresentation().equals(Presentation.VerticalList) ? footerContent : null);
        super.setContent(arrayList);
    }

    @Override
    protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (InboxModelViewHolder.VIEWTYPE_CONTENT == viewType) {
            return new InboxModelViewHolder(InboxModelViewHolder.inflateView(inflater, parent, InboxModelViewHolder.LAYOUT_ID));
        }
        return null;
    }

    @Override
    protected int getItemViewType(int position, ModelInbox item) {
        return InboxModelViewHolder.VIEWTYPE_CONTENT;
    }

    @Override
    protected void onBindViewHolder(Context context, PeasyViewHolder holder, int position, ModelInbox item) {
        if (holder.isContentView() || holder.isInstance(InboxModelViewHolder.class)) {
            ((InboxModelViewHolder) holder).createView(item);
        }
    }

    @Override
    public void onItemClick(View v, int viewType, final int position, final ModelInbox item, PeasyViewHolder vh) {
        if (viewType == InboxModelViewHolder.VIEWTYPE_CONTENT) {
            new AlertDialog.Builder(getContext())
                    .setTitle(item.title)
                    .setMessage(item.message)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getItem(position).read = true;
                            notifyItemChanged(position);
                        }
                    })
                    .show();
        } else {
            if (viewType == InboxHeaderViewHolder.VIEWTYPE_HEADER) {
                if (vh != null && vh.isInstance(InboxHeaderViewHolder.class)) {
                    final InboxHeaderViewHolder viewHolder = (InboxHeaderViewHolder) vh;
                    Toast.makeText(getContext(), viewHolder.tvTitle.getText(), Toast.LENGTH_SHORT).show();
                }
            } else if (viewType == InboxFooterViewHolder.VIEWTYPE_FOOTER) {
                if (vh != null && vh.isInstance(InboxFooterViewHolder.class)) {
                    final InboxFooterViewHolder viewHolder = (InboxFooterViewHolder) vh;
                    Toast.makeText(getContext(), viewHolder.tvTitle.getText(), Toast.LENGTH_SHORT).show();
                    smoothScrollToFirst();
                }
            }
        }
    }

    private Snackbar snackbar;

    @Override
    public void onViewScrolled(final RecyclerView recyclerView, int dx, int dy) {
        super.onViewScrolled(recyclerView, dx, dy);
        final int visibleThreshold = 1;
        final int totalItemCount = this.getItemCount();
        final int lastVisibleItem = this.getLastVisibleItemPosition();
        if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (snackbar == null) {
                getFab().setVisibility(View.VISIBLE);
                snackbar = Snackbar
                        .make(recyclerView, "No more message.", Snackbar.LENGTH_LONG)
                        .setAction("TOP", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (getRecyclerView().getLayoutManager() instanceof LinearLayoutManager) {
                                    smoothScrollToFirst();
                                }
                            }
                        });
            }
            if (!snackbar.isShown()) {
                snackbar.show();
            }
        }
    }

    public static class ModelInbox {
        String title = "";
        String message = "";
        String sender = "";
        boolean read = false;

        ModelInbox(String title, String message, String sender, boolean read) {
            this.title = title;
            this.message = message;
            this.sender = sender + "@scmp.com";
            this.read = read;
        }
    }

    public class InboxHeaderViewHolder extends PeasyHeaderViewHolder {
        public final static int LAYOUT_ID = R.layout.li_inbox_header;
        final TextView tvTitle;

        public InboxHeaderViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void createView(ModelInbox item) {
            tvTitle.setText("This is Top");
        }
    }

    public class InboxFooterViewHolder extends PeasyFooterViewHolder {
        public final static int LAYOUT_ID = R.layout.li_inbox_footer;
        final TextView tvTitle;

        public InboxFooterViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void createView(ModelInbox item) {
            tvTitle.setText("You hit bottom, Click to Top");
        }

    }

    public class InboxModelViewHolder extends PeasyContentViewHolder {
        public final static int LAYOUT_ID = R.layout.li_inbox_model;

        final TextView tvTitle, tvMessage, tvSender;
        final LinearLayout llInboxContainer;

        public InboxModelViewHolder(View itemView) {
            super(itemView);
            llInboxContainer = itemView.findViewById(R.id.llInboxContainer);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSender = itemView.findViewById(R.id.tvSender);
        }

        public void createView(ModelInbox item) {
            if (item == null) return;
            if (!item.read) {
                tvTitle.setSingleLine(false);
                tvTitle.setTextColor(Color.parseColor("#ffcc0000"));
                if (getStaggeredGridLayoutManager() != null) {
                    final String unreadTitle = (item.message.length() <= 5) ? "[NEW] " + item.message : item.message.substring(0, 5).concat("...");
                    tvTitle.setText(unreadTitle);
                    final String snapshot = (item.message.length() <= 5) ? item.message : item.message.substring(0, 5).concat("...");
                    tvMessage.setText(snapshot);
                } else {
                    tvTitle.setText(item.title);
                    final String snapshot = (item.message.length() <= 20) ? item.message : item.message.substring(0, 20).concat("...");
                    tvMessage.setText(snapshot);
                }
            } else {
                tvTitle.setText(item.title);
                tvTitle.setSingleLine(true);
                tvTitle.setTextColor(Color.parseColor("#ff0099cc"));
                final String snapshot = (item.message.length() <= 20) ? item.message : item.message.substring(0, 20).concat("...");
                tvMessage.setText(snapshot);
            }
            tvMessage.setSingleLine(true);
            tvMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            tvSender.setText(item.sender);
        }

    }
}