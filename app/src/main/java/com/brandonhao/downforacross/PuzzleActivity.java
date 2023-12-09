package com.brandonhao.downforacross;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.akop.ararat.core.Crossword;
import org.akop.ararat.core.CrosswordState;
import org.akop.ararat.view.CrosswordView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PuzzleActivity extends AppCompatActivity implements CrosswordView.OnLongPressListener, CrosswordView.OnStateChangeListener, CrosswordView.OnSelectionChangeListener {
    Crossword crossword;
    CrosswordView crosswordView;
    TextView hintView;
    CrosswordStateCache cache;

    private void updateHintView(){ //TODO: GSON serialize crossword to recover state
        hintView.setText(crosswordView.getSelectedWord().getHint());
    }

    private void initUi(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Down For A Cross");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.inflateMenu(R.menu.puzzle_menu);

        hintView = findViewById(R.id.hintView);
        hintView.setOnClickListener(this::onHintClick);

        findViewById(R.id.backButton).setOnClickListener(this::onButtonClick);
        findViewById(R.id.forwardButton).setOnClickListener(this::onButtonClick);
    }

    private void initCrossword(Intent intent) throws IOException{
        String jsonString = intent.getStringExtra("puzzle");
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        PuzzleFormatter formatter = new PuzzleFormatter();
        Crossword.Builder builder = new Crossword.Builder();
        formatter.read(builder, stream);
        crossword = builder.build();
    }

    private void initCrosswordView(){
        crosswordView = findViewById(R.id.crossword);
        crosswordView.setCrossword(crossword);
        crosswordView.setOnLongPressListener(this);
        crosswordView.setOnStateChangeListener(this);
        crosswordView.setOnSelectionChangeListener(this);
        crosswordView.setInputValidator(ch -> Character.isLetter(ch.charAt(0)));
        crosswordView.setUndoMode(CrosswordView.UNDO_NONE);
        crosswordView.setMarkerDisplayMode(CrosswordView.MARKER_CHEAT);
        crosswordView.selectWord(Crossword.Word.DIR_ACROSS, 0);
        onSelectionChanged(crosswordView, crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
        updateHintView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        initUi();
        try{
            initCrossword(getIntent());
        }
        catch (IOException e){
            Log.e("PuzzleActivity", e.toString());
            finish();
        }

        initCrosswordView();

        cache = new CrosswordStateCache(this.getApplicationContext());
        if(cache.crosswordCacheExists(crossword.getPid())){
            CrosswordState state = cache.readCrosswordCache(crossword.getPid());
            crosswordView.restoreState(state);
        }
        new Thread(() -> cache.run()).start();
    }

    public void onHintClick(View view){
        crosswordView.switchWordDirection();
    }

    public void onButtonClick(View view){
        Button button = (Button) view;
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(15);
        if(button.getId() == R.id.backButton){
            crosswordView.selectPreviousWord();
        }
        else {
            crosswordView.selectNextWord();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        cache.stopCachingTask();
        finish();
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check_square:
                crosswordView.checkCell(crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
                return true;
            case R.id.check_word:
                crosswordView.checkWord(crosswordView.getSelectedWord());
                return true;
            case R.id.check_puzzle:
                crosswordView.checkCrossword();
                return true;
            case R.id.reveal_square:
                crosswordView.solveChar(crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
                return true;
            case R.id.reveal_word:
                crosswordView.solveWord(crosswordView.getSelectedWord());
                return true;
            case R.id.reveal_puzzle:
                crosswordView.solveCrossword();
                return true;
            case R.id.reset_puzzle:
                crosswordView.reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCellLongPressed(@NonNull CrosswordView crosswordView, @NonNull Crossword.Word word, int i) {

    }

    @Override
    public void onSelectionChanged(@NonNull CrosswordView crosswordView, @Nullable Crossword.Word word, int i) {
        updateHintView();
    }

    @Override
    public void onCrosswordChanged(@NonNull CrosswordView crosswordView) {
        cache.cacheCrossword(crosswordView.getState());
    }

    @Override
    public void onCrosswordSolved(@NonNull CrosswordView crosswordView) {
        Toast.makeText(this, "Puzzle Solved!",
                Toast.LENGTH_SHORT).show();
        cache.stopCachingTask();
    }

    @Override
    public void onCrosswordUnsolved(@NonNull CrosswordView crosswordView) {

    }
}
