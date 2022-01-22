package com.brandonhao.downforacross;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.akop.ararat.core.Crossword;
import org.akop.ararat.core.CrosswordWriter;
import org.akop.ararat.view.CrosswordView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PuzzleActivity extends AppCompatActivity implements CrosswordView.OnLongPressListener, CrosswordView.OnStateChangeListener, CrosswordView.OnSelectionChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("puzzle");
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        PuzzleFormatter formatter = new PuzzleFormatter();
        Crossword.Builder builder = new Crossword.Builder();
        try{
            formatter.read(builder, stream);
        }
        catch (IOException e){
            Log.e("PuzzleActivity", e.toString());
        }

        Crossword crossword = builder.build();
        CrosswordView crosswordView = (CrosswordView) findViewById(R.id.crossword);
        crosswordView.setCrossword(crossword);
        onSelectionChanged(crosswordView, crosswordView.getSelectedWord(), crosswordView.getSelectedCell());
    }

    @Override
    public void onCellLongPressed(@NonNull CrosswordView crosswordView, @NonNull Crossword.Word word, int i) {

    }

    @Override
    public void onSelectionChanged(@NonNull CrosswordView crosswordView, @Nullable Crossword.Word word, int i) {
        
    }

    @Override
    public void onCrosswordChanged(@NonNull CrosswordView crosswordView) {

    }

    @Override
    public void onCrosswordSolved(@NonNull CrosswordView crosswordView) {
        Toast.makeText(this, "Puzzle Solved!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCrosswordUnsolved(@NonNull CrosswordView crosswordView) {

    }
}
