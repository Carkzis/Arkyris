package com.carkzis.arkyris.entries;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

}
