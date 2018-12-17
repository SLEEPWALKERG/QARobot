package com.example.adam.qarobot;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CQA_Thread extends Thread {
    private String question;
    private Handler mainhandler;
    private static final String CQA = "http://211.144.121.122:18887/proxy?p=";

    public CQA_Thread(String question, Handler mainhandler) {
        this.question = question;
        this.mainhandler = mainhandler;
    }

    @Override
    public void run() {
        Message message;
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
                //System.out.println(line);
                response.append(line);
            }
            message = new Message();
            message.what = 0;
            String tmp = response.toString();
            int start = tmp.indexOf("</br>");
            start += 5;
            int end = tmp.indexOf("</br>", start);
            message.obj = tmp.substring(start,end);
            mainhandler.sendMessage(message);
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
