package gr.aueb.BookingApp.backend.Master;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Room;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class handles requests to Master
 */
public class ActionsForMaster extends Thread{

    private int numberOfWorkers ;
    private ObjectInputStream inFromClient;
    private ObjectOutputStream outToClient;
    private static ObjectOutputStream outToClient2 ;

    public ActionsForMaster(Socket connection) {
        try {
            inFromClient = new ObjectInputStream(connection.getInputStream());
            outToClient = new ObjectOutputStream(connection.getOutputStream());
            this.numberOfWorkers = Config.numberOfWorkers;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            int action = inFromClient.readInt();    // Read action from Client
            switch (action){
                case 0:     // Add room
                    addRooms();
                    break;
                case 1:     // Search room by filter
                    search();
                    break;
                case 2:     // Add available dates for the rooms
                    addAvailableDates();
                    break;
                case 3:     // Reserve a room
                    book();
                    break;
                case 4:     // Rate a room
                    rate();
                    break;
                case 5:
                    returnToClient();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void returnToClient() {
        try {
            outToClient2.writeObject(inFromClient.readObject());        // Return the result from reducer to client
            outToClient2.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRooms(){

        ArrayList<Room> rooms ;
        try {
            rooms = (ArrayList<Room>) inFromClient.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Room room : rooms) {
            int roomHash = Math.abs(room.getRoomName().hashCode()) % numberOfWorkers;  // WorkerID : hash(RoomName)

            try {
                System.out.println("Routing request to worker " + roomHash);
                Socket requestSocket = new Socket(Config.workerHostIP[roomHash], Config.workerServerSocketPort[roomHash]); // Open connection with the right worker
                ObjectOutputStream outToWorker = new ObjectOutputStream(requestSocket.getOutputStream());

                outToWorker.writeInt(0);    // Action 0 : Add room
                outToWorker.flush();

                outToWorker.writeObject(room);
                outToWorker.flush();

                System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + numberOfWorkers));
                Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + numberOfWorkers], Config.workerServerSocketPort[roomHash + numberOfWorkers]); // Open connection with the right worker
                ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());

                outToWorkerReplica.writeInt(0);    // Action 0 : Add room
                outToWorkerReplica.flush();

                outToWorkerReplica.writeObject(room);
                outToWorkerReplica.flush();

            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    }

    /**
     * This method send a request with the filters given ny the user
     * to all Workers and waits for a response from the Reducer
     * This method performs the MapReduce process
     */
    private void search(){
        System.out.println("Searching rooms...");
        ArrayList<Integer> filterOptions ;
        ArrayList<Object> filters;
        try {
            filterOptions = (ArrayList<Integer>) inFromClient.readObject();
            filters =  (ArrayList<Object>) inFromClient.readObject() ;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        boolean connectionFailed = true;    // if the connection to worker fails

        for (int i = 0 ; i < numberOfWorkers ; i++){        // Send request to all workers

            try {
                System.out.println("Routing request to worker " + i);

                Socket workerRequestSocket = new Socket(Config.workerHostIP[i], Config.workerServerSocketPort[i]);
                connectionFailed = false;
                ObjectOutputStream outToWorker = new ObjectOutputStream(workerRequestSocket.getOutputStream());

                outToWorker.writeInt(1);    // Action 1 : Search room by filter
                outToWorker.flush();

                outToWorker.writeObject(filterOptions);     // Send filter option to Worker
                outToWorker.flush();

                outToWorker.writeObject(filters);        // Send filter to Worker
                outToWorker.flush();

            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (ConnectException connectException) {

                if(connectionFailed){       // That means the worker is down so the replica must handle the request
                    System.out.println("Worker " + i + " seems down.");
                    System.out.println("Routing request to worker's " + i + " replica, worker " + (i + Config.numberOfWorkers));
                    try {
                        Socket workerReplicaRequestSocket = new Socket(Config.workerHostIP[i + Config.numberOfWorkers], Config.workerServerSocketPort[i + Config.numberOfWorkers]);
                        ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(workerReplicaRequestSocket.getOutputStream());

                        outToWorkerReplica.writeInt(1);           // Action 1 : Search room by filter
                        outToWorkerReplica.flush();

                        outToWorkerReplica.writeObject(filterOptions);     // Send filter option to Worker
                        outToWorkerReplica.flush();

                        outToWorkerReplica.writeObject(filters);        // Send filter to Worker
                        outToWorkerReplica.flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    connectException.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        outToClient2 = outToClient;

    }

    private void addAvailableDates(){
        System.out.println("Adding dates..");
        boolean connectionFailed = true;    // if the connection to worker fails
        String roomName;
        Object dates;
        try {
            roomName = inFromClient.readUTF();
            dates = inFromClient.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int roomHash = Math.abs(roomName.hashCode()) % numberOfWorkers ;
        try {

            Socket requestSocket = new Socket(Config.workerHostIP[roomHash], Config.workerServerSocketPort[roomHash]);
            connectionFailed = false;
            ObjectOutputStream outToWorker = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream inFromWorker = new ObjectInputStream(requestSocket.getInputStream());

            System.out.println("Routing request to worker " + roomHash);

            outToWorker.writeInt(2);        // Action 2 : Add available dates
            outToWorker.flush();

            outToWorker.writeUTF(roomName);     // Send roomName to Worker
            outToWorker.flush();

            outToWorker.writeObject(dates);  // Send dates to Worker
            outToWorker.flush();


            Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
            ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
            ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());


            System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));

            outToWorkerReplica.writeInt(2);        // Action 2 : Add available dates
            outToWorkerReplica.flush();

            outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
            outToWorkerReplica.flush();

            outToWorkerReplica.writeObject(dates);     // Send dates to Worker
            outToWorkerReplica.flush();

            inFromWorkerReplica.readInt();

            outToClient.writeInt(inFromWorker.readInt());  // Send Worker response back to client
            outToClient.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (ConnectException connectException) {

            if(connectionFailed){   // That means the worker is down so the replica must handle the request
                System.out.println("Worker " + roomHash + " seems down.");
                System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));
                try {
                    Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
                    ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
                    ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());

                    outToWorkerReplica.writeInt(2);        // Action 2 : Add available dates
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeObject(dates);     // Send dates to Worker
                    outToWorkerReplica.flush();

                    outToClient.writeInt(inFromWorkerReplica.readInt());
                    outToClient.flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                connectException.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void book(){
        System.out.println("Booking room..");
        boolean connectionFailed = true;    // if the connection to worker fails
        String roomName ;
        String reservationName ;
        Object dates;
        try {
            roomName = inFromClient.readUTF();
            reservationName = inFromClient.readUTF()  ;
            dates = inFromClient.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int roomHash = Math.abs(roomName.hashCode()) % numberOfWorkers ;
        try {

            Socket requestSocket = new Socket(Config.workerHostIP[roomHash], Config.workerServerSocketPort[roomHash]);
            connectionFailed = false;
            ObjectOutputStream outToWorker = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream inFromWorker = new ObjectInputStream(requestSocket.getInputStream());

            System.out.println("Routing request to worker " + roomHash);
            outToWorker.writeInt(3);    // Action 3 : Reserve a room
            outToWorker.flush();

            outToWorker.writeUTF(roomName);     // Send roomName to Worker
            outToWorker.flush();

            outToWorker.writeUTF(reservationName);   // Send reservation name to Worker
            outToWorker.flush();

            outToWorker.writeObject(dates);  // Send dates to Worker
            outToWorker.flush();

            Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
            ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
            ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());

            System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));
            outToWorkerReplica.writeInt(3);    // Action 3 : Reserve a room
            outToWorkerReplica.flush();

            outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
            outToWorkerReplica.flush();

            outToWorkerReplica.writeUTF(reservationName);   // Send reservation name to Worker
            outToWorkerReplica.flush();

            outToWorkerReplica.writeObject(dates);  // Send dates to Worker
            outToWorkerReplica.flush();

            outToClient.writeInt(inFromWorker.readInt());       // Send Worker response back to client
            outToClient.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (ConnectException connectException) {

            if(connectionFailed) {   // That means the worker is down so the replica must handle the request
                System.out.println("Worker " + roomHash + " seems down.");
                System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));
                try {

                    Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
                    ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
                    ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());

                    outToWorkerReplica.writeInt(3);        // Action 2 : Add available dates
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeUTF(reservationName);   // Send reservation name to Worker
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeObject(dates);     // Send dates to Worker
                    outToWorkerReplica.flush();

                    outToClient.writeInt(inFromWorkerReplica.readInt());
                    outToClient.flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                connectException.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void rate(){
        System.out.println("Adding rate to room..");
        boolean connectionFailed = true;     // if the connection to worker fails
        String roomName ;
        Object rating;
        try {
            roomName = inFromClient.readUTF();
            rating =  inFromClient.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int roomHash = Math.abs(roomName.hashCode()) % numberOfWorkers ;
        try {
            Socket requestSocket = new Socket(Config.workerHostIP[roomHash], Config.workerServerSocketPort[roomHash]);
            connectionFailed = false;
            ObjectOutputStream outToWorker = new ObjectOutputStream(requestSocket.getOutputStream());
            ObjectInputStream inFromWorker = new ObjectInputStream(requestSocket.getInputStream());

            System.out.println("Routing request to worker " + roomHash);
            outToWorker.writeInt(4);    // Action 4 : Rate a room
            outToWorker.flush();

            outToWorker.writeUTF(roomName);     // Send roomName to Worker
            outToWorker.flush();

            outToWorker.writeObject(rating);   // Send rating to Worker
            outToWorker.flush();

            Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
            ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
            ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());

            System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));
            outToWorkerReplica.writeInt(4);    // Action 4 : Rate a room
            outToWorkerReplica.flush();

            outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
            outToWorkerReplica.flush();

            outToWorkerReplica.writeObject(rating);   // Send rating to Worker
            outToWorkerReplica.flush();

            outToClient.writeInt(inFromWorker.readInt());
            outToClient.flush();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (ConnectException connectException) {

            if(connectionFailed) {   // That means the worker is down so the replica must handle the request
                System.out.println("Worker " + roomHash + " seems down.");

                System.out.println("Routing request to worker's " + roomHash + " replica, worker " + (roomHash + Config.numberOfWorkers));
                try {

                    Socket requestSocketReplica = new Socket(Config.workerHostIP[roomHash + Config.numberOfWorkers], Config.workerServerSocketPort[roomHash + Config.numberOfWorkers]);
                    ObjectOutputStream outToWorkerReplica = new ObjectOutputStream(requestSocketReplica.getOutputStream());
                    ObjectInputStream inFromWorkerReplica = new ObjectInputStream(requestSocketReplica.getInputStream());

                    outToWorkerReplica.writeInt(4);    // Action 4 : Rate a room
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeUTF(roomName);     // Send roomName to Worker
                    outToWorkerReplica.flush();

                    outToWorkerReplica.writeObject(rating);   // Send rating to Worker
                    outToWorkerReplica.flush();

                    outToClient.writeInt(inFromWorkerReplica.readInt());
                    outToClient.flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                connectException.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
