package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kopirealm.peasyrecyclerview.PeasyContentViewHolder;
import com.kopirealm.peasyrecyclerview.PeasyRecyclerView;
import com.kopirealm.peasyrecyclerview.PeasyViewHolder;
import com.kopirealm.peasyrecyclerview.sample.R;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class SimpleVerticalListView extends PeasyRecyclerView.VerticalList<String> {

    private PresenterListener listener;

    SimpleVerticalListView(@NonNull Context context, RecyclerView recyclerView, ArrayList<String> arrayList, @NonNull PresenterListener listener) {
        // TODO Initialization
        super(context, recyclerView, arrayList);
        enableScrollEndDetection(3);
        this.listener = listener;
        this.listener.onContentChanged(getItemCount(), getColumnSize());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (this.listener != null) {
            this.listener.onContentChanged(getItemCount(), getColumnSize());
        }
    }

    @Override
    public void onViewReady() {
        this.listener.onViewScrollStateChanged(getRecyclerView(), SCROLL_STATE_IDLE);
    }

    @Override
    protected int getItemViewType(int position, String item) {
        // TODO Do Nothing but returning view type accordingly
        return (item == null) ? ContentViewHolder.VIEWTYPE_NOTHING : ContentViewHolder.VIEWTYPE_ID;
    }

    @Override
    protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        // TODO Do Nothing but initializing view holder with layout_id
        return new ContentViewHolder(inflateView(inflater, parent, ContentViewHolder.LAYOUT_ID));
    }

    @Override
    protected void onBindViewHolder(Context context, PeasyViewHolder holder, int position, String item) {
        // TODO Do Nothing but checking instance and populating item to view with view holder
        if (holder.isContentView() || holder.isInstance(ContentViewHolder.class)) {
            holder.asIs(ContentViewHolder.class).createView(context, item);
        }
    }

    @Override
    public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onViewScrollStateChanged(recyclerView, newState);
        this.listener.onViewScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onViewScrolled(recyclerView, dx, dy);
        this.listener.onViewScrolled(recyclerView, dx, dy);
    }

    @Override
    public void onViewScrolledToFirst(RecyclerView recyclerView) {
        super.onViewScrolledToFirst(recyclerView);
        this.listener.onViewScrolledToFirst(recyclerView);
    }

    @Override
    public void onViewScrolledToLast(RecyclerView recyclerView) {
        super.onViewScrolledToLast(recyclerView);
        this.listener.onViewScrolledToLast(recyclerView);
    }

    @Override
    public void onViewScrolledToEnd(RecyclerView recyclerView, int threshold) {
        super.onViewScrolledToEnd(recyclerView, threshold);
        this.listener.onViewScrolledToEnd(recyclerView, threshold);
    }

    @Override
    public void onItemClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder) {
        this.listener.onItemClick(view, viewType, position, item, viewHolder);
    }

    @Override
    public boolean onItemLongClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder) {
        return this.listener.onItemLongClick(view, viewType, position, item, viewHolder);
    }

    private static class ContentViewHolder extends PeasyContentViewHolder {
        static final int LAYOUT_ID = R.layout.li_demo2;
        TextView tvTitle;

        ContentViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        void createView(Context context, String item) {
            tvTitle.setText(item);
        }
    }
}
