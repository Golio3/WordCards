package com.example.golio.wordcards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OptionsWordActivity extends AppCompatActivity {

    private List<Options> options = new ArrayList<Options>();

    private static final int ENGLISH = 0;
    private static final int CZECH = 1;
    public static final int OPTION_RANDOM = 0;
    public static final int OPTION_SIDE = 1;
    public static final int OPTION_LANGUAGE = 2;
    public static final int OPTION_STEP = 3;

    private CheckBox cbRandom;
    private CheckBox cbSise;
    private RadioButton rbEnglish;
    private RadioButton rbCzech;
    private EditText editTextStep;

    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        db = new MyDatabaseHelper(this);
        db.getWritableDatabase();
        db.createDefaultOptionsIfNeed();

        options = db.getAllOptions();

        cbRandom = (CheckBox)findViewById(R.id.cbRandom);
        cbRandom.setChecked(checkRandom());

        cbSise = (CheckBox)findViewById(R.id.cbSide);
        cbSise.setChecked(checkSide());

        rbEnglish = (RadioButton)findViewById(R.id.rbEnglish);
        rbCzech = (RadioButton)findViewById(R.id.rbCzech);
        switch (options.get(OPTION_LANGUAGE).getValue())  {
            case ENGLISH: {
                rbEnglish.setChecked(true);
                rbCzech.setChecked(false);
                break;
            }
            case CZECH: {
                rbEnglish.setChecked(false);
                rbCzech.setChecked(true);
                break;
            }
        }

        editTextStep = (EditText)findViewById(R.id.editTextStep);
        editTextStep.setText(options.get(OPTION_STEP).getValue().toString());
    }

    public void buttonSaveClicked(View view)  {
        if(options.get(OPTION_STEP).getValue() < 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter positive step value", Toast.LENGTH_LONG).show();
            return;
        }

        if (cbRandom.isChecked()) {
            options.get(OPTION_RANDOM).setValue(1);
        } else {
            options.get(OPTION_RANDOM).setValue(0);
        }
        if (cbSise.isChecked()) {
            options.get(OPTION_SIDE).setValue(1);
        } else {
            options.get(OPTION_SIDE).setValue(0);
        }
        if (rbEnglish.isChecked()) {
            options.get(OPTION_LANGUAGE).setValue(ENGLISH);
        } else if (rbCzech.isChecked()) {
            options.get(OPTION_LANGUAGE).setValue(CZECH);
        }
        int step = Integer.decode(editTextStep.getText().toString());
        options.get(OPTION_STEP).setValue(step);
        db.updateOptions(options);

        // Back to MainActivity.
        this.onBackPressed();
    }

    public void buttonCancelClicked(View view)  {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    private boolean checkRandom() {
        if (options.get(OPTION_RANDOM).getValue() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSide() {
        if (options.get(OPTION_SIDE).getValue() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
