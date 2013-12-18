package com.roomates.storyquilt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roomates on 9/25/13.
 */
public class FragmentContributing extends FragmentBase {
    @Override
    public int getFragmentLayoutId() {
        return R.layout.fragment_stories;
    }

    @Override
    public List<Story> filterAdapterArray(List<Story> stories) {
        List<Story> writingStories = new ArrayList<Story>();
        for (Story tempStory: stories){
            if (userHandler.isWriter(tempStory.id)){
                writingStories.add(tempStory);
            }
        }
        return writingStories;
    }

    @Override
    public Firebase getFirebaseListReference() {
        return FireHandler.create("stories");
    }

    @Override
    public void onCreateViewExtended(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View v) {
        //Do Nothing
    }

    @Override
    public void onCreateOptionsMenuExtended(Menu menu, MenuInflater inflater) {
        //Do Nothing
    }
}
