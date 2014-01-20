package com.yugy.cnbeta.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.provider.Settings;

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
        try {
            if(Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1){
                if(!isTablet(activity)){
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
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

    public static boolean isTablet(Context context){
        return(context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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
