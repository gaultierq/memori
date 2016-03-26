package com.qg.memori;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.qg.memori.data.MemoryData;
import com.qg.memori.data.QuizzData;
import com.qg.memori.data.SQLHelper;

import java.sql.SQLException;

public class MemoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_detail_activity);

        final MemoryData memory  = MainActivity.readModel(getIntent().getExtras(), MemoryData.class);

        final TextView memQuestion = (TextView) findViewById(R.id.memory_question);
        memQuestion.setText(memory.question);
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLHelper sql = new SQLHelper(MemoryDetailActivity.this);
                SQLiteDatabase db = sql.getReadableDatabase();

                db.beginTransaction();
                try {
                    //mark memory as deleted
                    memory.deleted = true;
                    sql.getMemoryDao().update(memory);

                    db.delete(QuizzData.class.getSimpleName(), "memoryId = '" + memory.id + "'", null);
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                finish();
            }
        });


    }

}
