package com.qg.memori.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qg.memori.QuizzType;

import java.util.Date;

/**
 * Created by q on 29/02/2016.
 */
@DatabaseTable(tableName = "quizz")
public class QuizzData extends ModelData {
    @DatabaseField(id = true)
    public Long id;

    @DatabaseField
    public Long memoryId;

    public MemoryData memory;

    @DatabaseField
    public QuizzType type = QuizzType.TYPE_YOUR_ANSWER;

    @DatabaseField
    public Date dueDate;

    @DatabaseField
    public Integer score; // 0-10
}
