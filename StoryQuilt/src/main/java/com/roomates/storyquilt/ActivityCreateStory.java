package com.roomates.storyquilt;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by chris on 12/4/13.
 */
public class ActivityCreateStory extends Activity {
    //Views From XML
    TextView historyDisplay, submissionDisplay;
    EditText storyTitle, starterText;
    SeekBar historyLength, submissionLength;
    ToggleButton languageFilter;
    Button create;

    //SeekBar Constants
    int SUBMISSION_MAX = 25; //Words
    int SUBMISSION_MIN = 1; //Offsetting the SeekBar
    int SUBMISSION_DEFAULT = 4 - SUBMISSION_MIN;//Word slider default

    double HISTORY_TICK = 0.2;
    int HISTORY_MAX = (int)(10/HISTORY_TICK);
    int HISTORY_DEFAULT = (int)(1/HISTORY_TICK);

    //User Information
    UserHandler userHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Setting the XML
        setContentView(R.layout.activity_create);

        //Keyboard AutoHide
        autoHideKeyboard(findViewById(R.id.parent));

        //setup user handler
        userHandler = new UserHandler(this);

        //Set up Activity Views
        bindViews();

        //Seek Bar Setup On Change
        setupSeekBars();

        //Create Story
        setupCreateButton();
    }

    /**
     Setting up Views for CreateStory from XML
     */
    private void bindViews(){
        storyTitle = (EditText)findViewById(R.id.activity_create_storyTitle_textfield);
        starterText = (EditText)findViewById(R.id.activity_create_starterText_textfield);

        submissionDisplay = (TextView)findViewById(R.id.activity_create_submissionLength_textview);
        submissionDisplay.setText("Submission Length: " + SUBMISSION_DEFAULT + " words");

        historyDisplay = (TextView)findViewById(R.id.activity_create_historyLength_textview);
        historyDisplay.setText("History Length: " + Math.round(SUBMISSION_DEFAULT * HISTORY_TICK) + " words");

        historyLength = (SeekBar)findViewById(R.id.activity_create_historyLength_seekBar);
        submissionLength = (SeekBar)findViewById(R.id.activity_create_submissionLength_seekBar);

        languageFilter = (ToggleButton)findViewById(R.id.activity_create_languageFilter_toggleButton);
        create = (Button)findViewById(R.id.activity_create_create_button);
    }

    /**
     Setting up SeekBars
     */
    private void setupSeekBars(){
        submissionLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += SUBMISSION_MIN;
                submissionDisplay.setText("Word Limit: " + progress + " words ");
                updateHistorySeekBar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        historyLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateHistorySeekBar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        historyLength.setMax(HISTORY_MAX);
        historyLength.setProgress(HISTORY_DEFAULT);

        submissionLength.setMax(SUBMISSION_MAX);
        submissionLength.setProgress(SUBMISSION_DEFAULT);

        updateHistorySeekBar();
    }
    public void updateHistorySeekBar(){
        int numPosts = (int) (historyLength.getProgress() * HISTORY_TICK);
        long value = Math.round(numPosts * (submissionLength.getProgress() + SUBMISSION_MIN));

        String postDisplay = String.valueOf(value);
        if (postDisplay.contains(".")){
            String[] posts = postDisplay.split("\\.");
            postDisplay = posts[0];
            postDisplay += "." + posts[1].substring(0,1);
        } else {
            postDisplay =  String.valueOf(numPosts);}
        historyDisplay.setText("Posts Visible: " + postDisplay + " posts (" + value + " words)");
    }

    /**
     * Methods for binding events to Create Story Button
     * On Creating a story...
     */
    private void setupCreateButton(){
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get user input
                String starter = String.valueOf(starterText.getText());
                String title = String.valueOf(storyTitle.getText()).equals("") ? starter:(String.valueOf(storyTitle.getText()));

                //Check for validity of post.
                if (userHandler.user.email.equals("readonly")){
                    Toast.makeText(ActivityCreateStory.this, "Sign in to create a story!", Toast.LENGTH_SHORT).show();
                }
                else if (starter.equals("") || starter.split(" ").length > (submissionLength.getProgress() + 1)) {
                        Toast.makeText(ActivityCreateStory.this, "Please give the story an initial post or appropriate length!", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Piece> curPieces = new ArrayList<Piece>();
                    curPieces.add(new Piece(
                            userHandler.getEmail(),
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(starterText.getText())
                    ));
                    HashSet<String> writers = new HashSet<String>();
                    writers.add(userHandler.getEmail());

                    //String lastUpdated, String title, int ageLimit, int historyLimit, int textLimit, Piece[] pieces
                    Story curStory = new Story(String.valueOf(System.currentTimeMillis()),
                                    toTitleCase(title),
                                    (languageFilter.isChecked())? 13:0,
                                    (int) Math.round(historyLength.getProgress() * HISTORY_TICK * submissionLength.getProgress()),
                                    submissionLength.getProgress(),
                                    curPieces,
                                    writers
                                    );

                        userHandler.becomeWriter(FireHandler.pushStoryToList(curStory));

                    //End Activity
                    finish();
                }
            }
        });
    }

    /**
     * Methods Setting up Options Menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        (menu.findItem(R.id.gPlusSignOut)).setVisible(userHandler.isConnected());
        (menu.findItem(R.id.gPlusSignIn)).setVisible(!userHandler.isConnected());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Title Case Method
     */

    public String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    /**
     * Methods for auto-hiding the keyboard
     * @param view
     */
    public void autoHideKeyboard(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(ActivityCreateStory.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                autoHideKeyboard(innerView);
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
