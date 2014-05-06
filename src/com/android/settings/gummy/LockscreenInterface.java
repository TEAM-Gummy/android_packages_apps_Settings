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
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.util.gummy.DeviceUtils;
import com.android.internal.widget.LockPatternUtils;

import com.android.settings.R;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SettingsPreferenceFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockscreenInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenInterface";

    private static final String KEY_LOCK_CLOCK = "lock_clock";
    private static final String KEY_ENABLE_WIDGETS = "keyguard_enable_widgets";
    private static final String BATTERY_AROUND_LOCKSCREEN_RING = "battery_around_lockscreen_ring";
    private static final String KEY_ADVANCED_CATAGORY = "advanced_catagory";
    private static final String KEY_GENERAL_CATAGORY = "general_catagory";
    private static final String KEY_LOCKSCREEN_BUTTONS = "lockscreen_buttons";
    private static final String KEY_LOCKSCREEN_ROTATION = "lockscreen_rotation";
    private static final String PREF_LOCKSCREEN_TORCH = "lockscreen_torch";
    private static final String PREF_LOCKSCREEN_EXTRAS = "lockscreen_extras";
    private static final String KEY_ACTIVE_DISPLAY = "active_display";
    private static final String KEY_PEEK = "notification_peek";
    private static final String KEY_PEEK_PICKUP_TIMEOUT = "peek_pickup_timeout";

    private PreferenceScreen mLockscreenButtons;
    private PreferenceCategory mAdvancedCatagory;
    private PreferenceCategory mGeneralCatagory;
    private PreferenceScreen mLockscreenExtras;
    private PreferenceScreen mActiveDisplay;
    private CheckBoxPreference mNotificationPeek;
    private ListPreference mPeekPickupTimeout;

    private ChooseLockSettingsHelper mChooseLockSettingsHelper;
    private DevicePolicyManager mDPM;

    private CheckBoxPreference mEnableKeyguardWidgets;
    private CheckBoxPreference mLockRingBattery;
    private CheckBoxPreference mLockscreenRotation;
    private CheckBoxPreference mGlowpadTorch;

    private SettingsObserver mSettingsObserver = new SettingsObserver(new Handler());
    private final class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.PEEK_STATE),
                    false, this, UserHandle.USER_ALL);
            resolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.ENABLE_ACTIVE_DISPLAY),
                    false, this, UserHandle.USER_ALL);
            resolver.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.HARDWARE_KEYS_DISABLE),
                    false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            try {
                updateSettings();
            } catch (Exception e) {
                Log.e(TAG, "AJ is a whore.");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_interface);

        // Dont display the lock clock preference if its not installed
        removePreferenceIfPackageNotInstalled(findPreference(KEY_LOCK_CLOCK));

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

        mLockscreenRotation = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_ROTATION);
        if (mLockscreenRotation != null) {
            mLockscreenRotation.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTATION, 0) == 1);
        }

        mLockRingBattery = (CheckBoxPreference) findPreference(BATTERY_AROUND_LOCKSCREEN_RING);
        if (mLockRingBattery != null) {
            mLockRingBattery.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BATTERY_AROUND_LOCKSCREEN_RING, 0) == 1);
        }

        mAdvancedCatagory = (PreferenceCategory) prefs.findPreference(KEY_ADVANCED_CATAGORY);
        mLockscreenButtons = (PreferenceScreen) findPreference(KEY_LOCKSCREEN_BUTTONS);

        mLockscreenExtras = (PreferenceScreen) findPreference(PREF_LOCKSCREEN_EXTRAS);
        if (!DeviceUtils.isPhone(getActivity())) {
            mLockscreenExtras.setTitle(R.string.lockscreen_extras_title_targets_only);
            mLockscreenExtras.setSummary(R.string.lock_screen_summary_targets_only);
        }

        mGeneralCatagory = (PreferenceCategory) prefs.findPreference(KEY_GENERAL_CATAGORY);
        mGlowpadTorch = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_TORCH);
        mGlowpadTorch.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_GLOWPAD_TORCH, 0) == 1);

        if (!DeviceUtils.deviceSupportsTorch(getActivity())) {
            mGeneralCatagory.removePreference(mGlowpadTorch);
        }

        mActiveDisplay = (PreferenceScreen) findPreference(KEY_ACTIVE_DISPLAY);

        mNotificationPeek = (CheckBoxPreference) findPreference(KEY_PEEK);
        mNotificationPeek.setPersistent(false);

        mPeekPickupTimeout = (ListPreference) findPreference(KEY_PEEK_PICKUP_TIMEOUT);
        int peekTimeout = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.PEEK_PICKUP_TIMEOUT, 0, UserHandle.USER_CURRENT);
        mPeekPickupTimeout.setValue(String.valueOf(peekTimeout));
        mPeekPickupTimeout.setSummary(mPeekPickupTimeout.getEntry());
        mPeekPickupTimeout.setOnPreferenceChangeListener(this);

        SettingsObserver settingsObserver = new SettingsObserver(new Handler());
        settingsObserver.observe();

        updateSettings();
    }

    private void updateSettings() {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean peekEnabled = Settings.System.getInt(resolver,
                Settings.System.PEEK_STATE, 0) == 1;
        boolean adEnabled = Settings.System.getInt(resolver,
                Settings.System.ENABLE_ACTIVE_DISPLAY, 0) == 1;
        boolean mHardwareKeysDisable = Settings.System.getInt(resolver,
                Settings.System.HARDWARE_KEYS_DISABLE, 0) == 1;
        boolean mHasButtons = !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
        if (peekEnabled) {
            mActiveDisplay.setEnabled(false);
            mActiveDisplay.setSummary(R.string.ad_disabled_summary);
        } else {
            mActiveDisplay.setEnabled(true);
            mActiveDisplay.setSummary(R.string.ad_settings_summary);
        }

        if (adEnabled) {
            mNotificationPeek.setEnabled(false);
            mPeekPickupTimeout.setEnabled(false);
            mNotificationPeek.setSummary(R.string.notification_peek_disabled_summary);
        } else {
            mNotificationPeek.setEnabled(true);
            mPeekPickupTimeout.setEnabled(true);
            mNotificationPeek.setSummary(R.string.notification_peek_summary);
        }

        try {
            if ((mHardwareKeysDisable) || (!mHasButtons)) {
                mAdvancedCatagory.removePreference(mLockscreenButtons);
            }
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSettings();
        mSettingsObserver.observe();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getContentResolver().unregisterContentObserver(mSettingsObserver);
    }

    private boolean isToggled(Preference pref) {
        return ((CheckBoxPreference) pref).isChecked();
    }

    private boolean removePreferenceIfPackageNotInstalled(Preference preference) {
        String intentUri=((PreferenceScreen) preference).getIntent().toUri(1);
        Pattern pattern = Pattern.compile("component=([^/]+)/");
        Matcher matcher = pattern.matcher(intentUri);

        String packageName=matcher.find()?matcher.group(1):null;
        if(packageName != null) {
            try {
                getPackageManager().getPackageInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                Log.e(TAG,"package "+packageName+" not installed, hiding preference.");
                getPreferenceScreen().removePreference(preference);
                return true;
            }
        }
        return false;
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
        } else if (preference == mLockscreenRotation) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTATION, isToggled(preference) ? 1 : 0);
        } else if (preference == mGlowpadTorch) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_GLOWPAD_TORCH, isToggled(preference) ? 1 : 0);
            return true;
        } else if (preference == mNotificationPeek) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PEEK_STATE,
                    mNotificationPeek.isChecked() ? 1 : 0);
            return true;
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_PEEK_PICKUP_TIMEOUT.equals(key)) {
            int peekTimeout = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                Settings.System.PEEK_PICKUP_TIMEOUT, peekTimeout);
            updatePeekTimeoutOptions(objValue);
        }
        return true;
    }

    private void updatePeekTimeoutOptions(Object newValue) {
        int index = mPeekPickupTimeout.findIndexOfValue((String) newValue);
        int value = Integer.valueOf((String) newValue);
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.System.PEEK_PICKUP_TIMEOUT, value);
        mPeekPickupTimeout.setSummary(mPeekPickupTimeout.getEntries()[index]);
    }
}
