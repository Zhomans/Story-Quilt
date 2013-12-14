package com.roomates.storyquilt;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by evan on 9/25/13.
 */
public class ListenerNavTab implements ActionBar.TabListener {

    public Fragment fragment;

    public ListenerNavTab(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragmentContainer, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }
}
