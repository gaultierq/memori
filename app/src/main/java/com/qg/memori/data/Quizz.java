package com.qg.memori.data;

import com.qg.memori.QuizzType;

import java.util.Date;

/**
 * Created by q on 29/02/2016.
 */
public class Quizz extends ModelData {
    @SqlInfo(id = true)
    public Long id;

    @SqlInfo
    public Long memoryId;

    @SqlInfo
    public QuizzType type = QuizzType.TYPE_YOUR_ANSWER;

    @SqlInfo
    public Date dueDate;

    public Integer score; // 0-10
}
