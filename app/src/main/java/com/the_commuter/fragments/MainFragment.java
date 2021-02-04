package com.the_commuter.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.the_commuter.R;

public class MainFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = view.findViewById(R.id.main_frag_txt);
        TextView textView1 = view.findViewById(R.id.title);

        Typeface ostrichFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ostrich-regular.ttf");
        Typeface raleway = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Black.ttf");

        textView.setTypeface(ostrichFont);
        textView1.setTypeface(ostrichFont);
        return view;
    }
}