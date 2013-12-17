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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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
    String searchQueryText = "";


    int mode = SORTBY_POPULAR;
    //List View
    ListView stories;
    TextView sortBy;

    //ListAdapter
    AdapterStoryList storiesAdapter;

    //Firebase
    Firebase storyRef;

    //Search Item
    MenuItem searchItem;


    //Random List of Stories
    int NUM_RANDOM_STORIES = 6;
    HashSet<String> random;
    ArrayList<Story> original;
    int numStories;

    View v;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_allstories, null);
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
        sortBy.setBackground(new BitmapDrawable(getResources(),getRoundedCornerBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.white_background))));
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
        String modeText;
        switch (mode) {case SORTBY_NEW: modeText = "new"; break; case SORTBY_POPULAR: modeText = "popular"; break; case SORTBY_RANDOM: modeText = "random"; break; default: modeText = "random"; break;}
        ((TextView) v.findViewById(R.id.fragment_stories_sortby_text)).setText("sorted by: " + modeText);

        stories = (ListView) v.findViewById(R.id.fragment_stories_listview);
        stories.setBackground(new BitmapDrawable(getResources(),getRoundedCornerBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.white_background))));
        stories.setOnItemClickListener(goToStoryActivity());

        //Firebase
        storyRef = FireHandler.create("stories");

        //Adapter
        storiesAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                ArrayList<Story> filtered_stories = new ArrayList<Story>();
                for (Story story : stories) {
                    if (story.getTitle().toLowerCase().contains(searchQueryText.toLowerCase())) {
                        filtered_stories.add(story);
                    }
                }
                stories = filtered_stories;
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
                        Collections.shuffle(filtered);
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
        if (searchItem != null) {searchItem.collapseActionView();}
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem randomItem = menu.add(Menu.NONE, R.id.action_random, 100, "Random");
        final ImageView randomView = (ImageView) randomItem.getActionView();
        randomItem.setIcon(R.drawable.dice);
        randomItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        randomItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                numStories = original.size();
                Random num = new Random(System.currentTimeMillis());
                mode = SORTBY_RANDOM;
                ((TextView) getView().findViewById(R.id.fragment_stories_sortby_text)).setText("sorted by: random");
                random = new HashSet<String>();
                if (NUM_RANDOM_STORIES > numStories) NUM_RANDOM_STORIES = numStories;
                while (random.size() < NUM_RANDOM_STORIES) {
                    random.add((original.get(num.nextInt(numStories))).id);
                }
                setupListView(getView());
                return false;
            }
        });
        searchItem =  menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        final MenuItem addItem = menu.findItem(R.id.create_story);
        /*final ImageView addView = (ImageView) addItem.getActionView();*/

        if (searchView != null){
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchItem.isActionViewExpanded()){
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
                    setupListView(getView());
                    return false;
                }

            });
        }
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 15;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
