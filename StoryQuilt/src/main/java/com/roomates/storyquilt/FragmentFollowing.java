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
import java.util.List;


/**
 * Created by roomates on 9/25/13.
 */
public class FragmentFollowing extends Fragment {
    //MainActivity Views
    ListView following;

    //ListAdapters
    AdapterStoryList followingAdapter;

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
//        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Following");
        following = (ListView) v.findViewById(R.id.fragment_stories_listview);
        following.setOnItemClickListener(goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireHandler.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        followingAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                List<Story> readingStories = new ArrayList<Story>();
                Log.i("UserHandler Readers", userHandler.user.reading.toString());
                for (Story tempStory: stories){
                    if (userHandler.isReader(tempStory.id))
                        readingStories.add(tempStory);
                }
                return readingStories;
            }
        };

        following.setAdapter(followingAdapter);
    }
    @Override
    public void onPause(){
        super.onPause();
        followingAdapter.cleanup();
    }
    //On Item Click for AdapterStoryList
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story) following.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.add(Menu.NONE, R.id.search_all, 100, "Search");
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });
    }
}
