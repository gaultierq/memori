package com.qg.memori;

import android.content.Context;
import android.text.format.DateUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DbHelper;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    public static void scheduleNextQuizz(final Context context, final MemoryData memory) {
        /*
        DbHelper sqlHelper = new DbHelper(context);

        Dao<QuizzData, Long> qdao = sqlHelper.obtainDao(QuizzData.class);
        final QueryBuilder<QuizzData, Long> qb = qdao.queryBuilder();
*/
        final List<QuizzData> oldQuizzes = new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(DbHelper.NODE_OLD_QUIZZ_BY_MEMORY_UID).child(memory.id).orderByKey().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            QuizzData e = it.next().getValue(QuizzData.class);
                            oldQuizzes.add(e);
                        }

                        Logger.d("%d old quizzes found for memory %s", oldQuizzes.size(), memory.id);
                        Long nextQuizzDate = null;
                        int goodAnswerCount = QuizzData.countOnScore(oldQuizzes, 10);

                        //TODO: clean this code
                        if (goodAnswerCount < DELAYS.length) {
                            Long delay = DELAYS[goodAnswerCount];

                            //first quizz
                            if (oldQuizzes.isEmpty()) {
                                //TODO: assign it to a ref hour
                                nextQuizzDate = System.currentTimeMillis() + delay;
                            } else {
                                nextQuizzDate = oldQuizzes.get(0).dueDate + delay;
                            }
                        } else {
                            //memory is in your brain now :)
                            memory.nextQuizz = null;
                        }

                        if (nextQuizzDate != null) {
                            Logger.d("Next quizz for memory %s will be on %s", memory.id, new Date(nextQuizzDate));
                            QuizzData q = new QuizzData();
                            q.dueDate = nextQuizzDate;
                            q.memoryId = memory.id;
                            memory.nextQuizz = q;
                        }
                        DbHelper.updateMemory(memory);

                        NotificationManager.refreshNotification(context);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Logger.w("", databaseError.toException());
                    }
                });




    }

}
