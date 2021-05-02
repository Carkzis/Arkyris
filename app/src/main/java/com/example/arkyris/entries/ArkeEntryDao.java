package com.example.arkyris.entries;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArkeEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArkeEntryItem entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ArkeEntryItem> entries);

    @Query("DELETE FROM arke_entry_table")
    void deleteAll();

    @Query("SELECT * FROM arke_entry_table WHERE public = 1 ORDER BY date_time DESC")
    LiveData<List<ArkeEntryItem>> getAllPublicEntries();

    @Query("SELECT * FROM arke_entry_table LIMIT 1")
    ArkeEntryItem[] getAnyItem();

}
