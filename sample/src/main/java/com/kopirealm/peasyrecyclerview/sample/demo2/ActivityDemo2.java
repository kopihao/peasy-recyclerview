package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kopirealm.peasyrecyclerview.PeasyRecyclerView;
import com.kopirealm.peasyrecyclerview.sample.R;

public class ActivityDemo2 extends AppCompatActivity {

    PeasyRecyclerView.Presentation presentation = PeasyRecyclerView.Presentation.VerticalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final TabLayout.Tab[] presentationTabs = new TabLayout.Tab[]{
                tabLayout.newTab().setIcon(R.mipmap.ic_vertical_listview),
                tabLayout.newTab().setIcon(R.mipmap.ic_horizontal_listview),
                tabLayout.newTab().setIcon(R.mipmap.ic_basic_grid),
                //tabLayout.newTab().setIcon(R.mipmap.ic_spannable_grid),
                tabLayout.newTab().setIcon(R.mipmap.ic_vertical_staggered),
                tabLayout.newTab().setIcon(R.mipmap.ic_horizontal_staggered),
        };
        for (TabLayout.Tab tab : presentationTabs) {
            tabLayout.addTab(tab);
        }
        final ViewPager viewPager = findViewById(R.id.pager);
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setCurrentItem(Math.max(0, presentation.ordinal() - 1));
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
