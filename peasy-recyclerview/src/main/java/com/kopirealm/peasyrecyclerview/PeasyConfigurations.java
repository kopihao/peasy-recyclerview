package com.kopirealm.peasyrecyclerview;

import android.os.Bundle;

class PeasyConfigurations {

    private static String ExtraColumnSize = "column_size";

    /**
     * ]
     *
     * @param columnSize provided columnSize [{@value PeasyRecyclerView#DefaultGridColumnSize}, columnSize]
     * @return valid columns size
     */
    static int issueColumnSize(int columnSize) {
        return Math.max(Math.max(0, columnSize), PeasyRecyclerView.DefaultGridColumnSize);
    }

    /**
     * @param bundle     bundle
     * @param columnSize provided columnSize [{@value PeasyRecyclerView#DefaultGridColumnSize}, columnSize]
     * @return bundle with bundled value of {@value  ExtraColumnSize}
     */
    static Bundle bundleColumnSize(Bundle bundle, int columnSize) {
        final Bundle extraData = (bundle != null) ? bundle : new Bundle();
        extraData.putInt(ExtraColumnSize, issueColumnSize(columnSize));
        return extraData;
    }

    /**
     * @return bundled value of {@value  ExtraColumnSize}
     */
    static int getColumnSize(Bundle bundle) {
        return bundle.getInt(ExtraColumnSize, PeasyRecyclerView.DefaultGridColumnSize);
    }
}
