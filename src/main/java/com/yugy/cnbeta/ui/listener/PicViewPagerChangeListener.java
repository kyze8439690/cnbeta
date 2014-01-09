package com.yugy.cnbeta.ui.listener;

import android.support.v4.view.ViewPager;

import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.utils.ScreenUtils;

/**
 * Created by yugy on 14-1-8.
 */
public class PicViewPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {

    private SwipeBackActivity mActivity;
    private int mSize;
    private String mTitle;

    public PicViewPagerChangeListener(SwipeBackActivity activity, int size, String title){
        mActivity = activity;
        mSize = size;
        mTitle = title;
    }

    @Override
    public void onPageSelected(int i) {
        if(i == 0){
            mActivity.getSwipeBackLayout().setEdgeSize(ScreenUtils.getDisplayWidth(mActivity) / 2);
        }else{
            mActivity.getSwipeBackLayout().setEdgeSize(ScreenUtils.dp(mActivity, 20));
        }
        mActivity.getActionBar().setTitle("(" + (i + 1) + "/" + mSize + ") " + mTitle);
    }
}
