package com.roomates.storyquilt;

import java.util.ArrayList;

/**
 * Created by chris on 12/4/13.
 */
public class UserClass {
    String id, name;
    int age, reports, posts;
    boolean isBanned;
    ArrayList<StoryClass> writing, reading;


    public UserClass(){} //Firebase required constructor
    public UserClass(String name, int age, int reports, int posts, boolean isBanned, ArrayList<StoryClass> writing, ArrayList<StoryClass> reading){
        this.name = name;
        this.age = age;
        this.reports = reports;
        this.posts = posts;
        this.isBanned = isBanned;
        this.writing = writing;
        this.reading = reading;
    }

    //Firebase Get Methods
    public String getId() {return this.id;}
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
    public ArrayList<StoryClass> getWriting(){
        return this.writing;
    }
    public ArrayList<StoryClass> getReading(){ return this.reading; }

    //Setting the id from Firebase
    public void setId(String value){
        this.id = value;
    }

    /**
     * Become Writer from New
     */
    public void becomeWriter(StoryClass story){
        this.getWriting().add(story);
    }

    /**
     * Become Reader from New
     */
    public void becomeReader(StoryClass story){
        this.getReading().add(story);
    }

    /**
     * Become Reader from Writer
     */
    public void becomeReaderFromWriter(StoryClass story){
        this.getWriting().remove(story);
        this.getReading().add(story);
    }

    /**
     * Check User's Status as Reader
     */
    public boolean isReader(StoryClass story) {
        return this.getReading().contains(story);
    }

    /**
     * Check User's Status as Writer
     */
    public boolean isWriter(StoryClass story) {
        return this.getWriting().contains(story);
    }


}
