package com.example.arkyris.sandbox;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {

    // and member variables for DAO and list of words
    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;

    // constructor to get handle to db and initialise member variables
    WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAllWords();
    }

    // wrapper method to return cached words as LiveData
    // this is because room executes queries on a separate thread
    LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    // wrapper for insert() using AsyncTask
    public void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }

    public void deleteAll() {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.deleteAll();
        });
    }

    public void deleteWord(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.deleteWord(word);
        });
    }
}
