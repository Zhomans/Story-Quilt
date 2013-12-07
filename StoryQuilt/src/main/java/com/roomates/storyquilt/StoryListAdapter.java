package com.roomates.storyquilt;

import android.app.Activity;
import android.view.View;

import com.firebase.client.Query;

/**
 * Created by chris on 12/4/13.
 */
public class StoryListAdapter extends FirebaseListAdapter<StoryClass> {
    //Constructor for the adapter
    public StoryListAdapter(Query ref, Activity activity, int layout){
        super(ref, StoryClass.class, layout, activity);
    }

    @Override
    //Required method by FirebaseListAdapter
    protected void populateView(View view, StoryClass story){
        //TO-DO
    }
}
