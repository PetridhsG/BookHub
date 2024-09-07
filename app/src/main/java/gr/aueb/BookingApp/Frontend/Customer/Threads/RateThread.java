package gr.aueb.BookingApp.Frontend.Customer.Threads;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import gr.aueb.BookingApp.backend.Client.Client;
import gr.aueb.BookingApp.domain.Room;

public class RateThread extends Thread{
    private Handler handler;
    private String roomName;
    private float rating;
    public RateThread(Handler handler,String roomName,float rating){
        this.handler = handler;
        this.roomName = roomName;
        this.rating = rating;
    }

    @Override
    public void run(){
        int response = new Client().rate(roomName,rating);
        Message msg = handler.obtainMessage();
        msg.arg1 = response;
        handler.sendMessage(msg);
    }
}
