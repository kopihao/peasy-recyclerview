package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Arrays;

/**
 * An Adapter as well as a RecyclerView Binder
 *
 * @param <T> type of array list content
 */
public abstract class PeasyRecyclerView<T> extends RecyclerView.Adapter {

    /**
     * Available Presentations provided by PeasyRecyclerView
     */
    public enum Presentation {
        undefined,
        VerticalList,
        HorizontalList,
        BasicGrid,
        VerticalStaggeredGrid,
        HorizontalStaggeredGrid,
    }

    private Presentation presentation = Presentation.undefined;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<T> recyclerDataSource;
    private FloatingActionButton fab;
    private boolean smartHiding = false;
    private Bundle extraData = null;
    private PeasyHeaderContent<T> headerContent = null;
    private PeasyFooterContent<T> footerContent = null;
    public static final int DefaultGridColumnSize = 2;
    private static String ExtraColumnSize = "column_size";

    public PeasyRecyclerView(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
        this(context, recyclerView, arrayList, new Bundle());
    }

    public PeasyRecyclerView(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.extraData = extraData;
        this.onCreate(context, recyclerView, arrayList, extraData);
        this.setContent(arrayList, recyclerView);
    }

    /**
     * On PeasyRecyclerView created
     * Where you wish to execute some task before constructor run
     * Overriding this method is not an encouraging recommendation
     *
     * @param context
     * @param recyclerView
     * @param arrayList
     * @param extraData
     */
    public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
    }

    /**
     * Update your array list content with recycler view binding at the same time
     * Execute at {@link PeasyRecyclerView} constructor for once
     * Use {@link #setContent(ArrayList)} method to update content only
     *
     * @param arrayList
     * @param recyclerView
     */
    private void setContent(ArrayList<T> arrayList, RecyclerView recyclerView) {
        this.setContent(arrayList);
        this.recyclerView = recyclerView;
        this.configureRecyclerView(getRecyclerView());
        this.recyclerView.setAdapter(this);
    }

    /**
     * Method to update array list content
     *
     * @param content
     */
    public void setContent(final ArrayList<T> content) {
        final ArrayList<T> arrayList = (content == null) ? new ArrayList<T>() : new ArrayList<>(content);
        if (this.headerContent != null) {
            arrayList.add(0, this.headerContent.getData()); //add header as null
        }
        if (this.footerContent != null) {
            arrayList.add(this.footerContent.getData()); //add footer
        }
        this.recyclerDataSource = new ArrayList<T>(arrayList);
        this.notifyDataSetChanged();
    }

    /**
     * Method to retrieve latest contents
     *
     * @return
     */
    public ArrayList<T> getContent() {
        return new ArrayList<>(this.recyclerDataSource);
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
     * To host extra data
     *
     * @return
     */
    public Bundle getExtraData() {
        return extraData;
    }

    /**
     * To identify current {@link Presentation}
     *
     * @return
     */
    public Presentation getPresentation() {
        return this.presentation;
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
                PeasyRecyclerView.this.onViewInterceptTouchEvent(rv, e);
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
    public void anchorFAB(FloatingActionButton fab) {
        anchorFAB(fab, true);
    }

    /**
     * Method to bind FAB to RecyclerView
     *
     * @param fab
     * @param smartHiding
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, int, int)
     * @see PeasyViewHolder#handleFAB(RecyclerView, FloatingActionButton, MotionEvent, boolean)
     */
    public void anchorFAB(FloatingActionButton fab, boolean smartHiding) {
        this.fab = fab;
        this.smartHiding = smartHiding;
    }

    /**
     * To add header content
     * Should override {@link #setContent(ArrayList)} and call before its super method
     *
     * @param headerContent
     */
    public void setHeaderContent(PeasyHeaderContent<T> headerContent) {
        this.headerContent = headerContent;
    }

    /**
     * To add footer content
     * Should override {@link #setContent(ArrayList)} and call before its super method
     *
     * @param footerContent
     */
    public void setFooterContent(PeasyFooterContent<T> footerContent) {
        this.footerContent = footerContent;
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
     * Handle FAB button when RecyclerView scrolled
     * By default handle only RecyclerView setup by {@link #asVerticalListView()}
     * Which is {@link LinearLayoutManager#VERTICAL} mode
     *
     * @param recyclerView
     * @param fab
     * @param dx
     * @param dy
     */
    private void handleFAB(RecyclerView recyclerView, final FloatingActionButton fab, int dx, int dy) {
        if (this.smartHiding && getFab() != null) {
            final FloatingActionButton mFloatingActionButton = this.fab;
            if (getLinearLayoutManager() != null) {
                final int differential = dy;
                // final int differential = getLinearLayoutManager().getOrientation() == LinearLayoutManager.VERTICAL ? dy : dx;
                if (differential > 0) {
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
    }

    /**
     * By default, show FAB when all item visible within view port
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
        if (PeasyHeaderViewHolder.VIEWTYPE_NOTHING != getHeaderViewType(position)) {
            return getHeaderViewType(position);
        }
        if (PeasyFooterViewHolder.VIEWTYPE_NOTHING != getFooterViewType(position)) {
            return getFooterViewType(position);
        }
        return getItemViewType(position, getItem(position));
    }

    /**
     * To identify Header View Type
     *
     * @param position
     * @return
     */
    public int getHeaderViewType(int position) {
        if (headerContent != null) {
            if (headerContent.getData() == getItem(position) && (position == 0)) {
                return headerContent.getViewtype();
            }
        }
        return PeasyHeaderViewHolder.VIEWTYPE_NOTHING;
    }

    /**
     * To identify Footer View Type
     *
     * @param position
     * @return
     */
    public int getFooterViewType(int position) {
        if (footerContent != null) {
            if (footerContent.getData() == getItem(position) && (position == getLastIndex())) {
                return footerContent.getViewtype();
            }
        }
        return PeasyHeaderViewHolder.VIEWTYPE_NOTHING;
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
        if (headerContent != null) {
            if (viewType == headerContent.getViewtype()) {
                final PeasyHeaderViewHolder header = headerContent.onCreateViewHolder(inflater, parent, viewType);
                if (header != null) {
                    header.bindWith(this, viewType);
                    return header;
                }
            }
        }
        if (footerContent != null) {
            if (viewType == footerContent.getViewtype()) {
                final PeasyFooterViewHolder footer = footerContent.onCreateViewHolder(inflater, parent, viewType);
                if (footer != null) {
                    footer.bindWith(this, viewType);
                    return footer;
                }
            }
        }
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
        if (holder == null) return;
        onBindViewHolder((PeasyViewHolder) holder, position);
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolder(PeasyViewHolder holder, int position) {
        if (holder == null) return;
        if (headerContent != null) {
            if (holder.isHeaderView()) {
                headerContent.onBindViewHolder(getContext(), (PeasyHeaderViewHolder) holder, position, getItem(position));
            }
        }
        if (footerContent != null) {
            if (holder.isFooterView()) {
                footerContent.onBindViewHolder(getContext(), (PeasyFooterViewHolder) holder, position, getItem(position));
            }
        }
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
     * To get last index of content
     *
     * @return
     */
    public int getLastIndex() {
        return isEmpty() ? 0 : this.getItemCount() - 1;
    }

    /**
     * PLEASE OVERRIDE THIS
     *
     * @return first visible item position from Layout Manager
     */
    public int getFirstVisibleItemPosition() {
        return findFirstCompletelyVisibleItemPosition();
    }

    /**
     * PLEASE OVERRIDE THIS
     *
     * @return last visible item position from Layout Manager
     */
    public int getLastVisibleItemPosition() {
        return findLastCompletelyVisibleItemPosition();
    }

    /**
     * Adaptive method to findFirstCompletelyVisibleItemPosition according to instance of {@link android.support.v7.widget.RecyclerView.LayoutManager}
     *
     * @return
     */
    private int findFirstCompletelyVisibleItemPosition() {
        if (getLinearLayoutManager() != null) {
            if (getLinearLayoutManager().getOrientation() == LinearLayoutManager.VERTICAL) {
                return VerticalList.findFirstCompletelyVisibleItemPosition(getLinearLayoutManager());
            } else if (getLinearLayoutManager().getOrientation() == LinearLayoutManager.HORIZONTAL) {
                return HorizontalList.findFirstCompletelyVisibleItemPosition(getLinearLayoutManager());
            }
        } else if (getGridLayoutManager() != null) {
            return BasicGrid.findFirstCompletelyVisibleItemPosition(getGridLayoutManager());
        } else if (getStaggeredGridLayoutManager() != null) {
            if (getStaggeredGridLayoutManager().getOrientation() == LinearLayoutManager.VERTICAL) {
                return VerticalStaggeredGrid.findFirstCompletelyVisibleItemPositions(getStaggeredGridLayoutManager());
            } else if (getStaggeredGridLayoutManager().getOrientation() == StaggeredGridLayoutManager.HORIZONTAL) {
                return HorizontalStaggeredGrid.findFirstCompletelyVisibleItemPositions(getStaggeredGridLayoutManager());
            }
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * Adaptive method to findLastCompletelyVisibleItemPosition according to instance of {@link android.support.v7.widget.RecyclerView.LayoutManager}
     *
     * @return
     */
    private int findLastCompletelyVisibleItemPosition() {
        if (getLinearLayoutManager() != null) {
            if (getLinearLayoutManager().getOrientation() == LinearLayoutManager.VERTICAL) {
                return VerticalList.findLastCompletelyVisibleItemPosition(getLinearLayoutManager());
            } else if (getLinearLayoutManager().getOrientation() == LinearLayoutManager.HORIZONTAL) {
                return HorizontalList.findLastCompletelyVisibleItemPosition(getLinearLayoutManager());
            }
        } else if (getGridLayoutManager() != null) {
            return BasicGrid.findLastCompletelyVisibleItemPosition(getGridLayoutManager());
        } else if (getStaggeredGridLayoutManager() != null) {
            if (getStaggeredGridLayoutManager().getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                return VerticalStaggeredGrid.findLastCompletelyVisibleItemPositions(getStaggeredGridLayoutManager());
            } else if (getStaggeredGridLayoutManager().getOrientation() == StaggeredGridLayoutManager.HORIZONTAL) {
                return HorizontalStaggeredGrid.findLastCompletelyVisibleItemPositions(getStaggeredGridLayoutManager());
            }
        }
        return RecyclerView.NO_POSITION;
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
     * @param view
     * @param viewType
     * @param position
     * @param item
     * @param viewHolder
     */
    public void onItemClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
    }

    /**
     * Enhanced Implementation Layer of {@link View.OnLongClickListener#onLongClick(View)} (View)}
     * Target on itemView of {@link PeasyViewHolder}
     * Here you should define recycler view member long click action
     *
     * @param view
     * @param viewType
     * @param position
     * @param item
     * @param viewHolder
     * @return
     */
    public boolean onItemLongClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
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
    public void onViewScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrollStateChanged(RecyclerView, int)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     * Here you should define recycler view on scroll action with state feedback
     *
     * @param recyclerView
     * @param newState
     */
    public void onViewScrollStateChanged(final RecyclerView recyclerView, final int newState) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnItemTouchListener#onInterceptTouchEvent(RecyclerView, MotionEvent)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     *
     * @param rv
     * @param e
     */
    public void onViewInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
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

    /**
     * Help to cast provided PeasyViewHolder to its child class instance
     *
     * @param vh
     * @param cls
     * @param <VH>
     * @return
     */
    public static <VH extends PeasyViewHolder> VH GetViewHolder(PeasyViewHolder vh, Class<VH> cls) {
        return cls.cast(vh);
    }

    //==========================================================================================
    // CONTRACTUAL METHODS
    //==========================================================================================

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * Here you should return initialized {@link PeasyViewHolder}
     * Perform click action refer {@link #onItemClick(View, int, int, T, PeasyViewHolder)}
     * Perform long click refer {@link #onItemLongClick(View, int, int, T, PeasyViewHolder)}
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
    protected abstract void onBindViewHolder(final Context context, final PeasyViewHolder holder, final int position, final T item);

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#getItemViewType(int)}
     * Here you should define or decide view type
     *
     * @param position
     * @param item
     * @return
     */
    protected abstract int getItemViewType(final int position, final T item);

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
        this.presentation = Presentation.VerticalList;
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
        this.presentation = Presentation.HorizontalList;
        resetItemDecorations();
        resetItemAnimator();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Grid View
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return GridLayoutManager
     */
    public GridLayoutManager asGridView(final int columns) {
        return asGridView((columns == 0) ? BasicGrid.DefaultGridColumnSize : columns,
                new PeasyGridDividerItemDecoration(
                        getContext().getResources().getDimensionPixelSize(R.dimen.peasy_grid_divider_spacing),
                        (columns == 0) ? BasicGrid.DefaultGridColumnSize : columns
                ));
    }

    /**
     * Present as Grid View
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns
     * @param divider RecyclerView.ItemDecoration
     * @return
     */
    public GridLayoutManager asGridView(final int columns, RecyclerView.ItemDecoration divider) {
        this.presentation = Presentation.BasicGrid;
        resetItemDecorations();
        resetItemAnimator();
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), (columns == 0) ? BasicGrid.DefaultGridColumnSize : columns);
        getRecyclerView().setLayoutManager(layoutManager);
        if (divider != null) {
            getRecyclerView().addItemDecoration(divider);
        }
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }


    /**
     * Present as Staggered Grid View in Vertical Orientation
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(final int columns) {
        return asVerticalStaggeredGridView((columns == 0) ? BasicGrid.DefaultGridColumnSize : columns,
                new PeasyGridDividerItemDecoration(
                        getContext().getResources().getDimensionPixelSize(R.dimen.peasy_grid_divider_spacing),
                        (columns == 0) ? BasicGrid.DefaultGridColumnSize : columns
                ));
    }

    /**
     * Present as Staggered Grid View in Vertical Orientation
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns
     * @param divider RecyclerView.ItemDecoration
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(final int columns, RecyclerView.ItemDecoration divider) {
        this.presentation = Presentation.VerticalStaggeredGrid;
        return asStaggeredGridView(columns, StaggeredGridLayoutManager.VERTICAL, divider);
    }

    /**
     * Present as Staggered Grid View in Horizontal Orientation
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(final int columns) {
        return asHorizontalStaggeredGridView((columns == 0) ? BasicGrid.DefaultGridColumnSize : columns,
                new PeasyGridDividerItemDecoration(
                        getContext().getResources().getDimensionPixelSize(R.dimen.peasy_grid_divider_spacing),
                        (columns == 0) ? BasicGrid.DefaultGridColumnSize : columns
                ));
    }

    /**
     * Present as Staggered Grid View in Horizontal Orientation
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns
     * @param divider RecyclerView.ItemDecoration
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(final int columns, RecyclerView.ItemDecoration divider) {
        this.presentation = Presentation.HorizontalStaggeredGrid;
        return asStaggeredGridView(columns, StaggeredGridLayoutManager.HORIZONTAL, divider);
    }

    /**
     * Present as Staggered Grid View
     * Be noted, columns will be {@value BasicGrid#DefaultGridColumnSize} if input columns is 0
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns StaggeredGridLayoutManager.VERTICAL or StaggeredGridLayoutManager.HORIZONTAL
     * @param divider RecyclerView.ItemDecoration
     * @return StaggeredGridLayoutManager
     */
    private StaggeredGridLayoutManager asStaggeredGridView(final int columns, final int orientation, RecyclerView.ItemDecoration divider) {
        resetItemDecorations();
        resetItemAnimator();
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager((columns == 0) ? BasicGrid.DefaultGridColumnSize : columns, orientation);
        getRecyclerView().setLayoutManager(layoutManager);
        if (divider != null) {
            getRecyclerView().addItemDecoration(divider);
        }
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    //==========================================================================================

    /**
     * Simple ItemDecoration designed for GridLayoutManager use within RecyclerView
     * PeasyGridDividerItemDecoration require 2 parameters: int gridSpacingPx, int gridSize
     * Please check out {@link PeasyGridDividerItemDecoration#PeasyGridDividerItemDecoration(int, int)}
     * Credit to @see <a href="https://stackoverflow.com/a/29168276">decorating-recyclerview-with-gridlayoutmanager-to-display-divider-between-item</a>
     */
    public static class PeasyGridDividerItemDecoration extends RecyclerView.ItemDecoration {

        private int mSizeGridSpacingPx;
        private int mGridSize;

        private boolean mNeedLeftSpacing = false;

        public PeasyGridDividerItemDecoration(int gridSpacingPx, int gridSize) {
            mSizeGridSpacingPx = gridSpacingPx;
            mGridSize = gridSize;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            drawDefaultDivider(outRect, view, parent, state);
        }

        private void drawDefaultDivider(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int frameWidth = (int) ((parent.getWidth() - (float) mSizeGridSpacingPx * (mGridSize - 1)) / mGridSize);
            int padding = parent.getWidth() / mGridSize - frameWidth;
            int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
            if (itemPosition < mGridSize) {
                outRect.top = 0;
            } else {
                outRect.top = mSizeGridSpacingPx;
            }
            if (itemPosition % mGridSize == 0) {
                outRect.left = 0;
                outRect.right = padding;
                mNeedLeftSpacing = true;
            } else if ((itemPosition + 1) % mGridSize == 0) {
                mNeedLeftSpacing = false;
                outRect.right = 0;
                outRect.left = padding;
            } else if (mNeedLeftSpacing) {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx - padding;
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding;
                } else {
                    outRect.right = mSizeGridSpacingPx / 2;
                }
            } else if ((itemPosition + 2) % mGridSize == 0) {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx - padding;
            } else {
                mNeedLeftSpacing = false;
                outRect.left = mSizeGridSpacingPx / 2;
                outRect.right = mSizeGridSpacingPx / 2;
            }
            outRect.bottom = 0;
        }
    }

    //==========================================================================================

    /**
     * Blueprint to substitute RecyclerView.ViewHolder
     * Require you to return {@link PeasyViewHolder } from {@link #onCreateViewHolder(LayoutInflater, ViewGroup, int)}
     * Will pass back {@link PeasyViewHolder } to {@link #onBindViewHolder(Context, PeasyViewHolder, int, Object)}
     * Consist {@link PeasyViewHolder#VIEWTYPE_NOTHING} to use as non-existence ViewType
     */
    public static abstract class PeasyViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEWTYPE_NOTHING = Integer.MIN_VALUE;

        public PeasyViewHolder(View itemView) {
            super(itemView);
        }


        /**
         * Help to cast provided PeasyViewHolder to its instance
         *
         * @param cls
         * @param <VH>
         * @return
         */
        public <VH extends PeasyViewHolder> VH asIs(Class<VH> cls) {
            return PeasyRecyclerView.GetViewHolder(this, cls);
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
                    final int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        binder.onItemClick(v, viewType, position, binder.getItem(position), getInstance());
                    }
                }
            });
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int position = getLayoutPosition();
                    if (getLayoutPosition() != RecyclerView.NO_POSITION) {
                        return binder.onItemLongClick(v, viewType, position, binder.getItem(position), getInstance());
                    }
                    return false;
                }
            });
        }

        PeasyViewHolder getInstance() {
            return this;
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
     * PeasyViewHolder Blueprint for Header Purpose
     * With this implementation, view holder can be identified by {@link PeasyViewHolder#isHeaderView()}
     */
    public static abstract class PeasyHeaderViewHolder extends PeasyViewHolder {
        public static final int VIEWTYPE_HEADER = Integer.MAX_VALUE - 1;

        public PeasyHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * PeasyViewHolder Blueprint for Content Purpose
     * With this implementation, view holder can be identified by {@link PeasyViewHolder#isFooterView()}
     */
    public static abstract class PeasyContentViewHolder extends PeasyViewHolder {
        public static final int VIEWTYPE_CONTENT = Integer.MAX_VALUE - 2;

        public PeasyContentViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * PeasyViewHolder Blueprint for Footer Purpose
     * With this implementation, view holder can be identified by {@link PeasyViewHolder#isFooterView()}
     */
    public static abstract class PeasyFooterViewHolder extends PeasyViewHolder {
        public static final int VIEWTYPE_FOOTER = Integer.MAX_VALUE - 3;

        public PeasyFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    //==========================================================================================

    /**
     * Blueprint of Content that represents its Unique Coordination or has Special Purpose
     * PeasyCoordinatorContent represents its viewtype, data and viewholder implementations
     *
     * @param <D> Data Type
     */
    private static abstract class PeasyCoordinatorContent<VH extends PeasyViewHolder, D> {

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
    }

    /**
     * Extended Blueprint of @{@link PeasyCoordinatorContent}
     * Coordination : Header section of list
     *
     * @param <D>
     */
    public static abstract class PeasyHeaderContent<D> extends PeasyCoordinatorContent<PeasyHeaderViewHolder, D> {
        public PeasyHeaderContent(int viewtypeId, D data) {
            super(viewtypeId, data);
        }
    }

    /**
     * Extended Blueprint of @{@link PeasyCoordinatorContent}
     * Coordination : Content section of list
     *
     * @param <D> Data Type
     */
    public static abstract class PeasyFooterContent<D> extends PeasyCoordinatorContent<PeasyFooterViewHolder, D> {
        public PeasyFooterContent(int viewtypeId, D data) {
            super(viewtypeId, data);
        }
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

        public VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
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
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return position;
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

        private LinearLayoutManager layoutManager;

        public HorizontalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
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
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return position;
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

        private static Bundle bundleColumnSize(Bundle bundle, int columnSize) {
            final Bundle extraData = (bundle == null) ? new Bundle() : bundle;
            extraData.putInt(ExtraColumnSize, columnSize);
            return extraData;
        }

        private int columnSize = 0;

        public BasicGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            this(context, recyclerView, arrayList, DefaultGridColumnSize);
        }

        public BasicGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            this.columnSize = extraData.getInt(ExtraColumnSize, DefaultGridColumnSize); // Assign extraData values
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asGridView(this.columnSize);
        }

        public int getColumnSize() {
            return columnSize;
        }

        static int findLastCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findLastCompletelyVisibleItemPosition();
            position = (position == RecyclerView.NO_POSITION) ? layoutManager.findLastVisibleItemPosition() : position;
            return position;
        }

        static int findFirstCompletelyVisibleItemPosition(LinearLayoutManager layoutManager) {
            int position = layoutManager.findFirstCompletelyVisibleItemPosition();
            return position;
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

        private static Bundle bundleColumnSize(Bundle bundle, int columnSize) {
            final Bundle extraData = (bundle == null) ? new Bundle() : bundle;
            extraData.putInt(ExtraColumnSize, columnSize);
            return extraData;
        }

        private int columnSize = 0;

        public VerticalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            this(context, recyclerView, arrayList, DefaultGridColumnSize);
        }

        public VerticalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            this.columnSize = extraData.getInt(ExtraColumnSize, DefaultGridColumnSize); // Assign extraData values
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            super.asGridView(this.getColumnSize());
        }

        public int getColumnSize() {
            return this.columnSize;
        }

        static int findLastCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            into = layoutManager.findLastCompletelyVisibleItemPositions(into);
            int position = RecyclerView.NO_POSITION;
            {   // FIND MAX
                Arrays.sort(into);
                for (int i = 0; i < into.length; i++) {
                    if (into[i] == RecyclerView.NO_POSITION) continue; // No interest in this value
                    if (into[i] > position) {
                        position = into[i]; // Continue to replace if larger value met
                    }
                }
                position = Math.max(RecyclerView.NO_POSITION, position);
            }
            return position;
        }

        static int findFirstCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            int position = layoutManager.findFirstVisibleItemPositions(into)[0];
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

        private static Bundle bundleColumnSize(Bundle bundle, int columnSize) {
            final Bundle extraData = (bundle == null) ? new Bundle() : bundle;
            extraData.putInt(ExtraColumnSize, columnSize);
            return extraData;
        }

        private int columnSize = 0;

        public HorizontalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            this(context, recyclerView, arrayList, DefaultGridColumnSize);
        }

        public HorizontalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
            super(context, recyclerView, arrayList, bundleColumnSize(new Bundle(), columnSize));
        }

        @Override
        public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
            this.columnSize = extraData.getInt(ExtraColumnSize, DefaultGridColumnSize); // Assign extraData values
            super.onCreate(context, recyclerView, arrayList, extraData);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            this.asGridView(this.getColumnSize());
        }

        public int getColumnSize() {
            return this.columnSize;
        }

        static int findLastCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            into = layoutManager.findLastCompletelyVisibleItemPositions(into);
            int position = RecyclerView.NO_POSITION;
            {   // FIND MAX
                Arrays.sort(into);
                for (int i = 0; i < into.length; i++) {
                    if (into[i] == RecyclerView.NO_POSITION) continue; // No interest in this value
                    if (into[i] > position) {
                        position = into[i]; // Continue to replace if larger value met
                    }
                }
                position = Math.max(RecyclerView.NO_POSITION, position);
            }
            return position;
        }

        static int findFirstCompletelyVisibleItemPositions(StaggeredGridLayoutManager layoutManager) {
            int[] into = new int[layoutManager.getSpanCount()];
            int position = layoutManager.findFirstVisibleItemPositions(into)[0];
            return position;
        }
    }

}