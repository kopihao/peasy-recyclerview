package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Blueprint to substitute RecyclerView.ViewHolder
 * Require you to return {@link PeasyViewHolder } from {@link PeasyRecyclerView#onCreateViewHolder(LayoutInflater, ViewGroup, int)}
 * Will pass back {@link PeasyViewHolder } to {@link PeasyRecyclerView#onBindViewHolder(Context, PeasyViewHolder, int, Object)}
 * Consist {@value VIEWTYPE_NOTHING} to use as non-existence ViewType
 */
public abstract class PeasyViewHolder extends RecyclerView.ViewHolder {

    /**
     * Static method to help inflate parent view with layoutId
     *
     * @param inflater inflater
     * @param parent   parent
     * @param layoutId layoutId
     * @return View
     */
    public static View inflateView(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    /**
     * Help to cast provided PeasyViewHolder to its child class instance
     *
     * @param vh   PeasyViewHolder Parent Class
     * @param cls  Class of PeasyViewHolder
     * @param <VH> PeasyViewHolder Child Class
     * @return VH as Child Class Instance
     */
    public static <VH extends PeasyViewHolder> VH GetViewHolder(PeasyViewHolder vh, Class<VH> cls) {
        return cls.cast(vh);
    }

    //==========================================================================================

    public static final int VIEWTYPE_NOTHING = Integer.MIN_VALUE;

    public PeasyViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Will bind onClick and setOnLongClickListener here
     *
     * @param binder   {@link PeasyViewHolder} itself
     * @param viewType viewType ID
     */
    <T> void bindWith(final PeasyRecyclerView<T> binder, final int viewType) {
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                position = (position <= binder.getLastItemIndex()) ? position : getAdapterPosition();
                position = (position <= binder.getLastItemIndex()) ? position : RecyclerView.NO_POSITION;
                if (position != RecyclerView.NO_POSITION) {
                    binder.onItemClick(v, viewType, position, binder.getItem(position), getInstance());
                }
            }
        });
        this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = getLayoutPosition();
                position = (position <= binder.getLastItemIndex()) ? position : getAdapterPosition();
                position = (position <= binder.getLastItemIndex()) ? position : RecyclerView.NO_POSITION;
                if (getLayoutPosition() != RecyclerView.NO_POSITION) {
                    return binder.onItemLongClick(v, viewType, position, binder.getItem(position), getInstance());
                }
                return false;
            }
        });
    }

    public PeasyViewHolder getInstance() {
        return this;
    }

    /**
     * Help to cast provided PeasyViewHolder to its instance
     *
     * @param cls
     * @param <VH>
     * @return
     */
    public <VH extends PeasyViewHolder> VH asIs(Class<VH> cls) {
        return GetViewHolder(this, cls);
    }

    /**
     * To check this is instance of {@link PeasyViewHolder} or its child classes
     *
     * @param cls Class to check
     * @return
     */
    public boolean isInstance(Class cls) {
        return cls.isInstance(this);
    }

    /**
     * To check this is instance of {@link PeasyHeaderViewHolder}
     *
     * @return
     */
    public boolean isHeaderView() {
        return PeasyHeaderViewHolder.class.isInstance(this);
    }

    /**
     * To check this is instance of {@link PeasyContentViewHolder}
     *
     * @return
     */
    public boolean isContentView() {
        return PeasyContentViewHolder.class.isInstance(this);
    }

    /**
     * To check this is instance of {@link PeasyFooterViewHolder}
     *
     * @return
     */
    public boolean isFooterView() {
        return PeasyFooterViewHolder.class.isInstance(this);
    }


}
