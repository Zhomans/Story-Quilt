package com.roomates.storyquilt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zach on 12/7/13.
 */
public class StoryViewActivity extends Activity {
    //Views
    EditText newPost;
    Button addButton, quitButton;
    TextView storyTitle, recentPosts;

    StoryClass thisStory;
    UserClass currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Set up Activity Views
        bindViews();

        //Get Current Story
        getStory();
        //XXX Empty Story
        StoryClass emptyStory = new StoryClass("Now", "Empty Story", 2, 2, 2, new ArrayList<PieceClass>());
        thisStory = emptyStory;

        //Get Current User to Check for Reader or Writer
        //getUser();
        //XXX Empty User
        UserClass emptyUser = new UserClass("Me", 20, 0, 0, false, new ArrayList<StoryClass>(), new ArrayList<StoryClass>());
        currentUser = emptyUser;

        //Checks to see whether you are a reader or writer. If reader, show full story and don't show postCount, edittext and button.
        if (currentUser.isReader(thisStory)) {
            //If Reader




        } else {
            //If Writer or New

            //Populate Activity Views' Text
            populateViews();

            //Add Button
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Editable newPostText = newPost.getText();
                    if (newPostText != null){
                        if (checkWordCount(newPostText.toString())){
                            //Check to see if last post is by this user and don't let them if they are

                            //Other filters

                            //Add new Piece
                            PieceClass newPiece = new PieceClass(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                            thisStory.addPiece(newPiece);

                            //Make User a Writer if New
                            if (!currentUser.isWriter(thisStory)){
                                currentUser.becomeWriter(thisStory);
                            }

                            //Refresh Views
                        } else {
                            //Display Error box stating that over word limit
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
                    if (currentUser.isWriter(thisStory)) {
                        currentUser.becomeReaderFromWriter(thisStory);
                    } else {
                        currentUser.becomeReader(thisStory);
                    }
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
    private void populateViews(){
        storyTitle.setText(thisStory.getTitle());
        quitButton.setText("... "+String.valueOf(thisStory.getLength())+" Posts Later ...");
        recentPosts.setText(thisStory.getRecentPosts());
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
        StoryClass story = (StoryClass) inStory.getSerializableExtra("story");
    }

}
