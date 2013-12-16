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
import android.widget.TextView;

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

    //ListAdapters
    AdapterStoryList contributingAdapter;

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
//        ((TextView) v.findViewById(R.id.fragment_stories_title)).setText("Contributing");
        contributing = (ListView) v.findViewById(R.id.fragment_stories_listview);
        contributing.setBackground(new BitmapDrawable(getResources(), getRoundedCornerBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.white_background))));
        contributing.setOnItemClickListener(goToStoryActivity());
    }
    //Get Firebase Refs for Reading and Writing
    private void setFireBaseRefs(){
        storyRef = FireHandler.create("stories");
    }
    //Create and Set ArrayAdapters for the ListViews
    private void setListAdapters(){
        contributingAdapter = new AdapterStoryList(storyRef, getActivity(), R.layout.listitem_main_story){
            @Override
            protected List<Story> modifyArrayAdapter(List<Story> stories){
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
