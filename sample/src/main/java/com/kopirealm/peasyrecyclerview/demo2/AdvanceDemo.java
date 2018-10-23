package com.kopirealm.peasyrecyclerview.demo2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.kopirealm.peasyrecyclerview.R;

public class AdvanceDemo extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_demo);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.prv_vertical_listview:
//                return true;
//            case R.id.prv_horizontal_listview:
//                return true;
//            case R.id.prv_vertical_staggered_gridview:
//                return true;
//            case R.id.prv_horizontal_staggered_gridview:
//                return true;
//            case R.id.prv_basic_gridview:
//                return true;
//            case R.id.prv_spannable_gridview:
//                return true;
        }
        return false;
    }

}
