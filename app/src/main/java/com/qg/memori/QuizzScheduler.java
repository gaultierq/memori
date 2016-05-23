package com.qg.memori;

import android.content.Context;
import android.text.format.DateUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by q on 23/05/2016.
 */
public class QuizzScheduler {

    public final static Long[] DELAYS = new Long[] {
            2 * DateUtils.HOUR_IN_MILLIS,
            12 * DateUtils.HOUR_IN_MILLIS,
            2 * DateUtils.DAY_IN_MILLIS,
            10 * DateUtils.DAY_IN_MILLIS,
            30 * DateUtils.DAY_IN_MILLIS,
            6 * 30 * DateUtils.DAY_IN_MILLIS,

    };
//
//    //after 12 hours, 8am
//    public static Date nextFirstQuizzDate() {
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH, 1);
//        c.set(Calendar.HOUR_OF_DAY, 8);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTime();
//    }

    public static void scheduleNextQuizz(Context context, MemoryData memory) {
        SQLHelper sqlHelper = new SQLHelper(context);
        Dao<QuizzData, Long> qdao = sqlHelper.obtainDao(QuizzData.class);
        QueryBuilder<QuizzData, Long> qb = qdao.queryBuilder();
        List<QuizzData> quizzes;
        try {
            Where<QuizzData, Long> memoryId = qb.where().eq("memoryId", memory.id);
            qb.orderBy("dueDate", false);
            quizzes = memoryId.query();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Date nextQuizzDate = null;

        int pendingQuizzes = QuizzData.countOnScore(quizzes, null);
        if (pendingQuizzes > 1) {
            throw new RuntimeException("Multiple pending quizzes should not happen.");
        }
        else if (pendingQuizzes == 0) {
            int goodAnswerCount = QuizzData.countOnScore(quizzes, 10);
            if (goodAnswerCount < DELAYS.length) {
                Long delay = DELAYS[goodAnswerCount];
                nextQuizzDate = new Date(quizzes.get(0).dueDate.getTime() + delay);
            }
            else {
                memory.acquired = true;
                try {
                    sqlHelper.obtainDao(MemoryData.class).update(memory);
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            if (nextQuizzDate != null) {
                QuizzData q = new QuizzData();
                q.dueDate = nextQuizzDate;
                q.memoryId = memory.id;
                SQLHelper.safeInsert(context, q);
            }
        }

    }

}
