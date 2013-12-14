package com.roomates.storyquilt;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

/**
 * Created by chris on 12/4/13.
 */
public abstract class AdapterStoryList extends AdapterFirebaseList<Story> {

    //Constructor for the adapter
    public AdapterStoryList(Query ref, Activity activity, int layout){
        super(ref, Story.class, layout, activity);
    }

    @Override
    //Required method by AdapterFirebaseList
    protected void populateView(View view, Story story){
        ((TextView) view.findViewById(R.id.story_title_textview)).setText(story.title);
        ((TextView) view.findViewById(R.id.story_posts_textview)).setText(story.pieces.size() + " posts");
    }
}
