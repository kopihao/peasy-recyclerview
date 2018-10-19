package com.kopirealm.peasyrecyclerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A demonstration on How to use PeasyRecyclerView
 * How to extend PeasyRecyclerView and link it with Data Object
 * How to define view type, view holder, populate to view
 * How to handle action when : scrolling recyclerview, perform clicks on recyclerview item
 * How to handle header and footer contents easily
 * How to handle FAB button effortlessly
 * How to take advantage on reduce boilerplate implementation:
 * eg. getItem, getItemCount, findFirstCompletelyVisibleItemPosition, findLastCompletelyVisibleItemPosition
 * Enjoy intuitive, flatten and focused code design!
 */

//TODO This PeasyRecyclerView initialized as Vertical List
//public final class PeasyRVInbox extends PeasyRecyclerView.VerticalList<PeasyRVInbox.ModelInbox> {
//TODO This PeasyRecyclerView initialized as Horizontal List
//public final class PeasyRVInbox extends PeasyRecyclerView.HorizontalList<PeasyRVInbox.ModelInbox> {
//TODO This PeasyRecyclerView initialized as Basic Grid
//public final class PeasyRVInbox extends PeasyRecyclerView.BasicGrid<PeasyRVInbox.ModelInbox> {
//TODO This PeasyRecyclerView initialized as Vertical Staggered Grid
//public final class PeasyRVInbox extends PeasyRecyclerView.VerticalStaggeredGrid<PeasyRVInbox.ModelInbox> {
//TODO This PeasyRecyclerView initialized as Horizontal Staggered Grid
//public final class PeasyRVInbox extends PeasyRecyclerView.HorizontalStaggeredGrid<PeasyRVInbox.ModelInbox> {
//TODO This PeasyRecyclerView initialized undefined presentation, it will initialize presentation during run time
public final class PeasyRVInbox extends PeasyRecyclerView<PeasyRVInbox.ModelInbox> {

    public PeasyRVInbox(@NonNull Context context, RecyclerView recyclerView, FloatingActionButton fab, ArrayList<ModelInbox> arrayList) {
        // TODO Initialization
        super(context, recyclerView, arrayList);
        super.anchorFAB(fab);
        super.setThresholdOfEOL(2);
    }

    // TODO Header Content of PeasyRVInbox
    // private final PeasyHeaderContent<ModelInbox> headerContent = new PeasyHeaderContent<ModelInbox>(InboxHeaderViewHolder.VIEWTYPE_HEADER, null) {
    private final PeasyHeaderContent<ModelInbox> headerContent = new PeasyHeaderContent<ModelInbox>(InboxHeaderViewHolder.VIEWTYPE_HEADER, PeasyRVInbox.ModelInbox.buildInboxHeader()) {
        @Override
        protected PeasyHeaderViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            // TODO Do Nothing but initializing view holder with layout_id
            return new InboxHeaderViewHolder(InboxHeaderViewHolder.inflateView(inflater, parent, InboxHeaderViewHolder.LAYOUT_ID));
        }

