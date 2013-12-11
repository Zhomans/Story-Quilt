package com.roomates.storyquilt;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by chris on 12/4/13.
 */
public abstract class StoryListAdapter extends FirebaseListAdapter<Story> {

    //Constructor for the adapter
    public StoryListAdapter(Query ref, Activity activity, int layout){
        super(ref, Story.class, layout, activity);
    }

    @Override
    //Required method by FirebaseListAdapter
    protected void populateView(View view, Story story){
        ((TextView) view.findViewById(R.id.story_title_textview)).setText(story.getTitle());
    }
}
