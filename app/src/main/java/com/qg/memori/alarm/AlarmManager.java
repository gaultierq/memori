package com.qg.memori.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.qg.memori.MainActivity;
import com.qg.memori.R;
import com.qg.memori.TestActivity;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by q on 25/03/2016.
 */
public class AlarmManager extends BroadcastReceiver {

    public static final int NOTIF_BRAIN_TIME = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Toast.makeText(context, "alarm manager: on receive", Toast.LENGTH_LONG);

        final List<QuizzData> quizzesToTake;
        try {
            quizzesToTake = selectQuizzToTake(context);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (quizzesToTake.size() > 0) {
            sendNotification(context, quizzesToTake);
        }

        wl.release();
    }

    private void sendNotification(Context context, List<QuizzData> quizzesToTake) {
        Log.i(getClass().getSimpleName(), "displaying new quizzes to take");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        android.support.v4.app.NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Brain time")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.common_full_open_on_phone);

        int numQuizzes = quizzesToTake.size();
        notifBuilder.setContentText("You have " + numQuizzes + " pending quizz").setNumber(numQuizzes);

        Intent ttqa = new Intent(context, TestActivity.class);
        ttqa.putExtras(MainActivity.putListInBundle(new Bundle(), new ArrayList<QuizzData>(quizzesToTake)));
        ttqa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(ttqa);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIF_BRAIN_TIME, notifBuilder.build());
    }

    @Nullable
    private List<QuizzData> selectQuizzToTake(Context context) throws SQLException {
        final List<QuizzData> quizzesToTake;
        Date now = new Date();
        SQLHelper sql = new SQLHelper(context);
        {
            quizzesToTake = sql.getQuizzDao().queryBuilder().where().lt("dueDate", now).query();

            Dao<MemoryData, Long> mdao;
            mdao = sql.getMemoryDao();
            for (QuizzData q : quizzesToTake) {
                q.memory = mdao.queryForId(q.memoryId);
            }
        }
        //schedule next alarm
        {
            final List<QuizzData> next;
            QueryBuilder<QuizzData, Long> builder = sql.getQuizzDao().queryBuilder();
            builder.where().gt("dueDate", now);
            builder.orderBy("dueDate", false);
            builder.limit(1L);
            next = builder.query();

            long time = 0;
            if (next.size() > 0) {
                time = next.get(0).dueDate.getTime() - now.getTime();
            }
            scheduleNextAlarm(context, time);
        }
        return quizzesToTake;
    }

    public static void scheduleNextAlarm(Context context, long time) {
        cancelAlarm(context);
        if (time > 0) {
            addAlarm(context, time);
        }
    }

    public static void addAlarm(Context context, long time) {
        android.app.AlarmManager am =(android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pi);
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
