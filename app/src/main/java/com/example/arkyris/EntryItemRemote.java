package com.example.arkyris;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EntryItemRemote {

    @SerializedName("id")
    @Expose
    private long id;

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



}
