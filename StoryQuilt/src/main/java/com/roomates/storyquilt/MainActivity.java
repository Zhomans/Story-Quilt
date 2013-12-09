package com.roomates.storyquilt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.SignInButton;

public class MainActivity extends GooglePlusActivity {
    //Intent Request Codes
    View signInButton;

    //the settings/actionbar menu
    Menu menu;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase writingRef, readingRef;

    /**
     * Required by GooglePlusActivity
     */
    public void onConnectionStatusChanged() {
        //These are saved in GooglePlusActivity
        setEmail(previousEmail);
        setPersonFirstName(personFirstName);
        setPersonAge(personAge);
        chooseContentView();
        Log.i("Google", String.valueOf(mPlusClient.isConnected()));
        if (menu != null) {
            MenuItem signOutItem =  menu.findItem(R.id.gPlusSignOut);
            MenuItem signInItem =  menu.findItem(R.id.gPlusSignIn);
            if (!mPlusClient.isConnected()) {
                signOutItem.setVisible(false);
                signInItem.setVisible(true);
            } else {
                signOutItem.setVisible(true);
                signInItem.setVisible(false);
            }
        }
        Log.i("username", getEmail());
    }
    public void onActivityResultExtended(int requestCode, int resultCode, Intent data){}
    public void onCreateExtended(Bundle savedInstanceState) {
        //curInstanceState = savedInstanceState;
        previousEmail = getEmail();
        onConnectionStatusChanged();
    }
    public void chooseContentView() {
        signInButtonId = R.id.sign_in_button;
        if (!mPlusClient.isConnected()) {
            setContentView(R.layout.activity_login);
            Toast.makeText(this, "You may only read stories, please sign in to contribute", Toast.LENGTH_LONG).show();
            ((SignInButton) findViewById(signInButtonId)).setSize(SignInButton.SIZE_WIDE);
            signInButton = findViewById(signInButtonId);
            signInButton.setOnClickListener(this);
        } else {
            setContentView(R.layout.activity_main);
            //Set up MainActivity Views
            setListViews();
            setFireBaseRefs();
            setListAdapters();
        }
    }
    

    /**
     * Method for managing user Info
     */
    private String getEmail(){
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email", "readonly");
    }
    private void setEmail(String value){
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("email", value).commit();
    }
    private String getPersonFirstName(){
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", "");
    }
    private void setPersonFirstName(String value){
        getSharedPreferences("StoryQuilt", MODE_PRIVATE).edit().putString("personFirstName", value).commit();
    }
    private Integer getPersonAge() {
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getInt("personAge", 0);
    }
    private void setPersonAge(Integer value) {
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putInt("personAge", value).commit();
    } 

    /**
     * Methods for Handling List Views
     */
    //Grab ListViews from the XML
    private void setListViews(){
        writing = (ListView) findViewById(R.id.activity_main_writing_listview);
        reading = (ListView) findViewById(R.id.activity_main_reading_listview);
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        readingRef = FireConnection.create("users", "reading");
        writingRef = FireConnection.create("users", "writing");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        writingAdapter = new StoryListAdapter(writingRef, MainActivity.this, R.layout.listitem_main_story);
        readingAdapter = new StoryListAdapter(readingRef, MainActivity.this, R.layout.listitem_main_story);

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }

    /**
     * Activity Methods
     */
    //Options Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        onConnectionStatusChanged();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.create_story: //Create a new Story
                Intent createStory = new Intent(MainActivity.this, CreateStoryActivity.class);
                startActivity(createStory);
                break;

            case R.id.join_story: //Join an Existing Story
                Intent joinStory = new Intent(MainActivity.this, JoinStoryActivity.class);
                break;

            case R.id.gPlusSignIn: //Sign in Google+
                signIn();
                break;

            case R.id.gPlusSignOut:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
