package com.kopirealm.peasyrecyclerview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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

    public void setContent(ArrayList<T> arrayList, RecyclerView recyclerView) {
        this.setContent(arrayList);
        this.recyclerView = recyclerView;
        this.configureRecyclerView(getRecyclerView());
        this.recyclerView.setAdapter(this);
    }

    protected void configureRecyclerView(RecyclerView recyclerView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.setNestedScrollingEnabled(true);
        }
        this.recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (smartHiding && getFab() != null && getFab().getVisibility() != View.VISIBLE && hasAllItemVisible()) {
                    getFab().show();
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
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                interactWithFAB(recyclerView, dx, dy);
                onViewScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                onViewScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void setContent(ArrayList<T> arrayList) {
        this.recyclerDataSource = (arrayList == null) ? new ArrayList<T>() : arrayList;
        this.notifyDataSetChanged();
    }

    public void setFAB(FloatingActionButton fab) {
        setFAB(fab, false);
    }

    public void setFAB(FloatingActionButton fab, boolean smartHiding) {
        this.fab = fab;
        this.smartHiding = smartHiding;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    protected Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final PeasyViewHolder vh = onCreateViewHolder(inflater, parent, viewType);
        vh.bindWith(this, viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((PeasyViewHolder) holder, position);
    }

    private void onBindViewHolder(PeasyViewHolder holder, int position) {
        onBindViewHolder(getContext(), holder, position, getItem(position));
    }

    @Override
    public int getItemCount() {
        return recyclerDataSource.size();
    }

    public boolean isEmpty() {
        return (recyclerDataSource == null) ? true : recyclerDataSource.isEmpty();
    }

    public T getItem(int position) {
        return isEmpty() ? null : recyclerDataSource.get(position);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, getItem(position));
    }

    public int getLastVisibleItemPosition() {
        return -1;
    }

    public int getFirstVisibleItemPosition() {
        return -1;
    }

    public boolean hasAllItemVisible() {
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

    public void onItemClick(View v, int viewType, int position) {
    }

    public boolean onItemLongClick(View v, int viewType, int position) {
        return true;
    }

    public void onViewScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    public void onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    private void interactWithFAB(RecyclerView recyclerView, int dx, int dy) {
        if (smartHiding && getFab() != null) {
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

    public void smoothScrollToTop() {
        final RecyclerView recyclerView = getRecyclerView();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(0);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }

    protected abstract PeasyViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract int getItemViewType(int position, T item);

    protected abstract void onBindViewHolder(Context context, PeasyViewHolder holder, int position, T item);

    public static abstract class VerticalList<T> extends PeasyRecyclerView<T> {

        private LinearLayoutManager layoutManager;

        public VerticalList(@NonNull Context context, RecyclerView recyclerView, ArrayList arrayList) {
            super(context, recyclerView, arrayList);
        }

        @Override
        protected void configureRecyclerView(RecyclerView recyclerView) {
            super.configureRecyclerView(recyclerView);
            layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public int getLastVisibleItemPosition() {
            return layoutManager.findLastCompletelyVisibleItemPosition();
        }

        @Override
        public int getFirstVisibleItemPosition() {
            return layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    public static abstract class PeasyViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEWTYPE_NOTHING = Integer.MAX_VALUE * -1;

        public PeasyViewHolder(View itemView) {
            super(itemView);
        }

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

        public boolean isInstance(Class cls) {
            return cls.isInstance(this);
        }

        public static View inflateView(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            return inflater.inflate(layoutId, parent, false);
        }
    }

}