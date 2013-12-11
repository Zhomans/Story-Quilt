package com.roomates.storyquilt;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import android.app.FragmentTransaction;
import android.app.Fragment;

import com.firebase.client.Firebase;
import com.google.android.gms.common.SignInButton;

import java.util.HashMap;

public class MainTabActivity extends GooglePlusActivity {
    //Passing Menu from onCreateOptionsMenu to edit in onConnectionStatusChanged
    Menu menu;

    //Current User
    UserHandler userHandler;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase writingRef, readingRef;

    public LoginFragment loginFragment = new LoginFragment();
    public MyStoriesFragment myStoriesFragment = new MyStoriesFragment();
    public PopularStoriesFragment popularStoriesFragment = new PopularStoriesFragment();
    public SearchFragment searchFragment = new SearchFragment();

    public ActionBar actionBar;

    public void onCreateExtended(Bundle savedInstanceState) {
        //Setting the Button Id for both GooglePlusActivity and MainActivity
        setContentView(R.layout.activity_main_tab);
        actionBar = getActionBar();

        signInButtonId = R.id.sign_in_button;

        //Setting User Handler
        userHandler = new UserHandler(this);
        //Get Person Email (previously logged in)
        previousEmail = userHandler.getEmail();

        //Choose Content View to Show
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab myStoriesTab = actionBar.newTab().setText("My Stories");
        myStoriesTab.setTabListener(new NavTabListener(myStoriesFragment));

        ActionBar.Tab popularStoriesTab = actionBar.newTab().setText("Popular");
        popularStoriesTab.setTabListener(new NavTabListener(popularStoriesFragment));

        actionBar.addTab(myStoriesTab);
        actionBar.addTab(popularStoriesTab);
    }

    public void onConnectionStatusChanged() {
        //These are saved in GooglePlusActivity. Setting them to our SharedPreferences
        userHandler.setEmail(userInfo.get("personName"));
        userHandler.setPersonFirstName(userInfo.get("personEmail"));
        userHandler.setPersonAge(Integer.valueOf(userInfo.get("personAge")));
        userHandler.addUserToFirebase(userInfo);
        //Choose which content to show: SignIn or Main Activity (if different)
        //Set Action Settings Sign in or SignOut
        if (menu != null) {
            Boolean visibility = mPlusClient.isConnected();
            (menu.findItem(R.id.gPlusSignOut)).setVisible(visibility);
            (menu.findItem(R.id.gPlusSignIn)).setVisible(!visibility);
        }
    }

    public void onActivityResultExtended(int requestCode, int resultCode, Intent data){/*DO NOTHING*/}
    public HashMap<String,String> getUserInformation(){
        HashMap<String, String> userInfo = new HashMap<String, String>();
        userInfo.put("personName", mPlusClient.getCurrentPerson().getName().getGivenName());
        userInfo.put("personEmail", mPlusClient.getAccountName());
        userInfo.put("personAge", String.valueOf(mPlusClient.getCurrentPerson().getAgeRange().getMin()));
        return userInfo;
    }

    /**
     * Methods for Handling List Views
     */
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        readingRef = FireConnection.create("users", "reading");
        writingRef = FireConnection.create("users", "writing");
    }
    //Create and Set ArrayAdapters for the ListViews

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
                Intent createStory = new Intent(MainTabActivity.this, CreateStoryActivity.class);
                startActivity(createStory);
                break;

            case R.id.join_story: //Join an Existing Story
                Intent joinStory = new Intent(MainTabActivity.this, JoinStoryActivity.class);
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
