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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chris 10/13/2013.
 */
public class PopularStoriesFragment extends Fragment {
    //List View
    ListView popular;

    //ListAdapter
    StoryListAdapter popularAdapter;

    //Firebase
    Firebase storyRef;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_popular, null);
        setupListView(v);
        return v;

    }

    /**
     * Set up List View
     */
    //Setting up the view and bindings
    public void setupListView(View v){
        popular = (ListView) v.findViewById(R.id.activity_main_writing_listview);
        popular.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireConnection.create("stories");

        //Adapter
        popularAdapter = new StoryListAdapter(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> writingStories = new ArrayList<Story>();
                Collections.sort(writingStories, new Comparator<Story>() {
                    public int compare(Story s1, Story s2) { //#posts/#writers
                        int s1value = s1.pieces.size()/s1.writers.size();
                        int s2value = s2.pieces.size()/s2.writers.size();
                        if (s1value == s2value)
                            return 0;
                        return s1value < s2value ? -1 : 1;
                    }
                });
                return writingStories;
            }
        };
    }

    //On Item Click for StoryListAdapter
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), StoryViewActivity.class);
                goToStory.putExtra("story",((Story)popular.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
