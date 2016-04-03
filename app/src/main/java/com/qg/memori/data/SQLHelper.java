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

/**
 * Created by q on 27/02/2016.
 */

public class SQLHelper extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DB_NAME = "memory.db";

    private Dao<QuizzData, Long> quizzDao;
    private Dao<MemoryData, Long> memoryDao;


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

    public Dao<QuizzData, Long> getQuizzDao() throws SQLException {
        if (quizzDao == null) {
            quizzDao = getDao(QuizzData.class);
        }
        return quizzDao;
    }

    public Dao<MemoryData, Long> getMemoryDao() throws SQLException {
        if (memoryDao == null) {
            memoryDao = getDao(MemoryData.class);
        }
        return memoryDao;
    }


    public static void drop(Context context) {
        boolean res = context.deleteDatabase(DB_NAME);
        Assert.assertTrue(res);
    }
}
