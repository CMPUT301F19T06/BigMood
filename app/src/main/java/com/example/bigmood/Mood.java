package com.example.bigmood;

import android.app.Activity;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;


public class Mood implements Serializable {

    private String moodDescription;
    private String moodColor;
    private Timestamp moodDate;
    private String moodID;
    private String moodTitle;
    private String moodPhoto;
    private String moodEmoji;

    public Mood(){}

    public Mood (String mood) {
        this.moodTitle = mood;
    }

    public Mood (String moodTitle, String moodDescription, String moodColor, Timestamp moodDate){
        this.moodTitle = moodTitle;
        this.moodDescription = moodDescription;
        this.moodColor = moodColor;
        this.moodDate = moodDate;
    }

    public String getMoodTitle() {
        return moodTitle;
    }

    public void setMoodTitle(String moodTitle) {
        this.moodTitle = moodTitle;
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

    public String getMoodID() {
        return moodID;
    }

    public void setMoodID(String moodID) {
        this.moodID = moodID;
    }


    public String getMoodDescription() {
        return moodDescription;
    }
    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
    }

    /**
     * Had to make this a string or else no use
     * @return
     */
    public String getMoodColor() {
        return moodColor;
    }
    public void setMoodColor(String moodColor) {
        this.moodColor = moodColor;
    }

    public Timestamp getMoodDate() {
        return moodDate;
    }
    public void setMoodDate(Timestamp moodDate) {
        this.moodDate = moodDate;
    }
}
