package com.qg.memori.data;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by q on 27/02/2016.
 */

@DatabaseTable(tableName = "memory")
public class MemoryData extends ModelData  {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    public String question;

    @DatabaseField
    public String answer;

    @DatabaseField
    public String hint;

    @DatabaseField
    public Boolean deleted;

    @DatabaseField
    public Boolean acquired;

    @DatabaseField
    public MemoryType type = MemoryType.NONE;

    @NonNull
    public static MemoryData create(String question, String answer) {
        MemoryData m = new MemoryData();
        m.question = question;
        m.answer = answer;
        m.hint = null;
        m.deleted = false;
        return m;
    }
}
