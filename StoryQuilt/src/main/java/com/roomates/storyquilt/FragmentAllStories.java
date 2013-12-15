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
public class FragmentAllStories extends Fragment {
    //Sorting Mode
    final int SORTBY_NEW = 0;
    final int SORTBY_POPULAR = 1;
    //List View
    ListView stories;
    TextView sortBy;

    //ListAdapter
    AdapterStoryList storiesAdapter;

    //Firebase
    Firebase storyRef;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_allstories, null);
        setUpSortBy(v);
        setupListView(v);
        return v;

    }

    /**
     * Set up List View
     */
    //Set Up the button
    public void setUpSortBy(final View v) {
        sortBy = (TextView) v.findViewById(R.id.fragment_stories_sortby_text);
        sortBy.setClickable(true);
        sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortBy.getText().toString().split(": ")[1].equals("popular")){
                    sortBy.setText("sort by: new");
                    refreshAdapter(SORTBY_NEW);
                } else {
                    sortBy.setText("sort by: popular");
                    refreshAdapter(SORTBY_POPULAR);
                }
            }
        });
    }
    //Setting up the view and bindings
    public void setupListView(View v){
        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Stories");

        stories = (ListView) v.findViewById(R.id.fragment_stories_listview);
        stories.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireHandler.create("stories");

        //Adapter
        storiesAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
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
        stories.setAdapter(storiesAdapter);
    }
    //Refresh the adapter
    public void refreshAdapter(final int mode){
        storiesAdapter.cleanup();
        storiesAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                switch (mode){
                    case SORTBY_NEW:
                        Collections.sort(stories, new Comparator<Story>() {
                            public int compare(Story s1, Story s2) { //#posts/#writers
                                if (s1.lastUpdated.equals(s2.lastUpdated))
                                    return 0;
                                return Long.valueOf(s1.lastUpdated) > Long.valueOf(s2.lastUpdated) ? -1 : 1;
                            }
                        });
                        break;

                    case SORTBY_POPULAR:
                        Collections.sort(stories, new Comparator<Story>() {
                            public int compare(Story s1, Story s2) { //#posts/#writers
                                int s1value = s1.pieces.size()/s1.writers.size();
                                int s2value = s2.pieces.size()/s2.writers.size();
                                if (s1value == s2value)
                                    return 0;
                                return s1value > s2value ? -1 : 1;
                            }
                        });
                        break;
                }
                return stories;
            }
        };
        stories.setAdapter(storiesAdapter);
    }
    @Override
    public void onPause(){
        super.onPause();
        storiesAdapter.cleanup();
    }
    //On Item Click for AdapterStoryList
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story) stories.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }
}
