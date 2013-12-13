package com.roomates.storyquilt;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;

/**
 * Created by evan on 9/25/13.
 */
public class LoginFragment extends Fragment {
    //UserHandler
    UserHandler userHandler;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userHandler = new UserHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, null);
        setUpLoginViews(v);
        return v;

    }

    //LogIn Views
    public void setUpLoginViews(View v){
        //Set up SignInButton
        SignInButton signInButton = (SignInButton) v.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener((MainTabActivity)getActivity());
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //Set up ReadOnly Button
        Button readOnly = (Button) v.findViewById(R.id.fragment_signin_readonly);
        readOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do Stuff Here
            }
        });
    }
}
