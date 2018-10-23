package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kopirealm.peasyrecyclerview.sample.R;

public class ActivityDemo2 extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_vertical_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_horizontal_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_vertical_staggered));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_horizontal_staggered));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_basic_grid));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.mipmap.ic_spannable_grid));
        final ViewPager viewPager = findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
