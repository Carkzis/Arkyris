package com.example.arkyris.entries;

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

@Entity(tableName = "arke_entry_table")
public class ArkeEntryItem {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    @ColumnInfo(name = "remoteId")
    @Expose
    private String mRemoteId;

    @SerializedName("user")
    @ColumnInfo(name = "user")
    @Expose
    private String mName;

    @SerializedName("colour")
    @ColumnInfo(name = "colour")
    @Expose
    private final int mColour;

    @SerializedName("date_time")
    @ColumnInfo(name = "date_time")
    @Expose
    private String mDateTime;

    @SerializedName("deleted")
    @ColumnInfo(name = "deleted")
    @Expose
    private int mIsDeleted;

    @SerializedName("public")
    @ColumnInfo(name = "public")
    @Expose
    private final int mIsPublic;

    public ArkeEntryItem(String name, int colour, int isPublic) {
            mName = name;
            mColour = colour;
            mIsPublic = isPublic;
            mRemoteId = "";
    }

    public String getRemoteId() { return mRemoteId; }

    public String getName() { return mName; }

    public int getColour() { return mColour; }

    public String getDateTime() { return mDateTime; }

    public int getIsPublic() { return mIsPublic; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDate() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return localDateTime.format(formatter);
    }

    public String getTime() {
        OffsetDateTime localDateTime = OffsetDateTime.parse(mDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localDateTime.format(formatter);
    }

    public int getIsDeleted() {
        return mIsDeleted;
    }

    public void setIsDeleted(int mIsDeleted) {
        this.mIsDeleted = mIsDeleted;
    }

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
