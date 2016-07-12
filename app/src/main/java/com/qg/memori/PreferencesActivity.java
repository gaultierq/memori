package com.qg.memori;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TimePicker;

public class PreferencesActivity extends AppCompatActivity {

    public static final int LAYOUT = R.layout.activity_preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        configTimePicker();
    }

    private void configTimePicker() {
        TimePicker tp = (TimePicker) findViewById(R.id.config_workout_time);
        String[] time = SharedPrefsHelper.read(PreferencesActivity.this, Prefs.WORKOUT_TIME).split(":");
        int hour = Integer.parseInt(time[0].trim());
        int minute = Integer.parseInt(time[1].trim());
        if (Build.VERSION.SDK_INT >= 23) {
            tp.setHour(hour);
            tp.setMinute(minute);
        }
        else {
            tp.setCurrentHour(hour);
            tp.setCurrentMinute(minute);
        }
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                SharedPrefsHelper.write(PreferencesActivity.this, Prefs.WORKOUT_TIME, hourOfDay + ":" + minute);
            }
        });
    }


}
