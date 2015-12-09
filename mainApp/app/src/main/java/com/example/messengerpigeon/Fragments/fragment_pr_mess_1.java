package com.example.messengerpigeon.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messengerpigeon.R;

/**
 * Created by Пользователь on 09.12.2015.
 */
public class fragment_pr_mess_1 extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_protected_messages, container, false);
        return view;
    }
}
