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

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenInterface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
    private static final String TAG = "LockscreenInterface";

    private static final String PREF_LOCKSCREEN_AUTO_ROTATE = "lockscreen_auto_rotate";
    private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String PREF_LOCKSCREEN_TEXT_COLOR = "lockscreen_text_color";
    private static final String KEY_ADVANCED_CATAGORY = "advanced_catagory";
    private static final String KEY_WIDGET_OPTIONS = "lockscreen_widgets_catagory";
    private static final String KEY_LOCKSCREEN_BUTTONS = "lockscreen_buttons";
    private static final String HOME_UNLOCK_PREF = "home_unlock";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String KEY_LOCKSCREEN_ALL_WIDGETS = "lockscreen_all_widgets";
    private static final String KEY_LOCKSCREEN_MAXIMIZE_WIDGETS = "lockscreen_maximize_widgets";

    private PreferenceScreen mLockscreenButtons;
    private PreferenceCategory mAdvancedCatagory;

    CheckBoxPreference mLockscreenBattery;
    ColorPickerPreference mLockscreenTextColor;
    CheckBoxPreference mLockscreenAutoRotate;

    private CheckBoxPreference mAllWidgets;
    private CheckBoxPreference mCameraWidget;
    private CheckBoxPreference mHomeUnlock;
    private CheckBoxPreference mMaximizeWidgets;

    Context mContext;

    public boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_interface_settings);
        Preference mPref;

        mLockscreenAutoRotate = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_AUTO_ROTATE);
        mLockscreenAutoRotate.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_AUTO_ROTATE, false));

        mLockscreenBattery = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, false));

        mLockscreenTextColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TEXT_COLOR);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);

        PreferenceScreen prefs = getPreferenceScreen();
        mAdvancedCatagory = (PreferenceCategory) prefs.findPreference(KEY_ADVANCED_CATAGORY);

        mLockscreenButtons = (PreferenceScreen) findPreference(KEY_LOCKSCREEN_BUTTONS);
        if (!hasButtons()) {
            mAdvancedCatagory.removePreference(mLockscreenButtons);
        }

        mHomeUnlock = (CheckBoxPreference) findPreference(HOME_UNLOCK_PREF);
        mHomeUnlock.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.HOME_UNLOCK_SCREEN, 0) == 1);

        // Disable the HomeUnlock setting if no home button is available
        if (getActivity().getApplicationContext().getResources()
                .getBoolean(com.android.internal.R.bool.config_disableHomeUnlockSetting)) {
            mHomeUnlock.setEnabled(false);
        }

        mCameraWidget = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_CAMERA_WIDGET);
        mCameraWidget.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.KG_CAMERA_WIDGET, 0) == 1);

        mAllWidgets = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_ALL_WIDGETS);
        mAllWidgets.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.KG_ALL_WIDGETS, 1) == 1);

        mMaximizeWidgets = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_MAXIMIZE_WIDGETS);
        if (!Utils.isPhone(getActivity())) {
            PreferenceCategory widgetCategory = (PreferenceCategory) findPreference(KEY_WIDGET_OPTIONS);
            mPref = (Preference) findPreference(KEY_LOCKSCREEN_MAXIMIZE_WIDGETS);
            if (mPref != null)
                widgetCategory.removePreference(mMaximizeWidgets);
            mMaximizeWidgets = null;
        } else {
            mMaximizeWidgets.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
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
        if (preference == mLockscreenBattery) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenAutoRotate) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_AUTO_ROTATE,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mHomeUnlock) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HOME_UNLOCK_SCREEN,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mCameraWidget) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.KG_CAMERA_WIDGET, mCameraWidget.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mAllWidgets) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.KG_ALL_WIDGETS, mAllWidgets.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mMaximizeWidgets) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, mMaximizeWidgets.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean handled = false;
        if (preference == mLockscreenTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, intHex);
            return true;
        }
        return false;
    }
}
