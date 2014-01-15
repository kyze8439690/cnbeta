package com.yugy.cnbeta.ui.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.fragment.NewestNewsFragment;

/**
 * Created by yugy on 14-1-7.
 */
public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;
    private Fragment[] mFragments;

    public MainFragmentPagerAdapter(Activity activity, Fragment[] fragments) {
        super(activity.getFragmentManager());
        mTitles = activity.getResources().getStringArray(R.array.drawer_array);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments[i];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
