package com.roomates.storyquilt;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chris on 12/4/13.
 */
public class User implements Serializable{
    String email, name;
    int age, reports, posts;
    boolean isBanned;
    ArrayList<Story> writing, reading;


    public User(){} //Firebase required constructor

    public User(String email, String name, int age, int reports, int posts, boolean isBanned,
                ArrayList<Story> writing, ArrayList<Story> reading){
        this.email = email;
        this.name = name;
        this.age = age;
        this.reports = reports;
        this.posts = posts;
        this.isBanned = isBanned;
        this.writing = writing;
        this.reading = reading;
    }

    //Firebase Get Methods
    public String getId() {return this.email;}
    public String getName(){
        return this.name;
    }
    public int getAge(){
        return this.age;
    }
    public int getReports(){
        return this.reports;
    }
    public int getPosts(){
        return this.posts;
    }
    public boolean getIsBanned(){
        return this.isBanned;
    }
    public ArrayList<Story> getWriting(){
        return this.writing;
    }
    public ArrayList<Story> getReading(){ return this.reading; }

    //Setting the id from Firebase
    public void setId(String value){
        this.email = value;
    }

    /**
     * Become Writer from New
     */
    public void becomeWriter(Story story){
        this.getWriting().add(story);
    }

    /**
     * Become Reader from New
     */
    public void becomeReader(Story story){
        this.getReading().add(story);
    }

    /**
     * Become Reader from Writer
     */
    public void becomeReaderFromWriter(Story story){
        this.getWriting().remove(story);
        this.getReading().add(story);
    }

    /**
     * Check User's Status as Reader
     */
    public boolean isReader(Story story) {
        return this.getReading().contains(story);
    }

    /**
     * Check User's Status as Writer
     */
    public boolean isWriter(Story story) {
        return this.getWriting().contains(story);
    }

    //Format Email to Firebase Friendly format
    public static String formatEmail(String email){
        Log.i("Email", email.substring(0, email.length() - 10).replace(".",""));
        return email.substring(0, email.length() - 10).replace(".","");
    }

}
