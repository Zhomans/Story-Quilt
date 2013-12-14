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
public class ActivityStoryView extends Activity {
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
        FireHandler.create("story", getIntent().getStringExtra("story")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //Every time the story is updated
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
                    Toast.makeText(ActivityStoryView.this, getString(R.string.activity_story_twiceInARow), Toast.LENGTH_SHORT).show();
                } else {
                    if (newPostText != null || !newPostText.toString().equals("")){
                        if (curStory.checkWordCount(newPostText.toString())){
                            //Add new Piece
                            Piece newPiece = new Piece(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("personFirstName", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                            curStory.addPiece(newPiece);
                            //FireHandler.updateStoryInFirebase(curStory);

                            //Make User a Writer if New
                            if (!userHandler.isWriter(curStory.id)){
                                userHandler.becomeWriter(curStory.id);
                                curStory.writers.add(userHandler.user.email);
                            }
                        } else {
                            Toast.makeText(ActivityStoryView.this, getString(R.string.activity_story_overWordLimit).concat(String.valueOf(curStory.getTextLimit())), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    private void setQuitButton(){
        quitButton.setClickable(true);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Makes you a reader in the story, instead of a writer
                new AlertDialog.Builder(ActivityStoryView.this)
                        .setTitle("Are you sure you want to see the whole story?")
                        .setMessage("If you click okay, you will no longer be able to post, but you will be able to see the whole story.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userHandler.becomeReaderFromWriter(curStory.id);
                                curStory.writers.remove(userHandler.user.email);
                                ActivityStoryView.this.populateViewsAsReader();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onStop(){
        userHandler.stopConnection();
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
                curStory.writers.remove(userHandler.user.email);
                break;

            case R.id.join_story: //Join an Existing Story
                curStory.writers.add(userHandler.user.email);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
