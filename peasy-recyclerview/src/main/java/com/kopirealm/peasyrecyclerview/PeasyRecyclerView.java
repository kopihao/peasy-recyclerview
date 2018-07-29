package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * An Adapter as well as a RecyclerView Binder
 *
 * @param <T> type of array list content
 */
public abstract class PeasyRecyclerView<T> extends RecyclerView.Adapter {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<T> recyclerDataSource;
    private FloatingActionButton fab;
    private boolean smartHiding = false;

    public PeasyRecyclerView(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
        this.context = context;
        setContent(arrayList, recyclerView);
    }

    /**
     * Update your array list content with recycler view binding at the same time
     * Execute at {@link PeasyRecyclerView} constructor for once
     * Use {@link #setContent(ArrayList)} method to update content only
     *
     * @param arrayList
     * @param recyclerView
     */
    public void setContent(ArrayList<T> arrayList, RecyclerView recyclerView) {
        this.setContent(arrayList);
        this.recyclerView = recyclerView;
        this.configureRecyclerView(getRecyclerView());
        this.recyclerView.setAdapter(this);
    }

    /**
     * Method to update array list content
     *
     * @param arrayList
     */
    public void setContent(ArrayList<T> arrayList) {
        this.recyclerDataSource = (arrayList == null) ? new ArrayList<T>() : arrayList;
        this.notifyDataSetChanged();
    }

    /**
     * {@link Context} of {@link PeasyRecyclerView}
     *
     * @return context
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * {@link RecyclerView} of {@link PeasyRecyclerView}
     *
     * @return recyclerView
     */
    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    /**
     * Code Snippet to customize RecyclerView
     * Define as you like, eg. {@link LinearLayoutManager} , {@link android.support.v7.widget.RecyclerView.ItemAnimator }, {@link android.support.v7.widget.RecyclerView.ItemDecoration}
     * Helpful to create recycler view presentation
     * By default, FAB handling will be contracted at here
     *
     * @param recyclerView
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, int, int)
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, MotionEvent, boolean)
     */
    protected void configureRecyclerView(RecyclerView recyclerView) {
        this.enableNestedScroll(true);
        this.onRecyclerViewTouch(this.recyclerView);
        this.onRecyclerViewScroll(this.recyclerView);
    }

