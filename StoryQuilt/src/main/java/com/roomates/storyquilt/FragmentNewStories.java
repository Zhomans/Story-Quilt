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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by evan on 9/25/13.
 */
public class FragmentNewStories extends Fragment {

    //List View
    ListView newStories;

    //ListAdapter
    AdapterStoryList newAdapter;

    //Firebase
    Firebase storyRef;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stories, null);
        setupListView(v);
        return v;
    }

    /**
     * Set up List View
     */
    //Setting up the view and bindings
    public void setupListView(View v){
        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("New");
        newStories = (ListView) v.findViewById(R.id.fragment_stories_listview);
        newStories.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireHandler.create("stories");

        //Adapter
        newAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                Collections.sort(stories, new Comparator<Story>() {
                    public int compare(Story s1, Story s2) { //#posts/#writers
                        if (s1.lastUpdated.equals(s2.lastUpdated))
                            return 0;
                        return Long.valueOf(s1.lastUpdated) > Long.valueOf(s2.lastUpdated) ? -1 : 1;
                    }
                });
                return stories;
            }
        };

        newStories.setAdapter(newAdapter);
    }


    @Override
    public void onPause(){
        super.onPause();
        newAdapter.cleanup();
    }
    //On Item Click for AdapterStoryList
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story) newStories.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
