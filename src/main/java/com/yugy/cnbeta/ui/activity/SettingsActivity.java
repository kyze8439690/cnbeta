package com.yugy.cnbeta.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.fragment.SettingsFragment;
import com.yugy.cnbeta.utils.ScreenUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by yugy on 14-1-9.
 */
public class SettingsActivity extends SwipeBackActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBase.onCreate(this);
        getSwipeBackLayout().setEdgeSize(ScreenUtils.getDisplayWidth(this) / 3);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        ActivityBase.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_out);
    }
}
