package com.kopirealm.peasyrecyclerview.sample.demo1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kopirealm.peasyrecyclerview.PeasyRecyclerView;
import com.kopirealm.peasyrecyclerview.sample.FileReader;
import com.kopirealm.peasyrecyclerview.sample.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDemo1 extends AppCompatActivity {

    private JSONObject jsonDemo1;
    private TextView tvPresentation;
    private PeasyRecyclerView.Presentation inboxPresentation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        readJSONDemo1();
        setupHintBar();
        setupPeasyRVInboxLayout();
    }

    private void readJSONDemo1() {
        jsonDemo1 = FileReader.readRawJsonObject(getApplicationContext(), R.raw.demo1);
    }

    private void setupHintBar() {
        if (jsonDemo1 != null) {
            ((TextView) findViewById(R.id.tvHint)).setText(jsonDemo1.optString("hint", ""));
            findViewById(R.id.btnDismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeHintBar(300);
                }
            });
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeHintBar(300);
                }
            }, 10 * 1000);
        } else {
            removeHintBar(0);
        }
    }

    private void removeHintBar(long duration) {
        final View view = findViewById(R.id.llHintBar);
        if (duration == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.animate()
                    .scaleX(0)
                    .alpha(0.0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void setupPeasyRVInboxLayout() {
        tvPresentation = findViewById(R.id.tvPresentation);
        final FloatingActionButton fab = findViewById(R.id.fab);
        final PeasyRVInbox prvInbox = new PeasyRVInbox(this, (RecyclerView) findViewById(R.id.rvSample), fab, forgingInboxMessage());
        changePeasyRVInboxLayout(prvInbox);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSnackbarToResetContent(view, prvInbox);
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePeasyRVInboxLayout(prvInbox);
            }
        });
    }

    private void changePeasyRVInboxLayout(final PeasyRVInbox prvInbox) {
        final int nextOrdinal = (inboxPresentation == null) ? 0 : (inboxPresentation.ordinal() + 1) % PeasyRecyclerView.Presentation.values().length;
        inboxPresentation = PeasyRecyclerView.Presentation.values()[nextOrdinal];
        inboxPresentation = (inboxPresentation.equals(PeasyRecyclerView.Presentation.undefined)) ? PeasyRecyclerView.Presentation.VerticalList : inboxPresentation;
        if (inboxPresentation.equals(PeasyRecyclerView.Presentation.VerticalList)) {
            prvInbox.asVerticalListView();
        } else if (inboxPresentation.equals(PeasyRecyclerView.Presentation.HorizontalList)) {
            prvInbox.asHorizontalListView();
        } else if (inboxPresentation.equals(PeasyRecyclerView.Presentation.BasicGrid)) {
            prvInbox.asGridView(3);
        } else if (inboxPresentation.equals(PeasyRecyclerView.Presentation.VerticalStaggeredGrid)) {
            prvInbox.asVerticalStaggeredGridView(4);
        } else if (inboxPresentation.equals(PeasyRecyclerView.Presentation.HorizontalStaggeredGrid)) {
            prvInbox.asHorizontalStaggeredGridView(5);
        }
        prvInbox.setContent(forgingInboxMessage());
        tvPresentation.setText(inboxPresentation.name());
    }

    private void showSnackbarToResetContent(final View view, final PeasyRVInbox prvInbox) {
        Snackbar.make(view, "Reset Sample Inbox", Snackbar.LENGTH_LONG)
                .setAction("RESET", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prvInbox.setContent(forgingInboxMessage());
                        prvInbox.setPositionToFirst();
                    }
                }).show();
    }

    private ArrayList<ModelInbox> forgingInboxMessage() {
        return ModelInbox.forgingInboxMessage(jsonDemo1);
    }
}

