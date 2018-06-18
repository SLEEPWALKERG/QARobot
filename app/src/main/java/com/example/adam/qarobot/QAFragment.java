package com.example.adam.qarobot;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class QAFragment extends Fragment {

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private AlertDialog.Builder builder;
    private int indexofitem = 0;
    private AlertDialog dialog;
    private String[] ans = new String[4];
    private String content = null;
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private static final String CQA = "http://211.144.121.122:18887/proxy?p=";

    public QAFragment() {
        // Required empty public constructor
    }

    public static QAFragment newInstance(String param1) {
        QAFragment fragment = new QAFragment();
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initDataBase();
        View view = inflater.inflate(R.layout.fragment_qa, container, false);
        initMsgs();
        inputText = (EditText) view.findViewById(R.id.input_send);
        send = (Button) view.findViewById(R.id.btn_send);
        msgRecyclerView = (RecyclerView) view.findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    WorkThread cqa = new WorkThread(content, countDownLatch);
                    executorService.execute(cqa);
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Msg msg1 = new Msg(ans[0], Msg.TYPE_RECEIVED);
                    msgList.add(msg1);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            showdiag();
                            Looper.loop();
                        }
                    },8000);
                    //showdiag();
                }
            }
        });
        return view;
    }

    private void initMsgs() {
        Msg msg = new Msg("Hello", Msg.TYPE_RECEIVED);
        msgList.add(msg);
    }
    /*public void getans(final String s, final String question) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL(s+question);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        System.out.println(line);
                        response.append(line);
                    }
                    store_1(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }*/

    class WorkThread extends Thread {
        private String question;
        private CountDownLatch countDownLatch;

        public WorkThread(String question, CountDownLatch countDownLatch) {
            this.question = question;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(CQA + question);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(60000);
                connection.setReadTimeout(60000);
                connection.connect();
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    response.append(line);
                }
                store_1(response.toString());
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null)
                    connection.disconnect();
            }
        }
    }

    public void store_1(String r) {
        //String r = ans.substring(ans.indexOf("</br>") + 5, ans.indexOf("</br>", ans.indexOf("</br>") + 5));
        //String r = ans;
        this.ans[0] = r;
        this.ans[1] = r;
        this.ans[2] = r;
        this.ans[3] = r;
    }

    private void showdiag() {
        builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("请选择您认为是最好的答案");
        String[] choices = {"第一个答案", "第二个答案", "第三个答案", "第四个答案"};
        builder.setSingleChoiceItems(choices, indexofitem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                indexofitem = i;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (indexofitem < 2) {
                    MongoCollection<Document> collection = mongoDatabase.getCollection("checked");
                    Document document = new Document("question", content).append("answer", ans[indexofitem]);
                    collection.insertOne(document);
                } else {
                    MongoCollection<Document> collection = mongoDatabase.getCollection("unchecked");
                    Document document = new Document("question", content).append("answer", ans[indexofitem]);
                    collection.insertOne(document);
                }
                Toast.makeText(getActivity(), "thanks for your voting", Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void initDataBase() {
        try {
            mongoClient = new MongoClient("118.25.135.35", 27017);
            mongoDatabase = mongoClient.getDatabase("qarobottest");
            //Toast.makeText(this.getActivity(), "connect to the database successfully", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
