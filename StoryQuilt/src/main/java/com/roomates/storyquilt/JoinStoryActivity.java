package com.roomates.storyquilt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class JoinStoryActivity extends Activity {
    //JoinStoryActivity Views
    ListView popular, nascent;

    //ListAdapters
    StoryListAdapter popularAdapter, nascentAdapter;

    //Firebase
    Firebase popularRef, nascentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //Set up MainActivity Views
        setListViews();
        setFireBaseRefs();
        setListAdapters();
    }


    /**
        Methods for Handling List Views

     */
    //Grab ListViews from the XML
    private void setListViews(){
        popular = (ListView) findViewById(R.id.activity_main_writing_listview);
        nascent = (ListView) findViewById(R.id.activity_main_reading_listview);
    }

    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        nascentRef = FireConnection.create("stories", "reading");
        popularRef = FireConnection.create("users", "writing");
    }

    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        popularAdapter = new StoryListAdapter(popularRef, JoinStoryActivity.this, R.layout.listitem_main_story);
        nascentAdapter = new StoryListAdapter(nascentRef, JoinStoryActivity.this, R.layout.listitem_main_story);

        popular.setAdapter(popularAdapter);
        nascent.setAdapter(nascentAdapter);
    }

    /**
     * Activity Methods
     */

    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.join, menu);
        return true;
    }

    //Options Menu Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.create_story: //Create a new Story
                Intent createStory = new Intent(JoinStoryActivity.this, CreateStoryActivity.class);
                startActivity(createStory);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    }
}
