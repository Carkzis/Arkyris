package com.example.arkyris;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// need to annotate the Word class to make it meaningful to a Room database
@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    // NonNull means a field can never be null
    public Word(@NonNull String word) {this.mWord = word;}

    public String getWord() {return this.mWord;}

}
