/*
 * Copyright (C) 2014 Gummy
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

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.ArrayList;

public class AppOptions extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "AppOptions";

    private static final String KEY_APP_CATAGORY = "app_settings_catagory";
    private static final String KEY_APOLLO = "apollo";
    private static final String KEY_BROWSER = "browser";
    private static final String KEY_CALENDAR = "calendar";
    private static final String KEY_CONTACTS = "contacts";
    private static final String KEY_DESKCLOCK = "deskclock";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FILE_MANAGER = "file_manager";
    private static final String KEY_MMS = "mms";
    private static final String KEY_PHONE = "phone";

    private PreferenceCategory mAppCatagory;
    private PreferenceScreen mMms;
    private PreferenceScreen mPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {

            addPreferencesFromResource(R.xml.prefs_app_settings);

            PreferenceScreen prefSet = getPreferenceScreen();
            PackageManager pm = getPackageManager();

            mAppCatagory = (PreferenceCategory) prefSet.findPreference(KEY_APP_CATAGORY);

            // Dont display the app preferences if user disables or removes them
            try {
                if (!isPackageInstalled("com.andrew.apollo")) {
                    mAppCatagory.removePreference(findPreference(KEY_APOLLO));
                }
                if (!isPackageInstalled("com.android.browser")) {
                    mAppCatagory.removePreference(findPreference(KEY_BROWSER));
                }
                if (!isPackageInstalled("com.android.calendar")) {
                    mAppCatagory.removePreference(findPreference(KEY_CALENDAR));
                }
                if (!isPackageInstalled("com.android.contacts")) {
                    mAppCatagory.removePreference(findPreference(KEY_CONTACTS));
                }
                if (!isPackageInstalled("com.android.deskclock")) {
                    mAppCatagory.removePreference(findPreference(KEY_DESKCLOCK));
                }
                if (!isPackageInstalled("com.android.email")) {
                    mAppCatagory.removePreference(findPreference(KEY_EMAIL));
                }
                if (!isPackageInstalled("com.cyanogenmod.filemanager")) {
                    mAppCatagory.removePreference(findPreference(KEY_FILE_MANAGER));
                }
                if (!isPackageInstalled("com.android.mms")) {
                    mAppCatagory.removePreference(findPreference(KEY_MMS));
                } else if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                   mAppCatagory.removePreference(findPreference(KEY_MMS));
                }
                if (!isPackageInstalled("com.android.phone")) {
                    mAppCatagory.removePreference(findPreference(KEY_PHONE));
                } else if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                   mAppCatagory.removePreference(findPreference(KEY_PHONE));
                }
            } catch (Exception e) {
                // Do nothing
            }
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
