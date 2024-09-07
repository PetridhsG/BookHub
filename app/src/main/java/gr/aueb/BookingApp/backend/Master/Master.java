package gr.aueb.BookingApp.backend.Master;

import gr.aueb.BookingApp.Config.Config;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  This class implements the Master
 *  The Master communicates via Client,Workers and Reducer
 *  The Master gets the requests from the Client, provides them to Workers,
 *  gets the response from Workers or Reducer and provides it to the Client
 */
public class Master extends Thread {
    public static void main(String[] args){
        new Master().start();

    }

    private ServerSocket masterServerSocket;
    private final int masterServerSocketPort;


    public Master(){
        this.masterServerSocketPort = Config.masterServerSocketPort;
    }

    private void openMasterServer(){
        try {
            masterServerSocket = new ServerSocket(this.masterServerSocketPort); // Open ServerSocket at the given port number
            System.out.println("Master is up...");
            while (true) {
                Socket connection = masterServerSocket.accept();                // Accept requested connections
                ActionsForMaster action = new ActionsForMaster(connection);     // Handles the accepted connections
                action.start();
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                masterServerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        openMasterServer();
    }
}
