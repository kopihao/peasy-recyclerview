package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kopirealm.peasyrecyclerview.PeasyRecyclerView;
import com.kopirealm.peasyrecyclerview.sample.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class PresentorFragment extends Fragment
        implements PresentorListener {

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
        final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        if (getContext() != null) {
            switch (presentation) {
                case VerticalList:
                    peasyRecyclerView = new SimpleVerticalListView(getContext(), recyclerView, arrayList, this);
            }
        }
        header = rootView.findViewById(R.id.header);
        first = rootView.findViewById(R.id.first);
        last = rootView.findViewById(R.id.last);
        state = rootView.findViewById(R.id.state);
        onContentChanged(peasyRecyclerView == null ? 0 : peasyRecyclerView.getProvidedContentCount());
        onViewScrollStateChanged(recyclerView, SCROLL_STATE_IDLE);
        return rootView;
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
}