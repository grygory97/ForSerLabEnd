package com.grzegorzszawula.forser;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SwitchPreference s2 = (SwitchPreference) findPreference("2s");
            final SwitchPreference s5 = (SwitchPreference) findPreference("5s");
            final SwitchPreference s10 = (SwitchPreference) findPreference("10s");
            if (preferences.getBoolean("2s", true)) {
                s5.setChecked(false);
                s10.setChecked(false);
                s2.setEnabled(false);
            }
            if (preferences.getBoolean("5s", true)) {
                s2.setChecked(false);
                s10.setChecked(false);
                s5.setEnabled(false);
            }
            if (preferences.getBoolean("10s", true)) {
                s2.setChecked(false);
                s5.setChecked(false);
                s10.setEnabled(false);
            }

            s2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final boolean value = (Boolean) newValue;
                    if (value == true) {
                        s2.setEnabled(false);
                        s5.setEnabled(true);
                        s10.setEnabled(true);
                        s5.setChecked(false);
                        s10.setChecked(false);
                    }
                    preferences.edit().putBoolean("2s", value).apply();
                    return true;
                }
            });
            s5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final boolean value = (Boolean) newValue;
                    if (value == true) {
                        s2.setEnabled(true);
                        s5.setEnabled(false);
                        s10.setEnabled(true);
                        s2.setChecked(false);
                        s10.setChecked(false);
                    }
                    preferences.edit().putBoolean("5s", value).apply();
                    return true;
                }
            });
            s10.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final boolean value = (Boolean) newValue;
                    if (value == true) {
                        s2.setEnabled(true);
                        s5.setEnabled(true);
                        s10.setEnabled(false);
                        s2.setChecked(false);
                        s5.setChecked(false);
                    }
                    preferences.edit().putBoolean("10s", value).apply();
                    return true;
                }
            });


        }
    }
}