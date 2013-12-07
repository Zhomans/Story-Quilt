package com.roomates.storyquilt;

import android.app.Activity;
import android.os.Bundle;
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
        //XXX Empty Story
        StoryClass emptyStory = new StoryClass("Now", "Empty Story", 2, 2, 2, new PieceClass[]{});

        thisStory = emptyStory;

        //Populate Activity Views' Text
        populateViews();
    }

    /**
     Binding Views for StoryView from XML
     */
    void bindViews(){
        newPost = (EditText)findViewById(R.id.activity_story_edittext);

        addButton = (Button)findViewById(R.id.activity_story_button);
        quitButton = (Button)findViewById(R.id.activity_story_postsLater_textview);

        storyTitle = (TextView)findViewById(R.id.activity_story_title_textview);
        recentPosts = (TextView)findViewById(R.id.activity_story_recentPosts_textview);
    }

    /**
      Populating Views for StoryView from XML
     */
    void populateViews(){
        storyTitle.setText(thisStory.getTitle());
        quitButton.setText("... "+thisStory.getLength()+" Posts Later ...");
        recentPosts.setText(thisStory.getRecentPosts());
    }



}
