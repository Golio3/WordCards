package com.example.golio.wordcards;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddEditWordActivity extends AppCompatActivity {

    Word word;
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;
    private static final Integer STANDART_WEIGHT = 1000;

    private int mode;
    private EditText lex1;
    private EditText lex2;
    private EditText lex3;
    private EditText weight;
    private Spinner language;
    private int languageNum;

    private boolean needRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_word);

        this.lex1 = (EditText)this.findViewById(R.id.editTextLex1);
        this.lex2 = (EditText)this.findViewById(R.id.editTextLex2);
        this.lex3 = (EditText)this.findViewById(R.id.editTextLex3);
        this.weight = (EditText)this.findViewById(R.id.editTextWeight);
        languageComboBox();

        Intent intent = this.getIntent();
        this.word = (Word) intent.getSerializableExtra("word");
        if(word== null)  {
            this.mode = MODE_CREATE;
            this.weight.setText(STANDART_WEIGHT.toString());
            this.language.setSelection(0);
            this.languageNum = 0;
        } else  {
            this.mode = MODE_EDIT;
            this.lex1.setText(word.getLex1());
            this.lex2.setText(word.getLex2());
            this.lex3.setText(word.getLex3());
            this.weight.setText(word.getWeight().toString());
            this.language.setSelection(word.getLanguage());
            this.languageNum = word.getLanguage();
        }
    }

    private void languageComboBox() {
        this.language = (Spinner) findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.language.setAdapter(adapter);
        //s.setPrompt("Title");
        this.language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                languageNum = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    // User Click on the Save button.
    public void buttonSaveClicked(View view)  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        String wordLex = this.lex1.getText().toString();
        String translation = this.lex2.getText().toString();
        String transcription = this.lex3.getText().toString();
        String weightNum  = this.weight.getText().toString();

        if(wordLex.equals("") || translation.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please enter word and translation", Toast.LENGTH_LONG).show();
            return;
        }

        if(Integer.parseInt(weightNum) < 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter positive weight value", Toast.LENGTH_LONG).show();
            return;
        }

        if(mode==MODE_CREATE ) {
            this.word = new Word(wordLex, translation, transcription, languageNum, STANDART_WEIGHT);
            db.addWord(this.word);
        } else  {
            this.word.setLex1(wordLex);
            this.word.setLex2(translation);
            this.word.setLex3(transcription);
            this.word.setLanguage(languageNum);
            this.word.setWeight(Integer.parseInt(weightNum));
            db.updateWord(this.word);
        }

        this.needRefresh = true;

        // Back to MainActivity.
        this.onBackPressed();
    }

    // User Click on the Cancel button.
    public void buttonCancelClicked(View view)  {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    // When completed this Activity,
    // Send feedback to the Activity called it.
    @Override
    public void finish() {

        // Create Intent
        Intent data = new Intent();

        // Request MainActivity refresh its ListView (or not).
        data.putExtra("needRefresh", needRefresh);

        // Set Result
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }
}
