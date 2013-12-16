package com.roomates.storyquilt;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    //Views
    TextView historyDisplay, submissionDisplay;
    EditText storyTitle, starterText;
    SeekBar historyLength, submissionLength;
    ToggleButton languageFilter;
    Button create;

    //SeekBar Constants
    int SUBMISSION_MAX = 25; //Words
    int SUBMISSION_MIN = 1;
    int SUBMISSION_DEFAULT = 3;//Word slider default

    double HISTORY_TICK = 0.2;
    int HISTORY_MAX = (int)(10/HISTORY_TICK);
    int HISTORY_DEFAULT = SUBMISSION_DEFAULT;

    //User Information
    UserHandler userHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

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
        historyLength.setMax(HISTORY_MAX);
        historyLength.setProgress(HISTORY_DEFAULT);

        submissionLength.setMax(SUBMISSION_MAX);
        submissionLength.setProgress(SUBMISSION_DEFAULT);

        submissionLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += SUBMISSION_MIN;
                submissionDisplay.setText("Submission Length: " + progress + " words ");
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
    }
    public void updateHistorySeekBar(){
        int progress = (int)(historyLength.getProgress() + SUBMISSION_MIN * HISTORY_TICK);
        long value = Math.round(progress * HISTORY_TICK * (submissionLength.getProgress() + SUBMISSION_MIN));

        String postDisplay = String.valueOf(progress * HISTORY_TICK);
        if (postDisplay.contains(".")){
            String[] posts = postDisplay.split("\\.");
            postDisplay = posts[0];
            postDisplay += "." + posts[1].substring(0,1);
        } else {
            postDisplay =  String.valueOf(progress * HISTORY_TICK);}
        historyDisplay.setText("History Length: " + postDisplay + " posts (" + value + " words)");
    }

    /**
     * Methods for binding events to Create Story Button
     * On Creating a story...
     */
    private void setupCreateButton(){
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Firebase Ref
                //Grab Text
                String title =String.valueOf(storyTitle.getText());
                String starter = String.valueOf(starterText.getText());

                //Check if title exists
                if (title.equals("")) title = starter;
                if (starter.equals("") || starter.split(" ").length > (submissionLength.getProgress() + 1)) {
                    Toast.makeText(ActivityCreateStory.this, "Please give the story an initial post or appropriate length!", Toast.LENGTH_SHORT).show();
                }
                else {
                    ArrayList<Piece> curPieces = new ArrayList<Piece>();
                    curPieces.add(new Piece(
                            getSharedPreferences("StoryQuilt",MODE_PRIVATE).getString("email","Anonymous"),
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(starterText.getText())
                    ));
                    HashSet<String> writers = new HashSet<String>();
                    writers.add(getSharedPreferences("StoryQuilt", MODE_PRIVATE).getString("email","Anonymous"));

                    //String lastUpdated, String title, int ageLimit, int historyLimit, int textLimit, Piece[] pieces
                    Story curStory = new Story(String.valueOf(System.currentTimeMillis()),
                                    title,
                                    (languageFilter.isChecked())? 13:0,
                                    (int) Math.round(historyLength.getProgress() * HISTORY_TICK * submissionLength.getProgress()),
                                    submissionLength.getProgress(),
                                    curPieces,
                                    writers
                                    );

                    //Push to Firebase
                    userHandler.becomeWriter(FireHandler.pushStoryToList(curStory));
                    //End Activity
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //hide/show menu items
        (menu.findItem(R.id.gPlusSignOut)).setVisible(userHandler.isConnected());
        (menu.findItem(R.id.gPlusSignIn)).setVisible(!userHandler.isConnected());
        return true;
    }

    //Options Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.create_story) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
