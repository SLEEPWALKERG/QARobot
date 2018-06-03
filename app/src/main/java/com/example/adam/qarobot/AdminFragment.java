package com.example.adam.qarobot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AdminFragment extends Fragment {
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    private TextView question;
    private TextView answer;
    private Button confirm;
    private Button delete;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ArrayList<String> q = new ArrayList<>();
    private ArrayList<String> a = new ArrayList<>();
    private List<Document> documents = new ArrayList<>();

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
        initdatabase();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Getdata getdata = new Getdata(countDownLatch);
        getdata.start();

        return view;
    }

    private void initdatabase() {
        mongoClient = new MongoClient("118.25.135.35",27017);
        mongoDatabase = mongoClient.getDatabase("qarobottest");
    }

    class Getdata extends Thread {
        private CountDownLatch countDownLatch = null;
        public Getdata(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            MongoCollection<Document> collection = mongoDatabase.getCollection("unchecked");
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            int i = 0;
            while (mongoCursor.hasNext()){
                Document document = mongoCursor.next();
                documents.add(document);
                try {
                    JSONObject jsonObject = new JSONObject(document.toJson());
                    q.set(i, jsonObject.getString("question"));
                    a.set(i, jsonObject.getString("answer"));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        }
    }

}
