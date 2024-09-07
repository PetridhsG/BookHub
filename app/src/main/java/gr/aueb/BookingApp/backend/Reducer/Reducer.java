package gr.aueb.BookingApp.backend.Reducer;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *  This class implements the Reducer
 *  The Reducer communicates via Master and Workers
 *  The Reducer gets the requests from the Workers,handles them
 *  and send them directly to Master
 */
public class Reducer extends Thread{

    public static void main(String[] args){
        new Reducer().start();
    }
    private ServerSocket reducerServerSocket;
    private final int reducerServerSocketPort;
    public static ArrayList<Room> rooms = new ArrayList<>();
    public Reducer(){
        this.reducerServerSocketPort = Config.reducerServerSocketPort;
    }

    public void openReducerServer(){
        try {
            reducerServerSocket = new ServerSocket(this.reducerServerSocketPort);       // Open ServerSocket at the given port number
            System.out.println("Reducer is up...");
            while (true) {
                Socket connection = reducerServerSocket.accept();                       // Accept requested connections
                ActionsForReducer action = new ActionsForReducer(connection, rooms);    // Handles the accepted connections
                action.start();
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                reducerServerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        openReducerServer();
    }


}