        @Override
        protected void onBindViewHolder(Context context, PeasyHeaderViewHolder holder, int position, ModelInbox item) {
            // TODO Do Nothing but checking instance and populating item to view with view holder
            if (holder.isInstance(InboxHeaderViewHolder.class)) {
                ((InboxHeaderViewHolder) holder).createView(item);
            }
        }
    };

    //  TODO Footer Content of PeasyRVInbox
    // private final PeasyFooterContent<ModelInbox> footerContent = new PeasyFooterContent<ModelInbox>(InboxFooterViewHolder.VIEWTYPE_FOOTER, null) {
    private final PeasyFooterContent<ModelInbox> footerContent = new PeasyFooterContent<ModelInbox>(InboxFooterViewHolder.VIEWTYPE_FOOTER, PeasyRVInbox.ModelInbox.buildInboxFooter()) {
        @Override
        protected PeasyFooterViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            // TODO Do Nothing but initializing view holder with layout_id
            return new InboxFooterViewHolder(InboxFooterViewHolder.inflateView(inflater, parent, InboxFooterViewHolder.LAYOUT_ID));
        }

        @Override
        protected void onBindViewHolder(Context context, PeasyFooterViewHolder holder, int position, ModelInbox item) {
            // TODO Do Nothing but checking instance and populating item to view with view holder
            if (holder.isInstance(InboxFooterViewHolder.class)) {
                ((InboxFooterViewHolder) holder).createView(item);
            }
        }
    };

    @Override
    public void setContent(ArrayList<ModelInbox> arrayList) {
        // TODO Do Nothing but providing content
        super.setHeaderContent(getPresentation().equals(Presentation.VerticalList) ? headerContent : null);
        super.setFooterContent(getPresentation().equals(Presentation.VerticalList) ? footerContent : null);
        super.setContent(arrayList);
    }

    @Override
    protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        // TODO Do Nothing but initializing view holder with layout_id
        if (InboxModelViewHolder.VIEWTYPE_CONTENT == viewType) {
            return new InboxModelViewHolder(InboxModelViewHolder.inflateView(inflater, parent, InboxModelViewHolder.LAYOUT_ID));
        }
        return null;
    }

    @Override
    protected int getItemViewType(int position, ModelInbox item) {
        // TODO Do Nothing but returning view type accordingly
        return (item == null) ? PeasyViewHolder.VIEWTYPE_NOTHING : InboxModelViewHolder.VIEWTYPE_CONTENT;
    }

    @Override
    protected void onBindViewHolder(Context context, PeasyViewHolder holder, int position, ModelInbox item) {
        // TODO Do Nothing but checking instance and populating item to view with view holder
        if (holder.isContentView() || holder.isInstance(InboxModelViewHolder.class)) {
            ((InboxModelViewHolder) holder).createView(item);
        }
    }

    @Override
    public void onItemClick(final View v, int viewType, final int position, final ModelInbox item, PeasyViewHolder vh) {
        // TODO Do Nothing but defining click action on PeasyRVInbox item
        Log.d("[LOG]", getClass().getSimpleName() + "#" + "getColumnSize::" + this.getColumnSize());
        if (viewType == InboxModelViewHolder.VIEWTYPE_CONTENT) {
            new AlertDialog.Builder(getContext())
                    .setTitle(item.title)
                    .setMessage(item.message)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getItem(position).state = ModelInbox.InboxState.Read;
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
                    showScrollTopSnackbar(v);
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, int viewType, final int position, final ModelInbox item, PeasyViewHolder viewHolder) {
        // TODO Do Nothing but defining long click action on PeasyRVInbox item
        if (item.state.equals(ModelInbox.InboxState.Read)) {
            new AlertDialog.Builder(getContext())
                    .setTitle(item.title)
                    .setMessage(item.message)
                    .setPositiveButton("Mark As Unread", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getItem(position).state = ModelInbox.InboxState.Unread;
                            notifyItemChanged(position);
                        }
                    })
                    .show();
        }
        return super.onItemLongClick(view, viewType, position, item, viewHolder);
    }

    private Snackbar snackbar;

    private void showScrollTopSnackbar(final View v) {
        if (snackbar == null) {
            snackbar = Snackbar
                    .make(v, "No More Entry.", Snackbar.LENGTH_LONG)
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

    @Override
    public void onViewScrolled(final RecyclerView recyclerView, int dx, int dy) {
        // TODO Everything during RecyclerView scrolling
        super.onViewScrolled(recyclerView, dx, dy);
        if (hasReachedEndOfList(1)) {
            notifyDataSetChanged();
            getFab().hide();
        }
    }

    @Override
    public void onViewReachingEndOfList(RecyclerView recyclerView, int threshold) {
        // TODO Everything during RecyclerView scrolled to reach End of List
        super.onViewReachingEndOfList(recyclerView, threshold);
        showScrollTopSnackbar(recyclerView);
    }

    @Override
    public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
        // TODO Everything after RecyclerView scroll state updated
        super.onViewScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onViewInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        super.onViewInterceptTouchEvent(rv, e);
    }

    @Override
    public GridLayoutManager asGridView(int columns) {
        final GridLayoutManager layoutManager = super.asGridView(columns);
        Log.d("[LOG]", getClass().getSimpleName() + "#" + "asGridView::" + this.getColumnSize());
        return layoutManager;
    }

    @Override
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(int columns) {
        final StaggeredGridLayoutManager layoutManager = super.asVerticalStaggeredGridView(columns);
        Log.d("[LOG]", getClass().getSimpleName() + "#" + "asVerticalStaggeredGridView::" + this.getColumnSize());
        return layoutManager;
    }

    @Override
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(int columns) {
        final StaggeredGridLayoutManager layoutManager = super.asHorizontalStaggeredGridView(columns);
        Log.d("[LOG]", getClass().getSimpleName() + "#" + "asHorizontalStaggeredGridView::" + this.getColumnSize());
        return layoutManager;
    }

    // TODO Define data object represent PeasyRVInbox item, provide to PeasyRecyclerView as T(generic type)
    public final static class ModelInbox {

        public enum InboxState {
            Read, Unread, Header, Footer
        }

        String title = "";
        String message = "";
        String sender = "";
        InboxState state = InboxState.Unread;

        private ModelInbox(String title, String message, String sender, InboxState state) {
            this.title = title;
            this.message = message;
            this.sender = sender + "@scmp.com";
            this.state = state;
        }

        public static ModelInbox buildInboxMessage(String title, String message, String sender, boolean read) {
            return new ModelInbox(title, message, sender, (read) ? InboxState.Read : InboxState.Unread);
        }

        public static ModelInbox buildInboxHeader() {
            return new ModelInbox("", "", "", InboxState.Header);
        }

        public static ModelInbox buildInboxFooter() {
            return new ModelInbox("", "", "", ModelInbox.InboxState.Footer);
        }

    }


    // TODO Define view holder to PeasyRVInbox Header, find its views
    public final class InboxHeaderViewHolder extends PeasyHeaderViewHolder {
        public final static int LAYOUT_ID = R.layout.li_inbox_header;
        final TextView tvTitle;

        public InboxHeaderViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        // TODO Optional practice to define view population within view holder
        public void createView(ModelInbox item) {
            tvTitle.setText("This is Top");
        }
    }

    // TODO Define view holder to PeasyRVInbox Footer, find its views
    public final class InboxFooterViewHolder extends PeasyFooterViewHolder {
        public final static int LAYOUT_ID = R.layout.li_inbox_footer;
        final TextView tvTitle;

        public InboxFooterViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        // TODO Optional practice to define view population within view holder
        public void createView(ModelInbox item) {
            tvTitle.setText("This is Bottom");
        }

    }

    // TODO Define view holder to PeasyRVInbox Content, find its views
    public final class InboxModelViewHolder extends PeasyContentViewHolder {
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

        // TODO Optional practice to define view population within view holder
        public void createView(ModelInbox item) {
            if (item == null) return;
            if (item.state.equals(ModelInbox.InboxState.Unread)) {
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