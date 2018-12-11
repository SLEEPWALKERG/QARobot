package com.example.adam.qarobot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Listadapter extends ArrayAdapter<Qa_pair> {
    private int resourceId;

    public Listadapter(@NonNull Context context, int textViewResourceId, @NonNull List<Qa_pair> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         Qa_pair qaPair = getItem(position);
         View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
         TextView textView1 = (TextView) view.findViewById(R.id.tv_q);
         TextView textView2 = (TextView) view.findViewById(R.id.tv_a);
         textView1.setText(qaPair.getQuestion());
         textView2.setText(qaPair.getAnswer());
         return view;
    }
}
