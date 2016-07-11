package com.qg.memori.data;

import com.google.firebase.database.Exclude;
import com.qg.memori.QuizzType;

import java.util.List;
import java.util.Map;

/**
 * Created by q on 29/02/2016.
 */
public class QuizzData extends ModelData {

    public String id;

    public String memoryId;

    @Exclude
    public MemoryData memory;

    @Exclude
    public QuizzType type = QuizzType.TYPE_YOUR_ANSWER;

    public Long dueDate;

    public Integer score; // 0-10

    //the answer given by the user for this quizz
    @Exclude
    private String answer;

    public static int countOnScore(List<QuizzData> quizzes, Integer score) {
        int goodAnswer = 0;
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).score == score) {
                goodAnswer++;
            }
        }
        return goodAnswer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public Map<String, Object> toMap() {
        return DataHelper.introspect(this);
    }

}
