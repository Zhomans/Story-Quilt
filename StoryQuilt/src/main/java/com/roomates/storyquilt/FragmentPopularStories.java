package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by chris 10/13/2013.
 */
public class FragmentPopularStories extends Fragment {
    //List View
    ListView popular;

    //ListAdapter
    AdapterStoryList popularAdapter;

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
        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Popular");
        popular = (ListView) v.findViewById(R.id.fragment_stories_listview);
        popular.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireHandler.create("stories");

        //Adapter
        popularAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                Collections.sort(stories, new Comparator<Story>() {
                    public int compare(Story s1, Story s2) { //#posts/#writers
                        int s1value = s1.pieces.size()/s1.writers.size();
                        int s2value = s2.pieces.size()/s2.writers.size();
                        if (s1value == s2value)
                            return 0;
                        return s1value > s2value ? -1 : 1;
                    }
                });
                return stories;
            }
        };
        popular.setAdapter(popularAdapter);
    }
    @Override
    public void onPause(){
        super.onPause();
        popularAdapter.cleanup();
    }
    //On Item Click for AdapterStoryList
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story)popular.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
