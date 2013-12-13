package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by evan on 9/25/13.
 */
public class MyStoriesFragment extends Fragment {
    //MainActivity Views
    ListView writing, reading;

    //ListAdapters
    StoryListAdapter writingAdapter, readingAdapter;

    //Firebase
    Firebase storyRef;

    //UserHandler
    UserHandler userHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHandler = new UserHandler(getActivity());
        userHandler.updateUserFromFirebase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mystories, null);
        setUpMainPageViews(v);
        return v;
    }

    //MyStory views
    private void setUpMainPageViews(View v){
        setListViews(v);
        setFireBaseRefs();
        setListAdapters();
    }


    /**
     * Methods for Handling List Views
     */
    //Grab ListViews from the XML
    private void setListViews(View v){
        writing = (ListView) v.findViewById(R.id.fragment_main_writing_listview);
        reading = (ListView) v.findViewById(R.id.fragment_main_reading_listview);

        writing.setOnItemClickListener( goToStoryActivity());
        reading.setOnItemClickListener( goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireConnection.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        writingAdapter = new StoryListAdapter(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> writingStories = new ArrayList<Story>();
                for (Story tempStory: stories){
                    Log.i("modifyArrayAdapter", tempStory.id);
                    if (userHandler.user.writing.contains(tempStory.id)){
                        Log.i("modifyArrayAdapter Add", tempStory.id);
                        writingStories.add(tempStory);
                    }
                }
                return writingStories;
            }
        };
        readingAdapter = new StoryListAdapter(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> readingStories = new ArrayList<Story>();
                for (Story tempStory: stories){
                    if (userHandler.user.reading.contains(tempStory.id))
                        readingStories.add(tempStory);
                }
                return readingStories;
            }
        };

        writing.setAdapter(writingAdapter);
        reading.setAdapter(readingAdapter);
    }
    //On Item Click for StoryListAdapter
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), StoryViewActivity.class);
                goToStory.putExtra("story",((Story)writing.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
