package com.roomates.storyquilt;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMainTab extends ActivityGooglePlus {
    //Passing Menu from onCreateOptionsMenu to edit in onConnectionStatusChanged
    Menu menu;

    //Current User
    public FragmentLogin loginFragment = new FragmentLogin();
    public FragmentMyStories myStoriesFragment = new FragmentMyStories();
    public FragmentPopularStories popularStoriesFragment = new FragmentPopularStories();
    public FragmentNewStories newStoriesFragment = new FragmentNewStories();
    public FragmentSearch searchFragment = new FragmentSearch();

    public ActionBar actionBar;

    UserHandler userHandler;

    /**
     * Required by ActivityGooglePlus
     */
    public void onCreateExtended(Bundle savedInstanceState) {
        //Setting the Button Id for both ActivityGooglePlus and MainActivity
        setContentView(R.layout.activity_main_tab);
        //Setting User Handler
        userHandler = new UserHandler(this);
        //Get Person Email (previously logged in)
        previousEmail = userHandler.getEmail();



        //Set Up Fragments
        setUpFragments();

        if (!mPlusClient.isConnected()){
            goToFragment(loginFragment, "LOGIN");
        } else {
            goToFragment(myStoriesFragment, "STORIES");
        }
    }
    public void onConnectionStatusChanged() {
        //These are saved in ActivityGooglePlus. Setting them to our SharedPreferences
        userHandler.updateUserFromFirebase();

        Boolean connected = mPlusClient.isConnected();
        //Update Handler
        userHandler.setConnected(connected);
        Fragment fragment = getFragmentManager().findFragmentByTag("LOGIN");
        if (connected && (fragment).isVisible()){
            goToFragment(myStoriesFragment,"STORIES");
        }
        //Set Action Settings Sign in or SignOut
        if (menu != null) {
            (menu.findItem(R.id.gPlusSignOut)).setVisible(connected);
            (menu.findItem(R.id.gPlusSignIn)).setVisible(!connected);
        }
    }
    public void onActivityResultExtended(int requestCode, int resultCode, Intent data){/*DO NOTHING*/}
    public void getUserInformation(){
        userHandler.setPersonFirstName(mPlusClient.getCurrentPerson().getName().getGivenName());
        userHandler.setEmail(mPlusClient.getAccountName());
        try{userHandler.setPersonAge(mPlusClient.getCurrentPerson().getAgeRange().getMin());}catch (NullPointerException e){e.printStackTrace();userHandler.setPersonAge(18);}
    }

    /**
     * Fragments
     */
    public void setUpFragments(){
        //Choose Content View to Show
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //My Stories
        ActionBar.Tab myStoriesTab = actionBar.newTab().setText("My Stories");
        myStoriesTab.setTabListener(new ListenerNavTab(myStoriesFragment));

        //Popular Stories
        ActionBar.Tab popularStoriesTab = actionBar.newTab().setText("Popular");
        popularStoriesTab.setTabListener(new ListenerNavTab(popularStoriesFragment));

        //New Stories
        ActionBar.Tab newStoriesTab = actionBar.newTab().setText("New");
        newStoriesTab.setTabListener(new ListenerNavTab(newStoriesFragment));

        actionBar.addTab(myStoriesTab);
        actionBar.addTab(popularStoriesTab);
        actionBar.addTab(newStoriesTab);
    }
    public void goToFragment(Fragment fragment, String id){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragmentContainer, fragment, id);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
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
                Intent createStory = new Intent(ActivityMainTab.this, ActivityCreateStory.class);
                startActivity(createStory);
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
