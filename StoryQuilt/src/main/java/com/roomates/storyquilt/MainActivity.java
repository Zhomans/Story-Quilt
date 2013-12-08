package com.roomates.storyquilt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {
    //Intent Request Codes
    private final int LOGIN = 0; //Request code for logging in and getting email
    private final int SIGNOUT = 1; //Request code for logging in and getting email
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private View signInButton;

    //User's name from the google account

    //the settings/actionbar menu
    Menu menu;

    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase writingRef, readingRef;

    //google plus api
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        if (getEmail().equals("") || getEmail().equals("readonly")) {
            signInButton.setVisibility(View.VISIBLE);
        }

        mPlusClient = new PlusClient.Builder(this, this, this)
                //.setActions("http://schemas.google.com/CreateActivity"); //my (Mac-I) phone always crashes on this saying : "java.lang.NoSuchMethodError: Lcom/google/android/gms/plus/PlusClient$Builder;.setActions"
                .setScopes(Scopes.PLUS_PROFILE, Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        //Check if logged in
        if (getEmail().equals("readonly")) {
            Toast.makeText(this, "You may only read stories, please sign in to contribute", Toast.LENGTH_LONG).show();
        } else if (getEmail().equals("")) {
                setEmail("readonly");
//                signIn();
        }

        //Set up MainActivity Views
        setListViews();
        setFireBaseRefs();
        setListAdapters();
    }

    @Override

    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
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

    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    //Method for getting email
    private String getEmail(){
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email", "");
    }

    //Method for saving email
    private void setEmail(String value){
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("email", value).commit();
    }

    private String getPersonFirstName(){
        return getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", "");
    }

    //Method for saving email
    private void setPersonFirstName(String value){
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("personFirstName", value).commit();
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
            setEmail("readonly");
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
        signInButton.setVisibility(View.GONE);
        mConnectionProgressDialog.dismiss();
        String personFirstName = mPlusClient.getCurrentPerson().getName().getGivenName();
        if (!getEmail().equals(mPlusClient.getAccountName())) {
            Toast.makeText(this, personFirstName + ", you connected!", Toast.LENGTH_LONG).show();
        }
        setEmail(mPlusClient.getAccountName());
        setPersonFirstName(personFirstName);
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
        if (getEmail().equals("") || getEmail().equals("readonly")) {
            signOutItem.setVisible(false);
            signInItem.setVisible(true);
        } else {
            signOutItem.setVisible(true);
            signInItem.setVisible(false);
        }
        Log.i("usernameu", getEmail());
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
        readingRef = FireConnection.create("users", "reading");
        writingRef = FireConnection.create("users", "writing");
    }

    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        writingAdapter = new StoryListAdapter(writingRef, MainActivity.this, R.layout.listitem_main_writing);
        readingAdapter = new StoryListAdapter(readingRef, MainActivity.this, R.layout.listitem_main_story);

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }

    /**
     * Activity Methods
     */

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
        switch (item.getItemId()) {
            case R.id.create_story: //Create a new Story
                Intent createStory = new Intent(MainActivity.this, CreateStoryActivity.class);
                startActivity(createStory);

            case R.id.join_story: //Join an Existing Story
                Intent joinStory = new Intent(MainActivity.this, JoinStoryActivity.class);

            case R.id.gPlusSignIn: //Sign in Google+
                signOut();

            case R.id.gPlusSignOut:
                signIn();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("requestcode", Integer.toString(requestCode));
        switch (requestCode){
            case REQUEST_CODE_RESOLVE_ERR:
                if (resultCode == RESULT_OK) {
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
        }
        Log.i("email",getEmail());
    }
}
