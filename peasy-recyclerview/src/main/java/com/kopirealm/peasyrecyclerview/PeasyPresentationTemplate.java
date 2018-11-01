package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;

public class PeasyPresentationTemplate {

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

        static LinearLayoutManager newLayoutManager(final Context context) {
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            return layoutManager;
        }

        static int findFirstVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findFirstVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
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

        static LinearLayoutManager newLayoutManager(final Context context) {
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }

        static int findFirstVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findFirstVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
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
            super(context, recyclerView, arrayList, PeasyConfigurations.bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asGridView(this.getColumnSize());
        }

        static GridLayoutManager newLayoutManager(final Context context, final int columns) {
            return new GridLayoutManager(context, PeasyConfigurations.issueColumnSize(columns));
        }

        static int findFirstVisibleItemPosition(GridLayoutManager layoutManager) {
            int position = layoutManager.findFirstVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findFirstCompletelyVisibleItemPosition(GridLayoutManager layoutManager) {
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastVisibleItemPosition(GridLayoutManager layoutManager) {
            int position = layoutManager.findLastVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
        }

        static int findLastCompletelyVisibleItemPosition(GridLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            return Math.max(RecyclerView.NO_POSITION, position);
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
            super(context, recyclerView, arrayList, PeasyConfigurations.bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asVerticalStaggeredGridView(this.getColumnSize());
        }

        static StaggeredGridLayoutManager newLayoutManager(final Context context, final int columns) {
            return new StaggeredGridLayoutManager(PeasyConfigurations.issueColumnSize(columns), StaggeredGridLayoutManager.VERTICAL);
        }

        static int findFirstVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findFirstVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, (into[0]));
        }

        static int findFirstCompletelyVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findFirstCompletelyVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, (into[0]));
        }

        static int findLastVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findLastVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, getMaxPosition(into));
        }

        static int findLastCompletelyVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findLastCompletelyVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, getMaxPosition(into));
        }

        static int getMaxPosition(int[] into) {
            // Continue to replace until larger value met, assumed max value is last index of item
            Arrays.sort(into);
            int position = RecyclerView.NO_POSITION;
            for (int value : into) {
                position = Math.max(position, value);
            }
            return position;
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
            super(context, recyclerView, arrayList, PeasyConfigurations.bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            this.asHorizontalStaggeredGridView(this.getColumnSize());
        }

        static StaggeredGridLayoutManager newLayoutManager(final Context context, final int columns) {
            return new StaggeredGridLayoutManager(PeasyConfigurations.issueColumnSize(columns), StaggeredGridLayoutManager.HORIZONTAL);
        }

        static int findFirstVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findFirstVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, (into[0]));
        }

        static int findFirstCompletelyVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findFirstCompletelyVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, (into[0]));
        }

        static int findLastVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findLastVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, getMaxPosition(into));
        }

        static int findLastCompletelyVisibleItemPosition(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            layoutManager.findLastCompletelyVisibleItemPositions(into);
            return Math.max(RecyclerView.NO_POSITION, getMaxPosition(into));
        }

        static int getMaxPosition(int[] into) {
            // Continue to replace until larger value met, assumed max value is last index of item
            Arrays.sort(into);
            int position = RecyclerView.NO_POSITION;
            for (int value : into) {
                position = Math.max(position, value);
            }
            return position;
        }
    }

}
