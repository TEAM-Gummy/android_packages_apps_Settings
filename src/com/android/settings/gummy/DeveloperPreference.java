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

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.net.Uri;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DeveloperPreference extends SettingsPreferenceFragment {

    public static final String TAG = "DeveloperPreference";

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
    private static final String INKSLINGER420 = "https://twitter.com/InkSlinger420";
    private static final String MATTLGROFF = "https://twitter.com/mattlgroff";
    private static final String PAPI92 = "https://twitter.com/Anthony_J_Fox";
    private static final String THOMASRAINES = "https://twitter.com/thomasraines";
    private static final String WEDGESS = "https://twitter.com/wedgess";
    private static final String CHADFRAN84 = "https://twitter.com/Chadfran84";

    Preference mCphelps76;
    Preference mKejar31;
    Preference mapascual89;
    Preference mBkjolly;
    Preference mBuckmarble;
    Preference mBytecodeme;
    Preference mDhacker29;
    Preference mEmericanx;
    Preference mHashcode;
    Preference mHedwig34;
    Preference mInkSlinger420;
    Preference mmattlgroff;
    Preference mPapi92;
    Preference mtdm;
    Preference mthomasraines;
    Preference mWedgess;
    Preference mChadfran84;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.about_gummy_developer);
        addPreferencesFromResource(R.xml.prefs_gummy_developer);

        mCphelps76 = findPreference("cphelps76");
        mKejar31 = findPreference("Kejar31");
        mapascual89 = findPreference("apascual89");
        mBkjolly = findPreference("Bkjolly");
        mBuckmarble = findPreference("Buckmarble");
        mBytecodeme = findPreference("BytecodeMe");
        mDhacker29 = findPreference("dhacker29");
        mEmericanx = findPreference("Emericanx");
        mHashcode = findPreference("hashcode");
        mHedwig34 = findPreference("Hedwig34");
        mInkSlinger420 = findPreference("InkSlinger420");
        mmattlgroff = findPreference("mattlgroff");
        mPapi92 = findPreference("papi92");
        mtdm = findPreference("tdm");
        mthomasraines = findPreference("thomasraines");
        mWedgess = findPreference("wedgess");
        mChadfran84 = findPreference("Chadfran84");

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCphelps76) {
            launchUrl(CPHELPS76);
            return true;
        } else if (preference == mKejar31) {
            launchUrl(KEJAR31);
            return true;
        } else if (preference == mapascual89) {
            launchUrl(GUMMY_TWITTER);
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
        } else if (preference == mInkSlinger420) {
            launchUrl(INKSLINGER420);
            return true;
        } else if (preference == mmattlgroff) {
            launchUrl(MATTLGROFF);
            return true;
        } else if (preference == mPapi92) {
            launchUrl(PAPI92);
            return true;
        } else if (preference == mtdm) {
            launchUrl(GUMMY_TWITTER);
            return true;
        } else if (preference == mthomasraines) {
            launchUrl(THOMASRAINES);
            return true;
        } else if (preference == mWedgess) {
            launchUrl(WEDGESS);
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
