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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;
import com.qg.memori.alarm.NotificationManager;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.DbHelper;
import com.qg.memori.data.MemoryData;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final int LAYOUT = R.layout.activity_memori_app;
    protected ArrayAdapter adapter;

    ListMode mode = ListMode.MEMORY;
    private TextView listHeader;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();


    enum ListMode {
        MEMORY,
        QUIZZ
    }

    enum Platform {
        DEV,
        PROD
    }

    Platform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config();


        setContentView(LAYOUT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createContent();
    }

    private void config() {
        platform = Platform.valueOf(SharedPrefsHelper.read(this, Prefs.APP_PLATFORM));
        Logger.init("Memori").hideThreadInfo().methodCount(1);
    }

    private void createContent() {
        setTitle("Memori (" + platform + " - " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + ")");

        createContentView();

        configAddButton();
    }

    public void createContentView() {

        final ListView listView = (ListView) findViewById(R.id.memory_list);

        //add title
        if (listHeader == null) {
            listHeader = new TextView(this);
            listView.addHeaderView(listHeader);
        }
        listHeader.setText("" + mode);

        switch (mode) {
            case MEMORY: {
                final List<MemoryData> memories = new ArrayList<>();
//                try {
//                    memories = new DbHelper(this).obtainDao(MemoryData.class).queryForEq("deleted", false);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }

                final String userUid = user.getUid();


                database.child("memoryByUserUid").child(userUid).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                                while (it.hasNext()) {
                                    MemoryData e = it.next().getValue(MemoryData.class);
                                    memories.add(e);
                                }
                                Logger.d("retreived %d memories", memories.size());

                                adapter = new ModelDataArrayAdapter(MainActivity.this, memories);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        displayMemoryDetail(position);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Logger.w("", databaseError.toException());
                            }
                        });

                break;
            }
            case QUIZZ: {
                break;
            }
        }
    }

    private void displayMemoryDetail(int position) {
        Intent intent = new Intent(MainActivity.this, MemoryDetailActivity.class);
        intent.putExtras(DataHelper.putInBundle(new Bundle(), (MemoryData) adapter.getItem(position - 1)));
        MainActivity.this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createContentView();
    }

    private void configAddButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);


                builder.setTitle("What do you want to remember?");

                final View layout = getLayoutInflater().inflate(R.layout.add_dialog, null);
                builder.setView(layout);

                builder.setPositiveButton("Remember", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String question = ((TextView) layout.findViewById(R.id.question_box)).getText().toString();
                        String answer = ((TextView) layout.findViewById(R.id.answer_box)).getText().toString();

                        insertNewMemory(context, question, answer, platform == Platform.DEV ? new Date() : null);
                        createContentView();
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
    private void insertNewMemory(Context context, String question, String answer, Date forcedDueDate) {

        //inserting the memory
        MemoryData m = MemoryData.create(question, answer);

        String s = m.getClass().getSimpleName().toString().toLowerCase();
        DatabaseReference k = database.child(DbHelper.NODE_MEMORY_BY_USER_UID).child(user.getUid()).push();
        m.id = k.getKey();
        k.setValue(m);

        QuizzScheduler.scheduleNextQuizz(context, m);
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
            createContentView();
            return true;
        }
        if (id == R.id.action_display_memory) {
            mode = ListMode.MEMORY;
            createContentView();
            return true;
        }
        if (id == R.id.drop_and_add) {
            dropDb();

            insertNewMemory(this, "dummy question", "dummy answer", new Date());
            createContentView();
            return true;
        }
        if (id == R.id.drop_db) {
            dropDb();
            createContentView();
            return true;
        }
        if (id == R.id.alarm) {
            NotificationManager.scheduleNextAlarm(this, 1);
            Toast.makeText(this, "alarm in 1 seconds!", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.change_platform) {
            Platform nextPlatform = platform == Platform.DEV ? Platform.PROD : Platform.DEV;
            SharedPrefsHelper.write(this, Prefs.APP_PLATFORM, nextPlatform.name());
            config();
            createContent();
            return true;
        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        if (id == R.id.preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dropDb() {
        database.child(DbHelper.NODE_MEMORY_BY_USER_UID).removeValue();
        database.child(DbHelper.NODE_OLD_QUIZZ_BY_MEMORY_UID).removeValue();
    }


}
