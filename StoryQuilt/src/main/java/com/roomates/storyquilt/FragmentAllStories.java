package com.roomates.storyquilt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
public class FragmentAllStories extends FragmentBase {
    //Sorting Mode
    final int SORTBY_NEW = 0;
    final int SORTBY_POPULAR = 1;
    final int SORTBY_RANDOM = 2;
    int mode = SORTBY_POPULAR;

    //SortBy Text
    TextView sortBy;

    //Random List of Stories
    int NUM_RANDOM_STORIES = 6;
    HashSet<String> random;
    ArrayList<Story> original;
    int numStories;


    @Override
    public int getFragmentLayoutId() {
        return R.layout.fragment_stories;
    }

    @Override
    public List<Story> filterAdapterArray(List<Story> stories) {
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

    @Override
    public Firebase getFirebaseListReference() {
        return FireHandler.create("stories");
    }

    @Override
    public void onCreateOptionsMenuExtended(Menu menu, MenuInflater inflater) {
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
                FragmentAllStories.this.setUpMainPageViews(FragmentAllStories.this.getView());
                return false;
            }
        });
    }

    @Override
    public void onCreateViewExtended(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v) {
       setUpSortBy(v);
    }

    //Set Up the button
    public void setUpSortBy(final View v) {
        sortBy = (TextView) v.findViewById(R.id.fragment_stories_sortby_text);
        ListView listView = (ListView) v.findViewById(R.id.fragment_stories_listview);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listView.getLayoutParams();
        params.setMargins(20, 5, 20, 20); //substitute parameters for left, top, right, bottom
        listView.setLayoutParams(params);

        sortBy.setVisibility(View.VISIBLE);
        //sortBy.setBackground(new BitmapDrawable(getResources(),getRoundedCornerBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.white_background))));
        sortBy.setClickable(true);
        sortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortBy.getText().toString().split(": ")[1].equals("popular")){
                    sortBy.setText("sorted by: new");
                    mode = SORTBY_NEW;
                    FragmentAllStories.this.setUpMainPageViews(v);
                } else {
                    mode = SORTBY_POPULAR;
                    sortBy.setText("sorted by: popular");
                    FragmentAllStories.this.setUpMainPageViews(v);
                }
            }
        });
    }



}
