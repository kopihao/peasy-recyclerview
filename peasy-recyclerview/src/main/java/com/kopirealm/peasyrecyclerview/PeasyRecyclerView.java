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
import android.widget.LinearLayout;

import com.kopirealm.peasyrecyclerview.decor.PeasyGridDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An Binder as well as a adapter of RecyclerView
 *
 * @param <T> class type of array list content
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
    private ArrayList<T> displayedContents;
    private ArrayList<T> providedContents;
    private Bundle extraData = null;
    private PeasyHeaderContent<T> headerContent = null;
    private PeasyFooterContent<T> footerContent = null;
    private FloatingActionButton fab;
    private boolean enhancedFAB = false;
    private static final int DefaultEOLThreshold = 1;
    private static final int DisabledEOLThreshold = -1;
    private int thresholdOfEOL = DisabledEOLThreshold;
    private final AtomicBoolean lockEOL = new AtomicBoolean(true);

    //=============================
    // Constructor
    //=============================

    public PeasyRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ArrayList<T> arrayList) {
        this(context, recyclerView, arrayList, new Bundle());
    }

    public PeasyRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ArrayList<T> arrayList, @NonNull Bundle extraData) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.extraData = extraData;
        this.setContent(arrayList);
        this.onCreate(context, recyclerView, arrayList, getExtraData());
        this.enableNestedScroll(true);
        this.configureRecyclerView(recyclerView);
        this.configureRecyclerViewTouchEvent();
        this.configureRecyclerViewScrollEvent();
        this.recyclerView.setAdapter(this);
    }

    //=============================
    // Life Cycle & Feeds
    //=============================

    /**
     * Executes lines of code before constructor ended
     *
     * @param context      context
     * @param recyclerView recyclerView
     * @param arrayList    arrayList
     * @param extraData    extraData
     */
    public void onCreate(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
    }

    /**
     * Customize presentation of provided recyclerView
     * eg. {@link LinearLayoutManager} , {@link RecyclerView.ItemAnimator }, {@link RecyclerView.ItemDecoration}
     * By default, FAB handling will be contracted at here
     *
     * @param recyclerView recyclerView
     */
    protected void configureRecyclerView(RecyclerView recyclerView) {
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
     * @see #enhanceFAB(RecyclerView, FloatingActionButton, int, int)
     * @see #enhanceFAB(RecyclerView, FloatingActionButton, MotionEvent)
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
                    enhanceFAB(rv, getFab(), e);
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
                        enhanceFAB(recyclerView, getFab(), dx, dy);
                        onViewScrolled(recyclerView, dx, dy);
                        synchronized (lockEOL) {
                            if (!lockEOL.get()) {
                                final boolean inDirection = (dx > 0) || (dy > 0);
                                final int eolThreshold = thresholdOfEOL;
                                if (inDirection && hasReachedEndOfList(eolThreshold)) {
                                    lockEOL.set(!lockEOL.get());
                                    onViewReachingEndOfList(recyclerView, eolThreshold);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        onViewScrollStateChanged(recyclerView, newState);
                        synchronized (lockEOL) {
                            if (lockEOL.get() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                lockEOL.set(!lockEOL.get());
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
     * @param recyclerView recyclerView
     * @param fab          FloatingActionButton
     * @param dx           scrolling dx
     * @param dy           scrolling dy
     */
    private void enhanceFAB(RecyclerView recyclerView, final FloatingActionButton fab, int dx, int dy) {
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
     * @param rv  RecyclerView
     * @param fab FloatingActionButton
     * @param e   MotionEvent
     */
    private void enhanceFAB(RecyclerView rv, final FloatingActionButton fab, MotionEvent e) {
        if (hasAllContentsVisible()) {
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
     * @return current contents with {@link PeasyCoordinatorContent}
     */
    public ArrayList<T> getDisplayedContents() {
        return new ArrayList<>(this.displayedContents);
    }


    /**
     * @return current contents without {@link PeasyCoordinatorContent}
     */
    public ArrayList<T> getProvidedContents() {
        return new ArrayList<>(this.providedContents);
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
     * @return current {@link Presentation}
     */
    public Presentation getPresentation() {
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
     * @param content provided contents
     */
    public void setContent(final ArrayList<T> content) {
        this.providedContents = (content == null) ? new ArrayList<T>() : new ArrayList<>(content);
        this.displayedContents = (content == null) ? new ArrayList<T>() : new ArrayList<>(content);
        if (this.headerContent != null) {
            displayedContents.add(0, this.headerContent.getData());
        }
        if (this.footerContent != null) {
            displayedContents.add(this.footerContent.getData());
        }
        this.notifyDataSetChanged();
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
     * Set {@link RecyclerView#setNestedScrollingEnabled}
     *
     * @param enable true to enable, vice versa
     */
    public void enableNestedScroll(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.recyclerView.setNestedScrollingEnabled(enable);
        }
    }

    /**
     * To set End Of List threshold
     * Threshold must excedd {@value DefaultEOLThreshold} in order to trigger callback of {@link #onViewReachingEndOfList(RecyclerView, int)}
     *
     * @param thresholdOfEOL
     * @see #hasReachedEndOfList(int)
     * @see #onViewReachingEndOfList(RecyclerView, int)
     */
    public void setThresholdOfEOL(int thresholdOfEOL) {
        if (thresholdOfEOL >= DefaultEOLThreshold) {
            this.lockEOL.set(!this.lockEOL.get());
            this.thresholdOfEOL = thresholdOfEOL;
        }
    }

    /**
     * @return true if reaching end of contents
     * @see #hasReachedEndOfList(int)
     */
    public final boolean hasReachedEndOfList() {
        return hasReachedEndOfList(DefaultEOLThreshold);
    }

    /**
     * @param threshold Minimum value is {@value DefaultEOLThreshold } , recommended value is [1,5],
     * @return true if reaching end of contents within provided threshold
     */
    public final boolean hasReachedEndOfList(final int threshold) {
        final int totalItemCount = getDisplayedContentCount();
        final int lastVisibleItem = getLastVisibleItemPosition();
        return (totalItemCount <= (lastVisibleItem + Math.max(DefaultEOLThreshold, threshold)));
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
            if (headerContent.getData() == getDisplayedContentAt(position) && (position == 0)) {
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
            if (footerContent.getData() == getDisplayedContentAt(position) && (position == getLastDisplayedContentsIndex())) {
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
        return getItemViewType(position, getDisplayedContentAt(position));
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#getItemViewType(int)}
     * Define and Return view type of contents
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
     * Define and Return {@link PeasyViewHolder} of contents
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
                headerContent.onBindViewHolder(context, holder.asIs(PeasyHeaderViewHolder.class), position, getDisplayedContentAt(position));
            }
        } catch (Exception ignored) {
        }
        try {
            if (footerContent != null && holder.isFooterView()) {
                footerContent.onBindViewHolder(context, holder.asIs(PeasyFooterViewHolder.class), position, getDisplayedContentAt(position));
            }
        } catch (Exception ignored) {
        }
        onBindViewHolder(context, holder, position, getDisplayedContentAt(position));
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
     * @return value of {@link #getDisplayedContentCount()}
     */
    @Deprecated
    @Override
    public final int getItemCount() {
        return getProvidedContentCount();
    }

    /**
     * @return Size of displayed contents with {@link PeasyCoordinatorContent}
     */
    public final int getDisplayedContentCount() {
        return (this.displayedContents == null) ? 0 : this.displayedContents.size();
    }

    /**
     * @return Size of provided contents without {@link PeasyCoordinatorContent}
     */
    public final int getProvidedContentCount() {
        return (this.providedContents == null) ? 0 : this.providedContents.size();
    }

    /**
     * @return true if no contents displayed
     */
    public final boolean isEmpty() {
        return getDisplayedContentCount() <= 0;
    }

    /**
     * @return true if no contents provided
     */
    public final boolean isEmptyContent() {
        return getProvidedContentCount() <= 0;
    }

    /**
     * @return last index of displayed content with {@link PeasyCoordinatorContent}
     */
    public final int getLastDisplayedContentsIndex() {
        return getDisplayedContentCount() <= 0 ? 0 : this.getDisplayedContentCount() - 1;
    }

    /**
     * @return last index of provided content without {@link PeasyCoordinatorContent}
     */
    public final int getLastProvidedContentsIndex() {
        return getProvidedContentCount() <= 0 ? 0 : this.getProvidedContentCount() - 1;
    }

    /**
     * @param position displayed content position
     * @return value of {@link #getDisplayedContentAt(int)}
     */
    @Deprecated
    public final T getItem(int position) {
        return getDisplayedContentAt(position);
    }

    /**
     * @param position displayed content position
     * @return displayed content with {@link PeasyCoordinatorContent}
     */
    public final T getDisplayedContentAt(int position) {
        return getDisplayedContentCount() <= 0 ? null : this.displayedContents.get(position);
    }

    /**
     * @param position displayed content position
     * @return displayed content with {@link PeasyCoordinatorContent}
     */
    public final T getProvidedItemAt(int position) {
        return getProvidedContentCount() <= 0 ? null : this.providedContents.get(position);
    }

    /**
     * @return last index of displayed content with {@link PeasyCoordinatorContent}
     */
    public final T getLastDisplayedContent() {
        return getDisplayedContentAt(getLastDisplayedContentsIndex());
    }

    /**
     * @return last index of provided content without {@link PeasyCoordinatorContent}
     */
    public final T getLastProvidedContent() {
        return getProvidedItemAt(getLastProvidedContentsIndex());
    }

    //=============================
    // Content Visibility
    //=============================

    /**
     * Comprehensive findFirstCompletelyVisibleItemPosition() method
     *
     * @return first visible item position from Layout Manager
     */
    public final int getFirstVisibleItemPosition() {
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
     * Comprehensive findLastVisibleItemPosition() method
     *
     * @return last visible item position from Layout Manager
     */
    public final int getLastVisibleItemPosition() {
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
     * @return true if all contents are visible within view port
     */
    public final boolean hasAllContentsVisible() {
        return (getFirstVisibleItemPosition() != RecyclerView.NO_POSITION && getLastVisibleItemPosition() != RecyclerView.NO_POSITION) && (getFirstVisibleItemPosition() == 0 && getLastVisibleItemPosition() == getLastDisplayedContentsIndex());
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
    public StaggeredGridLayoutManager getStaggeredGridLayoutManager() {
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
    public final LinearLayoutManager asVerticalListView() {
        this.presentation = PeasyRecyclerView.Presentation.VerticalList;
        return asListView(LinearLayoutManager.VERTICAL);
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
    public final LinearLayoutManager asHorizontalListView() {
        this.presentation = PeasyRecyclerView.Presentation.HorizontalList;
        return asListView(LinearLayoutManager.HORIZONTAL);
    }

    /**
     * Present as List View
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param orientation {@value LinearLayoutManager#VERTICAL}  OR  {@value LinearLayoutManager#HORIZONTAL}
     * @return LinearLayoutManager
     */
    private LinearLayoutManager asListView(final int orientation) {
        resetItemDecorations();
        resetItemAnimator();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(orientation);
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), (orientation == LinearLayout.VERTICAL) ? DividerItemDecoration.VERTICAL : DividerItemDecoration.HORIZONTAL));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Grid View
     * Be noted, columns must not less than {@value PeasyPresentationTemplate#DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link #issueColumnSize(int)}
     * @return GridLayoutManager
     */
    public GridLayoutManager asGridView(int columns) {
        this.presentation = PeasyRecyclerView.Presentation.BasicGrid;
        resetItemDecorations();
        resetItemAnimator();
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), issueColumnSize(columns));
        PeasyPresentationTemplate.bundleColumnSize(getExtraData(), issueColumnSize(columns));
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(issuePeasyGridDivider(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    /**
     * Present as Staggered Grid View in Vertical Orientation
     * Be noted, columns must not less than {@value PeasyPresentationTemplate#DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration} provided by {@link #issuePeasyGridDivider(Context, int)}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link #issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(final int columns) {
        this.presentation = PeasyRecyclerView.Presentation.VerticalStaggeredGrid;
        return asStaggeredGridView(issueColumnSize(columns), StaggeredGridLayoutManager.VERTICAL);
    }

    /**
     * Present as Staggered Grid View in Horizontal Orientation
     * Be noted, columns must not less than {@value PeasyPresentationTemplate#DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns provided to {@link #issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(final int columns) {
        this.presentation = PeasyRecyclerView.Presentation.HorizontalStaggeredGrid;
        return asStaggeredGridView(issueColumnSize(columns), StaggeredGridLayoutManager.HORIZONTAL);
    }

    /**
     * Present as Staggered Grid View
     * Be noted, columns must not less than {@value PeasyPresentationTemplate#DefaultGridColumnSize}
     * Default divider is {@link PeasyGridDividerItemDecoration} provided by {@link #issuePeasyGridDivider(Context, int)}
     * <p>
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @param columns     provided to {@link #issueColumnSize(int)}
     * @param orientation {@value StaggeredGridLayoutManager#VERTICAL}  OR  {@value StaggeredGridLayoutManager#HORIZONTAL}
     * @return StaggeredGridLayoutManager
     */
    private StaggeredGridLayoutManager asStaggeredGridView(final int columns, final int orientation) {
        resetItemDecorations();
        resetItemAnimator();
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(issueColumnSize(columns), orientation);
        PeasyPresentationTemplate.bundleColumnSize(getExtraData(), issueColumnSize(columns));
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(issuePeasyGridDivider(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    //=============================
    // View Configurations
    //=============================

    /**
     * @return column size
     * @see PeasyPresentationTemplate#getColumnSize(Bundle)
     */
    public int getColumnSize() {
        return PeasyPresentationTemplate.getColumnSize(getExtraData());
    }

    /**
     * @param columnSize input column size
     * @return accepted column size
     * @see PeasyPresentationTemplate#issueColumnSize(int)
     */
    private int issueColumnSize(int columnSize) {
        return PeasyPresentationTemplate.issueColumnSize(columnSize);
    }

    /**
     * @param context    context
     * @param columnSize input column size
     * @return PeasyGridDividerItemDecoration
     * @see PeasyPresentationTemplate#issuePeasyGridDivider(Context, int)
     */
    private PeasyGridDividerItemDecoration issuePeasyGridDivider(Context context, int columnSize) {
        return PeasyPresentationTemplate.issuePeasyGridDivider(context, columnSize);
    }

    /**
     * Remove all added {@link RecyclerView.ItemDecoration}
     */
    public void resetItemDecorations() {
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
    public void resetItemAnimator() {
        try {
            getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        } catch (Exception ignored) {
        }
    }

    //=============================
    // Overriding Methods
    //=============================

    /**
     * Enhanced Implementation Layer of {@link View.OnClickListener#onClick(View)}
     * Target on itemView of {@link PeasyViewHolder}
     * Here you should define recycler view member single click action
     *
     * @param view       view
     * @param viewType   viewType
     * @param position   position
     * @param item       item
     * @param viewHolder viewHolder
     */
    public void onItemClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
    }

    /**
     * Enhanced Implementation Layer of {@link View.OnLongClickListener#onLongClick(View)} (View)}
     * Target on itemView of {@link PeasyViewHolder}
     * Here you should define recycler view member long click action
     *
     * @param view       view
     * @param viewType   viewType
     * @param position   position
     * @param item       item
     * @param viewHolder viewHolder
     * @return true by default
     */
    public boolean onItemLongClick(final View view, final int viewType, final int position, final T item, final PeasyViewHolder viewHolder) {
        return true;
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrolled(RecyclerView, int, int)}
     * Target on itemView of {@link PeasyRecyclerView#recyclerView}
     * Here you should define recycler view on scroll action with dy, dx feedback
     *
     * @param recyclerView recyclerView
     * @param dx           dx
     * @param dy           dy
     */
    public void onViewScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrollStateChanged(RecyclerView, int)}
     * Target on {@link RecyclerView} of {@link PeasyRecyclerView }
     * Here you should define recycler view on scroll action with state feedback
     *
     * @param recyclerView recyclerView
     * @param newState     newState
     */
    public void onViewScrollStateChanged(final RecyclerView recyclerView, final int newState) {
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrolled(RecyclerView, int, int)}
     * Target on itemView of {@link PeasyRecyclerView#recyclerView}
     * Here you should define recycler view on scroll action when it reach end of list
     * [WARNING]
     * This method will execute right after {@link #onViewScrolled(RecyclerView, int, int)}
     * when {@link #thresholdOfEOL} is more than or equal {@value #DefaultEOLThreshold }
     * Do not repeat duplication at {@link #onViewScrolled(RecyclerView, int, int)}
     * This method will utilize {@link #lockEOL} to avoid feedback spamming.
     *
     * @param recyclerView recyclerView
     * @param threshold    threshold
     */
    public void onViewReachingEndOfList(final RecyclerView recyclerView, final int threshold) {
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