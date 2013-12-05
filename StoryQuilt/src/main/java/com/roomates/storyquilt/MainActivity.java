package com.roomates.storyquilt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class MainActivity extends Activity {
    //Intent Request Codes
    private final int LOGIN = 0; //Request code for logging in and getting username

    //User's name from the google account
    String username;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase mainRef;
    Firebase writingRef, readingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if logged in
        username = getUserName();
        if (username == null) {
            goToUserLogin();
        }
        //Set up MainActivity Views
        setListViews();
        setFireBaseRefs();
        setListAdapters();
    }
    
    /**Methods for Managing Account Info
        getUserName()
        setUserName()
        gotoUserLogin()
    */
    //Method for getting username
    private String getUserName(){
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("username", null);
    }

    //Method for saving username
    private void setUserName(String value){
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("username", value).commit();
    }

    //Check for User Login
    private void goToUserLogin(){
        Intent getLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(getLogin, LOGIN);
    }

    /**
        Methods for Handling List Views

     */
    //Grab ListViews from the XML
    private void setListViews(){
        writing = (ListView) findViewById(R.id.activity_main_writing_listview);
        reading = (ListView) findViewById(R.id.activity_main_reading_listview);
    }

    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        mainRef = new Firebase("https://storyquilt.firebaseio.com");
        readingRef = mainRef.child("users").child("reading");
        writingRef = mainRef.child("users").child("writing");
    }

    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        writingAdapter = new StoryAdapter(writingRef, MainActivity.this, R.layout.listitem_main_writing);
        readingAdapter = new StoryAdapter(readingRef, MainActivity.this, R.layout.listitem_main_reading);

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }

    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Options Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case LOGIN: //Activity Result for Login Screen
                if (resultCode == RESULT_OK){
                    username = data.getStringExtra("username");
                    setUserName(username); //Save the username in sharedPreferences
                    Log.i("LoginResult", "Logged in as " + username);
                } else { Log.i("LoginResult", "Failed to Login");
                    Toast.makeText(MainActivity.this, "Failed to login to Google account. You can only read stories.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
