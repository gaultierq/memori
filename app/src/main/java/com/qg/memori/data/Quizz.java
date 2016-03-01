package com.qg.memori.data;

import com.qg.memori.QuizzType;

import java.util.Date;

/**
 * Created by q on 29/02/2016.
 */
public class Quizz extends ModelData {
    @SqlInfo(id = true)
    public long id;

    @SqlInfo
    public long memoryId;

    @SqlInfo
    public QuizzType type = QuizzType.TYPE_YOUR_ANSWER;

    public Date dueDate;

    public int score; // 0-10
}
