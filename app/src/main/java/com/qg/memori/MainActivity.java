package com.qg.memori;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.qg.memori.data.Memory;
import com.qg.memori.data.Quizz;
import com.qg.memori.data.SQLHelper;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected ArrayAdapter adapter;
    private SQLHelper sql;

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

    private void createList() {
        ListView listView = (ListView) findViewById(R.id.memory_list);

        //add title
        if (listHeader == null) {
            listHeader = new TextView(this);
            listView.addHeaderView(listHeader);
        }
        listHeader.setText("" + mode);

        switch (mode) {
            case MEMORY: {
                List<Memory> memories = obtainSLQHelper().fetchData(Memory.class);
                adapter = new MemoriesArrayAdapter(MainActivity.this, memories);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Memory mem = (Memory) adapter.getItem(position);
                        MemoryConfigFragment fragment = new MemoryConfigFragment();
                        fragment.setMemory(mem);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.fragment_container, fragment);
                        fragmentTransaction.commit();
                    }
                });
                break;
            }
            case QUIZZ: {
                List<Quizz> quizzs = obtainSLQHelper().fetchData(Quizz.class);
                adapter = new QuizzArrayAdapter(MainActivity.this, quizzs);
                listView.setAdapter(adapter);
                break;
            }
        }
    }

    @NonNull
    SQLHelper obtainSLQHelper() {
        return sql == null ? (sql = new SQLHelper(this)) : sql;
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


                        //inserting the memory
                        Memory m = new Memory();
                        m.question = questionBox.getText().toString();
                        m.answer = answerBox.getText().toString();
                        m.hint = null;

                        SQLHelper sql = new SQLHelper(context);
                        sql.getReadableDatabase().beginTransaction();
                        try {
                            Memory addedMemory = sql.insertData(m);
                            if (addedMemory.id < 0) {
                                throw new AssertionError("expecting id to be set");
                            }
                            //inserting a first quizz
                            Quizz q = new Quizz();
                            q.dueDate = new Date();
                            q.memoryId = addedMemory.id;
                            sql.insertData(q);
                            sql.getReadableDatabase().setTransactionSuccessful();
                        } finally {
                            sql.getReadableDatabase().endTransaction();
                        }
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

    void refresh() {
        if (adapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
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
        if (id == R.id.drop_db) {
            SQLHelper.drop(this);
            createList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
