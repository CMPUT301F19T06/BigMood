package com.example.bigmood;

import java.io.Serializable;

public class FriendContainer implements Serializable {
    //a simple structure that contains user ID and name so we can throw it around in contents
    private String UserID;
    private String DisplayName;

    private void FriendContainer(String UserID, String DisplayName){
        this.UserID = UserID;
        this.DisplayName = DisplayName;
    }
}
