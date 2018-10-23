package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.support.v7.widget.RecyclerView;

public interface PresentorListener {

    void onContentChanged(int count);

    void onViewScrollStateChanged(RecyclerView recyclerView, int newState);

    void onViewScrolled(RecyclerView recyclerView, int dx, int dy);

}
