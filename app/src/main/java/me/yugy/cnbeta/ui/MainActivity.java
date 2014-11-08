package me.yugy.cnbeta.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;


public class MainActivity extends ActionBarActivity implements MenuFragment.OnMenuSelectListener {

    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private String mTitle;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AllNewsFragment())
                    .commit();
            mTitle = getResources().getStringArray(R.array.toolbar_spinner_entries)[0];
            mType = 0;
            getSupportActionBar().setTitle(mTitle);
        }else{
            mTitle = savedInstanceState.getString("subject");
            mType = savedInstanceState.getInt("type");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("subject", mTitle);
        outState.putInt("type", mType);
    }

    @Override
    public void onMenuSelect(int type) {
        if(mType != type) {
            mType = type;
            mTitle = getResources().getStringArray(R.array.toolbar_spinner_entries)[type];
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (type) {
                case MenuFragment.TYPE_ALL_NEWS:
                    ft.replace(R.id.container, new AllNewsFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;
                case MenuFragment.TYPE_HOT_COMMENTS:
                    ft.replace(R.id.container, new HotCommentsFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;
                case MenuFragment.TYPE_RECOMMEND:
                    ft.replace(R.id.container, new RecommendFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    break;
            }
            ft.commit();
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }
}
