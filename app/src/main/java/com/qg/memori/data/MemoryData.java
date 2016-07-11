package com.qg.memori.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

/**
 * Created by q on 27/02/2016.
 */

public class MemoryData extends ModelData  {

    public String id;

    public String question;

    public String answer;

    public String hint;

    public Boolean deleted;

    public QuizzData nextQuizz;

    @Exclude
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
