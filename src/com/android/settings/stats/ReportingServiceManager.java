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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class ReportingServiceManager extends BroadcastReceiver {
    private static final long MILLIS_PER_MINUTE = 60L * 1000L;
    private static final long MILLIS_PER_HOUR = 60L * MILLIS_PER_MINUTE;
    private static final long MILLIS_PER_DAY = 24L * MILLIS_PER_HOUR;

    private static final long OPTOUT_INTERVAL = 1L * MILLIS_PER_HOUR;
    private static final long UPDATE_INTERVAL = 1L * MILLIS_PER_DAY;
    public  static final long ALARM_INTERVAL  = 3L * MILLIS_PER_HOUR;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = AnonymousStats.getPreferences(context);
        long lastCheckin = prefs.getLong(AnonymousStats.ANONYMOUS_LAST_CHECKED, 0);
        long now = System.currentTimeMillis();
        String action = intent.getAction();

        if (action != null) {
            // Broadcast handler
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                Log.d(ReportingService.TAG, "Boot complete");
                // Give the user one hour after first boot to opt out
                long initialTrigger = now;
                if (lastCheckin == 0) {
                    Log.d(ReportingService.TAG, "Using optout delay");
                    initialTrigger = now + OPTOUT_INTERVAL;
                }
                Intent alarmintent = new Intent(context, ReportingServiceManager.class);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.RTC,
                        initialTrigger, ALARM_INTERVAL,
                        PendingIntent.getBroadcast(context, 1, alarmintent, 0));
                return;
            }
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean noConnectivity = intent.getBooleanExtra("noConnectivity", false);
                Log.d(ReportingService.TAG, "Connectivity: noConnectivity=" + noConnectivity);
            }
        }

        // else alarm handler or connectivity change
        boolean optedIn = prefs.getBoolean(AnonymousStats.ANONYMOUS_OPT_IN, true);
        boolean checkinDue = (now - lastCheckin) > UPDATE_INTERVAL;

        Log.d(ReportingService.TAG, "now=" + now);
        Log.d(ReportingService.TAG, "lastCheckin=" + lastCheckin);
        Log.d(ReportingService.TAG, "optedIn=" + optedIn);
        Log.d(ReportingService.TAG, "checkinDue=" + checkinDue);

        if (optedIn && checkinDue) {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Log.d(ReportingService.TAG, "Network not connected");
                return;
            }

            Log.d(ReportingService.TAG, "Launch checkin");
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(context, ReportingService.class);
            context.startService(serviceIntent);
        }
    }
}
