package com.example.golio.wordcards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorkActivity extends AppCompatActivity {

    private final int WEIGHT_UP = 20;
    private final int WEIGHT_DOWN = 10;

    private List<Word> wordList = new ArrayList<Word>();
    private List<Options> options = new ArrayList<Options>();
    private Word selectedWord;
    private Integer wordNum = 0;
    private Button btnWord;
    private TextView txtTranscription;
    private boolean flip = false;
    private int weightAll = 0;

    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        btnWord = (Button) findViewById(R.id.btnWord);
        txtTranscription = (TextView) findViewById(R.id.txtTranscription);

        db = new MyDatabaseHelper(this);
        db.getWritableDatabase();
        db.createDefaultWordsIfNeed();
        db.createDefaultOptionsIfNeed();

        options = db.getAllOptions();
        int i = options.get(OptionsWordActivity.OPTION_LANGUAGE).getValue();
        wordList = db.getWordsByLanguage(i);
        if (wordList.size() > 0) {
            selectedWord = wordList.get(wordNum);
            changeWord();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error -  No words with selected language", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void btnWordClick(View v){
        if (flip) {
            btnWord.setText(selectedWord.getLex1());
            txtTranscription.setText("");
            flip = false;
        } else {
            btnWord.setText(selectedWord.getLex2());
            txtTranscription.setText(selectedWord.getLex3());
            flip = true;
        }
    }

    public void btnNextTrueClick(View v) {
        nextWord(true);
    }

    public void btnNextFalseClick(View v) {
        nextWord(false);
    }

    private void nextWord(boolean result) {
        changeWeight(result);
        weightAll();
        if (checkOptionBoolValue(OptionsWordActivity.OPTION_RANDOM)) {
            wordNum = randomWordOnWeight();
        } else {
            if (++wordNum >= wordList.size()) {
                wordNum = 0;
            }
        }

        try {
            selectedWord = wordList.get(wordNum);
            changeWord();
        } catch (Exception ex) {
            Log.i("error", ex.toString());
        }
    }

    private boolean checkOptionBoolValue(Integer fieldNum) {
        if (options.get(fieldNum).getValue() == 1) {
            return true;
        } else {
            return false;
        }
    }

    private void changeWord() {
        if (checkOptionBoolValue(OptionsWordActivity.OPTION_SIDE)) {
            btnWord.setText(selectedWord.getLex2());
            txtTranscription.setText(selectedWord.getLex3());
            flip = true;
        } else {
            btnWord.setText(selectedWord.getLex1());
            txtTranscription.setText("");
            flip = false;
        }
    }

    private void changeWeight(boolean result) {
        if (result) {
            selectedWord.setWeight(selectedWord.getWeight()
                    - options.get(OptionsWordActivity.OPTION_STEP).getValue());
            if (selectedWord.getWeight() <= 10) {
                selectedWord.setWeight(10);
            }
        } else {
            selectedWord.setWeight(selectedWord.getWeight()
                    + options.get(OptionsWordActivity.OPTION_STEP).getValue());
        }
        db.updateWord(selectedWord);
    }

    private void weightAll() {
        weightAll = 0;
        for (int i=0; i<wordList.size(); i++) {
            weightAll += wordList.get(i).getWeight();
        }
    }

    private int randomWordOnWeight() {
        Random r = new Random();
        int randomResult = r.nextInt(weightAll);
        int weight = 0;
        int id = -1;
        for (int i=0; i<wordList.size(); i++) {
            weight += wordList.get(i).getWeight();
            if (randomResult <= weight) {
                id = i;
                break;
            }
        }
        return id;
    }
}
