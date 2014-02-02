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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.android.internal.telephony.util.BlacklistUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class AdvancedSystemOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "AdvancedSystemOptions";

    private static final String KEY_ADVANCED_SYSTEM_OPTIONS = "advanced_system_options";
    private static final String KEY_BLACKLIST = "blacklist";

    private PreferenceScreen mBlacklist;
    private PreferenceCategory mAdvancedsystemOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_advanced_system_options);

        PreferenceScreen prefSet = getPreferenceScreen();

        // Determine options based on device telephony support
        mAdvancedsystemOptions = (PreferenceCategory) prefSet.findPreference(KEY_ADVANCED_SYSTEM_OPTIONS);
        mBlacklist = (PreferenceScreen) findPreference(KEY_BLACKLIST);
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            // No telephony, remove dependent options
            mAdvancedsystemOptions.removePreference(mBlacklist);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBlacklistSummary();
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

    private void updateBlacklistSummary() {
        if (mBlacklist != null) {
            if (BlacklistUtils.isBlacklistEnabled(getActivity())) {
                mBlacklist.setSummary(R.string.blacklist_summary);
            } else {
                mBlacklist.setSummary(R.string.blacklist_summary_disabled);
            }
        }
    }
}
