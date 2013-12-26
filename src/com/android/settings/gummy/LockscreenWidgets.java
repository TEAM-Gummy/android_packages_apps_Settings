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

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenWidgets extends SettingsPreferenceFragment {
    private static final String TAG = "LockscreenWidgets";

    private static final String LOCKSCREEN_MAXIMIZE_WIDGETS = "lockscreen_maximize_widgets";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String KEY_WIDGETS_CATAGORY = "widgets_catagory";

    private PreferenceCategory mWidgetsCatagory;

    private CheckBoxPreference mMaximizeKeyguardWidgets;
    private CheckBoxPreference mLockscreenUseCarousel;
    private CheckBoxPreference mCameraWidget;

    private boolean showCarousel() {
        return !getResources().getBoolean(R.bool.config_show_carousel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_widgets);

        PreferenceScreen prefs = getPreferenceScreen();

        mMaximizeKeyguardWidgets = (CheckBoxPreference) findPreference(LOCKSCREEN_MAXIMIZE_WIDGETS);
        if (mMaximizeKeyguardWidgets != null) {
            mMaximizeKeyguardWidgets.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
        }

        Resources keyguardResources = null;
        PackageManager pm = getPackageManager();
        try {
            keyguardResources = pm.getResourcesForApplication("com.android.keyguard");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final boolean cameraDefault = keyguardResources != null
                ? keyguardResources.getBoolean(keyguardResources.getIdentifier(
                "com.android.keyguard:bool/kg_enable_camera_default_widget", null, null)) : false;

        DevicePolicyManager dpm = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mCameraWidget = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_CAMERA_WIDGET);
        if (dpm.getCameraDisabled(null)
                || (dpm.getKeyguardDisabledFeatures(null)
                    & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_CAMERA) != 0) {
            prefs.removePreference(mCameraWidget);
        } else {
            mCameraWidget.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CAMERA_WIDGET, cameraDefault ? 1 : 0) == 1);
        }

        mWidgetsCatagory = (PreferenceCategory) prefs.findPreference(KEY_WIDGETS_CATAGORY);
        mLockscreenUseCarousel = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_USE_CAROUSEL);
        if (!showCarousel()) {
            mWidgetsCatagory.removePreference(mLockscreenUseCarousel);
        } else {
            mLockscreenUseCarousel.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
               Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, 0) == 1);
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

    private boolean isToggled(Preference pref) {
        return ((CheckBoxPreference) pref).isChecked();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mMaximizeKeyguardWidgets) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, isToggled(preference) ? 1 : 0);
        } else if (preference == mLockscreenUseCarousel) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, isToggled(preference) ? 1 : 0);
        } else if (preference == mCameraWidget) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CAMERA_WIDGET, isToggled(preference) ? 1 : 0);
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }
}
