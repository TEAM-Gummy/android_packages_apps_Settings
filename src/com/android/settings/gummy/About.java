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

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.Collections;

public class About extends SettingsPreferenceFragment {

    public static final String TAG = "About";

    private static final String GUMMY_REVIEW = "http://review.gummyrom.com/#/q/status:open,n,z";
    private static final String GUMMY_TWITTER = "https://twitter.com/TeamGummyRom";
    private static final String CPHELPS76 = "https://twitter.com/csphelps76";
    private static final String KEJAR31 = "https://twitter.com/kejar31";
    private static final String BKJOLLY = "https://twitter.com/bkjolly";
    private static final String BUCKMARBLE = "https://twitter.com/buckmarble";
    private static final String BYTECODEME = "https://twitter.com/BytecodeMe";
    private static final String DHACKER29 = "https://twitter.com/dhacker29";
    private static final String EMERICANX = "https://twitter.com/emericanx";
    private static final String HASHCODE = "https://twitter.com/Hashcode0f";
    private static final String HEDWIG34 = "https://twitter.com/hedwig34";
    private static final String PAPI92 = "https://twitter.com/Anthony_J_Fox";
    private static final String CHADFRAN84 = "https://twitter.com/Chadfran84";

    Preference mReviewUrl;
    Preference mCphelps76;
    Preference mKejar31;
    Preference mBkjolly;
    Preference mBuckmarble;
    Preference mBytecodeme;
    Preference mDhacker29;
    Preference mEmericanx;
    Preference mHashcode;
    Preference mHedwig34;
    Preference mPapi92;
    Preference mtdm;
    Preference mChadfran84;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.about_Gummy);
        addPreferencesFromResource(R.xml.prefs_about);

        mReviewUrl = findPreference("gummy_gerrit");
        mCphelps76 = findPreference("cphelps76");
        mKejar31 = findPreference("Kejar31");
        mBkjolly = findPreference("Bkjolly");
        mBuckmarble = findPreference("Buckmarble");
        mBytecodeme = findPreference("BytecodeMe");
        mDhacker29 = findPreference("dhacker29");
        mEmericanx = findPreference("Emericanx");
        mHashcode = findPreference("hashcode");
        mHedwig34 = findPreference("Hedwig34");
        mPapi92 = findPreference("papi92");
        mtdm = findPreference("tdm");
        mChadfran84 = findPreference("Chadfran84");

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mReviewUrl) {
            launchUrl(GUMMY_REVIEW);
            return true;
        } else if (preference == mCphelps76) {
            launchUrl(CPHELPS76);
            return true;
        } else if (preference == mKejar31) {
            launchUrl(KEJAR31);
            return true;
        } else if (preference == mBkjolly) {
            launchUrl(BKJOLLY);
            return true;
        } else if (preference == mBuckmarble) {
            launchUrl(BUCKMARBLE);
            return true;
        } else if (preference == mBytecodeme) {
            launchUrl(BYTECODEME);
            return true;
        } else if (preference == mDhacker29) {
            launchUrl(DHACKER29);
            return true;
        } else if (preference == mEmericanx) {
            launchUrl(EMERICANX);
            return true;
        } else if (preference == mHashcode) {
            launchUrl(HASHCODE);
            return true;
        } else if (preference == mHedwig34) {
            launchUrl(HEDWIG34);
            return true;
        } else if (preference == mPapi92) {
            launchUrl(PAPI92);
            return true;
        } else if (preference == mtdm) {
            launchUrl(GUMMY_TWITTER);
            return true;
        } else if (preference == mChadfran84) {
            launchUrl(CHADFRAN84);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent whatever = new Intent(Intent.ACTION_VIEW, uriUrl);
        getActivity().startActivity(whatever);
    }
}
