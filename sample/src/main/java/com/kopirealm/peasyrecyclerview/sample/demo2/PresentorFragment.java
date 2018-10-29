package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kopirealm.peasyrecyclerview.PeasyRecyclerView;
import com.kopirealm.peasyrecyclerview.PeasyViewHolder;
import com.kopirealm.peasyrecyclerview.sample.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class PresentorFragment extends Fragment
        implements PresentorListener, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int sectionNumber = 0;
    private PeasyRecyclerView.Presentation presentation = PeasyRecyclerView.Presentation.undefined;
    private PeasyRecyclerView peasyRecyclerView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private TextView header;
    private TextView first;
    private TextView last;
    private TextView state;

    public static PresentorFragment newInstance(int sectionNumber) {
        final PresentorFragment fragment = new PresentorFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            presentation = PeasyRecyclerView.Presentation.values()[sectionNumber];
        }
        for (int i = 0; i < 10; i++) {
            arrayList.add("" + i);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_presentator, container, false);
        header = rootView.findViewById(R.id.header);
        first = rootView.findViewById(R.id.first);
        last = rootView.findViewById(R.id.last);
        state = rootView.findViewById(R.id.state);
        if (getContext() != null) {
            final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            switch (presentation) {
                case VerticalList:
                    peasyRecyclerView = new SimpleVerticalListView(getContext(), recyclerView, arrayList, this);
                    break;
                case HorizontalList:
                    peasyRecyclerView = new SimpleHorizontalListView(getContext(), recyclerView, arrayList, this);
                    break;
                case BasicGrid:
                    peasyRecyclerView = new SimpleBasicGridView(getContext(), recyclerView, arrayList, this);
                    break;
                case VerticalStaggeredGrid:
                    peasyRecyclerView = new SimpleVerticalStaggeredGridView(getContext(), recyclerView, arrayList, this);
                    break;
                case HorizontalStaggeredGrid:
                    peasyRecyclerView = new SimpleHorizontalStaggeredGridView(getContext(), recyclerView, arrayList, this);
                    break;
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FloatingActionButton fabOpt0 = view.findViewById(R.id.fabOpt0);
        final FloatingActionButton fabOpt1 = view.findViewById(R.id.fabOpt1);
        final FloatingActionButton fabOpt2 = view.findViewById(R.id.fabOpt2);
        fabOpt0.setOnClickListener(this);
        fabOpt1.setOnClickListener(this);
        fabOpt2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabOpt0:
                if (peasyRecyclerView.getFirstVisibleItemPosition() > 0 && !peasyRecyclerView.hasAllContentsVisible()) {
                    peasyRecyclerView.smoothScrollToFirst();
                }
                break;
            case R.id.fabOpt1:
                break;
            case R.id.fabOpt2:
                break;
        }
    }

    @Override
    public void onContentChanged(int count) {
        header.setText(getString(R.string.section_format, presentation, String.format(Locale.ENGLISH, "[%d]", count)));
    }

    @Override
    public void onViewScrollStateChanged(RecyclerView recyclerView, int newState) {
        try {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    state.setText(getString(R.string.state_format, "Idle"));
                    if (recyclerView.getChildCount() != 0) {
                        state.setText(getString(R.string.shown_format, "" + recyclerView.getChildCount()));
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    state.setText(getString(R.string.state_format, "Dragging"));
                    break;
                case SCROLL_STATE_SETTLING:
                    state.setText(getString(R.string.state_format, "Settling"));
                    break;
                default:
                    throw new Exception("Unknown State");
            }
        } catch (Exception e) {
            state.setText(getString(R.string.state_format, ""));
        }
    }

    @Override
    public void onViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        try {
            first.setText(getString(R.string.first_format, "" + peasyRecyclerView.getFirstVisibleItemPosition()));
        } catch (Exception e) {
            first.setText(getString(R.string.first_format, ""));
        }
        try {
            last.setText(getString(R.string.last_format, "" + peasyRecyclerView.getLastVisibleItemPosition()));
        } catch (Exception e) {
            last.setText(getString(R.string.last_format, ""));
        }
    }

    @Override
    public void onItemClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder) {
        if (TextUtils.isEmpty(item)) return;
        Toast.makeText(getContext(), "onItemClick" + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(View view, int viewType, int position, String item, PeasyViewHolder viewHolder) {
        if (TextUtils.isEmpty(item)) return false;
        Toast.makeText(getContext(), "onItemLongClick" + item, Toast.LENGTH_SHORT).show();
        return true;
    }
}