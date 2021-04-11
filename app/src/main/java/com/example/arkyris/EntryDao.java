package com.example.arkyris;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EntryItem entry);

    @Query("DELETE FROM entry_table")
    void deleteAll();

    // LiveData helps app respond to data changes
    @Query("SELECT * FROM entry_table ORDER BY _entryId DESC")
    LiveData<List<EntryItem>> getAllEntries();

    // Test if there is anything in the table
    @Query("SELECT * FROM entry_table LIMIT 1")
    EntryItem[] getAnyItem();

    @Delete
    void deleteEntry(EntryItem entry);

    // TODO: This will toggle whether an item is consider public or not
    // Needs looking into how this is achieved; it is incomplete
    @Update
    void updatePublic(EntryItem entry);

}
