package com.roomates.storyquilt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * Created by zach on 12/7/13.
 */
public class StoryViewActivity extends Activity {
    //Views
    EditText newPost;
    Button addButton;
    TextView storyTitle, recentPosts, quitButton;

    UserHandler userHandler;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        userHandler = new UserHandler(this);

        bindViews();

        FireConnection.create("story", getIntent().getStringExtra("story")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Story curStory = dataSnapshot.getValue(Story.class);
                if (userHandler.isReader(curStory.id)){
                    populateViewsAsReader();
                } else {
                    populateViewsAsWriter();
                    setAddButton();
                }
                setQuitButton();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("Firebase", "Story connection failed");
            }
        });
    }

    private void setAddButton(){
        //Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable newPostText = newPost.getText();

                //Check to see if last post is by this user and don't let them if they are
                if (storyHandler.story.checkMostRecentPoster(userHandler.user)){
                    //Display Error box stating that you can't post twice in a row.
                    AlertDialog twiceInARow = new AlertDialog.Builder(StoryViewActivity.this).create();
                    twiceInARow.setCancelable(false); // This blocks the 'BACK' button
                    twiceInARow.setMessage(getString(R.string.activity_story_twiceInARow));
                    twiceInARow.setButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    twiceInARow.show();
                } else {
                    if (newPostText != null){
                        if (checkWordCount(newPostText.toString())){
                            //Other filters

                            //Add new Piece
                            Piece newPiece = new Piece(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                            storyHandler.story.addPiece(newPiece);

                            //Make User a Writer if New
                            if (!userHandler.isWriter(storyHandler.story.id)){
                                userHandler.becomeWriter(storyHandler.story.id);
                            }

                            //Refresh Views

                        } else {
                            AlertDialog overWordLimit = new AlertDialog.Builder(StoryViewActivity.this).create();
                            overWordLimit.setCancelable(false); // This blocks the 'BACK' button
                            overWordLimit.setMessage(getString(R.string.activity_story_overWordLimit).concat(String.valueOf(storyHandler.story.getTextLimit())));
                            overWordLimit.setButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            overWordLimit.show();
                        }
                    }
                }
            }
        });
    }

    private void setQuitButton(){
        //Quit Button
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Makes you a reader in the story, instead of a writer

                //XXX Add in confirmation Dialog box
//                    if (userHandler.isWriter(storyHandler.story)) {
//                        userHandler.becomeReaderFromWriter(storyHandler.story);
//                    } else {
//                        userHandler.becomeReader(storyHandler.story);
//                    }
                //XXX Update views to Reader
            }
        });
    }


    /**
     Binding Views for StoryView from XML
     */
    private void bindViews(){
        newPost = (EditText)findViewById(R.id.activity_story_edittext);

        addButton = (Button)findViewById(R.id.activity_story_button);
        quitButton = (TextView)findViewById(R.id.activity_story_postsLater_textview);

        storyTitle = (TextView)findViewById(R.id.activity_story_title_textview);
        recentPosts = (TextView)findViewById(R.id.activity_story_recentPosts_textview);
    }

     /**
      Populating Views for StoryView from XML
     */
    private void populateViewsAsWriter(){
        storyTitle.setText(storyHandler.story.getTitle());
        quitButton.setText("... "+String.valueOf(storyHandler.story.length())+" Posts Later ...");
        recentPosts.setText(storyHandler.story.recentPosts());
    }
    private void populateViewsAsReader(){
        storyTitle.setText(this.getTitle());
        quitButton.setVisibility(View.INVISIBLE);
        recentPosts.setText(storyHandler.story.fullStory());
        newPost.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
    }

    /**
     Checks Input str Word Count. true If Not Greater Than Text Limit
     */
    private boolean checkWordCount(String str){
        return storyHandler.story.getTextLimit() >= (str.length() - str.replaceAll(" ", "").length()+1);
    }



    //Options Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.story, menu);
        return true;
    }
    @Override

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.join_story).setVisible(userHandler.isReader(storyHandler.story.id));
        menu.findItem(R.id.leave_story).setVisible(userHandler.isWriter(storyHandler.story.id));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_story: //Leave a new Story
                break;

            case R.id.join_story: //Join an Existing Story
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
