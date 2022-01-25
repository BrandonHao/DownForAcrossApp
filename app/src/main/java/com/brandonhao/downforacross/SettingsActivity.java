package com.brandonhao.downforacross;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {
    private static final String[] PAGE_SIZES = {"50", "100", "150", "200", "250"};

    SettingReader settingReader;

    private void updateSettings(){
        settingReader.writeSettings(this.getBaseContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Down For A Cross");
        toolbar.inflateMenu(R.menu.main_menu);

        settingReader = new Gson().fromJson(getIntent().getStringExtra("SettingReader"), SettingReader.class);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PAGE_SIZES);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(arrayAdapter.getPosition(String.valueOf(settingReader.getPageSize())));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i) != null){
                    String value = (String) adapterView.getItemAtPosition(i);
                    settingReader.setPageSize(Integer.parseInt(value));
                    updateSettings();
                }}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }});

        SwitchCompat miniSwitch = findViewById(R.id.miniSwitch);
        miniSwitch.setChecked(settingReader.getAllowMiniPuzzles());
        miniSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            settingReader.setAllowMiniPuzzles(b);
            updateSettings(); });

        SwitchCompat normalSwitch = findViewById(R.id.normalSwitch);
        normalSwitch.setChecked(settingReader.getAllowMiniPuzzles());
        normalSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            settingReader.setAllowNormalPuzzles(b);
            updateSettings(); });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
