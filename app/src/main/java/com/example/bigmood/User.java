package com.example.bigmood;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class User {
    private String userId;
    private String displayName;
    private String profilePictureUrl;
    private ArrayList<String> friendsList;
    private ArrayList<String> pendingSent;
    private ArrayList<String> pendingReceived;
    private Timestamp dateCreated;

    public String getUserId() {return this.userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public String getDisplayName() {return this.displayName;}
    public void setDisplayName(String displayName) {this.displayName = displayName;}

    public String getProfilePictureUrl() {return this.profilePictureUrl;}
    public void setProfilePictureUrl(String profilePictureUrl) {this.profilePictureUrl = profilePictureUrl;}

    public ArrayList<String> getFriendsList() {return friendsList;}
    public void setFriendsList(ArrayList<String> friendsList) {this.friendsList = friendsList;}

    public ArrayList<String> getPendingSent() {return pendingSent;}
    public void setPendingSent(ArrayList<String> pendingSent) {this.pendingSent = pendingSent;}

    public ArrayList<String> getPendingReceived() {return pendingReceived;}
    public void setPendingReceived(ArrayList<String> pendingReceived) {this.pendingReceived = pendingReceived;}

    public Timestamp getDateCreated() {return dateCreated;}
    public void setDateCreated(Timestamp dateCreated) {this.dateCreated = dateCreated;}

    public User(String userId, String displayName) {
        this.setUserId(userId);
        this.setDisplayName(displayName);
    }




}
