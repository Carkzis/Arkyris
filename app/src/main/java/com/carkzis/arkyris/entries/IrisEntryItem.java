package com.carkzis.arkyris.entries;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Room item for Iris entries, which are entries visible purely to the user, whereas Arke
 * entries are public entries.
 */
@Entity(tableName = "iris_entry_table")
public class IrisEntryItem {

    // Note to self: calling it pk and also id is very confusing.
    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    @ColumnInfo(name = "remoteId")
    @Expose
    private String mRemoteId;

    @SerializedName("user")
    @ColumnInfo(name = "user")
    @Expose
    public String mName;

    @SerializedName("colour")
    @ColumnInfo(name = "colour")
    @Expose
    private final int mColour;

    @SerializedName("date_time")
    @ColumnInfo(name = "date_time")
    @Expose
    public String mDateTime;

    @SerializedName("deleted")
    @ColumnInfo(name = "deleted")
    @Expose
    public int mIsDeleted;

    @SerializedName("public")
    @ColumnInfo(name = "public")
    @Expose
    private final int mIsPublic;

    public IrisEntryItem(String name, int colour, int isPublic) {
        //mEntryId = 0;
        mName = name;
        mColour = colour;
        mIsPublic = isPublic;
        mRemoteId = "";
    }

    /**
     * Getter methods for getting data into the RecycleView.
     */
    public String getName() { return mName; }
    public String getRemoteId() { return mRemoteId; }
    public String getDateTime() { return mDateTime; }
    public int getColour() { return mColour; }
    public int getIsPublic() { return mIsPublic; }

    /**
     * Getter that takes the stored DateTime, and retrieves just the date from it as a String.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDate() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDateTime.format(formatter);
    }

    /**
     * Getter that takes the stored DateTime, and retrieves just the time from it as a String.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTime() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDateTime.format(formatter);
    }

    /**
     * Setters for data held in ArkeEntryItem.
     */
    public void setRemoteId(String mRemoteId) {
        this.mRemoteId = mRemoteId;
    }
    public void setDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }
    public void setName(String mName) {
        this.mName = mName;
    }


}
