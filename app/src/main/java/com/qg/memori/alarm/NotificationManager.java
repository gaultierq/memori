package com.qg.memori.alarm;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qg.memori.ExamActivity;
import com.qg.memori.MainActivity;
import com.qg.memori.R;
import com.qg.memori.RequestCallback;
import com.qg.memori.data.QuizzData;

import java.util.List;

/**
 * Created by q on 25/03/2016.
 */
public class NotificationManager extends BroadcastReceiver {

    public static final int NOTIF_EXAM = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Toast.makeText(context, "alarm manager: on receive", Toast.LENGTH_LONG);

        refreshNotification(context);

        wl.release();
    }

    public static void refreshNotification(final Context context) {
        ExamActivity.requestQuizzToTake(context, new RequestCallback<List<QuizzData>>() {
            @Override
            public void onSuccess(List<QuizzData> data) {
                sendNotification(context, data.size());
            }
        });
    }

    private static void sendNotification(Context context, int numQuizzes) {
        Logger.i("displaying %s new quizzes to take", numQuizzes);

        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (numQuizzes <= 0) {
            mNotificationManager.cancel(NOTIF_EXAM);
        }
        else {
            android.support.v4.app.NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("Brain time")
                    .setContentText("You've received new messages.")
                    .setSmallIcon(R.drawable.common_full_open_on_phone);

            notifBuilder.setContentText("You have " + numQuizzes + " pending quizz").setNumber(numQuizzes);

            Intent ttqa = new Intent(context, ExamActivity.class);
            //ttqa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(ttqa);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            notifBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(NOTIF_EXAM, notifBuilder.build());
        }
    }

    public static void scheduleNextAlarm(Context context, long time) {
        cancelAlarm(context);
        if (time > 0) {
            addAlarm(context, time);
        }
    }

    public static void addAlarm(Context context, long time) {
        android.app.AlarmManager am =(android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //TODO: restore
        //am.setInexactRepeating(android.app.NotificationManager.RTC_WAKEUP, System.currentTimeMillis(), time, pi);
        am.setExact(android.app.AlarmManager.RTC_WAKEUP, time, pi);
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, NotificationManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
