package me.yugy.cnbeta.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.yugy.cnbeta.ui.CommentsFragment;

/**
 * Created by yugy on 2014/9/6.
 */
public class CommentsPagerAdapter extends FragmentPagerAdapter {

    private CommentsFragment[] mFragments;

    public CommentsPagerAdapter(FragmentManager fm, CommentsFragment[] fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "全部评论";
            case 1:
                return "热门评论";
        }
        return super.getPageTitle(position);
    }
}
