package com.brandonhao.downforacross;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PuzzleList {
    private ArrayList<Puzzle> puzzles;

    public PuzzleList(String jsonPuzzleList){
        puzzles = new ArrayList<>();
        try {
            JSONObject puzzleList = new JSONObject(jsonPuzzleList);
            JSONArray jsonPuzzleArray = puzzleList.getJSONArray("puzzles");
            for(int i = 0; i < jsonPuzzleArray.length(); i++){
                puzzles.add(new Puzzle(jsonPuzzleArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Puzzle getPuzzle(int index){
        Puzzle puzzle = null;
        if(index >= 0 && index < puzzles.size()){
            puzzle = puzzles.get(index);
        }
        return puzzle;
    }

    public int getPuzzleCount(){
        return puzzles.size();
    }
}
