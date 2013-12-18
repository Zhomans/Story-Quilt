package com.roomates.storyquilt;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.SearchView;
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

    //Menu
    MenuItem searchItem;

    //Query String
    String searchQueryText = "";

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
        following = (ListView) v.findViewById(R.id.fragment_stories_listview);
        following.setBackground(new BitmapDrawable(getResources(), getRoundedCornerBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.white_background))));
        following.setOnItemClickListener(goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireHandler.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(final View v){
        followingAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
                ArrayList<Story> filtered_stories = new ArrayList<Story>();
                for (Story story : stories) {
                    if (story.getTitle().toLowerCase().contains(searchQueryText.toLowerCase())) {
                        filtered_stories.add(story);
                    }
                }
                stories = filtered_stories;

                if (FragmentFollowing.this.getView() != null && stories.size() != 0) {
                    TextView no_stories = (TextView) (FragmentFollowing.this.getView()).findViewById(R.id.other_no_stories);
                    if (stories.size() == 0) {
                        Log.d("Stories", "None");
                        no_stories.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("Stories", "Some");
                        no_stories.setVisibility(View.GONE);
                    }
                }

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
        if (searchItem != null) {searchItem.collapseActionView();}
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchItem =  menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null){
            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean queryTextFocused) {
                    if (!queryTextFocused) {
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
