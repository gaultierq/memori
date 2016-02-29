package com.qg.memori;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by q on 27/02/2016.
 */

public class SQLHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;


    SQLHelper(Context context) {
        super(context, "MEMORI_DB_3", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(makeCreateCommand(Memory.class));
        db.execSQL(makeCreateCommand(Quizz.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Memory insertRecollection(Context activity, String answer, String question) {
        return insertRecollection(activity, answer, question, null);
    }

    public static Memory insertRecollection(Context activity, String answer, String question, String hint) {
        SQLHelper sql = new SQLHelper(activity);
        Memory m = new Memory();
        m.question = question;
        m.answer = answer;
        m.hint = hint;
        SQLiteDatabase db = sql.getReadableDatabase();
        ContentValues cv = null;
        try {
            cv = makeData(m);
        } catch (IllegalAccessException e) {
            Log.e("sql", ""+e);
            return null;
        }
        db.insert(m.getClass().getSimpleName(), null, cv);
        return m;
    }

    public List<Memory> getAllRecollections() {
        List<Memory> recollections = new ArrayList<Memory>();

        Set<String> fields = enumDataField(Memory.class).keySet();
        Cursor cursor = getReadableDatabase().query(Memory.class.getSimpleName(),
                fields.toArray(new String[fields.size()])
                , null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Memory res = new Memory();
            if (readCursor(cursor, res)) {
                recollections.add(res);
            }
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return recollections;
    }


    @NonNull
    private static ContentValues makeData(Memory m) throws IllegalAccessException {
        ContentValues v = new ContentValues();
        for (Field f : m.getClass().getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }

            if (f.getType() == String.class) {
                v.put(f.getName(), (String) f.get(m));
            }
            else if (f.getType() == Boolean.class) {
                v.put(f.getName(), (Boolean) f.get(m));
            }
            else if (f.getType() == Date.class) {
                v.put(f.getName(), (Long) f.get(m));
            }
            else if (f.getType() == Integer.class) {
                v.put(f.getName(), (Integer) f.get(m));
            }
            else if (f.getType() == Long.class) {
                v.put(f.getName(), (Long) f.get(m));
            }
            else if (f.getType().isEnum()) {
                Enum e = ((Enum)f.get(m));
                if (e != null) {
                    v.put(f.getName(), e.ordinal());
                }
            }
            else {
                assert false : "not supported";
            }
        }
        return v;
    }


    @NonNull
    private static String makeCreateCommand(Class clazz) {
        StringBuilder res = new StringBuilder();
        res.append("CREATE TABLE ").append(clazz.getSimpleName()).append(" (");

        StringBuilder b = new StringBuilder();
        for (Field f : clazz.getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }
            if (b.length() > 0) {
                b.append(", ");
            }
            b.append(f.getName());
            if (f.getType() == String.class) {
                b.append(" string");
            }
            else if (f.getType() == Boolean.TYPE) {
                b.append(" boolean");
            }
            else if (f.getType() == Date.class) {
                b.append(" datetime");
            }
            else if (f.getType() == Integer.TYPE) {
                b.append(" integer");
            }
            else if (f.getType() == Long.TYPE) {
                b.append(" integer");
            }
            else if (f.getType().isEnum()) {
                b.append(" integer");
            }
            else {
                throw new AssertionError("not supported: "+f.getType());
            }
            if (sqlInfo.id()) {
                b.append(" primary key autoincrement");
            }
        }
        res.append(b);
        String s = res.append(");").toString();
        Log.i("sql", "sql create command: " + s);
        return s;
    }

    private Map<String, Class> enumDataField(Class clazz) {
        Map<String, Class> res = new LinkedHashMap<>();
        for (Field f : clazz.getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null) {
                continue;
            }
            res.put(f.getName(), f.getType());
        }
        return res;
    }

    private boolean readCursor(Cursor cursor, Object res) {
        int i = 0;
        try {
            for (Field f : res.getClass().getDeclaredFields()) {
                SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
                if (sqlInfo == null) {
                    continue;
                }

                if (f.getType() == String.class) {
                    f.set(res, cursor.getString(i++));

                }
                else if (f.getType() == Boolean.class) {
                    f.set(res, cursor.getInt(i++) != 0);
                }
                else if (f.getType() == Date.class) {
                    f.set(res, new Date(cursor.getInt(i++)));
                }
                else if (f.getType() == Integer.class) {
                    f.set(res, cursor.getInt(i++));
                }
                else if (f.getType() == Long.class) {
                    f.set(res, cursor.getInt(i++));
                }
                else if (f.getType().isEnum()) {
                    int ord = cursor.getInt(i++);
                    f.set(res, f.getClass().getEnumConstants()[ord]);
                }
                else {
                    assert false : "not supported";
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteByPK(Object memory) {
        Pair<String, Object> pk = readPK(memory);
        return getReadableDatabase().delete(memory.getClass().getSimpleName(), pk.first + " = '" + pk.second + "'", null) > 0;
    }

    private Pair<String, Object> readPK(Object o) {
        for (Field f : o.getClass().getDeclaredFields()) {
            SqlInfo sqlInfo = f.getAnnotation(SqlInfo.class);
            if (sqlInfo == null || !sqlInfo.id()) {
                continue;
            }
            try {
                return Pair.create(f.getName(), f.get(o));
            } catch (IllegalAccessException e) {
                return null;
            }
        }
        return null;
    }
}
