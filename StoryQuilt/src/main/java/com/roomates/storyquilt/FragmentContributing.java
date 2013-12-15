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
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by roomates on 9/25/13.
 */
public class FragmentContributing extends Fragment {
    //MainActivity Views
    ListView contributing;

    //ListAdapters
    AdapterStoryList contributingAdapter;

    //Firebase
    Firebase storyRef;

    //UserHandler
    UserHandler userHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHandler = new UserHandler(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories, null);
        setUpMainPageViews(v);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        setListAdapters();
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
        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Contributing");
        contributing = (ListView) v.findViewById(R.id.fragment_stories_listview);
        contributing.setOnItemClickListener(goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireHandler.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        contributingAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> writingStories = new ArrayList<Story>();
                for (Story tempStory: stories){
                    if (userHandler.isWriter(tempStory.id)){
                        writingStories.add(tempStory);
                    }
                }
                return writingStories;
            }
        };
        contributing.setAdapter(contributingAdapter);
    }
    @Override
    public void onPause(){
        super.onPause();
        contributingAdapter.cleanup();
    }
    //On Item Click for AdapterStoryList
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story) contributing.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
