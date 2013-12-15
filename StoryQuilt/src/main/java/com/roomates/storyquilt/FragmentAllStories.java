package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by chris 10/13/2013.
 */
public class FragmentAllStories extends Fragment {
    //Sorting Mode
    final int SORTBY_NEW = 0;
    final int SORTBY_POPULAR = 1;
    final int SORTBY_RANDOM = 2;


    int mode = SORTBY_POPULAR;
    //List View
    ListView stories;
    TextView sortBy;

    //ListAdapter
    AdapterStoryList storiesAdapter;

    //Firebase
    Firebase storyRef;

    //Random List of Stories
    final int NUM_RANDOM_STORIES = 6;
    HashSet<String> random;
    ArrayList<Story> original;
    int numStories;


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

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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
                    sortBy.setText("sorted by: new");
                    mode = SORTBY_NEW;
                   setupListView(v);
                } else {
                    mode = SORTBY_POPULAR;
                    sortBy.setText("sorted by: popular");
                    setupListView(v);
                }
            }
        });
    }
    //Setting up the view and bindings
    public void setupListView(View v){
        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Stories");
        ((TextView) v.findViewById(R.id.fragment_stories_sortby_text)).setText("sorted by: " + (mode==2? "random":"popular"));

        stories = (ListView) v.findViewById(R.id.fragment_stories_listview);
        stories.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireHandler.create("stories");

        //Adapter
        storiesAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                switch (mode){
                    case SORTBY_NEW:
                        original = new ArrayList<Story>();
                        original.addAll(stories);
                        Collections.sort(stories, new Comparator<Story>() {
                            public int compare(Story s1, Story s2) { //#posts/#writers
                                if (s1.lastUpdated.equals(s2.lastUpdated))
                                    return 0;
                                return Long.valueOf(s1.lastUpdated) > Long.valueOf(s2.lastUpdated) ? -1 : 1;
                            }
                        });
                        break;

                    case SORTBY_POPULAR:
                        original = new ArrayList<Story>();
                        original.addAll(stories);
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
                    case SORTBY_RANDOM:
                        Collections.sort(stories, new Comparator<Story>() {
                            public int compare(Story s1, Story s2) { //#posts/#writers
                                int s1value = s1.pieces.size()/s1.writers.size();
                                int s2value = s2.pieces.size()/s2.writers.size();
                                if (s1value == s2value)
                                    return 0;
                                return s1value > s2value ? -1 : 1;
                            }
                        });
                        ArrayList<Story> filtered = new ArrayList<Story>();
                        for (Story tempStory: stories){
                            if (random.contains(tempStory.id)){
                                filtered.add(tempStory);
                            }
                        }
                        return filtered;
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

    @Override
    public void onResume(){
        super.onResume();
        setupListView(getView());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(Menu.NONE, R.id.action_random, 100, "Random");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                numStories = original.size();
                Random num = new Random(System.currentTimeMillis());
                mode = SORTBY_RANDOM;
                ((TextView)getView().findViewById(R.id.fragment_stories_sortby_text)).setText("sorted by: random");
                random = new HashSet<String>();
                while (random.size() < NUM_RANDOM_STORIES){
                    random.add((original.get(num.nextInt(numStories))).id);
                }
                setupListView(getView());
                return false;
            }
        });
    }
}
