package com.example.adam.qarobot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AcountFragment extends Fragment {

    public AcountFragment() {
        // Required empty public constructor
    }

    public static AcountFragment newInstance(String param1) {
        AcountFragment fragment = new AcountFragment();
        Bundle args = new Bundle();
        args.putString("args1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acount, container, false);
        Bundle bundle = getArguments();
        String args1 = bundle.getString("args1");
        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(args1);
        return view;
    }


}
