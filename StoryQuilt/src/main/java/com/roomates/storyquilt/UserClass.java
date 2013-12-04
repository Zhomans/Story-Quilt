package com.roomates.storyquilt;

/**
 * Created by chris on 12/4/13.
 */
public class UserClass {
    String id, name;
    int age, reports, posts;
    boolean isBanned;
    String[] writing, reading;


    public UserClass(){} //Firebase required constructor
    public UserClass(String name, int age, int reports, int posts, boolean isBanned, String[] writing, String[] reading){
        this.name = name;
        this.age = age;
        this.reports = reports;
        this.posts = posts;
        this.isBanned = isBanned;
        this.writing = writing;
        this.reading = reading;
    }

    //Firebase Get Methods
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
    public String[] getWriting(){
        return this.writing;
    }
    public String[] getReading(){
        return this.reading;
    }
}
