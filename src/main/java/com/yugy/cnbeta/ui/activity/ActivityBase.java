package com.yugy.cnbeta.ui.activity;

import android.app.Activity;
import android.preference.PreferenceManager;

import com.umeng.analytics.MobclickAgent;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.ui.fragment.SettingsFragment;
import com.yugy.cnbeta.utils.MessageUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by yugy on 14-1-13.
 */
public class ActivityBase {

    /**
     * call in onCreate()
     */
    public static void onCreate(Activity activity){
        String fontPreferences = PreferenceManager.getDefaultSharedPreferences(activity).getString(SettingsFragment.KEY_PREF_FONT, "slab");
        if(fontPreferences.equals("default")){
            CalligraphyConfig.initDefault("");
        }else if(fontPreferences.equals("condensed")){
            CalligraphyConfig.initDefault("RobotoCondensed-Regular.ttf");
        }else if(fontPreferences.equals("slab")){
            CalligraphyConfig.initDefault("RobotoSlab-Regular.ttf");
        }
        MobclickAgent.onError(activity);
    }

    public static void onResume(Activity activity){
        MobclickAgent.onResume(activity);
    }

    public static void onPause(Activity activity){
        MobclickAgent.onPause(activity);
    }

    public static void onDestroy(Activity activity){
        RequestManager.getInstance().cancelRequests(activity);
    }

}
