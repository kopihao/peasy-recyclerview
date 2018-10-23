package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.kopirealm.peasyrecyclerview.decor.PeasyGridDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;

public class PeasyPresentationTemplate {

    static final int DefaultGridColumnSize = 2;
    static String ExtraColumnSize = "column_size";

    /**
     * @param columnSize provided columnSize [{@value DefaultGridColumnSize}, columnSize|]
     * @return valid columns size
     */
    static int issueColumnSize(int columnSize) {
        return Math.max(Math.max(0, columnSize), DefaultGridColumnSize);
    }

    /**
     * @param bundle     bundle
     * @param columnSize provided columnSize [{@value DefaultGridColumnSize}, columnSize|]
     * @return bundle with value of {@value ExtraColumnSize}
     */
    static Bundle bundleColumnSize(Bundle bundle, int columnSize) {
        final Bundle extraData = (bundle != null) ? bundle : new Bundle();
        extraData.putInt(ExtraColumnSize, issueColumnSize(columnSize));
        return extraData;
    }

    /**
     * @param bundle bundle with value of {@value ExtraColumnSize}
     * @return value of {@value ExtraColumnSize}
     */
    static int getColumnSize(Bundle bundle) {
        bundle = (bundle != null) ? bundle : new Bundle();
        return bundle.getInt(PeasyPresentationTemplate.ExtraColumnSize, 1);
    }

    /**
     * @param columnSize provided columnSize [{@value DefaultGridColumnSize}, columnSize|]
     * @return standard {@link PeasyGridDividerItemDecoration}
     */
    static PeasyGridDividerItemDecoration issuePeasyGridDivider(Context context, int columnSize) {
        return new PeasyGridDividerItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.peasy_grid_divider_spacing), PeasyPresentationTemplate.issueColumnSize(columnSize));
    }

    //==========================================================================================

    /**
     * Easy Peasy to implement Vertical Recycler View with LinearLayoutManager
     * Presentation : Vertical List View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalList<T> extends PeasyRecyclerView<T> {

        VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asVerticalListView();
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            position = (position == RecyclerView.NO_POSITION) ? layoutManager.findLastVisibleItemPosition() : position;
            return position;
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            return layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    /**
     * Easy Peasy to implement Horizontal Recycler View with LinearLayoutManager
     * Presentation : Horizontal List View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class HorizontalList<T> extends PeasyRecyclerView<T> {

        HorizontalList(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asHorizontalListView();
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            position = (position == RecyclerView.NO_POSITION) ? layoutManager.findLastVisibleItemPosition() : position;
            return position;
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            return layoutManager.findFirstCompletelyVisibleItemPosition();
        }

    }

    /**
     * Easy Peasy to implement Recycler View with GridLayoutManager
     * Presentation : Basic Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class BasicGrid<T> extends PeasyRecyclerView<T> {

        BasicGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asGridView(this.getColumnSize());
        }

        @Override
        public int getColumnSize() {
            return getExtraData().getInt(ExtraColumnSize, DefaultGridColumnSize);
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            position = (position == RecyclerView.NO_POSITION) ? layoutManager.findLastVisibleItemPosition() : position;
            return position;
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            return layoutManager.findFirstCompletelyVisibleItemPosition();
        }

    }

    /**
     * Easy Peasy to implement Vertical Recycler View with StaggeredGridLayoutManager
     * Presentation : Vertical Staggered Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalStaggeredGrid<T> extends PeasyRecyclerView<T> {

        VerticalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asVerticalStaggeredGridView(this.getColumnSize());
        }

        @Override
        public int getColumnSize() {
            return getExtraData().getInt(ExtraColumnSize, DefaultGridColumnSize);
        }

        static int findLastCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            into = layoutManager.findLastCompletelyVisibleItemPositions(into);
            int position = RecyclerView.NO_POSITION;
            {   // FIND MAX
                Arrays.sort(into);
                for (int anInto : into) {
                    if (anInto == RecyclerView.NO_POSITION) continue; // No interest in this value
                    if (anInto > position) {
                        position = anInto; // Continue to replace if larger value met
                    }
                }
                position = Math.max(RecyclerView.NO_POSITION, position);
            }
            return position;
        }

        static int findFirstCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            return layoutManager.findFirstVisibleItemPositions(into)[0];
        }

    }

    /**
     * Easy Peasy to implement Horizontal Recycler View with StaggeredGridLayoutManager
     * Presentation : Horizontal Staggered Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class HorizontalStaggeredGrid<T> extends PeasyRecyclerView<T> {

        HorizontalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            this.asHorizontalStaggeredGridView(this.getColumnSize());
        }

        @Override
        public int getColumnSize() {
            return getExtraData().getInt(ExtraColumnSize, DefaultGridColumnSize);
        }

        static int findLastCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            into = layoutManager.findLastCompletelyVisibleItemPositions(into);
            int position = RecyclerView.NO_POSITION;
            {   // FIND MAX
                Arrays.sort(into);
                for (int anInto : into) {
                    if (anInto == RecyclerView.NO_POSITION) continue; // No interest in this value
                    if (anInto > position) {
                        position = anInto; // Continue to replace if larger value met
                    }
                }
                position = Math.max(RecyclerView.NO_POSITION, position);
            }
            return position;
        }

        static int findFirstCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            return layoutManager.findFirstVisibleItemPositions(into)[0];
        }
    }

}