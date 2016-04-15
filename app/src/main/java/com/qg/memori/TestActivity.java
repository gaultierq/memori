package com.qg.memori;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.qg.memori.data.DataHelper;
import com.qg.memori.data.QuizzData;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_the_quizz);
        setTitle("Take the quizz");

        final List<QuizzData> quizzes = DataHelper.readDataList(getIntent().getExtras(), QuizzData.class);
        if (quizzes == null || quizzes.isEmpty()) {
            Toast.makeText(this, "no quizz to be taken", Toast.LENGTH_LONG).show();
        }
        else {

            TestDialogFragment frag = new TestDialogFragment(quizzes);

            frag.setOnDismissListener(new  DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    showResultFragment(quizzes);
                }
            });
            frag.show(getSupportFragmentManager(), "TestDialogFragment");

//            showResultFragment(quizzes);

        }
    }

    private void showResultFragment(List<QuizzData> quizzes) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, TestResultFragment.newInstance(new ArrayList<QuizzData>(quizzes)), "TestResultFragment");
        ft.commit();
    }

}
