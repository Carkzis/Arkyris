package com.example.arkyris.entries;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IrisEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(IrisEntryItem entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<IrisEntryItem> entries);

    @Query("DELETE FROM iris_entry_table")
    void deleteAll();

    // LiveData helps app respond to data changes
    @Query("SELECT * FROM iris_entry_table ORDER BY date_time DESC")
    LiveData<List<IrisEntryItem>> getAllEntries();

    // Test if there is anything in the table
    @Query("SELECT * FROM iris_entry_table LIMIT 1")
    IrisEntryItem[] getAnyItem();

    @Delete
    void deleteEntry(IrisEntryItem entry);

    // Toggles whether an item is consider public or not
    @Update
    void updatePublic(IrisEntryItem entry);

}
