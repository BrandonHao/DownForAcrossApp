package com.brandonhao.downforacross;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleInfo {
    //Info keys
    private static final String TITLE = "title";
    private static final String TYPE = "type";
    private static final String AUTHOR = "author";
    private static final String DESCRIPTION = "description";

    public String title;
    public String type;
    public String author;
    public String description;

    public PuzzleInfo(JSONObject jsonInfo){
        try{
            title = jsonInfo.getString(TITLE);
            type = jsonInfo.getString(TYPE);
            author = jsonInfo.getString(AUTHOR);
            description = jsonInfo.getString(DESCRIPTION);
        }
        catch (JSONException e){
            Log.e("PuzzleInfo", e.toString());
        }
    }
}
