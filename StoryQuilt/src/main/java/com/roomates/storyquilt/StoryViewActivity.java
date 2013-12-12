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
import android.widget.Toast;

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
    Story curStory;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Create the User Handler
        userHandler = new UserHandler(this);

        //Bind Activity Views
        bindViews();

        //Create the Story Firebase Connection
        FireConnection.create("story", getIntent().getStringExtra("story")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //Everytime the story is updated
                curStory= dataSnapshot.getValue(Story.class);
                if (userHandler.isReader(curStory.id)) populateViewsAsReader();
                else  populateViewsAsWriter();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("Firebase", "Story connection failed:" + firebaseError.getMessage());
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
        storyTitle.setText(curStory.title);
        recentPosts.setText(curStory.recentPosts());
        quitButton.setText("... "+String.valueOf(curStory.pieces.size())+" Posts Later ...");
        setAddButton();
        setQuitButton();
    }
    private void populateViewsAsReader(){
        storyTitle.setText(this.getTitle());
        recentPosts.setText(curStory.fullStory());

        quitButton.setVisibility(View.INVISIBLE);
        newPost.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.INVISIBLE);
    }
    private void setAddButton(){
        //Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable newPostText = newPost.getText();

                //Check to see if last post is by this user and don't let them if they are
                if (curStory.checkMostRecentPoster(userHandler.user)){
                    //Display Error toast stating that you can't post twice in a row.
                    Toast.makeText(StoryViewActivity.this, getString(R.string.activity_story_twiceInARow), Toast.LENGTH_SHORT).show();
                } else {
                    if (newPostText != null){
                        if (curStory.checkWordCount(newPostText.toString())){
                            //Other filters

                            //Add new Piece
                            Piece newPiece = new Piece(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                            curStory.addPiece(newPiece);

                            //Make User a Writer if New
                            if (!userHandler.isWriter(curStory.id)){
                                userHandler.becomeWriter(curStory.id);
                            }

                            //Refresh Views

                        } else {
                            //
                            Toast.makeText(StoryViewActivity.this, getString(R.string.activity_story_overWordLimit).concat(String.valueOf(curStory.getTextLimit())), Toast.LENGTH_SHORT).show();
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


    //Options Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.story, menu);
        return true;
    }
    @Override

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.join_story).setVisible(userHandler.isReader(curStory.id));
        menu.findItem(R.id.leave_story).setVisible(userHandler.isWriter(curStory.id));
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
