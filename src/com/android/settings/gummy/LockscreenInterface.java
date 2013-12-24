/*
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

import com.android.internal.widget.LockPatternUtils;

import com.android.settings.R;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenInterface extends SettingsPreferenceFragment {
    private static final String TAG = "LockscreenInterface";

    private static final String KEY_ENABLE_WIDGETS = "keyguard_enable_widgets";
    private static final String BATTERY_AROUND_LOCKSCREEN_RING = "battery_around_lockscreen_ring";
    private static final String LOCKSCREEN_MAXIMIZE_WIDGETS = "lockscreen_maximize_widgets";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String KEY_ADVANCED_CATAGORY = "advanced_catagory";
    private static final String KEY_WIDGETS_CATAGORY = "widgets_catagory";
    private static final String KEY_LOCKSCREEN_BUTTONS = "lockscreen_buttons";

    private PreferenceScreen mLockscreenButtons;
    private PreferenceCategory mAdvancedCatagory;
    private PreferenceCategory mWidgetsCatagory;

    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;

    private CheckBoxPreference mEnableKeyguardWidgets;
    private CheckBoxPreference mLockRingBattery;
    private CheckBoxPreference mMaximizeKeyguardWidgets;
    private CheckBoxPreference mLockscreenUseCarousel;
    private CheckBoxPreference mCameraWidget;

    public boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    public boolean showCarousel() {
        return !getResources().getBoolean(R.bool.config_show_carousel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_interface);

        // Enable or disable keyguard widget checkbox based on DPM state
        mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        PreferenceScreen prefs = getPreferenceScreen();
        mEnableKeyguardWidgets = (CheckBoxPreference) findPreference(KEY_ENABLE_WIDGETS);
        if (mEnableKeyguardWidgets != null) {
            final boolean disabled = (0 != (mDPM.getKeyguardDisabledFeatures(null)
                    & DevicePolicyManager.KEYGUARD_DISABLE_WIDGETS_ALL));
            if (disabled) {
                mEnableKeyguardWidgets.setSummary(
                        R.string.security_enable_widgets_disabled_summary);
            } else {
                mEnableKeyguardWidgets.setSummary("");
            }
            mEnableKeyguardWidgets.setEnabled(!disabled);
        }

        mLockRingBattery = (CheckBoxPreference) findPreference(BATTERY_AROUND_LOCKSCREEN_RING);
        if (mLockRingBattery != null) {
            mLockRingBattery.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BATTERY_AROUND_LOCKSCREEN_RING, 0) == 1);
        }

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

        mAdvancedCatagory = (PreferenceCategory) prefs.findPreference(KEY_ADVANCED_CATAGORY);
        mLockscreenButtons = (PreferenceScreen) findPreference(KEY_LOCKSCREEN_BUTTONS);
        if (!hasButtons()) {
            mAdvancedCatagory.removePreference(mLockscreenButtons);
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
        final String key = preference.getKey();
        final LockPatternUtils lockPatternUtils = mChooseLockSettingsHelper.utils();

        if (KEY_ENABLE_WIDGETS.equals(key)) {
            lockPatternUtils.setWidgetsEnabled(mEnableKeyguardWidgets.isChecked());
        } else if (preference == mLockRingBattery) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.BATTERY_AROUND_LOCKSCREEN_RING, isToggled(preference) ? 1 : 0);
        } else if (preference == mMaximizeKeyguardWidgets) {
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
