package com.example.bigmood;

import android.app.Activity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the data structure for the mood information. It implements serializable so that it can be passed
 * as an extra through intents.
 */

public class Mood implements Serializable {

    /**
     * The main type of the mood, such as Angry or Happy
     */
    private String moodTitle;
    /**
     * The description of the mood, a personal line
     */
    private String moodDescription;
    /**
     * The situation of the mood, a personal line
     */
    private String moodSituation;
    /**
     * mood username
     */
    private String moodUsername;



    /**
     * The hex code for the color, as a string
     */
    private String moodColor;
     /**
     * The date of the mood event
     */
    private transient Timestamp moodDate;
    /**
     * The unique Identifier for the mood
     */
    private String moodID;
    /**
     * The link to the moodPhoto
     */
    private String moodPhoto;
    /**
     * The name of the representative emoji
     */
    private String moodEmoji;


    /**
     * Long and Lat for location
     */
    private double longitude;



    private double latitude;

    private String moodCreator;

    /**
     * This is the empty constructor, this will represent the default Mood
     * TODO:Construct default
     */
    public Mood(String moodCreator){ this.moodCreator = moodCreator;}

    /**
     * This constructor is where all parameters are passed in and set in the object.
     * @param moodTitle
     * @param moodDescription
     * @param moodColor
     * @param moodDate
     */

    public Mood (String moodTitle, String moodDescription, String moodColor, Timestamp moodDate, String moodCreator){
        this.moodTitle = moodTitle;
        this.moodDescription = moodDescription;
        this.moodColor = moodColor;
        this.moodDate = moodDate;
        this.moodCreator = moodCreator;

    }

    /**
     * Get the mood title
     * @return
     * The mood title
     */
    public String getMoodTitle() {
        return moodTitle;
    }

    /**
     * Set the mood title
     * @param moodTitle
     * The title to be set to
     */
    public void setMoodTitle(String moodTitle) {
        this.moodTitle = moodTitle;
    }
    /**
     * Get the mood photo
     * @return
     * The mood photo
     */
    /**
     * get the username
     * @return
     */

    public String getMoodUsername() {
        return moodUsername;
    }

    /**
     * set the user name to a string
     * @param moodUsername
     */
    public void setMoodUsername(String moodUsername) {
        this.moodUsername = moodUsername;
    }
    public String getMoodPhoto() {
        return moodPhoto;
    }
    /**
     * Set the mood photo
     * @param moodPhoto
     * The photo to be set to
     */
    public void setMoodPhoto(String moodPhoto) {
        this.moodPhoto = moodPhoto;
    }
    /**
     * Get the mood emoji
     * @return
     * The mood emoji
     */
    public String getMoodEmoji() {
        return moodEmoji;
    }
    /**
     * Set the mood Emoji
     * @param moodEmoji
     * The emoji to be set to
     */
    public void setMoodEmoji(String moodEmoji) {
        this.moodEmoji = moodEmoji;
    }
    /**
     * Get the mood ID
     * @return
     * The mood ID
     */
    public String getMoodID() {
        return moodID;
    }
    /**
     * Set the mood ID
     * @param moodID
     * The ID to be set to
     */
    public void setMoodID(String moodID) {
        this.moodID = moodID;
    }
    /**
     * Get the mood description
     * @return
     * The mood description
     */
    public String getMoodDescription() {
        return moodDescription;
    }
    /**
     * Set the mood Description
     * @param moodDescription
     * The Description to be set to
     */
    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
    }

    /**
     * get the mood situation
     * @return: moodSituation as a string
     */
    public String getMoodSituation() {
        return moodSituation;
    }

    /**
     * set the mood situation as a string
     * @param moodSituation
     */
    public void setMoodSituation(String moodSituation) {
        this.moodSituation = moodSituation;
    }

    /**
     * Get the mood color
     * @return
     * The mood color. Had to make this a string or else it is currently not useable
     */
    public String getMoodColor() {
        return moodColor;
    }
    /**
     * Set the mood Color
     * @param moodColor
     * The Color to be set to
     */
    public void setMoodColor(String moodColor) {
        this.moodColor = moodColor;
    }

    public Timestamp getMoodDate() {
        return moodDate;
    }
     /**
     * Set the mood Date
     * @param moodDate
     * The Date object to be set to
     */
    public void setMoodDate(Timestamp moodDate) {
        this.moodDate = moodDate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getMoodCreator(){ return this.moodCreator; }

    public static Mood getFromDoc(DocumentSnapshot doc){
        String moodId = doc.getId();
        String moodPhoto = (String) doc.getData().get("moodPhoto");
        Mood mood = new Mood (doc.getString("moodTitle"), doc.getString("moodDescription")
                , doc.getString("moodColor"), doc.getTimestamp("moodDate"),doc.getString("moodCreator"));
        String moodEmoji = doc.getString("moodEmoji");
        String moodSituation = doc.getString("moodSituation");
        String moodUsername = doc.getString("userName");
        mood.setMoodID(moodId);
        mood.setMoodEmoji(moodEmoji);
        mood.setMoodUsername(moodUsername);
        mood.setMoodSituation(moodSituation);
        mood.setMoodPhoto(moodPhoto);
        return(mood);
    }


}
