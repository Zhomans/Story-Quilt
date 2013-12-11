package com.roomates.storyquilt;


import android.app.Activity;
import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
        updateUser(FireConnection.create("user", User.formatEmail(getEmail())));
    }

    /**
     * Firebase Information
     */
    //Add User Class in the firebase
    public void addUserToFirebase(HashMap<String, String> userInfo){
        if (this.user == null){
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
    //Update User Class in the firebase
    public void updateUserInFirebase(User user){

    }
    //Get User Class in the firebase
    public void updateUser(Firebase firebase){
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserHandler.this.user = snapshot.getValue(User.class);
            }

            public void onCancelled(FirebaseError error) {
            }
        });
    }


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

    public void setConnected(Boolean value) {
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putBoolean("connected", value).commit();
    }

    public Boolean getConnected() {
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getBoolean("connected", false);
    }


}
