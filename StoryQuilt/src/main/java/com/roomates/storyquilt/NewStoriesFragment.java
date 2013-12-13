package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by evan on 9/25/13.
 */
public class NewStoriesFragment extends Fragment {

    //List View
    ListView newStories;

    //ListAdapter
    StoryListAdapter newAdapter;

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
        newStories = (ListView) v.findViewById(R.id.fragment_stories_listview);
        newStories.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireConnection.create("stories");

        //Adapter
        newAdapter = new StoryListAdapter(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                return stories;
            }
        };
    }

    //On Item Click for StoryListAdapter
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), StoryViewActivity.class);
                goToStory.putExtra("story",((Story) newStories.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
