package com.qg.memori.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import junit.framework.Assert;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by q on 27/02/2016.
 */

public class SQLHelper extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DB_NAME = "memory.db";

    private Dao<QuizzData, Long> quizzDao;
    private Dao<MemoryData, Long> memoryDao;

    private Map<Class, Dao> daos = new HashMap<>();


    public SQLHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.i(SQLHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, QuizzData.class);
            TableUtils.createTable(connectionSource, MemoryData.class);
        } catch (SQLException e) {
            Log.e(SQLHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(SQLHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, QuizzData.class, true);
            TableUtils.dropTable(connectionSource, MemoryData.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(SQLHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }


    public <T> Dao<T, Long> obtainDao(Class<T> c) {
        if (!daos.containsKey(c)) {
            try {
                daos.put(c, super.getDao(c));
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return daos.get(c);
    }


    public static void drop(Context context) {
        boolean res = context.deleteDatabase(DB_NAME);
        Assert.assertTrue(res);
    }

    public static <T extends ModelData> void safeInsert(Context context, T item) {
        SQLHelper sql = new SQLHelper(context);
        try {

            int cr = sql.<T>obtainDao((Class<T>) item.getClass()).create(item);
            if (cr != 1) {
                throw new AssertionError("insertion has failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
