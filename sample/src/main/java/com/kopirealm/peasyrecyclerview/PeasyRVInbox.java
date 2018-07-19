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
import android.widget.TextView;

import java.util.ArrayList;

public class PeasyRVInbox extends PeasyRecyclerView.VerticalList<PeasyRVInbox.ModelInbox> {

    public PeasyRVInbox(@NonNull Context context, RecyclerView recyclerView, FloatingActionButton fab, ArrayList<ModelInbox> arrayList) {
        super(context, recyclerView, arrayList);
        this.setFAB(fab, true);
    }

    @Override
    protected PeasyPodHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ModelInboxViewHolder(ModelInboxViewHolder.inflateView(inflater, parent, ModelInboxViewHolder.INBOX_LAYOUT_ID));
    }

    @Override
    protected int getItemViewType(int position, ModelInbox item) {
        return ModelInboxViewHolder.INBOX_BASIC_TYPE;
    }

    @Override
    protected void onBindViewHolder(Context context, PeasyPodHolder holder, int position, ModelInbox item) {
        if (holder instanceof ModelInboxViewHolder) {
            ((ModelInboxViewHolder) holder).createView(item);
        }
    }

    @Override
    public void onItemClick(View v, int viewType, final int position) {
        final ModelInbox item = getItem(position);
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
                                    smoothScrollToTop();
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
            this.sender = sender + "@subscribe.com";
            this.read = read;
        }
    }

    public class ModelInboxViewHolder extends PeasyPodHolder {

        public final static int INBOX_BASIC_TYPE = 1;
        public final static int INBOX_LAYOUT_ID = R.layout.li_inbox_model;

        final TextView tvTitle, tvMessage, tvSender;

        public ModelInboxViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSender = itemView.findViewById(R.id.tvSender);
        }

        public void createView(ModelInbox item) {
            if (!item.read) {
                tvTitle.setSingleLine(false);
                tvTitle.setTextColor(Color.parseColor("#ffcc0000"));
                tvTitle.setText("[NEW] " + item.title);
            } else {
                tvTitle.setText(item.title);
                tvTitle.setSingleLine(true);
                tvTitle.setTextColor(Color.parseColor("#ff0099cc"));
            }
            final String snapshot = (item.message.length() <= 20) ? item.message : item.message.substring(0, 20).concat("...");
            tvMessage.setText(snapshot);
            tvMessage.setSingleLine(true);
            tvMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            tvSender.setText(item.sender);
        }

    }
}