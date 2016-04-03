package com.qg.memori;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qg.memori.data.QuizzData;

import java.util.List;

public class TakeTheQuizzActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_the_quizz);
        setTitle("Take the quizz!");

        List<QuizzData> quizzes = MainActivity.readListModel(getIntent().getExtras(), QuizzData.class);
        if (quizzes == null || quizzes.isEmpty()) {
            Toast.makeText(this, "no quizz to be taken", Toast.LENGTH_LONG).show();
        }
        else {
            ArrayAdapter<QuizzData> adapter = new ArrayAdapter<QuizzData>(this, -1, quizzes) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) TakeTheQuizzActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    TextView textView = (TextView) inflater.inflate(R.layout.list_text, parent, false);
                    QuizzData q = getItem(position);
                    textView.setText(q.memory.question);
                    return textView;
                }

            };
            ListView listView = (ListView) findViewById(R.id.quizz_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(null);
        }
    }
}
