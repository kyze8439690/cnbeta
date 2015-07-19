package me.yugy.cnbeta.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import me.yugy.cnbeta.R;

/**
 * Created by yugy on 14/11/8.
 */
public class SettingsActivity extends PreferenceActivity{

    public static void launch(Context context){
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("About")
                        .setMessage(R.string.about_desc)
                        .setPositiveButton(R.string.got_it, null)
                        .create().show();
                return true;
            }
        });
    }
}
