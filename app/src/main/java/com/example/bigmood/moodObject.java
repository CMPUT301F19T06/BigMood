package com.example.bigmood;

import android.app.Activity;

import java.io.Serializable;
import java.util.Date;


public class moodObject implements Serializable {

    private String moodType; //The type of mood

    private String moodDescription;
    private int moodColor;
    private Date moodDate;

    private String moodID;

    public String getMoodTitle() {
        return moodTitle;
    }

    public void setMoodTitle(String moodTitle) {
        this.moodTitle = moodTitle;
    }

    public String getMoodReason() {
        return moodReason;
    }

    public void setMoodReason(String moodReason) {
        this.moodReason = moodReason;
    }

    public String getMoodPhoto() {
        return moodPhoto;
    }

    public void setMoodPhoto(String moodPhoto) {
        this.moodPhoto = moodPhoto;
    }

    public String getMoodEmoji() {
        return moodEmoji;
    }

    public void setMoodEmoji(String moodEmoji) {
        this.moodEmoji = moodEmoji;
    }

    private String moodTitle;
    private String moodReason;
    private String moodPhoto;
    private String moodEmoji;

    public moodObject (String mood) {
        this.moodType = mood;
    }

    public void setMoodType(String moodType) {
        this.moodType = moodType;
    }

    public String getMoodID() {
        return moodID;
    }

    public void setMoodID(String moodID) {
        this.moodID = moodID;
    }

    public moodObject (String moodType, String moodDescription, int moodColor, Date moodDate){
        this.moodType = moodType;
        this.moodDescription = moodDescription;
        this.moodColor = moodColor;
        this.moodDate = moodDate;
    }

    public String getMoodType(){ return this.moodType; }

    public String getMoodDescription() { return moodDescription; }
    public void setMoodDescription(String moodDescription) { this.moodDescription = moodDescription; }

    public String getMoodColor() { return moodColor; }
    public void setMoodColor(String moodColor) { this.moodColor = moodColor; }

    public Date getMoodDate() { return moodDate; }
    public void setMoodDate(Date moodDate) { this.moodDate = moodDate; }
}
