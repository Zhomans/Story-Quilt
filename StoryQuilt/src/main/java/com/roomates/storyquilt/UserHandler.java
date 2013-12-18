package com.roomates.storyquilt;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

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

    //Passing the activity for Context
    Activity activity;

    //Firebase Reference
    Firebase firebase;

    //Signing in and Signing Out
    String previousUser = "readonly";
    //Constructor
    public UserHandler(Activity activity){
        this.activity = activity;
        //Base Case User
        this.user = newUser();
        //Get User Information From Firebase
        Log.d("USERHANDLER DEBUGGING", getEmail());
        updateUserFromFirebase();
    }

    /**
     * Firebase Information
     */
    public User newUser(){
        return this.user = new User(
                "readonly",
                "readonly",
                18,
                0,
                0,
                false,
                new ArrayList<String>(),
                new ArrayList<String>(),
                new ArrayList<String>());
    }
    public void updateUserFromFirebase(){
        FireHandler.create("users", User.formatEmail(getEmail())).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!isNetworkAvailable()){
                    Toast.makeText(activity, "Network is currently unavailable!", Toast.LENGTH_SHORT).show();
                }
                UserHandler.this.user = snapshot.getValue(User.class);
                Log.i("LOGGINGLOGGING", UserHandler.this.user.email);
                if (user == null) {FireHandler.pushUserToList(newUser());  UserHandler.this.user = newUser();}
                if (user.writing == null) {user.writing = new ArrayList<String>(); Log.i("UserHandlerActivity", activity.getLocalClassName() + " " + 0);}
                else {Log.i("UserHandlerActivity", activity.getLocalClassName() + " " + user.writing.size());}
                if (user.reading == null) user.reading = new ArrayList<String>();
                if (user.removed== null) user.removed = new ArrayList<String>();
            }

            public void onCancelled(FirebaseError error) {}
        });
    }
    //Clean up remaining connections
    public void cleanUp(){
        Firebase.goOffline();
    }
   /**
     * Manage User Information
     */
    public String getEmail(){
       return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email", "first");
    }
    public void setEmail(String value){
        this.user.email = User.formatEmail(value);
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putString("email", this.user.email).commit();
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


    /**
     * Change User Status
     */
    public void becomeWriter(String id){
        this.user.writing.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public void becomeReaderFromWriter(String id){
        this.user.writing.remove(id);
        this.user.reading.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public void removeStory(String id){
        this.user.removed.add(id);
        FireHandler.pushUserToList(this.user);
    }
    public boolean isRemoved(String id){
        return user != null && user.removed != null && user.removed.contains(id);
    }
    public boolean isReader(String id) {
        return user != null && user.reading != null && user.reading.contains(id);
    }
    public boolean isWriter(String id) {
        return user != null && user.writing != null && user.writing.contains(id);
    }

    /**
     * Get Connectivity
     */
    public void setConnected(Boolean value) {
        this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putBoolean("connected", value).commit();
    }
    public Boolean isConnected() {
        return this.activity.getSharedPreferences("StoryQuilt", MODE_PRIVATE).getBoolean("connected", false);
    }
    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
