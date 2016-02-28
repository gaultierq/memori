package com.qg.memori;

/**
 * Created by q on 27/02/2016.
 */


public class Memory {
    @SqlInfo(id = true)
    int id;

    @SqlInfo
    String question;
    @SqlInfo
    String answer;
    @SqlInfo
    String hint;
}
