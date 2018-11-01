package com.kopirealm.peasyrecyclerview;

import android.content.Context;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * An Binder as well as a adapter of RecyclerView
 *
 * @param <T> class type of array list content
 */
public abstract class PeasyRecyclerView<T> extends RecyclerView.Adapter {

    private PeasyPresentation presentation = PeasyPresentation.Undefined;
    static final int DefaultGridColumnSize = 2;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<T> contents;
    private Bundle extraData = null;
    private PeasyHeaderContent<T> headerContent = null;
    private PeasyFooterContent<T> footerContent = null;
    private FloatingActionButton fab;
    private boolean enhancedFAB = false;
    private static final int DefaultEOLThreshold = 1;
    private static final int DisabledEOLThreshold = -1;
    private int thresholdEOL = DisabledEOLThreshold;
    private final AtomicBoolean lockEOL = new AtomicBoolean(false);
    private Integer lastState = null;
    private PeasyRecyclerViewIdlePosition scrollPosition = PeasyRecyclerViewIdlePosition.TOP;

    //=============================
    // Constructor
    //=============================

    public PeasyRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ArrayList<T> arrayList) {
        this(context, recyclerView, arrayList, new Bundle());
    }

    public PeasyRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ArrayList<T> arrayList, @NonNull Bundle extraData) {
        this.context = context;
        this.extraData = extraData;
        this.recyclerView = recyclerView;
        this.recyclerView.setAdapter(this);
        this.configureRecyclerView(recyclerView);
        this.setContent(arrayList);
        this.onCreated(this);
    }

    //=============================
    // Life Cycle & Feeds
    //=============================

    /**
     * Executes lines of code before constructor ended
     *
     * @param peasyRecyclerView peasyRecyclerView
     */
    protected void onCreated(@NonNull PeasyRecyclerView peasyRecyclerView) {
    }

    /**
     * Customize presentation of provided recyclerView
     * eg. {@link LinearLayoutManager} , {@link RecyclerView.ItemAnimator }, {@link RecyclerView.ItemDecoration}
     * By default, FAB handling will be contracted at here
     *
     * @param recyclerView recyclerView
     */
    protected void configureRecyclerView(RecyclerView recyclerView) {
        this.enableNestedScroll(true);
        this.disableScrollEndDetection();
        this.configureRecyclerViewTouchEvent();
        this.configureRecyclerViewScrollEvent();
    }

    @Override
    public final void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        if (lastState == null) {
            lastState = RecyclerView.SCROLL_STATE_IDLE;
            onViewReady();
        }
        onViewHolderAttached(viewHolder);
    }

    //=============================
    // FAB Handling
    //=============================

    /**
     * Anchor FAB to {@link PeasyRecyclerView}
     * This will enhance FAB UX by default
     *
     * @param fab FloatingActionButton
     * @see #anchorFAB(FloatingActionButton, boolean)
     */
    public void anchorFAB(FloatingActionButton fab) {
        anchorFAB(fab, true);
    }

    /**
     * Anchor FAB to {@link PeasyRecyclerView}
     *
     * @param fab     FloatingActionButton
     * @param enhance enhance FAB UX
     * @see #enhanceFAB(FloatingActionButton, int, int)
     * @see #enhanceFAB(FloatingActionButton, MotionEvent)
     */
    public void anchorFAB(FloatingActionButton fab, boolean enhance) {
        this.fab = fab;
        this.enhancedFAB = enhance;
    }

    /***
     *  Provide default {@link RecyclerView.OnItemTouchListener} of provided recyclerView
     */
    private void configureRecyclerViewTouchEvent() {
        getRecyclerView().addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (isEnhancedFAB() && getFab() != null) {
                    enhanceFAB(getFab(), e);
                }
                onViewInterceptTouchEvent(rv, e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }

        });
    }

    /***
     *  Provide default {@link RecyclerView.OnScrollListener} of provided recyclerView
     */
    private void configureRecyclerViewScrollEvent() {
        getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        enhanceFAB(getFab(), dx, dy);
                        onViewScrolled(recyclerView, dx, dy);
                        onViewScrolledToEnd(recyclerView, dx, dy);
                    }
                });
            }

            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        lastState = newState;
                        onViewScrollStateChanged(recyclerView, newState);
                        if (newState == SCROLL_STATE_IDLE) {
                            if (checkViewScrolledToFirst()) {
                                onViewScrolledToFirst(recyclerView);
                            }
                            if (checkViewScrolledToLast()) {
                                onViewScrolledToLast(recyclerView);
                            }
                            synchronized (lockEOL) {
                                if (lockEOL.get()) {
                                    lockEOL.set(!lockEOL.get());
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    /**
     * Enhanced FAB UX Logic
     * Handle RecyclerView scrolling
     *
     * @param fab FloatingActionButton
     * @param dx  scrolling dx
     * @param dy  scrolling dy
     */
    private void enhanceFAB(final FloatingActionButton fab, int dx, int dy) {
        if (isEnhancedFAB() && getFab() != null) {
            final FloatingActionButton mFloatingActionButton = this.fab;
            if (getLinearLayoutManager() != null) {
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
    }

    /**
     * Enhanced FAB UX Logic
     * Handle RecyclerView scrolled
     * If all item visible within view port, FAB will show
     *
     * @param fab FloatingActionButton
     * @param e   MotionEvent
     */
    private void enhanceFAB(final FloatingActionButton fab, MotionEvent e) {
        if (hasAllItemsShown()) {
            if (fab.getVisibility() != View.VISIBLE) {
                fab.show();
            }
        }
    }

    //=============================
    // Getters
    //=============================

    /**
     * @return {@link Context} provided
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * @return {@link RecyclerView} provided
     */
    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    /**
     * @return current contents
     */
    public ArrayList<T> getContents() {
        return this.contents;
    }

    /**
     * @return true if FAB handled by this class
     */
    public boolean isEnhancedFAB() {
        return enhancedFAB;
    }

    /**
     * @return bundle of data
     */
    public Bundle getExtraData() {
        this.extraData = (extraData != null) ? extraData : new Bundle();
        return this.extraData;
    }

    /**
     * @return current presentation
     */
    public PeasyPresentation getPresentation() {
        return this.presentation;
    }

    /**
     * @return FloatingActionButton
     */
    public FloatingActionButton getFab() {
        return this.fab;
    }

    //=============================
    // Content Management
    //=============================

    /**
     * @param content provided content
     */
    public void setContent(final ArrayList<T> content) {
        this.contents = (content == null) ? new ArrayList<T>() : new ArrayList<>(content);
        if (this.headerContent != null) {
            this.contents.add(0, this.headerContent.getData());
        }
        if (this.footerContent != null) {
            this.contents.add(this.footerContent.getData());
        }
        super.notifyDataSetChanged();
        onContentChanged();
    }

    /**
     * @param content provided content to add
     */
    public void addContent(T content) {
        addContent(getItemCount(), content);
    }

    /**
     * @param index   index to add
     * @param content provided content to add
     */
    public void addContent(int index, T content) {
        getContents().add(index, content);
        super.notifyItemInserted(index);
        onContentChanged();
    }

    /**
     * @param content provided content to remove
     */
    public void removeContent(T content) {
        removeContent(getContents().indexOf(content));
    }

    /**
     * @param index index to remove
     */
    public void removeContent(int index) {
        if (index == -1) return;
        getContents().remove(index);
        super.notifyItemRemoved(index);
        onContentChanged();
    }

    /**
     * When provided data changes
     *
     * @see #setContent(ArrayList)
     * @see #addContent(Object)
     */
    public void onContentChanged() {
    }

    /**
     * Add Header to its content
     * Should override {@link PeasyHeaderContent#onBindViewHolder(Context, PeasyViewHolder, int, Object)}
     * Should override {@link PeasyHeaderContent#onCreateViewHolder(LayoutInflater, ViewGroup, int)}
     *
     * @param headerContent PeasyHeaderContent
     */
    public void setHeaderContent(PeasyHeaderContent<T> headerContent) {
        this.headerContent = headerContent;
    }

    /**
     * Add Footer to its content
     * Should override {@link PeasyFooterContent#onBindViewHolder(Context, PeasyViewHolder, int, Object)}
     * Should override {@link PeasyFooterContent#onCreateViewHolder(LayoutInflater, ViewGroup, int)}
     *
     * @param footerContent PeasyFooterContent
     */
    public void setFooterContent(PeasyFooterContent<T> footerContent) {
        this.footerContent = footerContent;
    }

    //=============================
    // Scrolling
    //=============================

    /**
     * @param enable true to enable nested scroll, vice versa
     * @see RecyclerView#setNestedScrollingEnabled(boolean)
     */
    public void enableNestedScroll(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.recyclerView.setNestedScrollingEnabled(enable);
        }
    }

    /**
     * @param thresholdOfEOL threshold to detect EOL, value must >= {@value DefaultEOLThreshold}
     * @see #hasScrolledToEnd() for logic
     * @see #onViewScrolledToEnd(RecyclerView, int) for callback
     */
    private void setThresholdEOL(int thresholdOfEOL) {
        this.lockEOL.set(!this.lockEOL.get());
        this.thresholdEOL = thresholdOfEOL;
    }

    /**
     * {@link PeasyRecyclerView#onViewScrolledToEnd(RecyclerView, int)} enabled
     *
     * @param thresholdOfEOL threshold to detect EOL, must >= {@value DefaultEOLThreshold}
     */
    public void enableScrollEndDetection(int thresholdOfEOL) {
        setThresholdEOL(Math.max(DefaultEOLThreshold, thresholdOfEOL));
    }

    /**
     * {@link PeasyRecyclerView#onViewScrolledToEnd(RecyclerView, int)} disabled
     *
     * @see #onViewScrolledToEnd(RecyclerView, int) [callback]
     */
    public void disableScrollEndDetection() {
        setThresholdEOL(DisabledEOLThreshold);
    }

    /**
     * @param recyclerView recyclerView
     * @param dx           scrolling dx
     * @param dy           scrolling dy
     */
    private synchronized void onViewScrolledToEnd(final RecyclerView recyclerView, final int dx, final int dy) {
        synchronized (lockEOL) {
            if (!lockEOL.get()) {
                final boolean inRightDirection = (dx > 0) || (dy > 0);
                if (inRightDirection && hasScrolledToEnd()) {
                    lockEOL.set(!lockEOL.get());
                    if (hasAllItemsShown()) return;
                    onViewScrolledToEnd(getRecyclerView(), thresholdEOL);
                }
            }
        }
    }

    /**
     * @return true if view is long and idle where first item visible, vice versa
     */
    public final boolean checkViewScrolledToFirst() {
        if (hasAllItemsShown()) return false;
        return getFirstVisibleItemPosition() == 0;
    }

    /**
     * @return true if view is long and idle where last item visible, vice versa
     */
    public final boolean checkViewScrolledToLast() {
        if (hasAllItemsShown()) return false;
        return getLastVisibleItemPosition() == getLastItemIndex();
    }

    /**
     * @return true if view met the end of list with {@link #thresholdEOL} as threshold
     * @see #thresholdEOL
     * @see #onViewScrolled(RecyclerView, int, int)
     */
    public final boolean hasScrolledToEnd() {
        final int totalItemCount = getItemCount();
        final int lastVisibleIndex = getLastVisibleItemPosition();
        return (totalItemCount <= (lastVisibleIndex + Math.max(DefaultEOLThreshold, thresholdEOL)));
    }

    /**
     * Navigate to top of recycler view without smooth scroll effect
     * Work on {@link PeasyRecyclerView.VerticalList} and {@link PeasyRecyclerView.HorizontalList} only
     */
    public void setPositionToFirst() {
        if (getLinearLayoutManager() != null) {
            getLinearLayoutManager().scrollToPositionWithOffset(0, 0);
        }
    }

    /**
     * Navigate to top of recycler view with smooth scroll effect
     * Work on {@link PeasyRecyclerView.VerticalList} and {@link PeasyRecyclerView.HorizontalList} only
     */
    public void smoothScrollToFirst() {
        if (getLinearLayoutManager() != null) {
            final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getRecyclerView().getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(0);
            getLinearLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    //=============================
    // Enhance getItemViewType
    //=============================

    /**
     * To identify content as Header
     *
     * @param position position
     * @return view type
     */
    private int getHeaderViewType(int position) {
        if (headerContent != null) {
            if (headerContent.getData() == getItem(position) && (position == 0)) {
                return headerContent.getViewtype();
            }
        }
        return PeasyHeaderViewHolder.VIEWTYPE_NOTHING;
    }

    /**
     * To identify content as Footer
     *
     * @param position position
     * @return view type
     */
    public int getFooterViewType(int position) {
        if (footerContent != null) {
            if (footerContent.getData() == getItem(position) && (position == getLastItemIndex())) {
                return footerContent.getViewtype();
            }
        }
        return PeasyHeaderViewHolder.VIEWTYPE_NOTHING;
    }

    @Override
    public final int getItemViewType(int position) {
        if (PeasyHeaderViewHolder.VIEWTYPE_NOTHING != getHeaderViewType(position)) {
            return getHeaderViewType(position);
        }
        if (PeasyFooterViewHolder.VIEWTYPE_NOTHING != getFooterViewType(position)) {
            return getFooterViewType(position);
        }
        return getItemViewType(position, getItem(position));
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#getItemViewType(int)}
     * Define and Return view type of content
     *
     * @param position position
     * @param item     item
     * @return view type
     */
    protected abstract int getItemViewType(final int position, final T item);

    //=============================
    // Enhance onCreateViewHolder
    //=============================

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * Define and Return {@link PeasyViewHolder} of content
     *
     * @param inflater
     * @param parent
     * @param viewType
     * @return {@link PeasyViewHolder}
     */
    protected abstract PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    //=============================
    // Enhance onBindViewHolder
    //=============================

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder(getContext(), (PeasyViewHolder) holder, position);
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        onBindViewHolder(getContext(), (PeasyViewHolder) holder, position);
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder   PeasyViewHolder
     * @param position position
     */
    private void onBindViewHolder(Context context, PeasyViewHolder holder, int position) {
        if (holder == null) return;
        try {
            if (headerContent != null && holder.isHeaderView()) {
                headerContent.onBindViewHolder(context, holder.asIs(PeasyHeaderViewHolder.class), position, getItem(position));
            }
        } catch (Exception ignored) {
        }
        try {
            if (footerContent != null && holder.isFooterView()) {
                footerContent.onBindViewHolder(context, holder.asIs(PeasyFooterViewHolder.class), position, getItem(position));
            }
        } catch (Exception ignored) {
        }
        onBindViewHolder(context, holder, position, getItem(position));
    }

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

    //=============================
    // Content Provider
    //=============================

    /**
     * @return content size
     */
    @Override
    public int getItemCount() {
        return this.contents == null ? 0 : this.contents.size();
    }

    /**
     * @return last index in content
     */
    public int getLastItemIndex() {
        return Math.max(0, this.getItemCount() - 1);
    }

    /**
     * @param position displayed content position
     * @return content at position
     */
    public final T getItem(int position) {
        return this.contents.get(position);
    }

    /**
     * @return content size is zero
     */
    public boolean isEmpty() {
        return this.getItemCount() <= 0;
    }

    //=============================
    // Content Visibility
    //=============================

    /**
     * @return value from {@link RecyclerView#getChildCount()}
     */
    public final int getChildCount() {
        return (getRecyclerView() == null) ? 0 : getRecyclerView().getChildCount();
    }

    /**
     * @return value from {@link RecyclerView#getChildAt(int)}
     */
    public final View getChildAt(int index) {
        return (getRecyclerView() == null) ? null : getRecyclerView().getChildAt(index);
    }

    /**
     * @return value from {@link RecyclerView#getChildAdapterPosition(View)}
     */
    public final int getChildAdapterPosition(View child) {
        if (child == null) return RecyclerView.NO_POSITION;
        return (getRecyclerView() == null) ? RecyclerView.NO_POSITION : getRecyclerView().getChildAdapterPosition(child);
    }

    /**
     * @return first visible item position from Layout Manager
     */
    public int getFirstVisibleItemPosition() {
        if (getVisibleItemCount() > 0) {
            return getChildAdapterPosition(getChildAt(0));
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * @return last visible item position from Layout Manager
     */
    public int getLastVisibleItemPosition() {
        if (getVisibleItemCount() > 0) {
            int itemPos = getChildAdapterPosition(getChildAt(getChildCount() - 1));
            itemPos = (itemPos != -1) ? itemPos : getChildAdapterPosition(getChildAt(getChildCount() - 2));
            return itemPos;
        }
        return RecyclerView.NO_POSITION;
    }

    /**
     * @return number items shown
     */
    public int getVisibleItemCount() {
        return getChildCount();
    }

    /**
     * @return true if everything displayed
     */
    public boolean hasAllItemsShown() {
        return getLastVisibleItemPosition() == getLastItemIndex() && getFirstVisibleItemPosition() == 0;
    }

    //=============================
    // Layout Managers
    //=============================

    /**
     * @param recyclerView recyclerView provided
     * @param cls          class to be casted
     * @param <LM>         casted class name
     * @return {@link RecyclerView.LayoutManager} as casted class instance, null if invalid type cast
     */
    private <LM extends RecyclerView.LayoutManager> LM getLayoutManager(RecyclerView recyclerView, Class<LM> cls) {
        try {
            if (cls.isInstance(recyclerView.getLayoutManager())) {
                return cls.cast(recyclerView.getLayoutManager());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * @return null if no {@link LinearLayoutManager} instance found
     */
    public final LinearLayoutManager getLinearLayoutManager() {
        return getLayoutManager(getRecyclerView(), LinearLayoutManager.class);
    }

    /**
     * @return null if no {@link GridLayoutManager} instance found
     */
    public final GridLayoutManager getGridLayoutManager() {
        return getLayoutManager(getRecyclerView(), GridLayoutManager.class);
    }

    /**
     * @return null if no {@link StaggeredGridLayoutManager} instance found
     */
    public final StaggeredGridLayoutManager getStaggeredGridLayoutManager() {
        return getLayoutManager(getRecyclerView(), StaggeredGridLayoutManager.class);
    }


    /**
     * Present as Vertical List View
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return LinearLayoutManager
     */
    public LinearLayoutManager asVerticalListView() {
        this.presentation = PeasyPresentation.VerticalList;
        resetItemDecorations();
        resetItemAnimator();
        final LinearLayoutManager layoutManager = PeasyRecyclerView.VerticalList.newLayoutManager(getContext());
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Horizontal List View
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return LinearLayoutManager
     */
    public LinearLayoutManager asHorizontalListView() {
        this.presentation = PeasyPresentation.HorizontalList;
        final LinearLayoutManager layoutManager = PeasyRecyclerView.HorizontalList.newLayoutManager(getContext());
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Grid View
     * Be noted, columns must not less than {@value  DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link PeasyConfigurations#issueColumnSize(int)}
     * @return GridLayoutManager
     */
    public GridLayoutManager asGridView(int columns) {
        this.presentation = PeasyPresentation.BasicGrid;
        resetItemDecorations();
        resetItemAnimator();
        final GridLayoutManager layoutManager = PeasyRecyclerView.BasicGrid.newLayoutManager(getContext(), columns);
        PeasyConfigurations.bundleColumnSize(getExtraData(), columns);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new PeasyGridDividerItemDecoration(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Staggered Grid View in Vertical Orientation
     * Be noted, columns must not less than {@value DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration} provided by {@link
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link #issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(final int columns) {
        this.presentation = PeasyPresentation.VerticalStaggeredGrid;
        resetItemDecorations();
        resetItemAnimator();
        final StaggeredGridLayoutManager layoutManager = PeasyRecyclerView.VerticalStaggeredGrid.newLayoutManager(getContext(), columns);
        PeasyConfigurations.bundleColumnSize(getExtraData(), columns);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new PeasyGridDividerItemDecoration(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Staggered Grid View in Horizontal Orientation
     * Be noted, columns must not less than {@value DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link PeasyConfigurations#issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(final int columns) {
        this.presentation = PeasyPresentation.HorizontalStaggeredGrid;
        resetItemDecorations();
        resetItemAnimator();
        final StaggeredGridLayoutManager layoutManager = PeasyRecyclerView.HorizontalStaggeredGrid.newLayoutManager(getContext(), columns);
        PeasyConfigurations.bundleColumnSize(getExtraData(), columns);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new PeasyGridDividerItemDecoration(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    //=============================
    // View Configurations
    //=============================

    /**
     * @return value of {@value  PeasyConfigurations#ExtraColumnSize}
     * @see PeasyConfigurations#getColumnSize(Bundle)
     */
    public final int getColumnSize() {
        return PeasyConfigurations.getColumnSize(getExtraData());
    }

    /**
     * Remove all added {@link RecyclerView.ItemDecoration}
     */
    public final void resetItemDecorations() {
        for (int i = 0; i < getRecyclerView().getItemDecorationCount(); i++) {
            try {
                getRecyclerView().removeItemDecoration(getRecyclerView().getItemDecorationAt(i));
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Remove all added {@link RecyclerView.ItemAnimator}
     */
    public final void resetItemAnimator() {
        try {
            getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        } catch (Exception ignored) {
        }
    }

    /**
     * inflate parent view with layoutId
     *
     * @param inflater inflater
     * @param parent   parent
     * @param layoutId layoutId
     * @return View
     */
    public final View inflateView(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    //=============================
    // Overriding Methods
    //=============================

    /**
     * First time ViewHolder Rendered
     *
     * @see #onViewAttachedToWindow(RecyclerView.ViewHolder) Enhanced Implementation
     */
    public void onViewReady() {
    }

    /**
     * Each time ViewHolder Rendered
     *
     * @see #onViewAttachedToWindow(RecyclerView.ViewHolder) Enhanced Implementation
     */
    public void onViewHolderAttached(@NonNull RecyclerView.ViewHolder viewHolder) {
    }

    /**
     * Here you should define recycler view member single click action
     *
     * @param view       view
     * @param viewType   viewType
     * @param position   position
     * @param item       item
     * @param viewHolder viewHolder
     * @return true by default
     * @see View.OnClickListener#onClick(View) Enhanced Implementation
     */
    public void onItemClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
    }

    /**
     * Indicate content is clicked with long tap
     *
     * @param view       view
     * @param viewType   viewType
     * @param position   position
     * @param item       item
     * @param viewHolder viewHolder
     * @return true by default
     * @see View.OnLongClickListener#onLongClick(View)  Enhanced Implementation
     */
    public boolean onItemLongClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
        return true;
    }

    /**
     * Indicate view is scrolling
     *
     * @param recyclerView recyclerView
     * @param dx           dx feedback
     * @param dy           dy feedback
     * @see RecyclerView.OnScrollListener#onScrolled(int, int) Enhanced Implementation
     */
    public void onViewScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
    }

    /**
     * Indicate view is provided new scroll state
     *
     * @param recyclerView recyclerView
     * @param newState     new scroll state
     * @see RecyclerView.OnScrollListener#onScrollStateChanged(int) Enhanced Implementation
     */
    public void onViewScrollStateChanged(final RecyclerView recyclerView, final int newState) {
    }

    /**
     * Indicate view scrolled to end of list
     *
     * @param recyclerView recyclerView
     * @param threshold    threshold
     * @see RecyclerView.OnScrollListener#onScrolled(int, int) Enhanced Implementation
     * @see #onViewScrolledToEnd(RecyclerView, int, int)
     * @see #setThresholdEOL(int)
     * @see #enableScrollEndDetection(int)
     * @see #disableScrollEndDetection()
     * @see #thresholdEOL
     */
    public void onViewScrolledToEnd(final RecyclerView recyclerView, final int threshold) {
    }

    /**
     * Indicate scrolled to position where first item is visible
     *
     * @param recyclerView recyclerView
     * @see RecyclerView.OnScrollListener#onScrolled(int, int) Enhanced Implementation
     * @see #checkViewScrolledToFirst()
     */
    public void onViewScrolledToFirst(final RecyclerView recyclerView) {
    }

    /**
     * Indicate scrolled to position where last item is visible
     *
     * @param recyclerView recyclerView
     * @see RecyclerView.OnScrollListener#onScrolled(int, int) Enhanced Implementation
     * @see #checkViewScrolledToLast()
     */
    public void onViewScrolledToLast(final RecyclerView recyclerView) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnItemTouchListener#onInterceptTouchEvent(RecyclerView, MotionEvent)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     *
     * @param recyclerView recyclerView
     * @param motionEvent  motionEvent
     */
    public void onViewInterceptTouchEvent(final RecyclerView recyclerView, final MotionEvent motionEvent) {
    }

    //==========================================================================================
    // Peasy Presentation Template
    //==========================================================================================

    /**
     * Easy Peasy to implement Vertical Recycler View with LinearLayoutManager
     * Presentation : Vertical List View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalList<T> extends PeasyPresentationTemplate.VerticalList<T> {
        public VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
            super(context, recyclerView, arrayList);
        }
    }

    /**
     * Easy Peasy to implement Horizontal Recycler View with LinearLayoutManager
     * Presentation : Horizontal List View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class HorizontalList<T> extends PeasyPresentationTemplate.HorizontalList<T> {
        public HorizontalList(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
            super(context, recyclerView, arrayList);
        }
    }

    /**
     * Easy Peasy to implement Recycler View with GridLayoutManager
     * Presentation : Basic Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class BasicGrid<T> extends PeasyPresentationTemplate.BasicGrid<T> {
        public BasicGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, columnSize);
        }
    }

    /**
     * Easy Peasy to implement Vertical Recycler View with StaggeredGridLayoutManager
     * Presentation : Vertical Staggered Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalStaggeredGrid<T> extends PeasyPresentationTemplate.VerticalStaggeredGrid<T> {
        public VerticalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, columnSize);
        }
    }

    /**
     * Easy Peasy to implement Horizontal Recycler View with StaggeredGridLayoutManager
     * Presentation : Horizontal Staggered Grid View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class HorizontalStaggeredGrid<T> extends PeasyPresentationTemplate.HorizontalStaggeredGrid<T> {
        public HorizontalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, int columnSize) {
            super(context, recyclerView, arrayList, columnSize);
        }
    }

}