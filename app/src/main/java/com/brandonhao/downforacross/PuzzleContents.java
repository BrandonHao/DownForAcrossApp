package com.brandonhao.downforacross;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PuzzleContents {
    //Contents Keys
    private static final String GRID = "grid";
    private static final String INFO = "info";
    private static final String CLUES = "clues";
    private static final String SHADES = "shades";
    private static final String CIRCLES = "circles";
    private static final String IS_PRIVATE = "private";

    public PuzzleGrid grid;
    public PuzzleInfo info;
    public PuzzleClues clues;
    public ArrayList<Integer> shades;
    public ArrayList<Integer> circles;
    public boolean isPrivate;

    public PuzzleContents (JSONObject jsonContents){
        shades = new ArrayList<>();
        circles = new ArrayList<>();

        try{
            grid = new PuzzleGrid(jsonContents.getJSONArray(GRID));
            info = new PuzzleInfo(jsonContents.getJSONObject(INFO));
            clues = new PuzzleClues(jsonContents.getJSONObject(CLUES));

            JSONArray jsonShades = jsonContents.getJSONArray(SHADES);
            for(int i = 0; i < jsonShades.length(); i++){
                shades.add(jsonShades.getInt(i));
            }

            JSONArray jsonCircles = jsonContents.getJSONArray(CIRCLES);
            for(int i = 0; i < jsonCircles.length(); i++){
                circles.add(jsonCircles.getInt(i));
            }

            isPrivate = jsonContents.getBoolean(IS_PRIVATE);
        }
        catch (JSONException e){}
    }
}
