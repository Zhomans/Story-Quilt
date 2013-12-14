package com.roomates.storyquilt;


import android.app.Activity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;

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

        this.user = new User(
                getEmail(),
                getPersonFirstName(),
                getPersonAge(),
                0,
                0,
                false,
                new ArrayList<String>(),
                new ArrayList<String>());
        FireHandler.pushUserToList(this.user);

        updateUserFromFirebase();
    }

    /**
     * Firebase Information
     */
    public void addUserToFirebase(){
        this.user = new User(
                getEmail(),
                getPersonFirstName(),
                getPersonAge(),
                0,
                0,
                false,
                new ArrayList<String>(),
                new ArrayList<String>());
        FireHandler.pushUserToList(this.user);
    }
    public void updateUserFromFirebase(){
        FireHandler.create("users", User.formatEmail(getEmail())).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserHandler.this.user = snapshot.getValue(User.class);
                if (user == null) addUserToFirebase();
                if (user.writing == null) {user.writing = new ArrayList<String>(); Log.i("UserHandlerActivity", activity.getLocalClassName() + " " + 0);}
                else {Log.i("UserHandlerActivity", activity.getLocalClassName() + " " + user.writing.size());}
                if (user.reading == null) user.reading = new ArrayList<String>();
                if (activity.getLocalClassName().equals("ActivityMainTab")) {
                    ((ActivityMainTab) activity).onConnectionStatusChanged();
                }

            }

            public void onCancelled(FirebaseError error) {
            }
        });
    }

/*
    public void stopConnection(){
        firebase.removeEventListener(listener);
    }
*/

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
    public void becomeWriter(String id){
        this.user.writing.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public void becomeReader(String id){
        this.user.reading.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public void becomeReaderFromWriter(String id){
        this.user.writing.remove(id);
        this.user.reading.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public boolean isReader(String id) {
        return user != null && user.reading != null && user.reading.contains(id);
    }
    public boolean isWriter(String id) {
        return user != null && user.writing != null && user.getWriting().contains(id);
    }
}
