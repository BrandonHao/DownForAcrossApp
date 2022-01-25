package com.brandonhao.downforacross;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SettingReader {
    private static final String SETTING_FILE_PATH = "/settings";

    private Settings settings;
    public SettingReader(){
        settings = new Settings();
    }

    public boolean readSettings(Context context){
        File settingsFile = new File(context.getFilesDir(), SETTING_FILE_PATH);

        if(settingsFile.exists() && settingsFile.canRead()){
            try{
                FileInputStream stream = new FileInputStream(settingsFile);
                BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
                settings = new Gson().fromJson(buff, Settings.class);

                return true;
            } catch (FileNotFoundException e) {
                Log.e("SettingsReader", e.toString());
            }
        }
        return false;
    }

    public boolean writeSettings(Context context){
        File settingsFile = new File(context.getFilesDir(), SETTING_FILE_PATH);
        File temp = new File(context.getFilesDir(), SETTING_FILE_PATH + ".temp");
        if(settingsFile.exists()){
            settingsFile.renameTo(temp);
            settingsFile.delete();
        }
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(settings);
            FileOutputStream stream = new FileOutputStream(settingsFile);
            stream.write(jsonString.getBytes(StandardCharsets.UTF_8));
            temp.delete();
            return true;
        } catch (IOException e) {
            Log.e("SettingsReader", e.toString());
            if(temp.exists()){
                temp.renameTo(settingsFile);
                temp.delete();
            }
        }
        return false;
    }

    public int getPageSize(){ return settings.pageSize; }
    public boolean getAllowMiniPuzzles(){ return settings.allowMiniPuzzles; }
    public boolean getAllowNormalPuzzles(){ return settings.allowNormalPuzzles; }


    public void setPageSize(int pageSize){ settings.pageSize = pageSize; }

    public void setAllowMiniPuzzles(boolean allowMiniPuzzles){
        settings.allowMiniPuzzles = allowMiniPuzzles;
    }

    public void setAllowNormalPuzzles(boolean allowNormalPuzzles){
        settings.allowNormalPuzzles = allowNormalPuzzles;
    }

    private static final class Settings{
        public int pageSize;
        public boolean allowMiniPuzzles;
        public boolean allowNormalPuzzles;

        public Settings(){
            pageSize = 50;
            allowMiniPuzzles = true;
            allowNormalPuzzles = true;
        }

        public Settings(int pageSize, boolean allowMiniPuzzles, boolean allowNormalPuzzles){
            this.pageSize = pageSize;
            this.allowMiniPuzzles = allowMiniPuzzles;
            this.allowNormalPuzzles = allowNormalPuzzles;
        }
    }
}
