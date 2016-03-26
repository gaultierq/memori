package com.qg.memori.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by q on 27/02/2016.
 */

@DatabaseTable(tableName = "memory")
public class MemoryData extends ModelData  {
    @DatabaseField(id = true)
    public Long id;

    @DatabaseField
    public String question;

    @DatabaseField
    public String answer;

    @DatabaseField
    public String hint;

    @DatabaseField
    public Boolean deleted;
}
