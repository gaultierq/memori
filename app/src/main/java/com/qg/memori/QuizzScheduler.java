package com.qg.memori;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by q on 23/05/2016.
 */
public class QuizzScheduler {

    //after 12 hours, 8am
    public static Date nextFirstQuizzDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
