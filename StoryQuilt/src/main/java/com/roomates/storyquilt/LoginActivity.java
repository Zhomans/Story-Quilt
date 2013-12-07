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
import android.widget.Toast;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

/**
 * Created by chris on 12/4/13.
 */
public class LoginActivity extends Activity implements ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        OnConnectionFailedListener, View.OnClickListener{

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final String TAG = "LoginActivity";
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPlusClient = new PlusClient.Builder(this, this, this)
                //.setActions("http://schemas.google.com/CreateActivity") //my (Mac-I) phone always crashes on this saying : "java.lang.NoSuchMethodError: Lcom/google/android/gms/plus/PlusClient$Builder;.setActions"
                .setScopes(Scopes.PLUS_LOGIN)  // Space separated list of scopes
                .build();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            if(extras.getString("signout")!=null){
                signOut();
            }
        }else{
            findViewById(R.id.sign_in_button).setOnClickListener(this);
            // Progress bar to be displayed if the connection failure is not resolved.
            mConnectionProgressDialog = new ProgressDialog(this);
            mConnectionProgressDialog.setMessage("Signing in...");
        }
    }

    public void signOut(){
        mPlusClient.connect();
        mPlusClient.clearDefaultAccount();
        mPlusClient.revokeAccessAndDisconnect(this);
        mPlusClient.disconnect();
        mPlusClient.connect();
        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        setResult(RESULT_OK, mainActivityIntent);
        finish();
    }

    @Override
    public void onAccessRevoked(ConnectionResult status) {
// mPlusClient is now disconnected and access has been revoked.
// We should now delete any data we need to comply with the
// developer properties. To reset ourselves to the original state,
// we should now connect again. We don't have to disconnect as that
// happens as part of the call.
        getSharedPreferences("StoryQuilt",MODE_PRIVATE).edit().putString("username", "readonly").commit();
        mPlusClient.connect();
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

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

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
        }
        // Save the result and resolve the connection failure upon a user click.
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        String accountName = mPlusClient.getAccountName();
        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainActivityIntent.putExtra("username", accountName);
        setResult(RESULT_OK, mainActivityIntent);
        finish();
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
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
        //some other button (not create_story) but It crashed without a real item
        if (id == R.id.create_story) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}