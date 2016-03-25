package com.qg.memori;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.qg.memori.data.Quizz;

import java.util.List;

public class TakeTheQuizzActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_the_quizz);

        List<Quizz> quizzes = MainActivity.readListModel(getIntent().getExtras(), Quizz.class);
        if (quizzes == null || quizzes.isEmpty()) {
            Toast.makeText(this, "no quizz to be taken", Toast.LENGTH_LONG);
        }
        else {
            ArrayAdapter<Quizz> adapter = new ModelDataArrayAdapter(this, quizzes);
            ListView listView = (ListView) findViewById(R.id.quizz_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(null);
        }
    }
}
