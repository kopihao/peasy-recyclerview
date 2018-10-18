package com.kopirealm.peasyrecyclerview;

import android.view.View;

/**
 * PeasyViewHolder Blueprint for Content Purpose
 * With this implementation, view holder can be identified by {@link PeasyViewHolder#isFooterView()}
 */
public abstract class PeasyContentViewHolder extends PeasyViewHolder {

    public static final int VIEWTYPE_ID = PeasyContentViewHolder.class.hashCode();
    @Deprecated
    public static final int VIEWTYPE_CONTENT = VIEWTYPE_ID;

    public PeasyContentViewHolder(View itemView) {
        super(itemView);
    }
}
