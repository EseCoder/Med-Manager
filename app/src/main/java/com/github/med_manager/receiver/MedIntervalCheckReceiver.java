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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.github.med_manager.LandingActivity;
import com.github.med_manager.R;
import com.github.med_manager.util.Util;

/**
 * Created by Ese Udom on 4/11/2018.
 */

/** Manages interval check and notifications*/
public class MedIntervalCheckReceiver extends BroadcastReceiver {

    private Context context;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private long interval;
    private String drugName;
    private int hours;

    public MedIntervalCheckReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        drugName = intent.getStringExtra("DrugName");
        interval = intent.getLongExtra("Interval", 1000*60*60*8);
        hours = intent.getIntExtra("Hours", 8);

        showNotification(33, drugName);
        startMedIntervalCheck(interval, drugName, hours);
    }

    /** Shows a notification on the status bar */
    private void showNotification(int id, String drugName) {
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
                        .setTicker(context.getString(R.string.its_time_noti_message, drugName))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentText(context.getString(R.string.its_time_noti_message, drugName));

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
                        (Util.MED_INT_NOTI_CLICK_CODE), PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }

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
}
