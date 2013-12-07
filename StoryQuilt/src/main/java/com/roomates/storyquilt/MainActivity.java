package com.roomates.storyquilt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        GooglePlayServicesClient.OnConnectionFailedListener {
    //Intent Request Codes
    private final int LOGIN = 0; //Request code for logging in and getting username
    private final int SIGNOUT = 1; //Request code for logging in and getting username
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    //User's name from the google account
    String username;

    //the settings/actionbar menu
    Menu menu;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase mainRef;
    Firebase writingRef, readingRef;

    //google plus api
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlusClient = new PlusClient.Builder(this, this, this)
                //.setActions("http://schemas.google.com/CreateActivity"); //my (Mac-I) phone always crashes on this saying : "java.lang.NoSuchMethodError: Lcom/google/android/gms/plus/PlusClient$Builder;.setActions"
                .setScopes(Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        //Check if logged in
        googlePlusClient();
        username = getUserName(); //getUserName();
        if (username.equals("readonly")) {
            Toast.makeText(this, "You may only read stories, please sign in to contribute", Toast.LENGTH_LONG).show();
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
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("username", "");
    }

    //Method for saving username
    private void setUserName(String value){
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("username", value).commit();
    }

    //Check for User Login
    private void googlePlusClient(){
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setActions("http://schemas.google.com/CreateActivity")
                .setScopes(Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
    }

    //Signing In to Google+
    public void signIn() {
        if (!mPlusClient.isConnected()) { //Create a new Story
            Log.i("testing","hello");
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
    }

    //Signing Out of Google+
    public void signOut() {
        Log.i("isConnected", Boolean.toString(mPlusClient.isConnected()));
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
                @Override
                public void onAccessRevoked(ConnectionResult connectionResult) {
                    // mPlusClient is now disconnected and access has been revoked.
                    // Trigger app logic to comply with the developer policies
                }
            });
            setUserName("readonly");
            mPlusClient.disconnect();
            mPlusClient.connect();
            Toast.makeText(this, "Successfully Signed Out", Toast.LENGTH_LONG).show();
        }
        updateSignOutandInButtonVisibility();
    }

    //Google+ Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("connectionresult",result.toString());
        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }

        // Save the intent so that we can start an activity when the user clicks
        // the sign-in button.
        mConnectionResult = result;
    }

    //Google+ Connection successful
    @Override
    public void onConnected(Bundle connectionHint) {
        mConnectionProgressDialog.dismiss();
        username = mPlusClient.getAccountName();
        Toast.makeText(this, username + " connected!", Toast.LENGTH_LONG).show();
        setUserName(username);
        updateSignOutandInButtonVisibility();
    }

    //Google+ Connection Disconnected
    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

    //Google+ Access Revoked
    public void onAccessRevoked(ConnectionResult status) {
        // mPlusClient is now disconnected and access has been revoked.
        // Trigger app logic to comply with the developer policies
    }

    //Visisbility of Login Button
    public void updateSignOutandInButtonVisibility() {
        MenuItem signOutItem = (MenuItem) menu.findItem(R.id.gPlusSignOut);
        MenuItem signInItem = (MenuItem) menu.findItem(R.id.gPlusSignIn);
        if (getUserName().equals("") || getUserName().equals("readonly")) {
            signOutItem.setVisible(false);
            signInItem.setVisible(true);
        } else {
            signOutItem.setVisible(true);
            signInItem.setVisible(false);
        }
        Log.i("usernameu",getUserName());
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
        writingAdapter = new StoryListAdapter(writingRef, MainActivity.this, R.layout.listitem_main_writing);
        readingAdapter = new StoryListAdapter(readingRef, MainActivity.this, R.layout.listitem_main_reading);

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }

    /**
     * Activity Methods
     */

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (username.equals("")) {
            setUserName("readonly");
            signIn();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        updateSignOutandInButtonVisibility();
        return true;
    }

    //Options Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.create_story) { //Create a new Story
            Intent createStory = new Intent(MainActivity.this, CreateStoryActivity.class);
            startActivity(createStory);
        }
        if (id == R.id.gPlusSignOut) { //Create a new Stor
            signOut();
        }
        if (id == R.id.gPlusSignIn) { //Create a new Story
            signIn();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("requestcode", Integer.toString(requestCode));
        switch (requestCode){
/*            case LOGIN: //Activity Result for Login Screen
                Log.i("requestcode", Integer.toString(requestCode));
                if (resultCode == RESULT_OK){
                    username = data.getStringExtra("username");
                    setUserName(username); //Save the username in sharedPreferences
                    updateSignOutandInButtonVisibility();
                    Log.i("LoginResult", "Logged in as " + username);
                } else { Log.i("LoginResult", "Failed to Login");
                    Toast.makeText(MainActivity.this, "Failed to login to Google account. You can only read stories.", Toast.LENGTH_SHORT).show();
                }
                break;
            case SIGNOUT:
                if (resultCode == RESULT_OK){
                    setUserName("readonly");
                    updateSignOutandInButtonVisibility();
                    Toast.makeText(MainActivity.this, "Signout Successful", Toast.LENGTH_SHORT).show();
                } else { Log.i("SignoutResult", "Failed to signout");
                    Toast.makeText(MainActivity.this, "Failed to signout of Google account. Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;*/
            case REQUEST_CODE_RESOLVE_ERR:
                if (resultCode == RESULT_OK) {
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
        }
        Log.i("username",getUserName());
    }


}
