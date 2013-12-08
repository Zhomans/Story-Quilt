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

/**
 * Created by zach on 12/7/13.
 */
public class StoryViewActivity extends Activity {
    //Views
    EditText newPost;
    Button addButton, quitButton;
    TextView storyTitle, recentPosts;

    StoryClass thisStory;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Set up Activity Views
        bindViews();

        //Get Current Story
        getStory();
        //XXX Empty Story
        StoryClass emptyStory = new StoryClass("Now", "Empty Story", 2, 2, 2, new PieceClass[]{});

        thisStory = emptyStory;

        //Populate Activity Views' Text
        populateViews();

        //Checks to see whether you are a reader or writer. If reader, show full story and don't show postCount, edittext and button.

        //Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable newPostText = newPost.getText();
                if (newPostText != null){
                    if (checkWordCount(newPostText.toString())){
                        //Other filters
                        PieceClass newPiece = new PieceClass(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                        //Add Piece to Story
                        //Makes you a writer if you aren't yet
                    };
                }
            }
        });

        //Quit Button
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Makes you a reader in the story, instead of a writer
            }
        });
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
     Checks Input str Word Count. true If Not Greater Than Text Limit.
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
