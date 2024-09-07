package gr.aueb.BookingApp.Frontend.Customer.Threads;

import android.os.Handler;
import android.os.Message;

import java.time.LocalDate;

import gr.aueb.BookingApp.backend.Client.Client;

public class ReservationThread extends Thread{
    private Handler handler;
    private String roomName;
    private String reservationName;
    private LocalDate[] dates;
    public ReservationThread(Handler handler,String roomName,String reservationName,LocalDate[] dates){
        this.handler = handler;
        this.roomName = roomName;
        this.reservationName = reservationName;
        this.dates = dates;
    }

    @Override
    public void run(){
        int response = new Client().book(roomName,reservationName,dates);
        Message msg = handler.obtainMessage();
        msg.arg1 = response;
        handler.sendMessage(msg);
    }
}