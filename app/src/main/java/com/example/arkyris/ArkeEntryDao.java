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
public interface ArkeEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ArkeEntryItem entry);

    @Query("DELETE FROM arke_entry_table")
    void deleteAll();

    @Query("SELECT * FROM arke_entry_table ORDER BY date_time DESC")
    LiveData<List<ArkeEntryItem>> getAllPublicEntries();

    @Query("SELECT * FROM arke_entry_table LIMIT 1")
    ArkeEntryItem[] getAnyItem();

    @Delete
    void deleteEntry(ArkeEntryItem entry);

    @Update
    void updatePublic(ArkeEntryItem entry);

}
