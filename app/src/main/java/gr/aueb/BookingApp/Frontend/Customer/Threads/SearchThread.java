package gr.aueb.BookingApp.Frontend.Customer.Threads;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import gr.aueb.BookingApp.backend.Client.Client;
import gr.aueb.BookingApp.domain.Room;

public class SearchThread extends Thread{
    private Handler handler;
    private ArrayList<Integer> filterOptions;
    private ArrayList<Object> filters;
    public SearchThread(Handler handler,ArrayList<Integer> filterOptions,ArrayList<Object> filters){
        this.handler = handler;
        this.filterOptions = filterOptions;
        this.filters = filters;
    }

    @Override
    public void run(){
        ArrayList<Room> results = new Client().search(filterOptions,filters);
        Message msg = handler.obtainMessage();
        msg.obj = results;
        handler.sendMessage(msg);
    }
}
