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

public class SimpleBasicGridView extends PeasyRecyclerView.BasicGrid<String> {

    private PresentorListener listener;

    public SimpleBasicGridView(@NonNull Context context, RecyclerView recyclerView, ArrayList<String> arrayList, @NonNull PresentorListener listener) {
        // TODO Initialization
        super(context, recyclerView, arrayList, 2);
        this.listener = listener;
    }

    @Override
    protected int getItemViewType(int position, String item) {
        // TODO Do Nothing but returning view type accordingly
        return (item == null) ? PeasyViewHolder.VIEWTYPE_NOTHING : ContentViewHolder.VIEWTYPE_ID;
    }

    @Override
    protected PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        // TODO Do Nothing but initializing view holder with layout_id
        return new ContentViewHolder(ContentViewHolder.inflateView(inflater, parent, ContentViewHolder.LAYOUT_ID));
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

    static class ContentViewHolder extends PeasyContentViewHolder {
        static final int LAYOUT_ID = R.layout.li_demo2;
        TextView tvTitle;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void createView(Context context, String item) {
            tvTitle.setText(item);
        }
    }
}
