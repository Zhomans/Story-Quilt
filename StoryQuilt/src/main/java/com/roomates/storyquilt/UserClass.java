package com.roomates.storyquilt;

/**
 * Created by chris on 12/4/13.
 */
public class UserClass {
    String email, name;
    int age, reports, posts;
    boolean isBanned;
    StoryClass[] writing, reading;


    public UserClass(){} //Firebase required constructor
    public UserClass(String email, String name, int age, int reports, int posts, boolean isBanned, StoryClass[] writing, StoryClass[] reading){
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
    public StoryClass[] getWriting(){
        return this.writing;
    }
    public StoryClass[] getReading(){
        return this.reading;
    }

    //Setting the id from Firebase
    public void setId(String value){
        this.email = value;
    }
}
