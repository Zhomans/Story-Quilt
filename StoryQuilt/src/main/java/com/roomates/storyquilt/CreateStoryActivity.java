package com.roomates.storyquilt;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by chris on 12/4/13.
 */
public class CreateStoryActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //Set up Activity Views
        bindViews();
    }

    /**
     Binding Views for CreateStory from XML
     */
    void bindViews(){
        findViewById(R.id.activity_create_storyTitle_textfield);
        findViewById(R.id.activity_create_starterText_textfield);

        findViewById(R.id.activity_create_historyLength_seekBar);
        findViewById(R.id.activity_create_submissionLength_seekBar);
        findViewById(R.id.activity_create_languageFilter_toggleButton);

        find
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