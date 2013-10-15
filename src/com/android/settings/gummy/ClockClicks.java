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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.os.Bundle;

import com.android.settings.util.ShortcutPickerHelper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClockClicks extends SettingsPreferenceFragment implements
        ShortcutPickerHelper.OnPickListener, OnPreferenceChangeListener {

    private static final String TAG = "ClockClicks";

    private static final String PREF_CLOCK_SHORTCLICK = "clock_shortclick";
    private static final String PREF_CLOCK_LONGCLICK = "clock_longclick";
    private static final String PREF_CLOCK_DOUBLECLICK = "clock_doubleclick";

    private ShortcutPickerHelper mPicker;
    private Preference mPreference;
    private String mString;

    private int shortClick = 0;
    private int longClick = 1;
    private int doubleClick = 2;

    ContentResolver mContentRes;

    ListPreference mClockShortClick;
    ListPreference mClockLongClick;
    ListPreference mClockDoubleClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_clock_clicks);

        PreferenceScreen prefSet = getPreferenceScreen();
        mContentRes = getActivity().getContentResolver();

        mPicker = new ShortcutPickerHelper(this, this);

        mClockShortClick = (ListPreference) findPreference(PREF_CLOCK_SHORTCLICK);
        mClockShortClick.setOnPreferenceChangeListener(this);
        mClockShortClick.setSummary(getProperSummary(mClockShortClick));

        mClockLongClick = (ListPreference) findPreference(PREF_CLOCK_LONGCLICK);
        mClockLongClick.setOnPreferenceChangeListener(this);
        mClockLongClick.setSummary(getProperSummary(mClockLongClick));

        mClockDoubleClick = (ListPreference) findPreference(PREF_CLOCK_DOUBLECLICK);
        mClockDoubleClick.setOnPreferenceChangeListener(this);
        mClockDoubleClick.setSummary(getProperSummary(mClockDoubleClick));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mClockShortClick) {
            mPreference = preference;
            mString = Settings.System.NOTIFICATION_CLOCK[shortClick];
            if (newValue.equals("**app**")) {
                mPicker.pickShortcut();
            } else {
                result = Settings.System.putString(mContentRes,
                        Settings.System.NOTIFICATION_CLOCK[shortClick], (String) newValue);
                mClockShortClick.setSummary(getProperSummary(mClockShortClick));
            }
        } else if (preference == mClockLongClick) {
            mPreference = preference;
            mString = Settings.System.NOTIFICATION_CLOCK[longClick];
            if (newValue.equals("**app**")) {
                mPicker.pickShortcut();
            } else {
                result = Settings.System.putString(mContentRes,
                        Settings.System.NOTIFICATION_CLOCK[longClick], (String) newValue);
                mClockLongClick.setSummary(getProperSummary(mClockLongClick));
            }
        } else if (preference == mClockDoubleClick) {
            mPreference = preference;
            mString = Settings.System.NOTIFICATION_CLOCK[doubleClick];
            if (newValue.equals("**app**")) {
                mPicker.pickShortcut();
            } else {
                result = Settings.System.putString(mContentRes,
                        Settings.System.NOTIFICATION_CLOCK[doubleClick], (String) newValue);
                mClockDoubleClick.setSummary(getProperSummary(mClockDoubleClick));
            }
        }
        return result;
    }

    public void shortcutPicked(String uri, String friendlyName, Bitmap bmp, boolean isApplication) {
        mPreference.setSummary(friendlyName);
        Settings.System.putString(mContentRes, mString, (String) uri);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ShortcutPickerHelper.REQUEST_PICK_SHORTCUT
                    || requestCode == ShortcutPickerHelper.REQUEST_PICK_APPLICATION
                    || requestCode == ShortcutPickerHelper.REQUEST_CREATE_SHORTCUT) {
                mPicker.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getProperSummary(Preference preference) {
        if (preference == mClockDoubleClick) {
            mString = Settings.System.NOTIFICATION_CLOCK[doubleClick];
        } else if (preference == mClockLongClick) {
            mString = Settings.System.NOTIFICATION_CLOCK[longClick];
        } else if (preference == mClockShortClick) {
            mString = Settings.System.NOTIFICATION_CLOCK[shortClick];
        }

        String uri = Settings.System.getString(mContentRes, mString);
        String empty = "";

        if (uri == null) {
            return empty;
        }

        if (uri.startsWith("**")) {
            if (uri.equals("**alarm**")) {
                return getResources().getString(R.string.alarm);
            } else if (uri.equals("**event**")) {
                return getResources().getString(R.string.event);
            } else if (uri.equals("**voiceassist**")) {
                return getResources().getString(R.string.voiceassist);
            } else if (uri.equals("**clockoptions**")) {
                return getResources().getString(R.string.clock_options);
            } else if (uri.equals("**today**")) {
                return getResources().getString(R.string.today);
            } else if (uri.equals("**null**")) {
                return getResources().getString(R.string.nothing);
            }
        } else {
            return mPicker.getFriendlyNameForUri(uri);
        }
        return null;
    }
}
