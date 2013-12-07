package com.roomates.storyquilt;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.ToggleButton;

/**
 * Created by chris on 12/4/13.
 */
public class CreateStoryActivity extends Activity{
    //Views
    EditText storyTitle, starterText;
    SeekBar historyLength, submissionLength;
    ToggleButton languageFilter;
    Button create;

    //Max SeekBar Limits
    final private int SUBMISSION_MAX = 50; //Words
    final private int SUBMISSION_DEFAULT = 3;//Word slider default

    final private int HISTORY_MAX = 100;
    final private int HISTORY_DEFAULT = SUBMISSION_DEFAULT;
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
    private void bindViews(){
        storyTitle = (EditText)findViewById(R.id.activity_create_storyTitle_textfield);
        starterText = (EditText)findViewById(R.id.activity_create_starterText_textfield);

        historyLength = (SeekBar)findViewById(R.id.activity_create_historyLength_seekBar);
        submissionLength = (SeekBar)findViewById(R.id.activity_create_submissionLength_seekBar);

        languageFilter = (ToggleButton)findViewById(R.id.activity_create_languageFilter_toggleButton);
        create = (Button)findViewById(R.id.activity_create_create_button);
    }

    private void setupSeekBars(){
        historyLength.setMax(HISTORY_MAX);
        historyLength.setProgress(HISTORY_DEFAULT);
        submissionLength.setMax(SUBMISSION_MAX);
        submissionLength.setProgress(SUBMISSION_DEFAULT);

        historyLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
                String title =String.valueOf(storyTitle.getText());
                String starter = String.valueOf(starterText.getText());
                if (title.equals("")) title = starter;
                //if (starter.split(" ") )

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