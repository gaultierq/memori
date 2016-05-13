package com.qg.memori;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_the_quizz);
        setTitle("Take the quizz");

        final List<QuizzData> quizzes;
        try {
            quizzes = selectQuizzToTake(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (quizzes == null || quizzes.isEmpty()) {
            Toast.makeText(this, "no quizz to be taken", Toast.LENGTH_LONG).show();
        }
        else {

            final TestDialogFragment frag = new TestDialogFragment();
            frag.setArguments(DataHelper.createBundleFromList(quizzes));

            frag.setOnDismissListener(new  DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showResultFragment(frag.getQuizzes());
                }
            });
            frag.show(getSupportFragmentManager(), "TestDialogFragment");
        }
    }

    private void showResultFragment(List<QuizzData> quizzes) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, TestResultFragment.newInstance(new ArrayList<>(quizzes)), "TestResultFragment");
        ft.commit();
    }


    @Nullable
    public static List<QuizzData> selectQuizzToTake(Context context) throws SQLException {
        final List<QuizzData> quizzesToTake;
        Date now = new Date();
        SQLHelper sql = new SQLHelper(context);
        {
            quizzesToTake = sql.obtainDao(QuizzData.class).queryBuilder().where().lt("dueDate", now).and().isNull("score").query();
            Dao<MemoryData, Long> mdao = sql.obtainDao(MemoryData.class);
            for (QuizzData q : quizzesToTake) {
                q.memory = mdao.queryForId(q.memoryId);
            }
        }
        //schedule next alarm
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
        return quizzesToTake;
    }

}
