package com.qg.memori.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qg.memori.QuizzType;

import java.util.Date;
import java.util.List;

/**
 * Created by q on 29/02/2016.
 */
@DatabaseTable(tableName = "quizz")
public class QuizzData extends ModelData {
    @DatabaseField(generatedId = true)
    public long id;

    @DatabaseField
    public long memoryId;

    public MemoryData memory;

    @DatabaseField
    public QuizzType type = QuizzType.TYPE_YOUR_ANSWER;

    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date dueDate;

    @DatabaseField
    public Integer score; // 0-10


    public static int countOnScore(List<QuizzData> quizzes, Integer score) {
        int goodAnswer = 0;
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).score == score) {
                goodAnswer++;
            }
        }
        return goodAnswer;
    }
}
