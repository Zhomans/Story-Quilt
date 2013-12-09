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
import android.widget.ToggleButton;

import com.firebase.client.Firebase;

/**
 * Created by chris on 12/4/13.
 */
public class CreateStoryActivity extends Activity{
    //Views
    TextView historyDisplay, submissionDisplay;
    EditText storyTitle, starterText;
    SeekBar historyLength, submissionLength;
    ToggleButton languageFilter;
    Button create;

    //Max SeekBar Limits
    int SUBMISSION_MAX = 50; //Words
    int SUBMISSION_DEFAULT = 3;//Word slider default

    int HISTORY_MAX = 100;
    int HISTORY_DEFAULT = SUBMISSION_DEFAULT;
    double HISTORY_TICK = 0.2;

    //Firebase Refs
    Firebase storyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

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
    //Binding Create Story Views
    private void bindViews(){
        storyTitle = (EditText)findViewById(R.id.activity_create_storyTitle_textfield);
        starterText = (EditText)findViewById(R.id.activity_create_starterText_textfield);

        submissionDisplay = (TextView)findViewById(R.id.activity_create_submissionLength_textview);
        historyDisplay = (TextView)findViewById(R.id.activity_create_historyLength_textview);

        historyLength = (SeekBar)findViewById(R.id.activity_create_historyLength_seekBar);
        submissionLength = (SeekBar)findViewById(R.id.activity_create_submissionLength_seekBar);

        languageFilter = (ToggleButton)findViewById(R.id.activity_create_languageFilter_toggleButton);
        create = (Button)findViewById(R.id.activity_create_create_button);
    }

    //Setup Seek Bars
    private void setupSeekBars(){
        historyLength.setMax(HISTORY_MAX);
        historyLength.setProgress(HISTORY_DEFAULT);
        submissionLength.setMax(SUBMISSION_MAX);
        submissionLength.setProgress(SUBMISSION_DEFAULT);

        submissionLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                submissionDisplay.setText("Submission Length: " + progress + " words");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        historyLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long value = Math.round(progress * HISTORY_TICK * submissionLength.getProgress());
                historyDisplay.setText("History Length: " + value + " words");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
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

                //String lastUpdated, String title, int ageLimit, int historyLimit, int textLimit, PieceClass[] pieces
                StoryClass curStory = new StoryClass(String.valueOf(System.currentTimeMillis()),
                                title,
                                (languageFilter.isChecked())? 13:0,
                                (int) Math.round(historyLength.getProgress() * HISTORY_TICK * submissionLength.getProgress()),
                                submissionLength.getProgress(),
                                new PieceClass[] {new PieceClass(
                                                            getSharedPreferences("StoryQuilt",MODE_PRIVATE).getString("username","Anonymous"),
                                                            String.valueOf(System.currentTimeMillis()),
                                                            String.valueOf(starterText.getText())
                                                            )}
                                );

                //Push to Firebase
                storyRef = FireConnection.create("stories");
                Firebase pushRef = storyRef.push();
                curStory.setId(pushRef.getName());
                pushRef.setValue(curStory);
                
                //End Activity
                finish();


            }
        });
    }

    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Options Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //some other button (not create_story) but It crashed without a real item
        if (id == R.id.create_story) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}