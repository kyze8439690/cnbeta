package com.yugy.cnbeta.ui.activity;

import android.app.Activity;
import android.app.Fragment;
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

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.ui.adapter.MainFragmentPagerAdapter;
import com.yugy.cnbeta.ui.fragment.NewestNewsFragment;
import com.yugy.cnbeta.ui.view.PagerSlidingTabStrip;
import com.yugy.cnbeta.utils.DebugUtils;
import com.yugy.cnbeta.utils.ScreenUtils;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static com.yugy.cnbeta.ui.listener.ListViewScrollObserver.OnListViewScrollListener;

public class MainActivity extends Activity implements OnRefreshListener, AdapterView.OnItemClickListener,
        OnListViewScrollListener{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Fragment[] mFragments;

    private int mTabHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setIcon(R.drawable.ic_action_logo);
        getActionBar().setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mTabHeight = ScreenUtils.dp(this, 48);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        mDrawer = (ListView) findViewById(R.id.main_left_drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.drawable.ic_drawer_toggle, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mDrawer.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.drawer_array)));
        mDrawer.setOnItemClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mFragments = new Fragment[]{
                new NewestNewsFragment(),
                new Fragment(),
                new Fragment()
        };
        mViewPager.setAdapter(new MainFragmentPagerAdapter(this, mFragments));
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.main_tabs);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        ((NewestNewsFragment) mFragments[0]).setOnScrollUpAndDownListener(this);
    }

    @Override
    public void onRefreshStarted(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
