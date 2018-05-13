package com.example.adam.qarobot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class AdminFragment extends Fragment {
    private TextView question;
    private TextView answer;
    private Button confirm;
    private Button delete;

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(String param1) {
        AdminFragment fragment = new AdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        confirm = (Button) view.findViewById(R.id.btn_confirm);
        delete = (Button) view.findViewById(R.id.btn_delete);
        SharedPreferences pref = this.getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        question. setText("how are you");
        answer.setText("I am fine");
        return view;
    }


}
