package com.example.bigmood;

import android.app.Activity;

import java.io.Serializable;
import java.util.Date;


public class moodObject implements Serializable {

    private String moodType; //The type of mood

    private String moodDescription;
    private String moodColor;
    private Date moodDate;

    public moodObject (String mood) {
        this.moodType = mood;
    }

    public moodObject (String moodType, String moodDescription, String moodColor, Date moodDate){
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
