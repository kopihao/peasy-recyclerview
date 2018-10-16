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
    private AtomicBoolean lockEOL = new AtomicBoolean(true);
    private int thresholdOfEOL = -1;
    public static final int DefaultEOLThreshold = 1;

    public PeasyRecyclerView(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList) {
        this(context, recyclerView, arrayList, new Bundle());
    }

    public PeasyRecyclerView(@NonNull Context context, RecyclerView recyclerView, ArrayList<T> arrayList, Bundle extraData) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.extraData = extraData;
        this.setThresholdOfEOL(-1);
        this.onCreate(context, recyclerView, arrayList, getExtraData());
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
        this.extraData = (extraData != null) ? extraData : new Bundle();
        return this.extraData;
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
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        PeasyRecyclerView.this.handleFAB(recyclerView, getFab(), dx, dy);
                        onViewScrolled(recyclerView, dx, dy);
                        synchronized (lockEOL) {
                            if (!lockEOL.get()) {
                                final boolean inDirection = (dx > 0) || (dy > 0);
                                final int eolThreshold = PeasyRecyclerView.this.thresholdOfEOL;
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
            public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        onViewScrollStateChanged(recyclerView, newState);
                        synchronized (lockEOL) {
                            if (!!lockEOL.get() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                lockEOL.set(!lockEOL.get());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * To set End Of List threshold
     * Threshold must excedd {@value #DefaultEOLThreshold} in order to trigger callback of {@link #onViewReachingEndOfList(RecyclerView, int)}
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
     * DO NOT OVERRIDE THIS
     * Please override {@link #onBindViewHolder(Context, PeasyViewHolder, int, T)}
     *
     * @param holder
     * @param position
     */
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;
        onBindViewHolder(getContext(), (PeasyViewHolder) holder, position);
    }

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolder(Context context, PeasyViewHolder holder, int position) {
        if (holder == null) return;
        if (headerContent != null) {
            if (holder.isHeaderView()) {
                headerContent.onBindViewHolder(context, (PeasyHeaderViewHolder) holder, position, getItem(position));
            }
        }
        if (footerContent != null) {
            if (holder.isFooterView()) {
                footerContent.onBindViewHolder(context, (PeasyFooterViewHolder) holder, position, getItem(position));
            }
        }
        onBindViewHolder(context, holder, position, getItem(position));
    }

    /**
     * DO NOT OVERRIDE THIS
     * Please override {@link #onBindViewHolder(Context, PeasyViewHolder, int, ArrayList)}
     *
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (holder == null) return;
        onBindViewHolder(getContext(), (PeasyViewHolder) holder, position, payloads);
    }


    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolder(Context context, PeasyViewHolder holder, int position, @NonNull List payloads) {
        if (holder == null) return;
        if (headerContent != null) {
            if (holder.isHeaderView()) {
                headerContent.onBindViewHolder(context, (PeasyHeaderViewHolder) holder, position, new ArrayList<T>(payloads));
            }
        }
        if (footerContent != null) {
            if (holder.isFooterView()) {
                footerContent.onBindViewHolder(context, (PeasyFooterViewHolder) holder, position, new ArrayList<T>(payloads));
            }
        }
        onBindViewHolder(context, holder, position, new ArrayList<T>(payloads));
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
     * To check reach end of contents
     * Default threshold is {@value #DefaultEOLThreshold }
     *
     * @return
     * @see #hasReachedEndOfList(int)
     */
    public boolean hasReachedEndOfList() {
        return hasReachedEndOfList(DefaultEOLThreshold);
    }

    /**
     * To check reach end of contents
     * Minimum threshold is {@value #DefaultEOLThreshold }
     *
     * @param threshold visibility count, recommended value is [1,5]
     * @return
     */
    public boolean hasReachedEndOfList(final int threshold) {
        final int totalItemCount = getItemCount();
        final int lastVisibleItem = getLastVisibleItemPosition();
        return (totalItemCount <= (lastVisibleItem + Math.max(DefaultEOLThreshold, threshold)));
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
     * Enhanced Implementation Layer of {@link RecyclerView.OnScrollListener#onScrolled(RecyclerView, int, int)}
     * Target on itemView of {@link PeasyRecyclerView#recyclerView}
     * Here you should define recycler view on scroll action when it reach end of list
     * [WARNING]
     * This method will execute right after {@link #onViewScrolled(RecyclerView, int, int)}
     * when {@link #thresholdOfEOL} is more than or equal {@value #DefaultEOLThreshold }
     * Do not repeat duplication at {@link #onViewScrolled(RecyclerView, int, int)}
     * This method will utilize {@link #lockEOL} to avoid feedback spamming.
     *
     * @param recyclerView
     * @param threshold
     */
    public void onViewReachingEndOfList(final RecyclerView recyclerView, final int threshold) {
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
    // CONTRACTUAL METHODS BEGIN
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
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#getItemViewType(int)}
     * Here you should define or decide view type
     *
     * @param position
     * @param item
     * @return
     */
    protected abstract int getItemViewType(final int position, final T item);

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

    //==========================================================================================
    // CONTRACTUAL METHODS END
    //==========================================================================================

    /**
     * Enhanced Implementation Layer of {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)} )}
     * Here you should populate views in {@link PeasyViewHolder} with item returned in this method
     *
     * @param context
     * @param holder
     * @param position
     * @param items
     */
    protected void onBindViewHolder(final Context context, final PeasyViewHolder holder, final int position, ArrayList<T> items) {
    }

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
     * <p>
     * Execute {@link #resetItemDecorations()}
     * Execute {@link #resetItemAnimator()}
     *
     * @return LinearLayoutManager
     */
    public LinearLayoutManager asVerticalListView() {
        this.presentation = Presentation.VerticalList;
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
    public LinearLayoutManager asHorizontalListView() {
        this.presentation = Presentation.HorizontalList;
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
     * @return
     */
    public LinearLayoutManager asListView(final int orientation) {
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
     * @param columns provided to {@link PeasyPresentationTemplate#issueColumnSize(int)}
     * @return GridLayoutManager
     */
    public GridLayoutManager asGridView(int columns) {
        this.presentation = Presentation.BasicGrid;
        resetItemDecorations();
        resetItemAnimator();
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), PeasyPresentationTemplate.issueColumnSize(columns));
        PeasyPresentationTemplate.bundleColumnSize(getExtraData(), PeasyPresentationTemplate.issueColumnSize(columns));
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
     * @param columns provided to {@link PeasyPresentationTemplate#issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asVerticalStaggeredGridView(final int columns) {
        this.presentation = Presentation.VerticalStaggeredGrid;
        return asStaggeredGridView(PeasyPresentationTemplate.issueColumnSize(columns), StaggeredGridLayoutManager.VERTICAL);
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
     * @param columns provided to {@link PeasyPresentationTemplate#issueColumnSize(int)}
     * @return StaggeredGridLayoutManager
     */
    public StaggeredGridLayoutManager asHorizontalStaggeredGridView(final int columns) {
        this.presentation = Presentation.HorizontalStaggeredGrid;
        return asStaggeredGridView(PeasyPresentationTemplate.issueColumnSize(columns), StaggeredGridLayoutManager.HORIZONTAL);
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
     * @param columns     provided to {@link PeasyPresentationTemplate#issueColumnSize(int)}
     * @param orientation {@value StaggeredGridLayoutManager#VERTICAL}  OR  {@value StaggeredGridLayoutManager#HORIZONTAL}
     * @return StaggeredGridLayoutManager
     */
    private StaggeredGridLayoutManager asStaggeredGridView(final int columns, final int orientation) {
        resetItemDecorations();
        resetItemAnimator();
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(PeasyPresentationTemplate.issueColumnSize(columns), orientation);
        PeasyPresentationTemplate.bundleColumnSize(getExtraData(), PeasyPresentationTemplate.issueColumnSize(columns));
        getRecyclerView().setLayoutManager(layoutManager);
        getRecyclerView().addItemDecoration(issuePeasyGridDivider(getContext(), columns));
        getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        return layoutManager;
    }

    public int getColumnSize() {
        return getExtraData().getInt(PeasyPresentationTemplate.ExtraColumnSize, 1);
    }

    /**
     * To build standard PeasyGridDividerItemDecoration
     *
     * @param columns must larger than {@value PeasyPresentationTemplate#DefaultGridColumnSize}
     * @return
     */
    public static PeasyGridDividerItemDecoration issuePeasyGridDivider(Context context, int columns) {
        return new PeasyGridDividerItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.peasy_grid_divider_spacing), PeasyPresentationTemplate.issueColumnSize(columns));
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
    // PeasyCoordinatorContent
    //==========================================================================================

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
    // PeasyPresentationTemplate
    //==========================================================================================

    /**
     * Easy Peasy to implement Vertical Recycler View with LinearLayoutManager
     * Presentation : Vertical List View
     * Implementation to reduce boilerplate code and enhance writability
     *
     * @param <T> type of array list content
     */
    public static abstract class VerticalList<T> extends PeasyPresentationTemplate.VerticalList<T> {
        public VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
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
        public HorizontalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
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
        public BasicGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
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
        public VerticalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
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
        public HorizontalStaggeredGrid(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList, int columnSize) {
            super(context, recyclerView, arrayList, columnSize);
        }
    }

}