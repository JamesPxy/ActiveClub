package com.pxy.eshore.ui.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pxy.eshore.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView tvMsg;
    private String message;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public HomeFragment(String message) {
        this.message = message;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvMsg = view.findViewById(R.id.message);
        tvMsg.setText(message);
        return view;
    }

}
