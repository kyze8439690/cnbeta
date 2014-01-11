package com.yugy.cnbeta.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.ui.adapter.MainFragmentPagerAdapter;
import com.yugy.cnbeta.ui.fragment.HotCommentListFragment;
import com.yugy.cnbeta.ui.fragment.NewestNewsFragment;
import com.yugy.cnbeta.ui.fragment.TopTenNewsFragment;
import com.yugy.cnbeta.ui.view.PagerSlidingTabStrip;
import com.yugy.cnbeta.ui.view.StackPageTransformer;
import com.yugy.cnbeta.utils.DebugUtils;
import com.yugy.cnbeta.utils.ScreenUtils;

import static com.yugy.cnbeta.ui.listener.ListViewScrollObserver.OnListViewScrollListener;

public class MainActivity extends Activity implements OnListViewScrollListener{

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private Fragment[] mFragments;

    private int mTabHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this);
        UmengUpdateAgent.update(this);
        setContentView(R.layout.activity_main);
        getActionBar().setIcon(R.drawable.ic_action_logo);
        getActionBar().setTitle("");

        mTabHeight = ScreenUtils.dp(this, 44);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new StackPageTransformer());
        mFragments = new Fragment[]{
                new NewestNewsFragment(),
                new HotCommentListFragment(),
                new TopTenNewsFragment()
        };
        mViewPager.setAdapter(new MainFragmentPagerAdapter(this, mFragments));
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.main_tabs);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mPagerSlidingTabStrip.getTranslationY() != 0) {
                    mPagerSlidingTabStrip.animate().translationY(0).start();
                }
            }
        });
        mPagerSlidingTabStrip.setOnTabClickListener(new PagerSlidingTabStrip.OnTabClickListener() {
            @Override
            public void onClick(int position) {
                if(position == mViewPager.getCurrentItem()){
                    ((ListFragment) mFragments[position]).getListView().smoothScrollToPositionFromTop(0, 0);
                }
            }
        });
        ((NewestNewsFragment) mFragments[0]).setOnScrollUpAndDownListener(this);
        ((HotCommentListFragment) mFragments[1]).setOnScrollUpAndDownListener(this);
        ((TopTenNewsFragment) mFragments[2]).setOnScrollUpAndDownListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(R.anim.activity_in, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
        float translationY = mPagerSlidingTabStrip.getTranslationY() + delta;
        if(translationY > 0){
            translationY = 0;
        }else if(translationY < -mTabHeight){
            translationY = -mTabHeight;
        }
        if(delta > 0 && mPagerSlidingTabStrip.getTranslationY() < 0){ // scroll to bottom, show the tab
            mPagerSlidingTabStrip.setTranslationY(translationY);
        }else if(delta < 0 && mPagerSlidingTabStrip.getTranslationY() > -mTabHeight){ //scroll to top, hide the tab
            mPagerSlidingTabStrip.setTranslationY(translationY);
        }
    }

    @Override
    public void onScrollIdle() {
//        DebugUtils.log("onScrollIdle");
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancelRequests(this);
        super.onDestroy();
    }
}
