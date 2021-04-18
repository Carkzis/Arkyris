package com.example.arkyris;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class EntryItemRemote {

    // Note to self: calling it pk and also id is very confusing.
    @SerializedName("pk")
    @Expose
    private String id;

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

    public EntryItemRemote(String name, int colour, int isPublic) {
        mName = name;
        mColour = colour;
        mIsPublic = isPublic;
    }

    // getters and setters
    public String getId() { return id; }

    public int getColour() { return mColour; }

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

    public void setIsPublic(int isPublic) { mIsPublic = isPublic; }

}
