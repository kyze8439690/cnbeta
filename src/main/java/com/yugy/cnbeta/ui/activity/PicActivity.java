package com.yugy.cnbeta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.adapter.PicFragmentPagerAdapter;
import com.yugy.cnbeta.ui.listener.PicViewPagerChangeListener;
import com.yugy.cnbeta.ui.view.PicViewPager;
import com.yugy.cnbeta.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by yugy on 14-1-8.
 */
public class PicActivity extends SwipeBackActivity{

    private PicViewPager mViewPager;
    private ArrayList<String> mImgUrls;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mImgUrls = getIntent().getStringArrayListExtra("list");
        mTitle = getIntent().getStringExtra("title");
        int index = getIntent().getIntExtra("current", 0);

        getActionBar().setTitle("(" + (index + 1) + "/" + mImgUrls.size() + ") " + mTitle);
        if(index == 0){
            getSwipeBackLayout().setEdgeSize(ScreenUtils.getDisplayWidth(this) / 2);
        }

        mViewPager = (PicViewPager) findViewById(R.id.pic_viewpager);
        mViewPager.setAdapter(new PicFragmentPagerAdapter(getFragmentManager(), mImgUrls));
        mViewPager.setOnPageChangeListener(new PicViewPagerChangeListener(this, mImgUrls.size(), mTitle));
        mViewPager.setCurrentItem(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }else{
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
