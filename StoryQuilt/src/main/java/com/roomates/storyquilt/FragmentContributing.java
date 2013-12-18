package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


/**
 * Created by roomates on 9/25/13.
 */
public class FragmentContributing extends Fragment {
    //MainActivity Views
    ListView contributing;

    //Context Menu
    final int REMOVE_STORY = 0;
    String[] menuItems = {"Never see this again"};


    //ListAdapters
    AdapterStoryList contributingAdapter;

    //Firebase
    Firebase storyRef;

    //Search Text
    String searchQueryText = "";

    //Search Bar
    MenuItem searchItem;
    //UserHandler
    UserHandler userHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHandler = new UserHandler(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories, null);
        Log.i("UserHandlerDebug", userHandler.user.email);
        setUpMainPageViews(v);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        setListAdapters(getView());
    }

    //MyStory views
    private void setUpMainPageViews(View v){
        setListViews(v);
        setFireBaseRefs();
        setListAdapters(v);
    }

    
    /**
     * Methods for Handling List Views
     */
    //Grab ListViews from the XML
    private void setListViews(View v){
        contributing = (ListView) v.findViewById(R.id.fragment_stories_listview);
        contributing.setOnItemClickListener(goToStoryActivity());
        registerForContextMenu(contributing);
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireHandler.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(final View v){
        contributingAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                int orig_size = stories.size();
                ArrayList<Story> filtered_stories = new ArrayList<Story>();
                for (Story story : stories) {
                    if (story.getTitle().toLowerCase().contains(searchQueryText.toLowerCase()) && !userHandler.user.removed.contains(story.id)) {
                        filtered_stories.add(story);
                    }
                }
                stories = filtered_stories;
                if (FragmentContributing.this.getView() != null) {
                    TextView no_stories = (TextView) (FragmentContributing.this.getView()).findViewById(R.id.other_no_stories);
                    if (stories.size() == 0) {
                        Log.d("Stories", "None");
                        no_stories.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("Stories", "Some");
                        no_stories.setVisibility(View.GONE);
                    }
                }

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
        if (searchItem != null) {searchItem.collapseActionView();}
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchItem =  menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null){
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean queryTextFocused) {
                    if (!queryTextFocused) {
                        Log.i("COLLAPSESEARCH","here");
                       searchItem.collapseActionView();
                       searchView.setQuery("", false);
                    }
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //should narrow again from filtered list on update
                    searchQueryText = newText;
                    setListAdapters(getView());
                    return false;
                }

            });
        }
    }

    /**
     * Context Menu for LongClickListener
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.fragment_stories_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(((Story) contributing.getItemAtPosition(info.position)).title);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case REMOVE_STORY:
                userHandler.removeStory(((Story) contributing.getItemAtPosition(info.position)).id);
                Toast.makeText(getActivity(), "You have removed \"" + ((Story) contributing.getItemAtPosition(info.position)).title + "\" from your app", Toast.LENGTH_SHORT).show();
                setUpMainPageViews(getView());
                break;
        }

        return true;
    }
}
