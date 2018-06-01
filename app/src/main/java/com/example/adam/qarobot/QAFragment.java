package com.example.adam.qarobot;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class QAFragment extends Fragment {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private AlertDialog.Builder builder;
    private int indexofitem = 0;
    private AlertDialog dialog;
    private String ans_0 = null;
    private String ans_1 = null;
    private String ans_2 = null;
    private String ans_3 = null;
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;

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
                String content = inputText.getText().toString();
                if (!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    getans("http://211.144.121.122:18887/proxy?p=",content);
                    /*adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);*/
                    //showdiag();
                    inputText.setText("");
                }
            }
        });
        return view;
    }

    private void initMsgs() {
        Msg msg = new Msg("Hello",Msg.TYPE_RECEIVED);
        msgList.add(msg);
    }
    public void getans(final String s, final String question) {
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
                    show(response.toString());
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
    }

    public void show(String ans){
        String r = ans.substring(ans.indexOf("</br>") + 5, ans.indexOf("</br>", ans.indexOf("</br>") + 5));
        this.ans_0 = r;
        this.ans_1 = r;
        this.ans_2 = r;
        this.ans_3 = r;
        Msg msg = new Msg(r,Msg.TYPE_RECEIVED);
        msgList.add(msg);
        adapter.notifyItemInserted(msgList.size()-1);
        msgRecyclerView.scrollToPosition(msgList.size()-1);
        showdiag();
    }

    private void showdiag(){
        builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("请选择您认为是最好的答案");
        String[] choices = {"第一个答案","第二个答案","第三个答案","第四个答案"};
        builder.setSingleChoiceItems(choices, indexofitem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                indexofitem = i;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(getActivity(),"thanks for your vote",Toast.LENGTH_SHORT);
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

    public void initDataBase(){
        try {
            mongoClient = new MongoClient("118.25.135.35",27017);
            mongoDatabase = mongoClient.getDatabase("qarobottest");
            Toast.makeText(this.getActivity(),"connect to the database successfully",Toast.LENGTH_SHORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
