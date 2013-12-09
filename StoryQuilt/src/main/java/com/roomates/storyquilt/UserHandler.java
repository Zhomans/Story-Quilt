package com.roomates.storyquilt;


import android.app.Activity;

/**
 * Created by chris on 12/9/13.
 */
public class UserHandler {
    //SharedPreferences Mode
    int MODE_PRIVATE = Activity.MODE_PRIVATE;
    User user;
    Activity activity;

    public UserHandler(Activity activity){
        this.activity = activity;
    }

    //Set the User for this Handler
    public void setUser(String email){
        this.user = FireConnection.getUserAt(FireConnection.create("user", User.formatEmail(email)));
    }

    //Manage User Information
    public String getEmail(){
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email", "readonly");
    }
    public void setEmail(String value){
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putString("email", value).commit();
    }
    public String getPersonFirstName(){
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", "");
    }
    public void setPersonFirstName(String value){
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putString("personFirstName", value).commit();
    }
    public Integer getPersonAge() {
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getInt("personAge", 0);
    }
    public void setPersonAge(Integer value) {
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putInt("personAge", value).commit();
    }
}
