package com.example.arkyris;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entry_table")
public class EntryItem {

    // Attributes for each entry
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_entryId")
    public int mEntryId;

    @ColumnInfo(name = "colour")
    private int mColour;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "time")
    private String mTime;

    @ColumnInfo(name = "public")
    // 0 for not public, 1 for public
    private int mIsPublic;

    public EntryItem(int colour, String date, String time, int isPublic) {
        mEntryId = 0;
        mDate = date;
        mTime = time;
        mColour = colour;
        mIsPublic = isPublic;
    }

    /**
     * Getter methods for getting Views into the RecycleView.
     * @return
     */

    public Integer getEntryId() { return mEntryId; }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public int getColour() { return mColour; }

    public int getIsPublic() { return mIsPublic; }

    public void setIsPublic(int isPublic) {
        mIsPublic = isPublic;
    }

}
