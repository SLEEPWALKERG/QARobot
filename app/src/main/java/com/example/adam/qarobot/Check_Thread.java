package com.example.adam.qarobot;

import android.os.Handler;
import android.os.Message;

public class Check_Thread extends Thread {

    private Handler mainhandler;

    public Check_Thread(Handler mainhandler) {
        this.mainhandler = mainhandler;
    }

    @Override
    public void run() {
        Message message = new Message();
        message.what = 4;
        while (QAFragment.getCou() != 2){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mainhandler.sendMessage(message);
    }
}
