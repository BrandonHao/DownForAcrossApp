package com.brandonhao.downforacross;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.akop.ararat.core.CrosswordState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrosswordStateCache implements Runnable{
    private static final String CACHE_DIR = "/game_cache/";

    private Context context;
    private final Stack<CrosswordState> buffer;
    private AtomicBoolean runCachingTask;

    public CrosswordStateCache(Context context){
        this.context = context;
        buffer = new Stack<>();
        runCachingTask = new AtomicBoolean(false);
        cacheInit();
    }

    private void cacheInit(){
        File cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
    }

    private boolean cacheCrosswordTask(CrosswordState crossword) {
        int pid = crossword.getPid();
        boolean cacheSuccess = true;
        File file = new File(context.getCacheDir(), CACHE_DIR + pid);
        File temp = new File(context.getCacheDir(), CACHE_DIR + pid + ".temp");

        //If the file already exists, create a temp copy in case caching fails
        if(file.exists()){
            file.renameTo(temp);
            file.delete();
        }

        //Serialize objects
        Gson gson = new Gson();
        String gsonString = gson.toJson(crossword);

        try{
            FileOutputStream outStream = new FileOutputStream(file);
            //Write the cache
            outStream.write(gsonString.getBytes(StandardCharsets.UTF_8));
            //Delete the temp if any
            if(temp.exists()){
                temp.delete();
            }
        }
        catch (IOException e){
            Log.e("CrosswordCache", "Failed to write crossword state");
            Log.e("CrosswordCache", e.toString());
            //If the write failed, attempt to restore the temp copy
            if(temp.exists()){
                temp.renameTo(file);
                temp.delete();
                cacheSuccess = false;
            }
        }
        return cacheSuccess;
    }

    public void cacheCrossword(CrosswordState crossword){
        synchronized (buffer){
            buffer.push(crossword);
        }
    }

    public CrosswordState readCrosswordCache(int pid){
        File file = new File(context.getCacheDir(), CACHE_DIR + pid);
        CrosswordState crossword = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
            Gson gson = new Gson();
            crossword = gson.fromJson(buff, CrosswordState.class);
        } catch (FileNotFoundException e) {
            Log.e("CrosswordCache", e.toString());
        }
        return crossword;
    }

    public boolean crosswordCacheExists(int pid){
        return new File(context.getCacheDir(), CACHE_DIR + pid).exists();
    }

    public void stopCachingTask(){
        runCachingTask.set(false);
    }

    @Override
    public void run() {
        runCachingTask.set(true);
        while(runCachingTask.get()){
            //Wait for something to cache
            while(buffer.isEmpty()){ try { Thread.sleep(50); }
                catch (InterruptedException e) { e.printStackTrace(); }}
            //If there's data in the buffer, get the latest one
            CrosswordState crossword;
            synchronized (buffer){
                crossword = buffer.pop();
            }
            //Write it to file, and if success clear the older data
            if(cacheCrosswordTask(crossword)){
                synchronized (buffer){
                    buffer.clear();
                }
            }
        }
    }
}
