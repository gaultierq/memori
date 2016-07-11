package com.qg.memori;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.DbHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExamActivity extends AppCompatActivity {


    private static final boolean IGNORE_TIME = true;
    public static final int LAYOUT = R.layout.activity_take_the_quizz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        setTitle("Take the quizz");

        requestQuizzToTake(this, new RequestCallback<List<QuizzData>>() {
            @Override
            public void onSuccess(List<QuizzData> quizzes) {
                if (quizzes == null || quizzes.isEmpty()) {
                    Toast.makeText(ExamActivity.this, "no quizz to be taken", Toast.LENGTH_LONG).show();
                }
                else {

                    final ExamDialogFragment frag = new ExamDialogFragment();
                    frag.setArguments(DataHelper.createBundleFromList(quizzes));

                    frag.setOnDismissListener(new  DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showResultFragment(ExamActivity.this, frag.getQuizzes());
                        }
                    });
                    frag.show(getSupportFragmentManager(), "ExamDialogFragment");
                }
            }
        });

    }

    public static void showResultFragment(FragmentActivity activity, List<QuizzData> quizzes) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, ExamResultFragment.newInstance(new ArrayList<>(quizzes)), "ExamResultFragment");
        ft.commit();
    }



    @Nullable
    public static void requestQuizzToTake(final Context context, final RequestCallback<List<QuizzData>> cb) {
        final TreeMap<Long, QuizzData> pendingQuizzes = new TreeMap<>();

        //Date now = new Date();
        //DbHelper sql = new DbHelper(context);
        {
//            quizzesToTake = sql.obtainDao(QuizzData.class).queryBuilder().where().lt("dueDate", now).and().isNull("score").query();
//            Dao<MemoryData, Long> mdao = sql.obtainDao(MemoryData.class);
//            for (QuizzData q : quizzesToTake) {
//                q.memory = mdao.queryForId(q.memoryId);
//            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            final String userId = user.getUid();
            final Query query = database.child(DbHelper.NODE_MEMORY_BY_USER_UID).child(userId).orderByChild("nextQuizz");
            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long nowMs = System.currentTimeMillis();
                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();


                            while (it.hasNext()) {
                                MemoryData m = it.next().getValue(MemoryData.class);
                                Logger.d("memory: %s with pending quizz: %s.", m.id, m.nextQuizz != null);
                                if (m.nextQuizz != null) {
                                    pendingQuizzes.put(m.nextQuizz.dueDate, m.nextQuizz);
                                    m.nextQuizz.memory = m;
                                } else {
                                    //TODO: orderBy + startAt + non-null value + assert

                                }
                            }
                            List<QuizzData> quizzToTake = new ArrayList<QuizzData>();
                            QuizzData firstUntakenQuizz = null;

                            for (Map.Entry<Long, QuizzData> e : pendingQuizzes.entrySet()) {
                                if (e.getValue().dueDate < nowMs || IGNORE_TIME) {
                                    quizzToTake.add(e.getValue());
                                } else {
                                    firstUntakenQuizz = e.getValue();
                                    break;
                                }
                            }
                            if (firstUntakenQuizz != null) {
                                NotificationManager.scheduleNextAlarm(context, Math.max(3600 * 1000, firstUntakenQuizz.dueDate - nowMs));
                            }

                            cb.onSuccess(quizzToTake);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Logger.w("", databaseError.toException());
                        }
                    });

        }
        //schedule next alarm
        /*
        {
            final List<QuizzData> next;
            QueryBuilder<QuizzData, Long> builder = sql.obtainDao(QuizzData.class).queryBuilder();
            builder.where().gt("dueDate", now);
            builder.orderBy("dueDate", false);
            builder.limit(1L);
            next = builder.query();

            long time = 0;
            if (next.size() > 0) {
                time = next.get(0).dueDate.getTime() - now.getTime();
            }

            NotificationManager.scheduleNextAlarm(context, time);
        }
        */
    }

}
