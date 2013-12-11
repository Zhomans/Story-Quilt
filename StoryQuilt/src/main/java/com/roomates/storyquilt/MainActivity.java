package com.roomates.storyquilt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GooglePlusActivity {
    //Passing Menu from onCreateOptionsMenu to edit in onConnectionStatusChanged
    Menu menu;

    //Current User
    UserHandler userHandler;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase storyRef;

    /**
     * Required by GooglePlusActivity
     */
    public void onCreateExtended(Bundle savedInstanceState) {
        //Setting the Button Id for both GooglePlusActivity and MainActivity
        signInButtonId = R.id.sign_in_button;

        //Setting User Handler
        userHandler = new UserHandler(this);

        //Get Person Email (previously logged in)
        previousEmail = userHandler.getEmail();

        //Choose Content View to Show
        chooseContentView();
    }
    public void onConnectionStatusChanged() {
        //Choose which content to show: SignIn or Main Activity (if different)
        chooseContentView();

        //Set Action Settings Sign in or SignOut
        if (menu != null) {
            Boolean visibility = mPlusClient.isConnected();
            userHandler.setConnected(visibility);
            (menu.findItem(R.id.gPlusSignOut)).setVisible(visibility);
            (menu.findItem(R.id.gPlusSignIn)).setVisible(!visibility);
        }
    }
    public void getUserInformation(){
        userHandler.setPersonFirstName(mPlusClient.getCurrentPerson().getName().getGivenName());
        userHandler.setEmail(mPlusClient.getAccountName());
        try{userHandler.setPersonAge(mPlusClient.getCurrentPerson().getAgeRange().getMin());}catch (NullPointerException e){e.printStackTrace();userHandler.setPersonAge(18);}
    }
    public void onActivityResultExtended(int requestCode, int resultCode, Intent data){/*DO NOTHING*/}

    /**
     * Manages Setting Content View based on LogIn State
     */
    //Choose SetContentView Content
    public void chooseContentView() {
        Boolean connected = mPlusClient.isConnected();
        userHandler.setConnected(connected);
        if (!connected) {
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
     * Methods for Handling List Views
     */
    //Grab ListViews from the XML
    private void setListViews(){
        writing = (ListView) findViewById(R.id.activity_main_writing_listview);
        reading = (ListView) findViewById(R.id.activity_main_reading_listview);

        writing.setOnItemClickListener( goToStoryActivity());
        reading.setOnItemClickListener( goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireConnection.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        writingAdapter = new StoryListAdapter(storyRef, MainActivity.this, R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> writingStories = new ArrayList<Story>();
                for (Story tempStory: stories){
                    Log.i("modifyArrayAdapter", tempStory.id);
                    if (userHandler.user.writing.contains(tempStory.id)){
                        Log.i("modifyArrayAdapter Add", tempStory.id);
                        writingStories.add(tempStory);
                    }
                }
                return writingStories;
            }
        };
        readingAdapter = new StoryListAdapter(storyRef, MainActivity.this, R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> readingStories = new ArrayList<Story>();
                for (Story tempStory: stories){
                    if (userHandler.user.reading.contains(tempStory.id))
                        readingStories.add(tempStory);
                }
                return readingStories;
            }
        };

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }
    //On Item Click for StoryListAdapter
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(MainActivity.this, StoryViewActivity.class);
                goToStory.putExtra("story",((Story)writing.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
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
                startActivity(joinStory);
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
