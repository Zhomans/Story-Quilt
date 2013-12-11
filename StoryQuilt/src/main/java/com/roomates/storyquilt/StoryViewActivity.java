package com.roomates.storyquilt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zach on 12/7/13.
 */
public class StoryViewActivity extends Activity {
    //Views
    EditText newPost;
    Button addButton, quitButton;
    TextView storyTitle, recentPosts;

    Story thisStory;
    User currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Set up Activity Views
        bindViews();

        //Get Current Story
        getStory();
        //XXX Empty Story
        Story emptyStory = new Story("Now", "Empty Story", 2, 2, 2, new ArrayList<Piece>());
        thisStory = emptyStory;

        //Get Current User to Check for Reader or Writer
        //getUser();
        //XXX Empty User
        User emptyUser = new User("email", "Me", 20, 0, 0, false, new ArrayList<Story>(), new ArrayList<Story>());
        currentUser = emptyUser;

        //Checks to see whether you are a reader or writer. If reader, show full story and don't show postCount, edittext and button.
        if (currentUser.isReader(thisStory)) {
            //If Reader

            //Populate Activity Views' Text
            populateViewsAsReader();



        } else {
            //If Writer or New

            //Populate Activity Views' Text
            populateViewsAsWriter();

            //Add Button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Editable newPostText = newPost.getText();

                    //Check to see if last post is by this user and don't let them if they are
                    if (thisStory.checkMostRecentPoster(currentUser)){
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
                                thisStory.addPiece(newPiece);

                                //Make User a Writer if New
                                if (!currentUser.isWriter(thisStory)){
                                    currentUser.becomeWriter(thisStory);
                                }

                                //Refresh Views

                            } else {
                                AlertDialog overWordLimit = new AlertDialog.Builder(StoryViewActivity.this).create();
                                overWordLimit.setCancelable(false); // This blocks the 'BACK' button
                                overWordLimit.setMessage(getString(R.string.activity_story_overWordLimit).concat(String.valueOf(thisStory.getTextLimit())));
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

            //Quit Button
            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Makes you a reader in the story, instead of a writer

                    //XXX Add in confirmation Dialog box
//                    if (currentUser.isWriter(thisStory)) {
//                        currentUser.becomeReaderFromWriter(thisStory);
//                    } else {
//                        currentUser.becomeReader(thisStory);
//                    }
                    //XXX Update views to Reader
                }
            });
        }
    }

    /**
     Binding Views for StoryView from XML
     */
    private void bindViews(){
        newPost = (EditText)findViewById(R.id.activity_story_edittext);

        addButton = (Button)findViewById(R.id.activity_story_button);
        quitButton = (Button)findViewById(R.id.activity_story_postsLater_textview);

        storyTitle = (TextView)findViewById(R.id.activity_story_title_textview);
        recentPosts = (TextView)findViewById(R.id.activity_story_recentPosts_textview);
    }

    /**
      Populating Views for StoryView from XML
     */
    private void populateViewsAsWriter(){
        storyTitle.setText(thisStory.getTitle());
        quitButton.setText("... "+String.valueOf(thisStory.length())+" Posts Later ...");
        recentPosts.setText(thisStory.recentPosts());
    }
    private void populateViewsAsReader(){
        storyTitle.setText(this.getTitle());
        quitButton.setVisibility(View.INVISIBLE);
        recentPosts.setText(thisStory.fullStory());
        newPost.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
    }

    /**
     Checks Input str Word Count. true If Not Greater Than Text Limit
     */
    private boolean checkWordCount(String str){
        return thisStory.getTextLimit() >= (str.length() - str.replaceAll(" ", "").length()+1);
    }

    /**
     * Get Story from Intent
     */
    private void getStory(){
        Intent inStory = getIntent();
        Story story = (Story) inStory.getSerializableExtra("story");
    }

}
