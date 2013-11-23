/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.settings.stats;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PreviewData extends SettingsPreferenceFragment {
    private static final String TARGET_NAME = "preview_target_name";
    private static final String MODEL_NAME = "preview_model_name";
    private static final String ANDROID_VERSION = "preview_android_version";
    private static final String ROM_VERSION = "preview_rom_version";
    private static final String CARRIER_NAME = "preview_carrier_name";
    private static final String CARRIER_ID = "preview_carrier_id";
    private static final String COUNTRY_CODE = "preview_country_code";
    private static final String DEVICE_ID = "preview_device_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preview_data);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final Context context = getActivity();

        prefSet.findPreference(TARGET_NAME).setSummary(Utilities.getDeviceTargetName());
        prefSet.findPreference(MODEL_NAME).setSummary(Utilities.getDeviceModelName());
        prefSet.findPreference(ANDROID_VERSION).setSummary(Utilities.getAndroidVersion());
        prefSet.findPreference(ROM_VERSION).setSummary(Utilities.getRomVersion());
        prefSet.findPreference(CARRIER_NAME).setSummary(Utilities.getCarrierName(context));
        prefSet.findPreference(CARRIER_ID).setSummary(Utilities.getCarrierId(context));
        prefSet.findPreference(COUNTRY_CODE).setSummary(Utilities.getCountryCode(context));
        prefSet.findPreference(DEVICE_ID).setSummary(Utilities.getDeviceId(context));
    }
}
