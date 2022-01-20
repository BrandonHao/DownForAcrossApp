package com.brandonhao.downforacross;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PuzzleClues {
    //Clues Keys
    private static final String DOWN = "down";
    private static final String ACROSS = "across";

    public ArrayList<String> downClues;
    public ArrayList<String> acrossClues;

    public PuzzleClues(JSONObject jsonClues){
        downClues = new ArrayList<>();
        acrossClues = new ArrayList<>();

        try{
            JSONArray jsonDownClues = jsonClues.getJSONArray(DOWN);
            JSONArray jsonAcrossClues = jsonClues.getJSONArray(ACROSS);

            for(int i = 0; i < jsonDownClues.length(); i++){
                downClues.add(jsonDownClues.getString(i));
            }
            for(int i = 0; i < jsonAcrossClues.length(); i++){
                acrossClues.add(jsonAcrossClues.getString(i));
            }
        }
        catch(JSONException e){}

    }
}
