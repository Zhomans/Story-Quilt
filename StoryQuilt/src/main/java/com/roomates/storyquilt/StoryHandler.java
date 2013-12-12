package com.roomates.storyquilt;


import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * Created by chris on 12/9/13.
 */
public class StoryHandler {
    //Handling the Story
    Story story;

    public StoryHandler(String id){
        updateStoryFromFirebase(id);
    }

    /**
     * Firebase Information
     */
    //Update User Class in the firebase
    public void updateStoryInFirebase(){
        FireConnection.pushStoryToList(this.story);
    }

    //Get User Class in the firebase
    public void updateStoryFromFirebase(String id){
        FireConnection.create("stories", id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StoryHandler.this.story = snapshot.getValue(Story.class);
                Log.i("UserHandler", "updating User");
                Log.i("UserHandler", "updating User:" + (StoryHandler.this.story));
            }

            public void onCancelled(FirebaseError error) {
            }
        });
    }


}
