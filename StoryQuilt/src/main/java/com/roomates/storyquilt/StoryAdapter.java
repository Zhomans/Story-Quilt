package com.roomates.storyquilt;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

/**
 * Created by chris on 12/4/13.
 */
public class StoryAdapter extends FirebaseListAdapter<StoryClass> {
    //Constructor for the adapter
    public StoryAdapter(Query ref, Activity activity, int layout){
        super(ref, StoryClass.class, layout, activity);
    }

    @Override
    //Required method by FirebaseListAdapter
    protected void populateView(View view, StoryClass story){
        ((TextView) view.findViewById(R.id.story_title_textview)).setText(story.getTitle());
    }
}
