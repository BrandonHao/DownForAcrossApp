package com.brandonhao.downforacross;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Puzzle {
    //JSON Keys
    //Top Level Keys
    private static final String PID = "pid";
    private static final String CONTENTS = "content";
    private static final String STATS = "stats";

    public String jsonString;
    public int pid;
    public PuzzleStats stats;
    public PuzzleContents contents;

    public Puzzle(JSONObject jsonObject){
        try{
            jsonString = jsonObject.toString();
            pid = jsonObject.getInt(PID);
            contents = new PuzzleContents(jsonObject.getJSONObject(CONTENTS));
            stats = new PuzzleStats(jsonObject.getJSONObject(STATS));
        }
        catch (JSONException e){
            Log.e("Puzzle", e.toString());
        }
    }

    public String toString() {
        return jsonString;
    }
}
