package com.qg.memori.data;

import com.qg.memori.QuizzType;

import java.util.Date;

/**
 * Created by q on 29/02/2016.
 */
public class Quizz extends ModelData {
    @SqlInfo(id = true)
    public int id;

    @SqlInfo
    public int memoryId;

    @SqlInfo
    public QuizzType type;

    public Date dueDate;

    public int score; // 0-10
}
