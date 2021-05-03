package com.application.myapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

public class SettingFragment extends PreferenceFragment {
    public static final String scanDevice = "Setting_Scan_Device";
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferrence);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(scanDevice)){
                    Preference devicePref = findPreference(key);
                    devicePref.setSummary(sharedPreferences.getString(key,""));

                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        Preference devicePref = findPreference(scanDevice);
        devicePref.setSummary(getPreferenceScreen().getSharedPreferences().getString(scanDevice,""));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        Preference devicePref = findPreference(scanDevice);
        devicePref.setSummary(getPreferenceScreen().getSharedPreferences().getString(scanDevice,""));
    }
}
