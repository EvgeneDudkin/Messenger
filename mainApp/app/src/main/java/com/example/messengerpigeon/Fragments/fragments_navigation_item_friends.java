package com.example.messengerpigeon.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengerpigeon.R;

public class fragments_navigation_item_friends extends Fragment{

    public class Friends extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_friends,
                    container, false);

            return v;
        }
    }
}
