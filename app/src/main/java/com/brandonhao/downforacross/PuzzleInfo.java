package com.brandonhao.downforacross;

import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleInfo {
    //Info keys
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String AUTHOR = "author";
    private static final String DESCRIPTION = "description";

    public String name;
    public String type;
    public String author;
    public String description;

    public PuzzleInfo(JSONObject jsonInfo){
        try{
            name = jsonInfo.getString(NAME);
            type = jsonInfo.getString(TYPE);
            author = jsonInfo.getString(AUTHOR);
            description = jsonInfo.getString(DESCRIPTION);
        }
        catch (JSONException e){}
    }
}
