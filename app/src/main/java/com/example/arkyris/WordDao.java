package com.example.arkyris;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Word word);

    @Query("DELETE FROM word_table")
    void deleteAll();

    // LiveData helps app respond to data changes
    @Query("SELECT * FROM word_table ORDER BY word ASC")
    LiveData<List<Word>> getAllWords();

    // Tests if there is a single word, no additional code required here
    @Query("SELECT * FROM word_table LIMIT 1")
    Word[] getAnyWord();

    @Delete
    void deleteWord(Word word);
}
