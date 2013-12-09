package com.roomates.storyquilt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;

public class MainActivity extends GooglePlusActivity {
    //Passing Menu from onCreateOptionsMenu to edit in onConnectionStatusChanged
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
        //These are saved in GooglePlusActivity. Setting them to our SharedPreferences
        setEmail(personEmail);
        setPersonFirstName(personFirstName);
        setPersonAge(personAge);
        addUserToFirebase();
        //Choose which content to show: SignIn or Main Activity (if different)
        chooseContentView();

        //Set Action Settings Sign in or SignOut

        if (menu != null) {
            Boolean visibility = mPlusClient.isConnected();
            (menu.findItem(R.id.gPlusSignOut)).setVisible(visibility);
            (menu.findItem(R.id.gPlusSignIn)).setVisible(!visibility);
        }
    }
    public void onActivityResultExtended(int requestCode, int resultCode, Intent data){/*DO NOTHING*/}
    public void onCreateExtended(Bundle savedInstanceState) {
        //Setting the Button Id for both GooglePlusActivity and MainActivity
        signInButtonId = R.id.sign_in_button;

        //Get Person Email (previously logged in)
        personEmail = getEmail();

        //Choose Content View to Show
        chooseContentView();
    }

    /**
     * Manages Setting Content View based on LogIn State
     */
    //Choose SetContentView Content
    public void chooseContentView() {
        if (!mPlusClient.isConnected()) {
            setContentView(R.layout.activity_login);
            setUpLoginViews();
        } else {
            setContentView(R.layout.activity_main);
            //Set up MainPage Views
            setUpMainPageViews();
        }
    }
    //LogIn Views
    public void setUpLoginViews(){
        //Set up SignInButton
        SignInButton signInButton = (SignInButton) findViewById(signInButtonId);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        //Set up ReadOnly Button

        Button readOnly = (Button) findViewById(R.id.activity_signin_readonly);
        readOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do Stuff Here
            }
        });
    }
    //MainPage views
    private void setUpMainPageViews(){
        setListViews();
        setFireBaseRefs();
        setListAdapters();
    }

    /**
     * Method for managing user Info
     */
    private void addUserToFirebase(){
        Firebase firebase_user =FireConnection.create("users",personEmail.replace(".", ""));
        firebase_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object value = snapshot.getValue();
                if (value == null) {
                    UserClass user = new UserClass(personEmail.replace(".", ""), personFirstName, personAge,
                            0, 0, false, new ArrayList<StoryClass>(), new ArrayList<StoryClass>());
                    FireConnection.pushUserToList(FireConnection.create("users"), user);
                } else {
                    //user already exists
                }
            }

            @Override
            public void onCancelled(FirebaseError e) {
                Log.e("Firebase Error", e.getMessage());
            }
        });
    }
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
