package com.brandonhao.downforacross;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RequestHandler restApi;
    PuzzleList puzzles;
    FileHandler fileHandler;
    CrosswordStateCache cache;
    SettingReader settingReader;

    private void puzzleItemClickAction(View view){
        TextView sourceView = (TextView) view;
        Puzzle puzzle = puzzles.getPuzzle(sourceView.getText().toString());

        Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
        intent.putExtra("puzzle", puzzle.jsonString);
        MainActivity.this.startActivity(intent);
    }

    private ArrayList<TextView> createTextViewList(){
        ArrayList<TextView> textViews = new ArrayList<>();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(Puzzle p : puzzles.getPuzzles()){
            TextView t = new TextView(this.getApplicationContext());
            t.setText(p.contents.info.title);
            t.setMaxLines(1);
            t.setMinLines(1);
            t.setTextSize(20);
            t.setClickable(true);
            t.setLayoutParams(new LinearLayout.LayoutParams(layoutParams));
            t.setOnClickListener(this::puzzleItemClickAction);
            if(cache.crosswordCacheExists(p.pid)){
                t.setTextColor(getResources().getColor(R.color.purple_700));
            }
            else{
                t.setTextColor(getResources().getColor(R.color.white));
            }
            textViews.add(t);
        }
        return textViews;
    }

    public void PopulatePuzzleList(String searchFilter, boolean newQuery){
        runOnUiThread(() -> findViewById(R.id.progressBar).setVisibility(View.VISIBLE));
        restApi.threadBusy.set(true);
        if(newQuery) {
            puzzles.clearPuzzles();
            settingReader.readSettings(this.getBaseContext());
            restApi.setQueryParams(
                    0,
                    settingReader.getPageSize(),
                    searchFilter,
                    settingReader.getAllowMiniPuzzles(),
                    settingReader.getAllowNormalPuzzles());
        }
        else {
            restApi.nextPageQuery();
        }

        Result.Success<String> result = (Result.Success<String>)restApi.getPuzzleList();
        puzzles.addPuzzles(result.data);
        ArrayList<TextView> textViews = createTextViewList();
        runOnUiThread(() -> {
            final LinearLayout puzzleList = findViewById(R.id.puzzleList);
            for (TextView t : textViews) {
                puzzleList.addView(t);
            }
        });
        runOnUiThread(() -> findViewById(R.id.progressBar).setVisibility(View.INVISIBLE));
        restApi.threadBusy.set(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restApi = new RequestHandler();
        setContentView(R.layout.activity_main);
        puzzles = new PuzzleList();
        cache = new CrosswordStateCache(this);
        fileHandler = new FileHandler(getBaseContext());
        settingReader = new SettingReader();

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Down For A Cross");
        toolbar.inflateMenu(R.menu.main_menu);

        final ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getChildAt(0).getBottom()
                    <= (scrollView.getHeight() + scrollView.getScrollY())) {
                if(!restApi.threadBusy.get()){
                new Thread(() -> PopulatePuzzleList("", false)).start(); }}});

        final ImageButton button = findViewById(R.id.searchButton);
        button.setOnClickListener(v -> {
            final EditText textBox = findViewById(R.id.searchFilter);
            final String searchFilter = textBox.getText().toString();
            final LinearLayout puzzleList = findViewById(R.id.puzzleList);

            puzzleList.removeAllViews();
            new Thread(()-> PopulatePuzzleList(searchFilter, true)).start();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void startSettingsActivity(){
        settingReader.readSettings(this.getBaseContext());
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("SettingReader", new Gson().toJson(settingReader));
        MainActivity.this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startSettingsActivity();
        return true;
    }
}