package com.roomates.storyquilt;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;


/**
 * Created by chris on 12/8/13.
 *
 * A general abstract class that implements GooglePlus Account Signin for Android Apps.
 * Activities extend this abstract class and implement methods declared at the bottom of
 * file.
 */
public abstract class ActivityGooglePlus extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {
    //SignIn Button Id from Activity
    int signInButtonId;

    //Google Plus API Classes Used
    private ProgressDialog mConnectionProgressDialog;
    public PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    //Request Codes for Intents
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    //Managing Periodic Connection Status and User Info
    String previousEmail = "";


    /**
     * Methods for Activity
     * onCreate, onActivityResult, onStart, onPause, onResume
     * More can be implemented from inheritee
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(Scopes.PLUS_PROFILE, Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
        onCreateExtended(savedInstanceState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_RESOLVE_ERR:
                if (resultCode == RESULT_OK) {
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
        }
        onActivityResultExtended(requestCode, resultCode, data);
    }
    @Override
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



    /**
     * Methods required by GooglePlusAPI
     */
    //Google+ Connection successful
    public void onConnected(Bundle connectionHint) {
        mConnectionProgressDialog.dismiss();
        onConnectionStatusChanged();
        refreshViewOnConnection();
    }
    //Google+ Connection Disconnected
    public void onDisconnected() {
        onConnectionStatusChanged();
    }
    //Google+ Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
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
            mConnectionProgressDialog.dismiss();
        }
        // Save the intent so that we can start an activity when the user clicks
        // the sign-in button.
        mConnectionResult = result;
    }
    //On GooglePlus AccessRevoked
    public void onAccessRevoked(ConnectionResult status) {
        // mPlusClient is now disconnected and access has been revoked.
        // Trigger app logic to comply with the developer policies
    }


    /**
     * Signing in and out
     */
    //Signing In to Google+
    public void signIn() {
        mPlusClient.disconnect();
        if (!mPlusClient.isConnected()) { //Create a new Story
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    //signIn();
                    mPlusClient.connect();
                }
            }
        }
    }

    //Signing Out of Google+
    public void signOut() {
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
                @Override
                public void onAccessRevoked(ConnectionResult connectionResult) {
                    // mPlusClient is now disconnected and access has been revoked.
                    // Trigger app logic to comply with the developer policies
                }
            });

            mPlusClient.disconnect();
            refreshViewOnConnection();
            Toast.makeText(this, "Successfully signed out of StoryQuilt", Toast.LENGTH_LONG).show();
        }
        onConnectionStatusChanged();
    }

    /**
     * Required by View.onClickListener
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == signInButtonId) {
            signIn();
        }
    }

    /**
     * Methods to be implemented by inheritee
     */
    public abstract void onConnectionStatusChanged();
    public abstract void onActivityResultExtended(int requestCode, int resultCode, Intent data);
    public abstract void onCreateExtended(Bundle savedInstanceState);
    public abstract void getUserInformation();
    public abstract void refreshViewOnConnection();
}
