/*
 * Copyright 2018 Open Source Project
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
package com.github.med_manager.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.github.med_manager.LandingActivity;
import com.github.med_manager.R;
import com.github.med_manager.util.Util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ese Udom on 10/10/2017.
 */

/** Manages start and end date check and notification */
public class MedStartAndEndCheckReceiver extends BroadcastReceiver {

    private Context context;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public MedStartAndEndCheckReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        //Retrieve medication start and end dates stored with shared preferences as strings
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String dates = sharedPreferences.getString(context.getResources().getString(R.string.prefs_file_med_dates_key), "");

        //Parse the string and assign an id for the notification
        parseSPData(dates, 32);

    }

    /** Parses the med start and end dates string from shared preferences */
    private void parseSPData(String str, int id) {
        try {
            if (!str.isEmpty()) {
                int interval;
                int hours;
                if (str.contains(",")) {
                    String arr[] = str.split(",");
                    for (String s : arr) {
                        interval = Integer.parseInt(s.split("|")[2]);
                        hours = Integer.parseInt(s.split("|")[4].isEmpty() ? "0" : s.split("|")[4]);
                        interval = 86400000 / interval;  //24hrs in milliseconds divided by the intake interval, is the alarm interval
                        if (hours > 0)
                            interval = 1000 * 60 * 60 * hours;
                        checkStart(new DateTime(Long.parseLong(s.split("|")[0])), id, interval, s.split("|")[3], hours);
                        checkEnd(new DateTime(Long.parseLong(s.split("|")[1])), id, s.split("|")[3]);
                    }
                } else {
                    interval = Integer.parseInt(str.split("|")[2]);
                    hours = Integer.parseInt(str.split("|")[4].isEmpty() ? "0" : str.split("|")[4]);
                    interval = 86400000 / interval;  //24hrs in milliseconds divided by the intake interval, is the alarm interval
                    if (hours > 0)
                        interval = 1000 * 60 * 60 * hours;
                    checkStart(new DateTime(Long.parseLong(str.split("|")[0])), id, interval, str.split("|")[3], hours);
                    checkEnd(new DateTime(Long.parseLong(str.split("|")[1])), id, str.split("|")[3]);
                }
            }
        } catch (Exception e) {

        }
    }

    /** Shows a notification on the status bar */
    private void showNotification(int id, String drugName, int which) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Build and configure a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setSound(soundUri)
                        .setTicker((which == 1 ? context.getString(R.string.med_about_start_noti_message, drugName) : context.getString(R.string.med_ends, drugName)))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentText((which == 1 ? context.getString(R.string.med_about_start_noti_message, drugName) : context.getString(R.string.med_ends, drugName)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder.setCategory(Notification.CATEGORY_REMINDER);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, LandingActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LandingActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        (which == 1? Util.MED_START_NOTI_CLICK_CODE : Util.MED_END_NOTI_CLICK_CODE), PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }

    /**Starts an alarm for medication intervals
     * @param interval
     * @param drugName
     * @param hours
     */
    private void startMedIntervalCheck(long interval, String drugName, int hours) {
        // Retrieve a PendingIntent that will perform a broadcast
        Intent checkIntent = new Intent(context, MedIntervalCheckReceiver.class);
        checkIntent.putExtra("DrugName", drugName);
        checkIntent.putExtra("Interval", interval);
        checkIntent.putExtra("Hours", hours);
        pendingIntent = PendingIntent.getBroadcast(context, Util.MED_INTERVAL_CHECK_CODE, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        manager.set(AlarmManager.RTC_WAKEUP, interval, pendingIntent);
        Log.d("MSAECheckReceiver", "Med Interval Check Alarm started");
    }

    /** Cancels alarm for medication intervals
     * @param interval
     * @param drugName
     * @param hours
     */
    private void cancelMedIntervalCheck(long interval, String drugName, int hours) {
        try {
            Intent checkIntent = new Intent(context, MedIntervalCheckReceiver.class);
            checkIntent.putExtra("DrugName", drugName);
            checkIntent.putExtra("Interval", interval);
            pendingIntent = PendingIntent.getBroadcast(context, Util.MED_INTERVAL_CHECK_CODE, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        } catch (Exception e) {
            Log.d("MSAECheckReceiver", "Exception while cancelling Event Start Check Alarm");
        }

        Log.d("MSAECheckReceiver", "Med Interval Check Alarm cancelled");
    }

    /** Checks for medication start and sends notification*/
    private void checkStart(DateTime dt, int id, long interval, String drugName, int hours) {
        int year = dt.getYear();
        int month = dt.getMonthOfYear();
        int day = dt.getDayOfMonth();

        DateTime now = DateTime.now(DateTimeZone.UTC);
        int nyear = now.getYear();
        int nmonth = now.getMonthOfYear();
        int nday = now.getDayOfMonth();

        if (year == nyear) {
            if (month == nmonth) {
                if (day == nday) {
                    //The number indicates if its start or end
                    showNotification(id, drugName, 1);
                    //Start interval alarm
                    startMedIntervalCheck(interval, drugName, hours);
                }
            }
        }
    }

    /** Checks for medication end and sends notification*/
    private void checkEnd(DateTime dt, int id, String drugName) {
        int year = dt.getYear();
        int month = dt.getMonthOfYear();
        int day = dt.getDayOfMonth();

        DateTime now = DateTime.now(DateTimeZone.UTC);
        int nyear = now.getYear();
        int nmonth = now.getMonthOfYear();
        int nday = now.getDayOfMonth();

        if (year == nyear) {
            if (month == nmonth) {
                if (day == nday) {
                    //The number indicates if its start or end
                    showNotification(id, drugName, 0);
                    //Remove date
                    removeDate(drugName);
                }
            }
        }
    }

    /**Removes alarm dates from the store since medication has ended*/
    private void removeDate(String drugName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String dates = sharedPreferences.getString(context.getResources().getString(R.string.prefs_file_med_dates_key), "");

        if (!dates.isEmpty()) {
            if (dates.contains(",")) {
                String ds[] = dates.split(",");
                List<String> list = new LinkedList<>(Arrays.asList(ds));
                for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
                    String s = iterator.next();
                    if (s.contains(drugName)) {
                        iterator.remove();
                    }
                }
                if (list.size() > 1) {
                    dates = "";
                    for (String s : list) {
                        dates = dates + "," + s;
                    }
                } else {
                    dates = list.get(0);
                }
            } else {
                dates = "";
            }
            sharedPreferences.edit()
                    .putString(context.getResources().getString(R.string.prefs_file_med_dates_key), dates)
                    .apply();
        }
    }
}
