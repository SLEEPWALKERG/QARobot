package com.example.adam.qarobot;

import android.os.Handler;
import android.os.Message;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Getdata_thread extends Thread{
    private Handler mainhandler;
    private List<Qa_pair> lst = new ArrayList<>();

    public Getdata_thread(Handler mainhandler) {
        this.mainhandler = mainhandler;
    }


    @Override
    public void run() {
        MongoClient mongoClient = new MongoClient("118.25.135.35", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("te");
        MongoCollection<Document> collection = mongoDatabase.getCollection("unchecked");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            try {
                JSONObject jsonObject = new JSONObject(document.toJson());
                Qa_pair qaPair = new Qa_pair(jsonObject.getString("question"),jsonObject.getString("answer"));
                lst.add(qaPair);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Message message = new Message();
        message.what = 0;
        message.obj = lst;
        mainhandler.sendMessage(message);
    }
}
