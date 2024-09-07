package gr.aueb.BookingApp.backend.Worker;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Room;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *  This class implements the Worker
 *  The Worker communicates via Master and Reducer
 *  The Worker gets the requests from the Master,handles them
 *  and send them to Reducer or directly to Master
 */
public class Worker extends Thread {
    private final int workerID;     // Unique worker ID
    private ServerSocket workerServerSocket;
    private final int workerServerSocketPort;
    private ArrayList<Room> rooms;      // The rooms that worker handles

    public static void main(String[] args){
        new Worker(Integer.parseInt(args[0])).start();
    }


    public Worker(int workerID){
        this.workerID = workerID;
        this.workerServerSocketPort = Config.workerServerSocketPort[this.workerID];
        rooms = new ArrayList<>();
    }

    public void openWorkerServer(){
        try {
            workerServerSocket = new ServerSocket(this.workerServerSocketPort);      // Open ServerSocket at the given port number
            if(workerID >= Config.numberOfWorkers){
                System.out.println("Worker " + this.workerID + ", worker's " + (this.workerID - Config.numberOfWorkers) + " replica is up...");
            }
            else{
                System.out.println("Worker " + this.workerID + " is up...");
            }
            while (true) {
                Socket connection = workerServerSocket.accept();                                        // Accept requested connections
                ActionsForWorker action = new ActionsForWorker(connection, this.workerID, rooms);       // Handles the accepted connections
                action.start();
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                workerServerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        openWorkerServer();
    }

}
