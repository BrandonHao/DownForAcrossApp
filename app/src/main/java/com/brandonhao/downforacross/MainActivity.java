package com.brandonhao.downforacross;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RequestHandler restApi;
    PuzzleList puzzles;

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
            TextView t = new TextView(this);
            t.setText(p.contents.info.title);
            t.setMaxLines(1);
            t.setMinLines(1);
            t.setTextSize(20);
            t.setClickable(true);
            t.setLayoutParams(new LinearLayout.LayoutParams(layoutParams));
            t.setOnClickListener(this::puzzleItemClickAction);
            textViews.add(t);
        }
        return textViews;
    }

    public void PopulatePuzzleList(String searchFilter){
        puzzles.clearPuzzles();
        restApi.setQueryParams(0, 50, searchFilter, true, true);
        Result.Success<String> result = (Result.Success<String>)restApi.getPuzzleList();
        puzzles.addPuzzles(result.data);
        ArrayList<TextView> textViews = createTextViewList();
        for (TextView t : textViews) {
            runOnUiThread(() -> {
                final LinearLayout puzzleList = (LinearLayout) findViewById(R.id.puzzleList);
                puzzleList.addView(t);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restApi = new RequestHandler();
        setContentView(R.layout.activity_main);
        puzzles = new PuzzleList();
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Down For A Cross");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.inflateMenu(R.menu.main_menu);

        final ImageButton button = (ImageButton) findViewById(R.id.searchButton);
        button.setOnClickListener(v -> {
            final EditText textBox = (EditText) findViewById(R.id.searchFilter);
            final String searchFilter = textBox.getText().toString();
            final LinearLayout puzzleList = (LinearLayout) findViewById(R.id.puzzleList);
            puzzleList.removeAllViews();
            new Thread(()-> PopulatePuzzleList(searchFilter)).start();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_main_setting:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}