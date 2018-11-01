package com.kopirealm.peasyrecyclerview.sample.demo2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PresenterPagerAdapter extends FragmentPagerAdapter {

    private int totalSize;

    PresenterPagerAdapter(FragmentManager fm, int totalSize) {
        super(fm);
        this.totalSize = totalSize;
    }

    @Override
    public Fragment getItem(int position) {
        return PresenterFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return totalSize;
    }
}
