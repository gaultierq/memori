package com.qg.memori.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by q on 27/02/2016.
 */


/*
    Db structure

    memoryByUserUid {
        user_toto: [
            memory1: {
                id: azerty,
                question,
                ...
                pendingQuizz {

                }
            }
        ]
    },
    oldQuizzByMemoryUid {
        azerty: [
            quizz1: {
            },
            ...
            ]
    }

 */
public class DbHelper /*extends OrmLiteSqliteOpenHelper*/ {

    public static final String NODE_MEMORY_BY_USER_UID = "memoryByUserUid";
    public static final String NODE_OLD_QUIZZ_BY_MEMORY_UID = "oldQuizzByMemoryUid";

    public static void updateMemory(MemoryData m) {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(NODE_MEMORY_BY_USER_UID).child(u.getUid()).child(m.id).setValue(m);
    }

/*
    private static final int DATABASE_VERSION = DbVersion.last();
    public static final String DB_NAME = "memory.db";

    private Map<Class, Dao> daos = new HashMap<>();

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.i(DbHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, QuizzData.class);
            TableUtils.createTable(connectionSource, MemoryData.class);
        } catch (SQLException e) {
            Log.e(DbHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        for (int v = oldVersion + 1; v <= newVersion; v++) {
            DbVersion dbVersion = DbVersion.valueOf("V_" + v);
            try {
                migrateDb(db, connectionSource, dbVersion);
            } catch (SQLException e) {
                throw new RuntimeException("Db upgrade has failed.", e);
            }
        }
    }

    private void migrateDb(SQLiteDatabase db, ConnectionSource connectionSource, DbVersion dbVersion) throws SQLException {
        switch (dbVersion) {
            case V_1:
                throw  new RuntimeException("Cannot upgrade to V1");
            case V_2:
                obtainDao(MemoryData.class).executeRaw("alter table `memory` add column type varchar default 'NONE';");
                break;
            case V_3:
                obtainDao(MemoryData.class).executeRaw("alter table `memory` add column acquired TINYINT(2) default 0;");
                break;
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
*/
}
