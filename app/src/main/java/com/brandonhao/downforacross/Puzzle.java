package com.brandonhao.downforacross;

import org.json.JSONException;
import org.json.JSONObject;

public class Puzzle {
    //JSON Keys
    //Top Level Keys
    private static final String PID = "pid";
    private static final String CONTENTS = "contents";
    private static final String STATS = "stats";

    public int pid;
    public PuzzleStats stats;
    public PuzzleContents contents;

    public Puzzle(JSONObject jsonObject){
        try{
            pid = jsonObject.getInt(PID);
            contents = new PuzzleContents(jsonObject.getJSONObject(CONTENTS));
            stats = new PuzzleStats(jsonObject.getJSONObject(STATS));
        }
        catch (JSONException e){}
    }
}
