package com.roomates.storyquilt;


import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chris on 12/9/13.
 */
public class UserHandler {
    //SharedPreferences Private Mode
    int MODE_PRIVATE = Activity.MODE_PRIVATE;

    //Handling the User
    User user;

    //Passing the activity for context
    Activity activity;

    public UserHandler(Activity activity){
        this.activity = activity;
    }

     /**
     * Set the User
     */
    public void setUser(String email){
        this.user = FireConnection.getUserAt(FireConnection.create("user", User.formatEmail(email)));
    }

    /**
     * Firebase Information
     */
    public void addUserToFirebase(HashMap<String, String> userInfo){
        user = FireConnection.getUserAt(FireConnection.create("users", User.formatEmail(userInfo.get("personEmail"))));
        if (user == null){
            FireConnection.pushUserToList(
                    new User(
                            userInfo.get("personEmail"),
                            userInfo.get("personName"),
                            Integer.valueOf(userInfo.get("personAge")),
                            0,
                            0,
                            false,
                            new ArrayList<Story>(),
                            new ArrayList<Story>()));
        }
    }
    public void updateUserInFirebase(User user){/*To-DO*/}

    /**
     * Manage User Information
     */
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
