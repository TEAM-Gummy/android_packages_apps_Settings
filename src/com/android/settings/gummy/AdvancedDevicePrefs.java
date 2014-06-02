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

package com.android.settings.gummy;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import org.cyanogenmod.hardware.AdaptiveBacklight;

public class AdvancedDevicePrefs extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "AdvancedDevicePrefs";

    private static final String KEY_ADAPTIVE_BACKLIGHT = "adaptive_backlight";
    private static final String KEY_ADVANCED_DISPLAY_SETTINGS = "advanced_display_settings";
    private static final String KEY_ADVANCED_DEVICE_SETTINGS = "advanced_device_settings";
    private static final String KEY_0CLICK_SETTINGS = "advanced_0click_settings";
    private static final String KEY_GESTURE_SETTINGS = "advanced_gesture_settings";
    private static final String KEY_SCREEN_COLOR_SETTINGS = "screencolor_settings";
    private static final String KEY_MORE_DEVICE_SETTINGS = "more_device_settings";

    private CheckBoxPreference mAdaptiveBacklight;
    private PreferenceScreen mScreenColorSettings;
    private PreferenceScreen mMoreDeviceSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.advanced_device_prefs);

        mAdaptiveBacklight = (CheckBoxPreference) findPreference(KEY_ADAPTIVE_BACKLIGHT);
        if (!AdaptiveBacklight.isSupported()) {
            getPreferenceScreen().removePreference(mAdaptiveBacklight);
            mAdaptiveBacklight = null;
        }

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_0CLICK_SETTINGS);

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_GESTURE_SETTINGS);

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_ADVANCED_DEVICE_SETTINGS);

        Utils.updatePreferenceToSpecificActivityFromMetaDataOrRemove(getActivity(),
                getPreferenceScreen(), KEY_ADVANCED_DISPLAY_SETTINGS);

        mScreenColorSettings = (PreferenceScreen) findPreference(KEY_SCREEN_COLOR_SETTINGS);
        if (!isPostProcessingSupported()) {
            getPreferenceScreen().removePreference(mScreenColorSettings);
        }

        mMoreDeviceSettings = (PreferenceScreen) findPreference(KEY_MORE_DEVICE_SETTINGS);

        hideMoreSettingsPref();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdaptiveBacklight != null) {
            mAdaptiveBacklight.setChecked(AdaptiveBacklight.isEnabled());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mAdaptiveBacklight) {
            return AdaptiveBacklight.setEnabled(mAdaptiveBacklight.isChecked());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }

    /**
     * Restore the properties associated with this preference on boot
     * @param ctx A valid context
     */
    public static void restore(Context ctx) {
        if (AdaptiveBacklight.isSupported()) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            final boolean enabled = prefs.getBoolean(KEY_ADAPTIVE_BACKLIGHT, true);
            if (!AdaptiveBacklight.setEnabled(enabled)) {
                Log.e(TAG, "Failed to restore adaptive backlight settings.");
            } else {
                Log.d(TAG, "Adaptive backlight settings restored.");
            }
        }
    }

    private boolean isPostProcessingSupported() {
        boolean ret = true;
        final PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.qualcomm.display", PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            ret = false;
        }
        return ret;
    }

    private boolean isMorePrefEmpty() {
        return MoreDeviceSettings.hasItems();
    }

    private void hideMoreSettingsPref() {
        if (!isMorePrefEmpty()) {
            getPreferenceScreen().removePreference(mMoreDeviceSettings);
        }
    }
}
