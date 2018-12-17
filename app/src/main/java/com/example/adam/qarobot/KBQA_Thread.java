package com.example.adam.qarobot;


import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;


import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class KBQA_Thread extends Thread {
    private Handler mainhandler;
    private String question;
    private static final String KBQA = "http://holer.org:65122/qa";

    public KBQA_Thread(Handler mainhandler, String question) {
        this.mainhandler = mainhandler;
        this.question = question;
    }

    @Override
    public void run() {
        Message message = new Message();
        try {
            OkHttpClient client = new OkHttpClient();
            Request.Builder rebuilder = new Request.Builder();
            HttpUrl.Builder builder = HttpUrl.parse("http://holer.org:65122/qa").newBuilder();
            builder.addQueryParameter("passages", "");
            builder.addQueryParameter("query", question);
            rebuilder.url(builder.build());
            Request request = rebuilder.build();
            Response response = client.newCall(request).execute();
            String s = response.body().string();
            JSONObject jsonObject = new JSONObject(s);
            message.what = 1;
            message.obj = jsonObject.get("answer");
            mainhandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
