package com.roomates.storyquilt;

import android.app.Activity;

import com.firebase.client.Firebase;

/**
 * Created by chris on 12/14/13.
 */
public abstract class ActivityFirebase extends Activity {
    /**
     * Activity Methods
     */
    @Override
    public void onStop(){
        super.onStop();
        Firebase.goOffline();
    }

    @Override
    public void onPause(){
        super.onPause();
        Firebase.goOffline();
    }

/*    @Override
    public void onStart(){
        super.onStart();
        Firebase.goOnline();
    }

    @Override
    public void onResume(){
        super.onResume();
        Firebase.goOnline();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Firebase.goOffline();
    }*/
}
