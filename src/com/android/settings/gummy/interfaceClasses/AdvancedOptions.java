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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.VolumePanel;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class AdvancedOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "AdvancedOptions";

    private static final String KEY_HARDWARE_KEYS = "hardware_keys";
    private static final String KEY_ADVANCED_OPTIONS = "advanced_options";
    private static final String ENABLE_QUICKBOOT_KEY = "enable_quickboot";
    private static final String QUICKBOOT_PACKAGE_NAME = "com.qapp.quickboot";
    private static final String VOLUME_PANEL_BG_COLOR = "volume_panel_bg_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x00ffffff;

    private PreferenceScreen mHardwareKeys;
    private PreferenceCategory mAdvancedOptions;
    private CheckBoxPreference mQuickBoot;
    private ColorPickerPreference mVolumePanelBgColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_advanced_options);

        PreferenceScreen prefSet = getPreferenceScreen();

        mAdvancedOptions = (PreferenceCategory) prefSet.findPreference(KEY_ADVANCED_OPTIONS);

        mHardwareKeys = (PreferenceScreen) findPreference(KEY_HARDWARE_KEYS);

        // Quickboot
        mQuickBoot = findAndInitCheckboxPref(ENABLE_QUICKBOOT_KEY);
        if (!isPackageInstalled(QUICKBOOT_PACKAGE_NAME)) {
            mAdvancedOptions.removePreference(mQuickBoot);
        }

        // Volume panel background color
        mVolumePanelBgColor =
                (ColorPickerPreference) findPreference(VOLUME_PANEL_BG_COLOR);
        mVolumePanelBgColor.setOnPreferenceChangeListener(this);
        final int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_PANEL_BG_COLOR, 0x00ffffff);
        String hexColor = String.format("#%08x", (0x00ffffff & intColor));
        if (hexColor.equals("#00ffffff")) {
            mVolumePanelBgColor.setSummary(R.string.default_color);
        } else {
            mVolumePanelBgColor.setSummary(hexColor);
        }
        mVolumePanelBgColor.setNewPreviewColor(intColor);
        setHasOptionsMenu(true);

        removeHardwareKeyPreference();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset_default_message)
                .setIcon(R.drawable.ic_settings_backup)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.shortcut_action_reset);
        alertDialog.setMessage(R.string.qs_style_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
        Settings.System.putInt(getContentResolver(),
                Settings.System.VOLUME_PANEL_BG_COLOR, DEFAULT_BACKGROUND_COLOR);
        mVolumePanelBgColor.setNewPreviewColor(DEFAULT_BACKGROUND_COLOR);
        mVolumePanelBgColor.setSummary(R.string.default_color);
    }

        @Override
    public void onResume() {
        super.onResume();
        removeHardwareKeyPreference();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeHardwareKeyPreference();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mQuickBoot) {
            Settings.Global.putInt(getActivity().getContentResolver(),
                    Settings.Global.ENABLE_QUICKBOOT,
                    mQuickBoot.isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mVolumePanelBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.default_color);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.VOLUME_PANEL_BG_COLOR,
                    intHex);
            return true;
        }
        return true;
    }

    private static boolean isPackageInstalled(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private CheckBoxPreference findAndInitCheckboxPref(String key) {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
        if (pref == null) {
            throw new IllegalArgumentException("Cannot find preference with key = " + key);
        }
        return pref;
    }

    private void removePreference(Preference preference) {
        getPreferenceScreen().removePreference(preference);
    }

    private boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    private void removeHardwareKeyPreference() {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean mHardwareKeysDisable = Settings.System.getInt(resolver,
                Settings.System.HARDWARE_KEYS_DISABLE, 0) == 1;
        try {
            if ((mHardwareKeysDisable) || (!hasButtons())) {
                mAdvancedOptions.removePreference(mHardwareKeys);
            }
        } catch (Exception e) {
            // Do nothing
        }
    }
}
