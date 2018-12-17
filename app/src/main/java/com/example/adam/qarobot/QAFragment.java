package com.example.adam.qarobot;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.List;


public class QAFragment extends Fragment {

    private static int cou = 0;
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
    private Handler mhandler;
    private static final int ans_cqa = 0;
    private static final int ans_kbqa = 1;
    private static final int checked = 4;

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

    @SuppressLint("HandlerLeak")
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
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ans_cqa:
                        String str_cqa = (String) msg.obj;
                        Msg msg1 = new Msg("社区问答：\n" + str_cqa, Msg.TYPE_RECEIVED);
                        msgList.add(msg1);
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        store_1((String)(msg.obj),ans_cqa);
                        cou++;
                        break;
                    case ans_kbqa:
                        Msg msg2 = new Msg("阅读理解模型：\n" + (String)(msg.obj), Msg.TYPE_RECEIVED);
                        msgList.add(msg2);
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        store_1((String)(msg.obj),ans_kbqa);
                        cou++;
                        break;
                    case checked:
                        showdiag();
                        cou = 0;
                        break;
                    default:
                            break;
                }
            }
        };
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                    cou = 0;
                    /*CountDownLatch countDownLatch = new CountDownLatch(1);
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
                    //showdiag();*/
                    CQA_Thread cqa_thread = new CQA_Thread(content, mhandler);
                    cqa_thread.start();
                    KBQA_Thread kbqa_thread = new KBQA_Thread(mhandler,content);
                    kbqa_thread.start();
                    Check_Thread check_thread = new Check_Thread(mhandler);
                    check_thread.start();
                }
            }
        });
        return view;
    }

    private void initMsgs() {
        Msg msg = new Msg("Hello", Msg.TYPE_RECEIVED);
        msgList.add(msg);
    }


    public void store_1(String r, int index) {
        this.ans[index] = r;
    }

    private void showdiag() {
        builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("请选择您认为是最好的答案");
        String[] choices = {"社区问答", "阅读理解模型"};
        builder.setSingleChoiceItems(choices, indexofitem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                indexofitem = i;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (indexofitem == 1) {
                    MongoCollection<Document> collection = mongoDatabase.getCollection("checked");
                    Document document = new Document("question", content).append("answer", ans[indexofitem]);
                    collection.insertOne(document);
                } else if (indexofitem == 0){
                    MongoCollection<Document> collection = mongoDatabase.getCollection("unchecked");
                    Document document = new Document("question", content).append("answer", ans[indexofitem]);
                    System.out.println(content);
                    System.out.println(ans[indexofitem]);
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
        mongoClient = new MongoClient("118.25.135.35", 27017);
        mongoDatabase = mongoClient.getDatabase("te");
        Toast.makeText(this.getActivity(), "connect to the database successfully", Toast.LENGTH_SHORT);
    }

    public static int getCou() {
        return cou;
    }
}
