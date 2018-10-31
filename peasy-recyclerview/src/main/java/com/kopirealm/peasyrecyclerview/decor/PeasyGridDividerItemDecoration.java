package com.kopirealm.peasyrecyclerview.decor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Simple ItemDecoration designed for GridLayoutManager use within RecyclerView
 * PeasyGridDividerItemDecoration require 2 parameters: int gridSpacingPx, int gridSize
 * Please check out {@link PeasyGridDividerItemDecoration#PeasyGridDividerItemDecoration(int, int)}
 * Credit to @see <a href="https://stackoverflow.com/a/29168276"> decorating-recyclerview-with-gridlayoutmanager-to-display-divider-between-item</a>
 */
public class PeasyGridDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mSizeGridSpacingPx;
    private int mGridSize;

    private boolean mNeedLeftSpacing = false;

    public PeasyGridDividerItemDecoration(int gridSpacingPx, int gridSize) {
        mSizeGridSpacingPx = gridSpacingPx;
        mGridSize = gridSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutParams layoutParams = ((RecyclerView.LayoutParams) view.getLayoutParams());
        final int position = layoutParams.getViewAdapterPosition();
        drawDividerWithOffset(outRect, view, parent, layoutParams, position);
    }

    private void drawDividerWithOffset(Rect outRect, View view, RecyclerView parent, RecyclerView.LayoutParams layoutParams, int position) {
        int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
        int padding = parent.getWidth() / mGridSize - frameWidth;
        outRect.set(padding, padding, padding, padding);
    }

//    private void drawDefaultDivider(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
//        int padding = parent.getWidth() / mGridSize - frameWidth;
//        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
//        if (itemPosition < mGridSize) {
//            outRect.top = 0;
//        } else {
//            outRect.top = mSizeGridSpacingPx;
//        }
//        if (itemPosition % mGridSize == 0) {
//            outRect.left = 0;
//            outRect.right = padding;
//            mNeedLeftSpacing = true;
//        } else if ((itemPosition + 1) % mGridSize == 0) {
//            mNeedLeftSpacing = false;
//            outRect.right = 0;
//            outRect.left = padding;
//        } else if (mNeedLeftSpacing) {
//            mNeedLeftSpacing = false;
//            outRect.left = mSizeGridSpacingPx - padding;
//            if ((itemPosition + 2) % mGridSize == 0) {
//                outRect.right = mSizeGridSpacingPx - padding;
//            } else {
//                outRect.right = mSizeGridSpacingPx / 2;
//            }
//        } else if ((itemPosition + 2) % mGridSize == 0) {
//            mNeedLeftSpacing = false;
//            outRect.left = mSizeGridSpacingPx / 2;
//            outRect.right = mSizeGridSpacingPx - padding;
//        } else {
//            mNeedLeftSpacing = false;
//            outRect.left = mSizeGridSpacingPx / 2;
//            outRect.right = mSizeGridSpacingPx / 2;
//        }
//        outRect.bottom = 0;
//    }
}