package com.example.arkyris;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "arke_entry_table")
public class ArkeEntryItem {

    // Attributes for each entry
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_entryId")
    public int mEntryId;

    // Note to self: calling it pk and also id is very confusing.
    @SerializedName("pk")
    @Expose
    private String mRemoteId;

    @SerializedName("user")
    @Expose
    private String mName;

    @SerializedName("colour")
    @Expose
    private int mColour;

    @SerializedName("date_time")
    @Expose
    private String mDateTime;

    @SerializedName("deleted")
    @Expose
    private int mIsDeleted;

    @SerializedName("public")
    @Expose
    private int mIsPublic;

    public ArkeEntryItem(String name, int colour, int isPublic) {
        mName = name;
        mColour = colour;
        mIsPublic = isPublic;
    }

    // getters and setters
    public String getId() { return mRemoteId; }

    public int getColour() { return mColour; }

    public String getDateTime() { return mDateTime; }

    public int getIsPublic() { return mIsPublic; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDate() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDateTime.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTime() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDateTime.format(formatter);
    }

}
