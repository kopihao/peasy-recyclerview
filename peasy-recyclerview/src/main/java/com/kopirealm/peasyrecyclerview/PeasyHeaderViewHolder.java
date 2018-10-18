package com.kopirealm.peasyrecyclerview;

import android.view.View;

/**
 * PeasyViewHolder Blueprint for Header Purpose
 * With this implementation, view holder can be identified by {@link PeasyViewHolder#isHeaderView()}
 */
public abstract class PeasyHeaderViewHolder extends PeasyViewHolder {

    public static final int VIEWTYPE_ID = PeasyHeaderViewHolder.class.hashCode();
    @Deprecated
    public static final int VIEWTYPE_HEADER = VIEWTYPE_ID;

    public PeasyHeaderViewHolder(View itemView) {
        super(itemView);
    }
}