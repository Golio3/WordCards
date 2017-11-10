package com.example.golio.wordcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_main_toolbar);
        setSupportActionBar(myToolbar);*/
    }

    public void openDictionary(View v){
        Intent intent = new Intent(this, DictionaryActivity.class);
        startActivity(intent);
    }

    public void openOptions(View v){
        Intent intent = new Intent(this, OptionsWordActivity.class);
        startActivity(intent);
    }

    public void openWork(View v){
        Intent intent = new Intent(this, WorkActivity.class);
        startActivity(intent);
    }
}


