package com.brandonhao.downforacross;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PuzzleGrid {
    private ArrayList<String> grid;

    public PuzzleGrid(JSONArray jsonGrid){
        grid = new ArrayList<>();

        try{
            for(int i = 0; i < jsonGrid.length(); i++){
                grid.add(jsonGrid.getString(i));
            }
        }
        catch (JSONException e){}
    }
}
