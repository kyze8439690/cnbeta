package com.yugy.cnbeta.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.view.AppMsg;

import static android.preference.Preference.OnPreferenceClickListener;

/**
 * Created by yugy on 14-1-9.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        findPreference(KEY_PREF_CHECK_UPDATE).setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UmengUpdateAgent.forceUpdate(getActivity());
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                        switch (i){
                            case UpdateStatus.No:
                                AppMsg.makeText(getActivity(), "您现在使用的就是最新版本", AppMsg.STYLE_INFO).show();
                                break;
                            case UpdateStatus.Timeout:
                                AppMsg.makeText(getActivity(), "网络超时", AppMsg.STYLE_CONFIRM).show();
                                break;
                        }
                        UmengUpdateAgent.setUpdateListener(null);
                    }
                });
                return true;
            }
        });

    }

    public static final String KEY_PREF_REFRESH_AMOUNT = "pref_refresh_amount";
    public static final String KEY_PREF_CHECK_UPDATE = "pref_check_update";

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
