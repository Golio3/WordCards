package com.example.golio.wordcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Golio on 03.09.2017.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DictionaryV2";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_DICTIONARY = "DICTIONARY";
    private static final String TABLE_OPTIONS = "OPTIONS";
    private static final String COLUMN_OPTIONS_ID = "_id";
    private static final String COLUMN_OPTIONS_TYPE = "TYPE";
    private static final String COLUMN_OPTIONS_VALUE = "VALUE";
    private static final String COLUMN_DICT_ID = "_id";
    protected static final String COLUMN_DICT_LEX1 = "LEX1";
    protected static final String COLUMN_DICT_LEX2 = "LEX2";
    private static final String COLUMN_DICT_LEX3 = "LEX3";
    private static final String COLUMN_DICT_LANGUAGE = "LANGUAGE";
    private static final String COLUMN_DICT_WEIGHT = "WEIGHT";
    private static final String TAG = "SQLite";
    private static final String[] COLUMS_DICTIONARY = {COLUMN_DICT_ID, COLUMN_DICT_LEX1, COLUMN_DICT_LEX2,
            COLUMN_DICT_LEX3, COLUMN_DICT_LANGUAGE};
    private static final String[] COLUMS_OPTIONS = {COLUMN_OPTIONS_ID, COLUMN_OPTIONS_TYPE,
            COLUMN_OPTIONS_VALUE};

    public MyDatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        // Script to create table.
        String CREATE_TABLE_DICTIONARY = "CREATE TABLE " + TABLE_DICTIONARY + "(" + COLUMN_DICT_ID
                + " INTEGER PRIMARY KEY, " + COLUMN_DICT_LEX1 + " TEXT, " + COLUMN_DICT_LEX2
                + " TEXT, " + COLUMN_DICT_LEX3 + " TEXT, " +  COLUMN_DICT_LANGUAGE + " INTEGER, "
                + COLUMN_DICT_WEIGHT + " INTEGER) ";
        String CREATE_TABLE_OPTIONS = "CREATE TABLE " + TABLE_OPTIONS  + "(" + COLUMN_OPTIONS_ID
                + " INTEGER PRIMARY KEY, " + COLUMN_OPTIONS_TYPE + " TEXT, " + COLUMN_OPTIONS_VALUE
                + " INTEGER) ";
        // Execute script.
        db.execSQL(CREATE_TABLE_DICTIONARY);
        db.execSQL(CREATE_TABLE_OPTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");

        // Save table
        backupTable(db, TABLE_DICTIONARY, COLUMS_DICTIONARY);
        //backupTable(db, TABLE_OPTIONS, COLUMS_OPTIONS);

        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);

        // Recreate
        onCreate(db);

        revertTableFromBackup(db, TABLE_DICTIONARY, COLUMS_DICTIONARY);
        //revertTableFromBackup(db, TABLE_OPTIONS, COLUMS_OPTIONS);
    }

    private void backupTable (SQLiteDatabase db, String table, String[] columns) {
        String sql;

        // Save table
        sql = "CREATE TEMPORARY TABLE BACKUP_" + table + "(";
        sql = addColumsInSqlString(columns, sql);
        sql = sql.substring(0, (sql.length() - 2));
        sql += ")";
        db.execSQL(sql);

        sql = "INSERT INTO BACKUP_" + table + " SELECT ";
        sql = addColumsInSqlString(columns, sql);
        sql = sql.substring(0, (sql.length() - 2));
        sql += " FROM " + table;
        db.execSQL(sql);
    }

    private void revertTableFromBackup(SQLiteDatabase db, String table, String[] columns) {
        String sql = "";

        sql = "INSERT INTO " + table + "(";
        sql = addColumsInSqlString(columns, sql);
        sql = sql.substring(0, (sql.length() - 2));
        sql += ") SELECT ";
        sql = addColumsInSqlString(columns, sql);
        sql = sql.substring(0, (sql.length() - 2));
        sql += " FROM BACKUP_" + table;
        db.execSQL(sql);
    }

    private String addColumsInSqlString (String[] columns, String sql) {
        for (int i = 0; i < columns.length; i++) {
            sql += columns[i] + ", ";
        }
        return sql;
    }

    // If Dictionary table has no data
    // default, Insert 2 records.
    public void createDefaultWordsIfNeed()  {
        int count = this.getWordsCount();
        if(count == 0) {
            Word word = new Word("преимущества", "advantages", "", 1, 1000);
            this.addWord(word);
            word = new Word("показано ниже", "shown below", "", 1, 1000);
            this.addWord(word);
        }
    }

    public void createDefaultOptionsIfNeed()  {
        int count = this.getOptionssCount();
        if(count != 4) {
            Options option = new Options("random", 0);
            this.addOptions(option);
            option = new Options("side", 0);
            this.addOptions(option);
            option = new Options("language", 0);
            this.addOptions(option);
            option = new Options("step", 0);
            this.addOptions(option);
        }
    }

    public void addWord(Word word) {
        Log.i(TAG, "MyDatabaseHelper.addWord ... " + word.getId());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DICT_LEX1, word.getLex1());
        values.put(COLUMN_DICT_LEX2, word.getLex2());
        values.put(COLUMN_DICT_LEX3, word.getLex3());
        values.put(COLUMN_DICT_LANGUAGE, word.getLanguage());
        values.put(COLUMN_DICT_WEIGHT, word.getWeight());

        // Inserting Row
        db.insert(TABLE_DICTIONARY, null, values);

        // Closing database connection
        db.close();
    }

    public void addOptions(Options option) {
        Log.i(TAG, "MyDatabaseHelper.addOption ... " + option.getId());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_OPTIONS_TYPE, option.getType());
        values.put(COLUMN_OPTIONS_VALUE, option.getValue());

        // Inserting Row
        db.insert(TABLE_OPTIONS, null, values);

        // Closing database connection
        db.close();
    }


    public Word getWord(int id) {
        Log.i(TAG, "MyDatabaseHelper.getWord ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DICTIONARY, COLUMS_DICTIONARY,
                COLUMN_DICT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Word word = new Word(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)));
        // return word
        return word;
    }


    public Options getOptions(int id) {
        Log.i(TAG, "MyDatabaseHelper.getOptions ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPTIONS, COLUMS_OPTIONS,
                COLUMN_DICT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Options option = new Options(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                Integer.parseInt(cursor.getString(2)));
        // return option
        return option;
    }

    public List<Word> getWordsByLanguage(int language) {
        Log.i(TAG, "MyDatabaseHelper.getWordsByLanguage ... " );

        List<Word> wordList = new ArrayList<Word>();
        String selectQuery = "SELECT  * FROM " + TABLE_DICTIONARY + " WHERE " + COLUMN_DICT_LANGUAGE
                + "=" + language + " ORDER BY " + COLUMN_DICT_WEIGHT + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Word word = cursorToWord(cursor);
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        // return word list
        return wordList;
    }

    public List<Word> getAllWords() {
        Log.i(TAG, "MyDatabaseHelper.getAllWords ... " );

        List<Word> wordList = new ArrayList<Word>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DICTIONARY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Word word = cursorToWord(cursor);
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        // return word list
        return wordList;
    }


    public List<Options> getAllOptions() {
        Log.i(TAG, "MyDatabaseHelper.getAllOptions ... " );

        List<Options> optionsList = new ArrayList<Options>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OPTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Options option = cursorToOptions(cursor);
                optionsList.add(option);
            } while (cursor.moveToNext());
        }
        // return word list
        return optionsList;
    }

    public Cursor getAllWordsAsCursor() {
        Log.i(TAG, "MyDatabaseHelper.getAllWordsAsCursor ... " );

        List<Word> wordList = new ArrayList<Word>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DICTIONARY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllOptionsAsCursor() {
        Log.i(TAG, "MyDatabaseHelper.getAllOptionsAsCursor ... " );

        List<Options> optionsList = new ArrayList<Options>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OPTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getWordsCount() {
        Log.i(TAG, "MyDatabaseHelper.getWordsCount ... ");

        String countQuery = "SELECT  * FROM " + TABLE_DICTIONARY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int getOptionssCount() {
        Log.i(TAG, "MyDatabaseHelper.getOptionsCount ... ");

        String countQuery = "SELECT  * FROM " + TABLE_OPTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public int updateWord(Word word) {
        Log.i(TAG, "MyDatabaseHelper.updateWord ... " + word.getId());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DICT_LEX1, word.getLex1());
        values.put(COLUMN_DICT_LEX2, word.getLex2());
        values.put(COLUMN_DICT_LEX3, word.getLex3());
        values.put(COLUMN_DICT_LANGUAGE, word.getLanguage());
        values.put(COLUMN_DICT_WEIGHT, word.getWeight());

        // updating row
        return db.update(TABLE_DICTIONARY, values, COLUMN_DICT_ID + " = ?",
                new String[]{String.valueOf(word.getId())});
    }

    public void updateOptions(List<Options> options) {
        Log.i(TAG, "MyDatabaseHelper.updateOptions ... ");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i=0; i<options.size(); i++) {
            values.put(COLUMN_OPTIONS_TYPE, options.get(i).getType());
            values.put(COLUMN_OPTIONS_VALUE, options.get(i).getValue());

            // updating row
            db.update(TABLE_OPTIONS, values, COLUMN_DICT_ID + " = ?", new String[]{String.valueOf(options.get(i).getId())});
        }
    }

    public void deleteWord(Word word) {
        Log.i(TAG, "MyDatabaseHelper.updateWord ... " + word.getId());

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DICTIONARY, COLUMN_DICT_ID + " = ?",
                new String[]{String.valueOf(word.getId())});
        db.close();
    }

    public Word cursorToWord(Cursor cursor){
        Word word = new Word();
        word.setId(Integer.parseInt(cursor.getString(0)));
        word.setLex1(cursor.getString(1));
        word.setLex2(cursor.getString(2));
        word.setLex3(cursor.getString(3));
        word.setLanguage(cursor.getInt(4));
        word.setWeight(cursor.getInt(5));
        return word;
    }

    public Options cursorToOptions(Cursor cursor){
        Options option = new Options();
        option.setId(Integer.parseInt(cursor.getString(0)));
        option.setType(cursor.getString(1));
        option.setValue(cursor.getInt(2));
        return option;
    }
}
