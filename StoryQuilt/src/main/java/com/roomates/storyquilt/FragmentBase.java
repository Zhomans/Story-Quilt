package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
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
import java.util.List;

/**
 * Created by chris on 12/18/13.
 * Cleaned: 12/18/13
 */
public abstract class FragmentBase extends Fragment {
    /**
     * Variables used for the ListView
     */
    ListView listView;
    AdapterStoryList listAdapter;
    Firebase listRef;

    /**
     * Variables used for Context Menu
     * OnLongClickItem
     */
    final int REMOVE_STORY = 0;
    String[] menuItems = {"Never see this again"};

    /**
     * Variables used for Searching the List
     */
    String searchQueryText = ""; //Search Text
    MenuItem searchItem; //Search Bar Item

    /**
     * User Information
     */
    UserHandler userHandler;

    /**
     * General Fragment Methods
     */
    @Override
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
        View v = inflater.inflate(getFragmentLayoutId(), null);
        setUpMainPageViews(v);
        onCreateViewExtended(inflater, container, savedInstanceState, v);
        return v;
    }

    /**
     * Methods handling safe-fragment switching
     */
    @Override
    public void onStart(){
        super.onStart();
        setListAdapters(); //Reset the List
    }

    @Override
    public void onPause(){
        super.onPause();
        if (searchItem != null) {searchItem.collapseActionView();}
        listAdapter.cleanup();
    }

    /**
     * Methods for Handling List Views
     */
    public void setUpMainPageViews(View v){
        setListViews(v);
        setFireBaseRefs();
        setListAdapters();
    }
    private void setListViews(View v){
        listView = (ListView) v.findViewById(R.id.fragment_stories_listview);
        listView.setOnItemClickListener(goToStoryActivity());
        registerForContextMenu(listView);
    }
    private void setFireBaseRefs(){
        listRef = getFirebaseListReference();
    }
    private void setListAdapters(){
        listAdapter = new AdapterStoryList(listRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                ArrayList<Story> filtered_stories = new ArrayList<Story>();
                for (Story story : stories) {
                    if (story.getTitle().toLowerCase().contains(searchQueryText.toLowerCase()) && !userHandler.isRemoved(story.id)) {
                        filtered_stories.add(story);
                    }
                }
                stories = filterAdapterArray(filtered_stories);
                //Visibility of the No Stories TextBox.
                if (FragmentBase.this.getView() != null) {
                    TextView no_stories = (TextView) (FragmentBase.this.getView()).findViewById(R.id.no_stories);
                    if (stories.size() == 0) {
                        Log.d("Stories", "None");
                        no_stories.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("Stories", "Some");
                        Log.d("Stories", stories.get(0).title);
                        no_stories.setVisibility(View.GONE);
                    }
                }
                return stories;
            }
        };
        listView.setAdapter(listAdapter);
    }
    private AdapterView.OnItemClickListener goToStoryActivity() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToStory = new Intent(getActivity(), ActivityStoryView.class);
                goToStory.putExtra("story",((Story) listView.getItemAtPosition(position)).id);
                startActivity(goToStory);
            }
        };
    }

    /**
     * Adding OptionsMenu Items
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        onCreateOptionsMenuExtended(menu, inflater);
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
                    setListAdapters();
                    return false;
                }

            });
        }
    }

    /**
     * Context Menu for LongClickListener
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.fragment_stories_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(((Story) listView.getItemAtPosition(info.position)).title);
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
                userHandler.removeStory(((Story) listView.getItemAtPosition(info.position)).id);
                Toast.makeText(getActivity(), "You have removed \"" + ((Story) listView.getItemAtPosition(info.position)).title + "\" from your app", Toast.LENGTH_SHORT).show();
                setUpMainPageViews(getView());
                break;
        }

        return true;
    }

    /**
     * Abstract need to be implemented classes
     */
    public abstract int getFragmentLayoutId();
    public abstract Firebase getFirebaseListReference();
    public abstract List<Story> filterAdapterArray(List<Story> stories);
    public abstract void onCreateOptionsMenuExtended(final Menu menu, MenuInflater inflater);
    public abstract void onCreateViewExtended(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v);
}
