package com.qg.memori.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.qg.memori.MainActivity;
import com.qg.memori.TakeTheQuizzActivity;
import com.qg.memori.data.Quizz;
import com.qg.memori.data.SQLHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 25/03/2016.
 */
public class AlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Toast.makeText(context, "alarm manager: on receive", Toast.LENGTH_LONG);

        long now = System.currentTimeMillis();

        SQLHelper sql = new SQLHelper(context);
        final List<Quizz> quizzesToTake;
        {
            SQLHelper.QueryParams params = new SQLHelper.QueryParams();
            params.selection = "dueDate < " + now;
            quizzesToTake = sql.fetchData(Quizz.class, params);
        }

        //schedule next alarm
        {
            SQLHelper.QueryParams params = new SQLHelper.QueryParams();
            params.selection = "dueDate > " + now;
            params.orderBy = "dueDate DESC";
            params.limit = "1";

            List<Quizz> next = sql.fetchData(Quizz.class, params);
            long time = 0;
            if (next.size() > 0) {
                time = next.get(0).dueDate.getTime() - now;
            }
            scheduleNextAlarm(context, time);
        }

        if (quizzesToTake.size() > 0) {
            Intent ii = new Intent(context, TakeTheQuizzActivity.class);
            ii.putExtras(MainActivity.putListInBundle(new Bundle(), new ArrayList<Quizz>(quizzesToTake)));
            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //TODO: notification instead
            context.startActivity(ii);
        }

        wl.release();
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
