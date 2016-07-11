package com.qg.memori;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.logger.Logger;
import com.qg.memori.data.DataHelper;
import com.qg.memori.data.DbHelper;
import com.qg.memori.data.MemoryData;

public class MemoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_detail_activity);

        final MemoryData memory  = DataHelper.readData(getIntent().getExtras(), MemoryData.class);

        final TextView memQuestion = (TextView) findViewById(R.id.memory_question);
        memQuestion.setText(memory.question);
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MemoryData m = memory;
                m.deleted = true;

                DbHelper.updateMemory(m);

                FirebaseDatabase.getInstance().getReference().child(DbHelper.NODE_OLD_QUIZZ_BY_MEMORY_UID).child(m.id).setValue(null, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Logger.e("error while removing quizzes", databaseError);
                        } else {
                            finish();
                        }
                    }
                });

            }
        });


    }

}
