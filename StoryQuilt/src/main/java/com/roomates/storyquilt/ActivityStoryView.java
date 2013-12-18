package com.roomates.storyquilt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
public class ActivityStoryView  extends Activity {
    //Menu
    Menu menu;
    //Views
    EditText newPost;
    Button addButton;
    TextView storyTitle, recentPosts, quitButton;

    UserHandler userHandler;
    Story curStory;
    String passStory;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        passStory = getIntent().getStringExtra("story");
        setup();
    }

    private void setup() {
        setContentView(R.layout.activity_story);
        //Touch off keyboard
        setupUI(findViewById(R.id.parent));

        //Create the User Handler
        userHandler = new UserHandler(this);

        passStory = getIntent().getStringExtra("story");

        //Create the Story Firebase Connection
        FireHandler.create("stories", passStory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //Every time the story is updated
                curStory = dataSnapshot.getValue(Story.class);
                if (userHandler.isReader(curStory.id)) {
                    Log.i("reader?","true");
                    setContentView(R.layout.activity_story_following);
                    bindViewsAsReader();
                    populateViewsAsReader();
                } else {

                }                    Log.i("writer?","true");
                bindViewsAsWriter();
                populateViewsAsWriter();
                if (menu!=null){
                    Log.i("Debugger", "here");
                    menu.findItem(R.id.join_story).setVisible(userHandler.isReader(curStory.id));
                    menu.findItem(R.id.leave_story).setVisible(userHandler.isWriter(curStory.id));
                }
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
    private void bindViewsAsWriter(){
        newPost = (EditText)findViewById(R.id.activity_story_edittext);
        final TextView remaining = (TextView) findViewById(R.id.activity_story_text_remaining);
        remaining.setText(curStory.textLimit + " words left");
        //Text Watcher
        newPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    remaining.setText(curStory.textLimit + " words left.");
                } else {
                    int wordCount = s.toString().trim().split(" ").length;
                    if ((curStory.textLimit - wordCount) == 0){
                        remaining.setText("No words left.");
                    } else if (curStory.textLimit - wordCount < 0) {
                        remaining.setText(Math.abs(curStory.textLimit - wordCount) + " words over");
                    } else {
                        remaining.setText((curStory.textLimit - wordCount) + " words left");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addButton = (Button)findViewById(R.id.activity_story_button);
        quitButton = (TextView)findViewById(R.id.activity_story_postsLater_textview);

        storyTitle = (TextView)findViewById(R.id.activity_story_title_textview);
        recentPosts = (TextView)findViewById(R.id.activity_story_recentPosts_textview);
        recentPosts.setMovementMethod(new ScrollingMovementMethod());
    }
    /**
     Binding Views for StoryView from XML
     */
    private void bindViewsAsReader(){
        storyTitle = (TextView)findViewById(R.id.activity_story_title_textview);
        recentPosts = (TextView)findViewById(R.id.activity_story_recentPosts_textview);
        recentPosts.setMovementMethod(new ScrollingMovementMethod());
    }

     /**
      Populating Views for StoryView from XML
     */
    private void populateViewsAsWriter(){
        storyTitle.setText(curStory.title);
        recentPosts.setText(curStory.recentPosts());
        int curNum = curStory.pieces.size() - curStory.historyLimit/curStory.textLimit;
        quitButton.setText("... "+String.valueOf(curNum>0? curNum:0)+" Posts Later ...");
        setAddButton();
        setQuitButton();
    }
    private void populateViewsAsReader(){
        storyTitle.setText(this.getTitle());
        recentPosts.setText(curStory.fullStory());
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
                } else if (userHandler.user.email.equals("readonly")){
                    Toast.makeText(ActivityStoryView.this, "Sign in to post to a story!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (newPostText != null || !newPostText.toString().equals("")){
                        if (curStory.checkWordCount(newPostText.toString())){
                            //Add new Piece
                            Piece newPiece = new Piece(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email", ""), String.valueOf(System.currentTimeMillis()), newPostText.toString());
                            curStory.addPiece(newPiece);
                            //Make User a Writer if New
                            if (!userHandler.isWriter(curStory.id)){
                                userHandler.becomeWriter(curStory.id);
                                curStory.writers.add(userHandler.user.email);
                            }
                            newPost.setText("");
                            FireHandler.updateStoryInFirebase(curStory);

                        } else {
                            Toast.makeText(ActivityStoryView.this, getString(R.string.activity_story_overWordLimit).concat(String.valueOf(curStory.getTextLimit())), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void leaveStory() {
        //Makes you a reader in the story, instead of a writer
        new AlertDialog.Builder(ActivityStoryView.this)
                .setTitle("Are you sure you want to see the whole story?")
                .setMessage("If you click okay, you will no longer be able to post, but you will be able to see the whole story.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userHandler.becomeReaderFromWriter(curStory.id);
                        curStory.writers.remove(userHandler.user.email);
                        //ActivityStoryView.this.bindViewsAsReader();
                        //ActivityStoryView.this.populateViewsAsReader();

                        setup();
                       //breaks the back button
//                        Intent goToStory = new Intent(ActivityStoryView.this, ActivityStoryView.class);
//                        goToStory.putExtra("story",passStory);
//                        startActivity(goToStory);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
    }

    private void setQuitButton(){
        quitButton.setClickable(true);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveStory();
            }
        });
    }

    //Options Menu Setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.story, menu);
        if (!userHandler.isReader(curStory.id) && !userHandler.isWriter(curStory.id)) {
            menu.findItem(R.id.join_story).setVisible(true);
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_story: //Leave a new Story
                leaveStory();
                break;

            case R.id.join_story: //Join an Existing Story
                curStory.writers.add(userHandler.user.email);
                menu.findItem(R.id.join_story).setVisible(false);
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(ActivityStoryView.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
