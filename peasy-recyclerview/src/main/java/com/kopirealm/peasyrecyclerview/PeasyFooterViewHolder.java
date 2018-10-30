package com.kopirealm.peasyrecyclerview;


import android.view.View;

/**
 * PeasyViewHolder Blueprint for Footer Purpose
 * With this implementation, view holder can be identified by {@link PeasyViewHolder#isFooterView()}
 */
public abstract class PeasyFooterViewHolder extends PeasyViewHolder {

    public static final int VIEWTYPE_ID = PeasyFooterViewHolder.class.hashCode();

    public static final int VIEWTYPE_FOOTER = VIEWTYPE_ID;

    public PeasyFooterViewHolder(View itemView) {
        super(itemView);
    }
}