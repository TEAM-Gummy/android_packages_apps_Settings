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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;
import com.android.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportingService extends Service {
    /* package */ static final String TAG = "AnonymousStats";

    private StatsUploadTask mTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Log.d(TAG, "ReportingService starting");

        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTask = new StatsUploadTask();
            mTask.execute();
        }

        return Service.START_REDELIVER_INTENT;
    }

    private class StatsUploadTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            String deviceTargetName = Utilities.getDeviceTargetName();
            String deviceModelName = Utilities.getDeviceModelName();
            String androidVersion = Utilities.getAndroidVersion();
            String romVersion = Utilities.getRomVersion();
            String carrierName = Utilities.getCarrierName(getApplicationContext());
            String carrierId = Utilities.getCarrierId(getApplicationContext());
            String countryCode = Utilities.getCountryCode(getApplicationContext());
            String deviceId = Utilities.getDeviceId(getApplicationContext());

            // report to the stats service
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(getString(R.string.stats_submit_url));
            boolean success = false;
            try {
                List<NameValuePair> kv = new ArrayList<NameValuePair>(2);
                kv.add(new BasicNameValuePair("target_name", deviceTargetName));
                kv.add(new BasicNameValuePair("model_name", deviceModelName));
                kv.add(new BasicNameValuePair("android_version", androidVersion));
                kv.add(new BasicNameValuePair("rom_version", romVersion));
                kv.add(new BasicNameValuePair("carrier_name", carrierName));
                kv.add(new BasicNameValuePair("carrier_id", carrierId));
                kv.add(new BasicNameValuePair("country_code", countryCode));
                kv.add(new BasicNameValuePair("device_id", deviceId));

                httpPost.setEntity(new UrlEncodedFormEntity(kv));
                HttpResponse response = httpClient.execute(httpPost);
                StatusLine sl = response.getStatusLine();
                int code = sl.getStatusCode();
                Log.i(TAG, "Response code=" + code);
                success = (code == 200);
            } catch (IOException e) {
                Log.w(TAG, "Could not upload stats checkin", e);
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d(TAG, "Post execute: success");
                final Context context = ReportingService.this;
                SharedPreferences prefs = AnonymousStats.getPreferences(context);
                long now = System.currentTimeMillis();
                prefs.edit().putLong(AnonymousStats.ANONYMOUS_LAST_CHECKED, now).apply();
            }
            else {
                Log.d(TAG, "Post execute: fail");
            }

            stopSelf();
        }
    }
}