    /**
     * Enabling RecyclerView NestedScroll property
     *
     * @param enable
     */
    public void enableNestedScroll(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.recyclerView.setNestedScrollingEnabled(enable);
        }
    }

    /**
     * TRY NOT OVERRIDE THIS
     * This is crucial to handle FAB
     *
     * @param recyclerView
     */
    private void onRecyclerViewTouch(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (smartHiding && getFab() != null) {
                    PeasyRecyclerView.this.handleFAB(rv, getFab(), e, hasAllContentsVisible());
                }
                PeasyRecyclerView.this.onInterceptTouchEvent(rv, e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * TRY NOT OVERRIDE THIS
     * This is crucial to handle FAB
     *
     * @param recyclerView
     */
    private void onRecyclerViewScroll(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                PeasyRecyclerView.this.handleFAB(recyclerView, getFab(), dx, dy);
                onViewScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                onViewScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /**
     * Method to bind FAB to RecyclerView
     * By default, will handle FAB automatically
     *
     * @param fab
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, int, int)
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, MotionEvent, boolean)
     */
    public void setFAB(FloatingActionButton fab) {
        setFAB(fab, false);
    }

    /**
     * Method to bind FAB to RecyclerView
     *
     * @param fab
     * @param smartHiding
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, int, int)
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, MotionEvent, boolean)
     */
    public void setFAB(FloatingActionButton fab, boolean smartHiding) {
        this.fab = fab;
        this.smartHiding = smartHiding;
    }

    /**
     * Getter for FloatingActionButton
     *
     * @return FloatingActionButton
     */
    public FloatingActionButton getFab() {
        return this.fab;
    }

    /**
     * @param recyclerView
     * @param dx
     * @param dy
     */
    private void handleFAB(RecyclerView recyclerView, final FloatingActionButton fab, int dx, int dy) {
        if (this.smartHiding && getFab() != null) {
            final FloatingActionButton mFloatingActionButton = this.fab;
            if (dy > 0) {
                if (mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                }
            } else {
                if (mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        }
    }

    /**
     * Scrolling downward will hide FAB
     * Scrolling upward will show FAB
     * If all items visible within view port, will show FAB
     *
     * @param rv         RecyclerView
     * @param e          MotionEvent
     * @param allVisible has all contents visible within view port?
     * @param fab        FloatingActionButton
     */
    protected void handleFAB(RecyclerView rv, final FloatingActionButton fab, MotionEvent e, boolean allVisible) {
        if (allVisible) {
            if (fab.getVisibility() != View.VISIBLE) {
                fab.show();
            }
        }
    }

    /**
     * DO NOT OVERRIDE THIS
     * Please override {@link #getItemViewType(int, Object)}
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, getItem(position));
    }

    /**
     * DO NOT OVERRIDE THIS
     * Please override {@link #onCreateViewHolder(LayoutInflater, ViewGroup, int)}
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PeasyViewHolder vh = onCreateViewHolder(inflater, parent, viewType);
        vh.bindWith(this, viewType);
        return vh;
    }

    /**
     * DO NOT OVERRIDE THIS
     * Please override {@link #onBindViewHolder(Context, PeasyViewHolder, int, Object)}
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((PeasyViewHolder) holder, position);
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolder(PeasyViewHolder holder, int position) {
        onBindViewHolder(getContext(), holder, position, getItem(position));
    }

    /**
     * To check size of content
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return this.recyclerDataSource.size();
    }

    /**
     * To check content size is zero/content is empty
     *
     * @return
     */
    public boolean isEmpty() {
        return (this.recyclerDataSource == null) ? true : this.recyclerDataSource.isEmpty();
    }

    /**
     * Retrieve content at position
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return isEmpty() ? null : this.recyclerDataSource.get(position);
    }

    /**
     * PLEASE OVERRIDE THIS
     *
     * @return first visible item position from Layout Manager
     */
    public int getFirstVisibleItemPosition() {
        return -1;
    }

    /**
     * PLEASE OVERRIDE THIS
     *
     * @return last visible item position from Layout Manager
     */
    public int getLastVisibleItemPosition() {
        return -1;
    }

    /**
     * To check all contents are visible
     *
     * @return
     */
    public boolean hasAllContentsVisible() {
        try {
            if (getFirstVisibleItemPosition() == -1 || getLastVisibleItemPosition() == -1) {
                return false;
            } else {
                return getFirstVisibleItemPosition() == 0 && getLastVisibleItemPosition() == (getItemCount() - 1);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enhanced Implementation Layer of {@link View.OnClickListener#onClick(View)}
     * Target on itemView of {@link PeasyViewHolder}
     * Here you should define recycler view member single click action
     *
     * @param v
     * @param viewType
     * @param position
     * @return
     */
    public void onItemClick(View v, int viewType, int position) {
    }

    /**
     * Enhanced Implementation Layer of {@link View.OnLongClickListener#onLongClick(View)} (View)}
     * Target on itemView of {@link PeasyViewHolder}
     * Here you should define recycler view member long click action
     *
     * @param v
     * @param viewType
     * @param position
     * @return
     */
    public boolean onItemLongClick(View v, int viewType, int position) {
        return true;
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrolled(RecyclerView, int, int)}
     * Target on itemView of {@link PeasyRecyclerView#recyclerView}
     * Here you should define recycler view on scroll action with dy, dx feedback
     *
     * @param recyclerView
     * @param dx
     * @param dy
     */
    public void onViewScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrollStateChanged(RecyclerView, int)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     * Here you should define recycler view on scroll action with state feedback
     *
     * @param recyclerView
     * @param newState
     */
    public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnItemTouchListener#onInterceptTouchEvent(RecyclerView, MotionEvent)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     *
     * @param rv
     * @param e
     */
    public void onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    /**
     * Method to navigate to top of recycler view with smooth scroll
     */
    public void smoothScrollToFirst() {
        if (getLinearLayoutManager() != null) {
            final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this.recyclerView.getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(0);
            getLinearLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    /**
     * Method to navigate to top of recycler view with smooth scroll
     */
    public void setPositionToFirst() {
        if (getLinearLayoutManager() != null) {
            getLinearLayoutManager().scrollToPositionWithOffset(0, 0);
        }
    }

    /**
     * Method to attempt retrieving {@link android.support.v7.widget.RecyclerView.LayoutManager} of RecyclerView as {@link LinearLayoutManager}
     *
     * @return null if no {@link LinearLayoutManager} instance found
     */
    public LinearLayoutManager getLinearLayoutManager() {
        try {
            if (getRecyclerView().getLayoutManager() instanceof LinearLayoutManager) {
                return (LinearLayoutManager) this.recyclerView.getLayoutManager();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Method to attempt retrieving {@link android.support.v7.widget.RecyclerView.LayoutManager} of RecyclerView as {@link GridLayoutManager}
     *
     * @return null if no {@link GridLayoutManager} instance found
     */
    public GridLayoutManager getGridLayoutManager() {
        try {
            if (getRecyclerView().getLayoutManager() instanceof GridLayoutManager) {
                return (GridLayoutManager) this.recyclerView.getLayoutManager();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Method to attempt retrieving {@link android.support.v7.widget.RecyclerView.LayoutManager} of RecyclerView as {@link StaggeredGridLayoutManager}
     *
     * @return null if no {@link StaggeredGridLayoutManager} instance found
     */
    public StaggeredGridLayoutManager getStaggeredGridLayoutManager() {
        try {
            if (getRecyclerView().getLayoutManager() instanceof StaggeredGridLayoutManager) {
                return (StaggeredGridLayoutManager) this.recyclerView.getLayoutManager();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    //==========================================================================================
    // CONTRACTUAL METHODS
    //==========================================================================================

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * Here you should return initialized {@link PeasyViewHolder}
     *
     * @param inflater
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     * Here you should populate views in {@link PeasyViewHolder} with item returned in this method
     *
     * @param context
     * @param holder
     * @param position
     * @param item
     */
    protected abstract void onBindViewHolder(Context context, PeasyViewHolder holder, int position, T item);

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#getItemViewType(int)}
     * Here you should define or decide view type
     *
     * @param position
     * @param item
     * @return
     */
    protected abstract int getItemViewType(int position, T item);


    /**
     * To reset added {@link android.support.v7.widget.RecyclerView.ItemDecoration}
     */
    public void resetItemDecorations() {
        for (int i = 0; i < getRecyclerView().getItemDecorationCount(); i++) {
            try {
                getRecyclerView().removeItemDecoration(getRecyclerView().getItemDecorationAt(i));
            } catch (Exception e) {
            }
        }
    }

    /**
     * To reset added {@link android.support.v7.widget.RecyclerView.ItemAnimator}
     */
    public void resetItemAnimator() {
        try {
            getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
        }
    }

    /**
     * Present as Vertical List View
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return LinearLayoutManager
     */
    public LinearLayoutManager asVerticalListView() {
        resetItemDecorations();
        resetItemAnimator();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Horizontal List View
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return LinearLayoutManager
     */
    public LinearLayoutManager asHorizontalListView() {
        resetItemDecorations();
        resetItemAnimator();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    //==========================================================================================

    /**
     * Blueprint to substitute RecyclerView.ViewHolder
     * Require you to return {@link PeasyViewHolder } from {@link #onCreateViewHolder(LayoutInflater, ViewGroup, int)}
     * Will pass back {@link PeasyViewHolder } to {@link #onBindViewHolder(Context, PeasyViewHolder, int, Object)}
     * Consist {@link PeasyViewHolder#VIEWTYPE_NOTHING} to use as non-existence ViewType
     */
    public static abstract class PeasyViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEWTYPE_NOTHING = Integer.MAX_VALUE * -1;

        public PeasyViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Will bind onClick and setOnLongClickListener here
         *
         * @param binder   {@link PeasyViewHolder} itself
         * @param viewType
         */
        void bindWith(final PeasyRecyclerView binder, final int viewType) {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLayoutPosition() != RecyclerView.NO_POSITION) {
                        binder.onItemClick(v, viewType, getLayoutPosition());
                    }
                }
            });
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getLayoutPosition() != RecyclerView.NO_POSITION) {
                        return binder.onItemLongClick(v, viewType, getLayoutPosition());
                    }
                    return false;
                }
            });
        }

        /**
         * To check instanceof
         *
         * @param cls Class to check
         * @return
         */
        public boolean isInstance(Class cls) {
            return cls.isInstance(this);
        }

        /**
         * Static method to help inflate parent view with layoutId
         *
         * @param inflater
         * @param parent
         * @param layoutId
         * @return
         */
        public static View inflateView(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            return inflater.inflate(layoutId, parent, false);
        }
    }

    /**
     * Easy Peasy to implement Vertical Recycler View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalList<T> extends PeasyRecyclerView<T> {

        private LinearLayoutManager layoutManager;

        public VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            this.layoutManager = this.asVerticalListView();
        }

        @Override
        public int getLastVisibleItemPosition() {
            return this.layoutManager.findLastCompletelyVisibleItemPosition();
        }

        @Override
        public int getFirstVisibleItemPosition() {
            return this.layoutManager.findFirstCompletelyVisibleItemPosition();
        }

        @Override
        public LinearLayoutManager getLinearLayoutManager() {
            return this.layoutManager;
        }
    }

    public static abstract class HorizontalList<T> extends PeasyRecyclerView<T> {

        private LinearLayoutManager layoutManager;

        public HorizontalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            this.layoutManager = this.asHorizontalListView();
        }

        @Override
        public int getLastVisibleItemPosition() {
            return this.layoutManager.findLastCompletelyVisibleItemPosition();
        }

        @Override
        public int getFirstVisibleItemPosition() {
            return this.layoutManager.findFirstCompletelyVisibleItemPosition();
        }

        @Override
        public LinearLayoutManager getLinearLayoutManager() {
            return this.layoutManager;
        }
    }

}