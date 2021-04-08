package com.example.arkyris;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class WordViewModel extends AndroidViewModel {

    private WordRepository mRepository;

    private LiveData<List<Word>> mAllWords;

    public WordViewModel (Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    // getter method for getting all the words,
    // hides implementation from the UI
    LiveData<List<Word>> getAllWords() { return mAllWords; }

    // wrapper for insert that calls Repositor's insert() method,
    // hides implementation of insert() from UI
    public void insert(Word word) { mRepository.insert(word); }

    public void deleteAll() { mRepository.deleteAll(); }

    public void deleteWord(Word word) { mRepository.deleteWord(word); }

}
