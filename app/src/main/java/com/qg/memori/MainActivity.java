package com.qg.memori;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.qg.memori.data.ModelData;
import com.qg.memori.data.Quizz;
import com.qg.memori.data.SQLHelper;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected ArrayAdapter adapter;

    ListMode mode = ListMode.MEMORY;
    private TextView listHeader;

    @Nullable
    static <T extends ModelData> T readModel(Bundle b, Class<T> modelToRead) {
        return modelToRead.cast(b.getSerializable(modelToRead.getSimpleName()));
    }

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
                Memory m = new Memory();
                m.deleted = false;
                List<Memory> memories = new SQLHelper(this).fetchSimilar(m);
                adapter = new MemoriesArrayAdapter(MainActivity.this, memories);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MemoryDetailActivity.class);
                        intent.putExtras(putInBundle(new Bundle(), (Memory) adapter.getItem(position - 1)));
                        MainActivity.this.startActivity(intent);
                    }
                });
                break;
            }
            case QUIZZ: {
                List<Quizz> quizzes = new SQLHelper(this).fetchData(Quizz.class, null);
                adapter = new QuizzArrayAdapter(MainActivity.this, quizzes);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(null);
                break;
            }
        }
    }

    public static Bundle putInBundle(Bundle bundle, ModelData v) {
        bundle.putSerializable(v.getClass().getSimpleName(), v);
        return bundle;
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
