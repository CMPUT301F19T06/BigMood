package com.example.bigmood;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * The data structure class for holding the users information, this class is not passed through the
 * intents but the targetUser is, the user object is then called from the database.
 */
public class User {
    /**
     * A unique id used by google. This is saved on user creation and is not editable
     */
    private String userId;
    /**
     * The display name of the user set in their google account
     */
    private String displayName;
    /**
     * The link to the users profile picture
     */
    private String profilePictureUrl;
    /**
     * the list of the users friends
     */
    private ArrayList<String> friendsList;
    /**
     * The list of pending friend requests the user has sent
     */
    private ArrayList<String> pendingSent;
    /**
     * The list of friend requests the user has received
     */
    private ArrayList<String> pendingReceived;
    /**
     * The date that the user first registered and logged into the app
     */
    private Timestamp dateCreated;

    /**
     * Get the user Id
     * @return
     * The id of the user
     */
    public String getUserId() {return this.userId;}

    /**
     * Set the id of the user
     * @param userId
     * The new user id
     */
    public void setUserId(String userId) {this.userId = userId;}
    /**
     * Get the user display name
     * @return
     * The display name of the user
     */
    public String getDisplayName() {return this.displayName;}
    /**
     * Set the display name of the user
     * @param displayName
     * The new display name
     */
    public void setDisplayName(String displayName) {this.displayName = displayName;}
    /**
     * Get the user picture
     * @return
     * The picture of the user
     */
    public String getProfilePictureUrl() {return this.profilePictureUrl;}
    /**
     * Set the profile picture of the user
     * @param profilePictureUrl
     * The new profile picture url
     */
    public void setProfilePictureUrl(String profilePictureUrl) {this.profilePictureUrl = profilePictureUrl;}
    /**
     * Get the user friends list
     * @return
     * The list of the users friends
     */
    public ArrayList<String> getFriendsList() {return friendsList;}
    /**
     * Set the friends list of the user
     * @param friendsList
     * The new user friends list
     */
    public void setFriendsList(ArrayList<String> friendsList) {this.friendsList = friendsList;}
    /**
     * Get the user sent requests
     * @return
     * The sent requests of the user
     */
    public ArrayList<String> getPendingSent() {return pendingSent;}
    /**
     * Set the pending sent list of the user
     * @param pendingSent
     * The new pending list
     */
    public void setPendingSent(ArrayList<String> pendingSent) {this.pendingSent = pendingSent;}
    /**
     * Get the user received requests
     * @return
     * The received requests of the user
     */
    public ArrayList<String> getPendingReceived() {return pendingReceived;}
    /**
     * Set the recieved request list of the user
     * @param pendingReceived
     * The new pending list
     */
    public void setPendingReceived(ArrayList<String> pendingReceived) {this.pendingReceived = pendingReceived;}
    /**
     * Get the user timestamp
     * @return
     * The timestamp of the user
     */
    public Timestamp getDateCreated() {return dateCreated;}
    /**
     * Set the starting date of the user
     * @param dateCreated
     * The new user date
     */
    public void setDateCreated(Timestamp dateCreated) {this.dateCreated = dateCreated;}

    /**
     * The current constructor of the user
     */
    public User(String userId, String displayName) {
        this.setUserId(userId);
        this.setDisplayName(displayName);
    }

    public User (){}

}
