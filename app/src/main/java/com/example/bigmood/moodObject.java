package com.example.bigmood;

import android.app.Activity;

import java.io.Serializable;
import java.util.Date;


public class moodObject implements Serializable {

    private String moodType; //The type of mood

    private String moodDescription;
    private int moodColor;
    private Date moodDate;

    public moodObject (String mood) {
        this.moodType = mood;
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

    public int getMoodColor() { return moodColor; }
    public void setMoodColor(int moodColor) { this.moodColor = moodColor; }

    public Date getMoodDate() { return moodDate; }
    public void setMoodDate(Date moodDate) { this.moodDate = moodDate; }
}
