package com.qg.memori.data;

/**
 * Created by q on 27/02/2016.
 */


public class Memory extends ModelData  {
    @SqlInfo(id = true)
    public int id;

    @SqlInfo
    public String question;
    @SqlInfo
    public String answer;
    @SqlInfo
    public String hint;
}
