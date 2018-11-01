package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kopirealm.peasyrecyclerview.PeasyViewHolder;

public interface PresentorListener {

    void onContentChanged(int count, int columns);

    void onViewScrolledToEnd(final RecyclerView recyclerView, final int threshold);

    void onViewScrollStateChanged(RecyclerView recyclerView, int newState);

    void onViewScrolled(RecyclerView recyclerView, int dx, int dy);

    void onItemClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder);

    boolean onItemLongClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder);

}
