package com.example.messengerpigeon.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengerpigeon.R;

/**
 * Created by Пользователь on 20.11.2015.
 */
public class fragments_messages_item extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
        return v;
    }

    
}
