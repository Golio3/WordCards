package com.example.golio.wordcards;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DictionaryActivity extends AppCompatActivity {

    private static final int MENU_ITEM_EDIT = 222;
    private static final int MENU_ITEM_CREATE = 333;
    private static final int MENU_ITEM_DELETE = 444;
    private static final int MY_REQUEST_CODE = 1000;

    private SimpleCursorAdapter listViewAdapter;
    ListView listDict;
    Cursor cursor;
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        listDict = (ListView) findViewById(R.id.listViewDictionary);

        db = new MyDatabaseHelper(this);
        db.getWritableDatabase();
        db.createDefaultWordsIfNeed();
        db.createDefaultOptionsIfNeed();
        cursor = db.getAllWordsAsCursor();
        startManagingCursor(cursor);

        String[] from = new String[] {MyDatabaseHelper.COLUMN_DICT_LEX1, MyDatabaseHelper.COLUMN_DICT_LEX2};
        int[] to = new int[] { R.id.tvLex1, R.id.tvLex2 };

        this.listViewAdapter = new SimpleCursorAdapter(this,
                R.layout.dictionary_view_adapter, cursor, from, to);

        // Assign adapter to ListView
        this.listDict.setAdapter(this.listViewAdapter);

        // Register the ListView for Context menu
        registerForContextMenu(this.listDict);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo)    {

        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Select The Action");

        // groupId, itemId, order, title
        //menu.add(0, MENU_ITEM_CREATE , 0, "Create Word");
        menu.add(0, MENU_ITEM_EDIT , 0, "Edit Word");
        menu.add(0, MENU_ITEM_DELETE, 1, "Delete Word");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor cursor = (Cursor) this.listDict.getItemAtPosition(acmi.position);
        final Word selectedWord =  db.cursorToWord(cursor);

        if(item.getItemId() == MENU_ITEM_EDIT ){
            Intent intent = new Intent(this, AddEditWordActivity.class);
            intent.putExtra("word", selectedWord);

            // Start AddEditNoteActivity, (with feedback).
            this.startActivityForResult(intent,MY_REQUEST_CODE);
        }
        else if(item.getItemId() == MENU_ITEM_DELETE){
            // Ask before deleting.
            new AlertDialog.Builder(this)
                    .setMessage(selectedWord.getLex1() +". Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteWord(selectedWord);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else {
            return false;
        }
        return true;
    }

    public void btnItemCreate(View v) {
        Intent intent = new Intent(this, AddEditWordActivity.class);
        this.startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // Delete a record
    private void deleteWord(Word word)  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.deleteWord(word);
        // Refresh ListView.
        cursor.requery();
    }

    // When AddEditWordActivity completed, it sends feedback.
    // (If you start it using startActivityForResult ())
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {/*
                this.wordList.clear();
                MyDatabaseHelper db = new MyDatabaseHelper(this);
                List<Word> list = db.getAllWords();
                this.wordList.addAll(list);
                // Notify the data change (To refresh the ListView).
                this.listViewAdapter.notifyDataSetChanged();*/
                cursor.requery();
            }
        }
    }

}
