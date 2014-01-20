package com.yugy.cnbeta.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.umeng.update.UmengUpdateAgent;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.fragment.CommentFragment;
import com.yugy.cnbeta.ui.fragment.HotCommentListFragment;
import com.yugy.cnbeta.ui.fragment.NewestNewsFragment;
import com.yugy.cnbeta.ui.fragment.NewsFragment;
import com.yugy.cnbeta.ui.listener.OnCommentButtonClickListener;
import com.yugy.cnbeta.ui.listener.OnNewsItemClickListener;
import com.yugy.cnbeta.ui.fragment.TopTenNewsFragment;
import com.yugy.cnbeta.utils.DebugUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener, OnNewsItemClickListener, OnCommentButtonClickListener {

    private static final int REQUEST_SETTINGS = 0;
    public static final int RESULT_SETTING_FONT_CHANGED = 1;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mListView;
    private NewestNewsFragment mNewestNewsFragment;
    private HotCommentListFragment mHotCommentListFragment;
    private TopTenNewsFragment mTopTenNewsFragment;

    private int mCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugUtils.log("MainActivity onCreate");
        ActivityBase.onCreate(this);
        UmengUpdateAgent.update(this);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mListView = (ListView) findViewById(R.id.main_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_toggle, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(drawerView == mListView){
                    super.onDrawerSlide(drawerView, slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if(drawerView == mListView){
                    super.onDrawerOpened(drawerView);
                }else{
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if(drawerView == mListView){
                    super.onDrawerClosed(drawerView);
                }else{
                    getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_left, Gravity.START);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, Gravity.END);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("");
        getActionBar().setIcon(R.drawable.ic_action_logo);

        mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.view_drawer_list_item, getResources().getStringArray(R.array.drawer_array)));
        mListView.setOnItemClickListener(this);

        if(savedInstanceState == null){
            mNewestNewsFragment = new NewestNewsFragment();
            getFragmentManager().beginTransaction().add(R.id.main_container, mNewestNewsFragment).commit();
            mListView.setItemChecked(0, true);
            mCurrentPosition = 0;
        }else{
            mCurrentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
                overridePendingTransition(R.anim.activity_in, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SETTINGS && resultCode == RESULT_SETTING_FONT_CHANGED){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", mCurrentPosition);
    }



    @Override
    protected void onResume() {
        super.onResume();
        DebugUtils.log("MainActivity onResume");
        ActivityBase.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        DebugUtils.log("MainActivity onPause");
        ActivityBase.onPause(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onDestroy() {
        ActivityBase.onDestroy(this);
        super.onDestroy();
        DebugUtils.log("MainActivity onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mCurrentPosition != position){
            switch (position){
                case 0:
                    if(mNewestNewsFragment == null){
                        mNewestNewsFragment = new NewestNewsFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.main_container, mNewestNewsFragment).commit();
                    break;
                case 1:
                    if(mHotCommentListFragment == null){
                        mHotCommentListFragment = new HotCommentListFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.main_container, mHotCommentListFragment).commit();
                    break;
                case 2:
                    if(mTopTenNewsFragment == null){
                        mTopTenNewsFragment = new TopTenNewsFragment();
                    }
                    getFragmentManager().beginTransaction().replace(R.id.main_container, mTopTenNewsFragment).commit();
                    break;
            }
            mCurrentPosition = position;
            mDrawerLayout.closeDrawer(mListView);
            mListView.setItemChecked(position, true);
        }
    }

    @Override
    public void onNewsClick(Bundle bundle) {
        if(findViewById(R.id.main_content_container) != null){
            bundle.putBoolean("transparentActionBar", false);
            NewsFragment newsFragment = new NewsFragment();
            newsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_content_container, newsFragment).commit();
        }else{
            bundle.putBoolean("transparentActionBar", true);
            Intent intent = new Intent(this, NewsActivity.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_in, 0);
        }
    }

    @Override
    public void OnCommentClick(Bundle bundle) {
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mDrawerLayout.openDrawer(Gravity.END);
        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.main_comment_container, commentFragment).commit();
    }
}
