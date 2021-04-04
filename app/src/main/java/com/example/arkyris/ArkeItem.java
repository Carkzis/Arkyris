package com.example.arkyris;

public class ArkeItem {

    // Attributes for each card
    private int mImage;
    private int mColour;
    private String mDate;
    private String mTime;

    public ArkeItem(int image, int colour, String date, String time) {
        mImage = image;
        mDate = date;
        mTime = time;
        mColour = colour;
    }

    /**
     * Getter methods for getting Views into the RecycleView.
     * @return
     */
    public int getImage() {
        return mImage;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public int getColour() { return mColour; }

}
