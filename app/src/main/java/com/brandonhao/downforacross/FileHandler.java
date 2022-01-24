package com.brandonhao.downforacross;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {
    Context context;

    public FileHandler(Context context){
        this.context = context;
    }

    private void writeToCache(String str, String fileName) throws IOException {
        File file = File.createTempFile(fileName, null, context.getCacheDir());
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str);
        bw.close();
    }

    @SuppressLint("SimpleDateFormat")
    private void writePuzzleListSync(PuzzleList puzzleList) {
        StringBuilder str = new StringBuilder();
        try{
            for(Puzzle p : puzzleList.getPuzzles()){
                str.append(p.jsonString);
            }
            String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            writeToCache(str.toString(), fileName);
        }
        catch (IOException e){
            Log.d("FileHandler", e.toString());
        }
    }

    public void writePuzzleList(PuzzleList puzzleList){
        new Thread(() ->{
           writePuzzleListSync(puzzleList);
        });
    }
}
