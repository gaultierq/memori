package com.qg.memori;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

/**
 * Created by q on 27/02/2016.
 */

public class RecollectionSQL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String RECOLLECTION_TABLE_NAME = "recollection";

    public static final String QUESTION = "question";
    public static final String HINT = "hint";
    public static final String ANSWER = "answer";
    private static final String RECOLLECTION_TABLE_CREATE =
            "CREATE TABLE " + RECOLLECTION_TABLE_NAME + " (" +
                    QUESTION + " TEXT, " +
                    HINT + " TEXT, " +
                    ANSWER + " TEXT);";

    RecollectionSQL(Context context) {
        super(context, "MEMORI_DB", null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECOLLECTION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void insertRecollection(Context activity, String answer, String question) {
        insertRecollection(activity, answer, question, null);
    }

    public static void insertRecollection(Context activity, String answer, String question, String hint) {
        RecollectionSQL sql = new RecollectionSQL(activity);
        SQLiteDatabase db = sql.getReadableDatabase();
        db.insert(RECOLLECTION_TABLE_NAME, null, makeData(answer, question, hint));
    }

    @NonNull
    private static ContentValues makeData(String answer, String question, String hint) {
        ContentValues v = new ContentValues();
        v.put(QUESTION, question);
        v.put(ANSWER, answer);
        v.put(HINT, hint);
        return v;
    }
}
