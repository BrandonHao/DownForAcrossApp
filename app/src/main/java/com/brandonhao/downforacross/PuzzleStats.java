package com.brandonhao.downforacross;

import org.json.JSONException;
import org.json.JSONObject;

public class PuzzleStats {
    //Stats keys
    private static final String NUM_SOLVES = "numSolves";

    public int numSolves;

    public PuzzleStats(JSONObject jsonStats){
        try {
            numSolves = jsonStats.getInt(NUM_SOLVES);
        }
        catch (JSONException e){}
    }
}
