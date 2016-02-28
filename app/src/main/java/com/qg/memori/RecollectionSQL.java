package com.qg.memori;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 27/02/2016.
 */

public class RecollectionSQL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String RECOLLECTION_TABLE_NAME = "recollection";

    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String HINT = "hint";
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

    public static Memory insertRecollection(Context activity, String answer, String question) {
        return insertRecollection(activity, answer, question, null);
    }

    public static Memory insertRecollection(Context activity, String answer, String question, String hint) {
        RecollectionSQL sql = new RecollectionSQL(activity);
        Memory m = new Memory();
        m.question = question;
        m.answer = answer;
        m.hint = hint;
        SQLiteDatabase db = sql.getReadableDatabase();
        db.insert(RECOLLECTION_TABLE_NAME, null, makeData(m));
        return m;
    }

    public List<Memory> getAllRecollections() {
        List<Memory> recollections = new ArrayList<Memory>();

        Cursor cursor = getReadableDatabase().query(RECOLLECTION_TABLE_NAME,
                new String[] {QUESTION, ANSWER, HINT}, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Memory recollection = cursorToRecollection(cursor);
            recollections.add(recollection);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return recollections;
    }


    @NonNull
    private static ContentValues makeData(Memory m) {
        ContentValues v = new ContentValues();
        v.put(QUESTION, m.question);
        v.put(ANSWER, m.answer);
        v.put(HINT, m.hint);
        return v;
    }

    private Memory cursorToRecollection(Cursor cursor) {
        Memory recollection = new Memory();
        recollection.question = (cursor.getString(0));
        recollection.answer = (cursor.getString(1));
        recollection.hint = (cursor.getString(2));
        return recollection;
    }

}
