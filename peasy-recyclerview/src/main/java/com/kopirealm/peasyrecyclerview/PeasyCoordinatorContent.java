package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Blueprint of special Content that has its unique Coordination within view
 * This class hold both view-type, data and view-holder
 *
 * @param <D> Data Type
 */
public abstract class PeasyCoordinatorContent<VH extends PeasyViewHolder, D> {

    private int viewtype;
    private D data;

    public PeasyCoordinatorContent(int viewtype, D data) {
        this.viewtype = viewtype;
        this.data = data;
    }

    public int getViewtype() {
        return viewtype;
    }

    public D getData() {
        return data;
    }

    protected abstract VH onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract void onBindViewHolder(Context context, VH holder, int position, D item);

    protected void onBindViewHolder(Context context, VH holder, int position, ArrayList<D> items) {
    }

}