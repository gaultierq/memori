package com.qg.memori;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected ArrayAdapter adapter;

    ListMode mode = ListMode.MEMORY;
    private TextView listHeader;


    enum ListMode {
        MEMORY,
        QUIZZ
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memori_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createList();

        configAddButton();
    }

    public void createList() {
        ListView listView = (ListView) findViewById(R.id.memory_list);

        //add title
        if (listHeader == null) {
            listHeader = new TextView(this);
            listView.addHeaderView(listHeader);
        }
        listHeader.setText("" + mode);

        switch (mode) {
            case MEMORY: {
                List<MemoryData> memories = null;
                try {
                    memories = new SQLHelper(this).obtainDao(MemoryData.class).queryForEq("deleted", false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapter = new ModelDataArrayAdapter(MainActivity.this, memories);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MemoryDetailActivity.class);
                        intent.putExtras(DataHelper.putInBundle(new Bundle(), (MemoryData) adapter.getItem(position - 1)));
                        MainActivity.this.startActivity(intent);
                    }
                });
                break;
            }
            case QUIZZ: {
                List<QuizzData> quizzes = null;
                try {
                    quizzes = new SQLHelper(this).obtainDao(QuizzData.class).queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapter = new ModelDataArrayAdapter(MainActivity.this, quizzes);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(null);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createList();
    }

    private void configAddButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("What do you want to remember?");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText questionBox = new EditText(context);
                questionBox.setHint("Type your question");
                layout.addView(questionBox);

                final EditText answerBox = new EditText(context);
                answerBox.setHint("Type your answer");
                layout.addView(answerBox);

                builder.setView(layout);

                builder.setPositiveButton("Remember", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String question = questionBox.getText().toString();
                        String answer = answerBox.getText().toString();

                        insertNewMemory(context, question, answer, 0);


                        createList();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    /**
     * simple way to remember something new
     */
    private static void insertNewMemory(Context context, String question, String answer, long firstQuizzDelayMs) {

        //inserting the memory
        MemoryData m = MemoryData.create(question, answer);
        SQLHelper.safeInsert(context, m);

        //inserting a first quizz
        QuizzData q = new QuizzData();
        q.dueDate = new Date(System.currentTimeMillis() + firstQuizzDelayMs);
        q.memoryId = m.id;
        SQLHelper.safeInsert(context, q);

        NotificationManager.refreshNotification(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memori_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_display_quizz) {
            mode = ListMode.QUIZZ;
            createList();
            return true;
        }
        if (id == R.id.action_display_memory) {
            mode = ListMode.MEMORY;
            createList();
            return true;
        }
        if (id == R.id.drop_and_add) {
            SQLHelper.drop(this);
            insertNewMemory(this, "dummy question", "dummy answer", 0);
            createList();
            return true;
        }
        if (id == R.id.drop_db) {
            SQLHelper.drop(this);
            createList();
            return true;
        }
        if (id == R.id.alarm) {
            NotificationManager.scheduleNextAlarm(this, 1);
            Toast.makeText(this, "alarm in 1 seconds!", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.scheduele_test_1) {
            int n = 1;
            for (int i = 0; i < 5; i++) {
                insertNewMemory(this, Integer.toString(i), Integer.toString(i), i * 4 * 1000);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
