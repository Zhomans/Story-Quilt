package com.roomates.storyquilt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

/**
 * Created by chris on 12/8/13.
 */
public abstract class GooglePlusActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {

    //Google Plus API Classes Used
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    //Request Codes for Intents
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    //Managing Periodic Connection Status and User Info
    String previousEmail = "";
    String personFirstName = "";
    Integer personAge = 0;


    /**
     * Methods for Activity
     * onCreate, onActivityResult, onStart, onPause, onResume
     * More can be implemented from inheritee
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreateExtended(savedInstanceState);
        super.onCreate(savedInstanceState);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(Scopes.PLUS_PROFILE, Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");


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
        Log.i("email", previousEmail);

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
        personFirstName = mPlusClient.getCurrentPerson().getName().getGivenName();
        if (!previousEmail.equals(mPlusClient.getAccountName())) {
            Toast.makeText(this, personFirstName + ", you connected!", Toast.LENGTH_LONG).show();
            previousEmail = mPlusClient.getAccountName();
            personAge = mPlusClient.getCurrentPerson().getAgeRange().getMin();
        }
        onConnectionStatusChanged();
    }
    //Google+ Connection Disconnected
    public void onDisconnected() {
        Log.d("GooglePlusActivity", "disconnected");
    }
    //Google+ Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("GooglePlusAcitivity Connection Failed",result.toString());
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

            mPlusClient.disconnect();
            mPlusClient.connect();
            Toast.makeText(this, "Successfully Signed Out", Toast.LENGTH_LONG).show();
        }
        onConnectionStatusChanged();
    }



    /**
     * Required by View.onClickListener
     */
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



    /**
     * Methods to be implemented by inheritee
     */
    public abstract void onConnectionStatusChanged();
    public abstract void onActivityResultExtended(int requestCode, int resultCode, Intent data);
    public abstract void onCreateExtended(Bundle savedInstanceState);
}
