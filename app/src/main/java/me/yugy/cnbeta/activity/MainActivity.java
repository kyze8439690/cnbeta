package me.yugy.cnbeta.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.app.common.core.BaseActivity;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.fragment.AllNewsFragment;
import me.yugy.cnbeta.fragment.HotCommentsFragment;
import me.yugy.cnbeta.fragment.RecommendFragment;
import me.yugy.cnbeta.widget.TitleFlipperView;


public class MainActivity extends BaseActivity {

    @InjectView(R.id.pager) ViewPager mPager;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.title_flipper) TitleFlipperView mTitleFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mPager.setAdapter(new MainFragmentAdapter());
        mPager.setOffscreenPageLimit(3);
        mTitleFlipper.setViewPager(mPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            SettingsActivity.launch(this);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class MainFragmentAdapter extends FragmentPagerAdapter {
        public MainFragmentAdapter() {
            super(MainActivity.this.getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllNewsFragment();
                case 1:
                    return new RecommendFragment();
                case 2:
                    return new HotCommentsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_news);
                case 1:
                    return getString(R.string.recommend_news);
                case 2:
                    return getString(R.string.hot_comments);
                default:
                    return null;
            }
        }
    }
}
