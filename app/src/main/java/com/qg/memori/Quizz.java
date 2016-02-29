package com.qg.memori;

import java.util.Date;

/**
 * Created by q on 29/02/2016.
 */
public class Quizz {
    @SqlInfo(id = true)
    int id;

    @SqlInfo
    int memoryId;

    @SqlInfo
    QuizzType type;

    Date dueDate;

    int score; // 0-10
}
