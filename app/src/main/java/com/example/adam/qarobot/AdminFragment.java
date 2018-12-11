package com.example.adam.qarobot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AdminFragment extends Fragment {
    private final static String s = "Everything has been checked. Have a rest!";
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private static List<Document> documents = new ArrayList<>();
    private List<Qa_pair> list = new ArrayList<>();
    private Handler handler;
    private ListView listView;
    private Listadapter adapter;


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

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        list.clear();
                        for (Qa_pair qaPair : (List<Qa_pair>) msg.obj){
                            list.add(qaPair);
                        }
                        Initview();
                }
            }
        };
        Initdatabase();
        adapter = new Listadapter(view.getContext(), R.layout.list_item, list);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                final Document docu = new Document();
                docu.append("question", list.get(i).getQuestion());
                docu.append("answer", list.get(i).getAnswer());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Please check");
                builder.setMessage("Please choose to delete it or save it to our database");
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MongoCollection<Document> collection = mongoDatabase.getCollection("checked");
                        collection.insertOne(docu);
                        Toast.makeText(getContext(),"Successfully save into the adtabase",Toast.LENGTH_SHORT).show();
                        list.remove(index);
                        Delete(docu);
                        Initview();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Delete(docu);
                        Toast.makeText(getContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        list.remove(index);
                        Initview();
                    }
                });
                builder.show();
            }
        });
        Getdata_thread mythread = new Getdata_thread(handler);
        mythread.start();
        /*confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MongoCollection<Document> collection = mongoDatabase.getCollection("checked");
                collection.insertOne(documents.get(i));
                Toast.makeText(getContext(),"Successfully push to the database",Toast.LENGTH_SHORT).show();
                Delete(documents.get(i));
                i += 1;
                if (i < max){
                    question.setText(q.get(i));
                    answer.setText(a.get(i));
                }else{
                    Toast.makeText(getContext(),"Everything has been checked",Toast.LENGTH_SHORT).show();
                    question.setText(s);
                    answer.setText("");
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete(documents.get(i));
                i += 1;
                if (i < max){
                    question.setText(q.get(i));
                    answer.setText(a.get(i));
                }else{
                    Toast.makeText(getContext(),"Everything has been checked",Toast.LENGTH_SHORT).show();
                    question.setText(s);
                    answer.setText("");
                }
            }
        });*/
        return view;
    }

    private void Initdatabase() {
        mongoClient = new MongoClient("118.25.135.35", 27017);
        mongoDatabase = mongoClient.getDatabase("te");
    }


    private void Delete(Document document){
        MongoCollection<Document> collection = mongoDatabase.getCollection("unchecked");
        collection.deleteMany(document);
    }

    private void Initview(){
        if (list.size() == 0)
            Toast.makeText(getContext(),s, Toast.LENGTH_LONG).show();
        adapter.notifyDataSetChanged();
    }
}
