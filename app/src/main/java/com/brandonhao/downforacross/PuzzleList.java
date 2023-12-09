package com.brandonhao.downforacross;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PuzzleList {
    private ArrayList<Puzzle> puzzles;



    public PuzzleList(){
        puzzles = new ArrayList<>();
    }

    public void addPuzzles(String jsonPuzzleList){
        try {
            JSONObject puzzleList = new JSONObject(jsonPuzzleList);
            JSONArray jsonPuzzleArray = puzzleList.getJSONArray("puzzles");
            for(int i = 0; i < jsonPuzzleArray.length(); i++){
                JSONObject jsonPuzzle = jsonPuzzleArray.getJSONObject(i);
                Log.d("Puzzle Parse", jsonPuzzle.getString("pid"));
                puzzles.add(new Puzzle(jsonPuzzle));
            }
        }
        catch (Exception e){
            Log.e("PuzzleList", e.toString());
        }
    }

    public Puzzle getPuzzle(int index){
        Puzzle puzzle = null;
        if(index >= 0 && index < puzzles.size()){
            puzzle = puzzles.get(index);
        }
        return puzzle;
    }

    public Puzzle getPuzzle(String title){
        Puzzle puzzle = null;
        for(Puzzle p : puzzles){
            if(p.contents.info.title.equals(title)){
                puzzle = p;
                break;
            }
        }
        return puzzle;
    }

    public InputStream getPuzzleInputStream(String title){
        InputStream stream = null;
        for(Puzzle p : puzzles){
            if(p.contents.info.title.equals(title)){
                stream = new ByteArrayInputStream(p.jsonString.getBytes(StandardCharsets.UTF_8));
                break;
            }
        }

        return stream;
    }

    public ArrayList<Puzzle> getPuzzles(){
        return puzzles;
    }

    public void clearPuzzles(){
        puzzles.clear();
    }

    public int getPuzzleCount(){
        return puzzles.size();
    }
}
