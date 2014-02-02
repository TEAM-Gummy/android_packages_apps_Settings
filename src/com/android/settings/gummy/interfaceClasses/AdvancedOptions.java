/*
 * Copyright (C) 2013 Gummy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.gummy.interfaceClasses;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class AdvancedOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "AdvancedOptions";

    private static final String KEY_HARDWARE_KEYS = "hardware_keys";
    private static final String KEY_ADVANCED_OPTIONS = "advanced_options";

    private PreferenceScreen mHardwareKeys;
    private PreferenceCategory mAdvancedOptions;

    public boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_advanced_options);

        PreferenceScreen prefSet = getPreferenceScreen();

        // Only show the hardware keys config on a device that does not have a navbar
        mAdvancedOptions = (PreferenceCategory) prefSet.findPreference(KEY_ADVANCED_OPTIONS);
        mHardwareKeys = (PreferenceScreen) findPreference(KEY_HARDWARE_KEYS);

        if (!hasButtons()) {
            mAdvancedOptions.removePreference(mHardwareKeys);
        }

    }

        @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }
}
