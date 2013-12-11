package com.roomates.storyquilt;


import android.app.Activity;

import com.firebase.client.DataSnapshot;
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
        updateUserFromFirebase();
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
                            new ArrayList<String>(),
                            new ArrayList<String>()));
        }
    }
    //Update User Class in the firebase
    public void updateUserInFirebase(){
        FireConnection.pushUserToList(this.user);
    }
    //Get User Class in the firebase
    public void updateUserFromFirebase(){
        FireConnection.create("users", User.formatEmail(getEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserHandler.this.user = snapshot.getValue(User.class);
                if (user.writing == null){ user.writing = new ArrayList<String>();}
                if (user.reading == null){ user.reading = new ArrayList<String>();}
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
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putString("email", User.formatEmail(value)).commit();
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
    public Boolean isConnected() {
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getBoolean("connected", false);
    }


    /**
     * Change User Status
     */
    //Become Writer from New
    public void becomeWriter(String id){
        this.user.writing.add(id);
        updateUserInFirebase();
    }
    //Become Reader from New
    public void becomeReader(String id){
        this.user.reading.add(id);
        updateUserInFirebase();
    }
    //Become Reader from Writer
    public void becomeReaderFromWriter(String id){
        this.user.writing.remove(id);
        this.user.reading.add(id);
        updateUserInFirebase();
    }
    //Check User's Status as Reader
    public boolean isReader(String id) {
        return user.getReading().contains(id);
    }
    //Check User's Status as Writer
    public boolean isWriter(String id) {
        return user.getWriting().contains(id);
    }

}
