package com.example.bigmood;

import java.io.Serializable;

public class moodObject implements Serializable {
    private String mood;

    public moodObject (String mood) {
        this.mood = mood;
    }

    public String mood(){
        return this.mood;
    }
}
