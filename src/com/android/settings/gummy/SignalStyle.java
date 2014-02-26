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

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.util.Helpers;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class SignalStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "SignalStyle";

    private static final String KEY_STYLE_CATAGORY = "signal_style";
    private static final String KEY_SHOW_4G = "show_4g_for_lte";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUSBAR_SIGNAL_TEXT_COLOR = "status_bar_signal_color";

    private PreferenceCategory mStyleCatagory;
    private ListPreference mStatusBarSignal;
    private ColorPickerPreference mStatusBarSignalColor;
    private CheckBoxPreference mShow4G;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_signal_style);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStyleCatagory = (PreferenceCategory) prefSet.findPreference(KEY_STYLE_CATAGORY);

        mShow4G = (CheckBoxPreference) findPreference(KEY_SHOW_4G);
            mShow4G.setOnPreferenceChangeListener(this);

        mStatusBarSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);
        int signalStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_SIGNAL_TEXT, 0);
        mStatusBarSignal.setValue(String.valueOf(signalStyle));
        mStatusBarSignal.setSummary(mStatusBarSignal.getEntry());
        mStatusBarSignal.setOnPreferenceChangeListener(this);

        mStatusBarSignalColor = (ColorPickerPreference) findPreference(STATUSBAR_SIGNAL_TEXT_COLOR);
        mStatusBarSignalColor.setOnPreferenceChangeListener(this);
        int defaultColor = getResources().getColor(
                com.android.internal.R.color.white);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, defaultColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarSignalColor.setSummary(hexColor);

        isTelephony();
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShow4G) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SHOW_4G_FOR_LTE,
                    ((CheckBoxPreference)preference).isChecked() ? 0 : 1);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mStatusBarSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT, signalStyle);
            mStatusBarSignal.setSummary(mStatusBarSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarSignalColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, intHex);
            return true;
        }
        return false;

    }

    private void isTelephony() {
        // Determine options based on device telephony support
        PackageManager pm = getPackageManager();
        try {
            if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                // No telephony, remove dependent options
                mStyleCatagory.removePreference(findPreference(KEY_SHOW_4G));
                mStyleCatagory.removePreference(findPreference(STATUS_BAR_SIGNAL));
                mStyleCatagory.removePreference(findPreference(STATUSBAR_SIGNAL_TEXT_COLOR));
            }
        } catch (Exception e) {
            // Do nothing
        }
    }
}
