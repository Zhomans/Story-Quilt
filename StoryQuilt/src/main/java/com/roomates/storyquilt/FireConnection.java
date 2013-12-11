package com.roomates.storyquilt;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * Created by chris on 12/8/13.
 */
public class FireConnection {
    final static String url = "https://storyquilt.firebaseio.com";
    static User user;

    //Creates firebase ref given a tuple of children
    public static Firebase create(String... children) {
        Firebase firebase = new Firebase(url);
        for (String child : children){
            firebase = firebase.child(child);
        }
        return firebase;
    }

    //Pushes Story to List
    public static void pushStoryToList(Firebase firebase, Story value){
        Firebase ref = firebase.push();
        value.setId(ref.getName());
        ref.setValue(value);
    }
    //Pushes User to List
    public static void pushUserToList(User value){
        create("users", User.formatEmail(value.email)).setValue(value);
    }

    public static User getUserAt(Firebase firebase){
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            public void onCancelled(FirebaseError error) {
            }
        });
        return user;
    }

    //Push Piece to a Story
    public static void  pushPieceToStory(Story story, Piece value){
        Firebase ref = create("stories", story.id, "pieces", String.valueOf(story.pieces.size()));
        ref.setValue(value);
    }
}
