package gr.aueb.BookingApp.backend.Reducer;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class handles requests to Reducer
 */
public class ActionsForReducer extends Thread {

    private static int workerNodesSent = 0;
    private ObjectInputStream inFromWorker;
    private ArrayList<Room> rooms;

    ActionsForReducer(Socket connection,ArrayList<Room> rooms) {
        try {
            this.inFromWorker = new ObjectInputStream(connection.getInputStream());
            this.rooms = rooms;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method receive all the data that Workers send,
     * add them to a list and send the list back to Master
     * This method performs the MapReduce process
     */
    @Override
    public void run() {

            try {
                ArrayList<Room> filteredRooms;
                if(inFromWorker != null) {
                    if (workerNodesSent < Config.numberOfWorkers) {
                        filteredRooms = (ArrayList<Room>) inFromWorker.readObject();
                        if(!filteredRooms.isEmpty()){
                            synchronized (rooms) {
                                rooms.addAll(filteredRooms);
                            }
                        }

                        synchronized ((Object) workerNodesSent) {
                            workerNodesSent++;
                        }
                        System.out.println("Gathering rooms data from workers...");

                    }
                    if (workerNodesSent == Config.numberOfWorkers) {
                        System.out.println("Rooms data gathering finished!");
                        try {
                            System.out.println("Writing results to master...");

                            Socket masterRequestSocket = new Socket(Config.masterHostIP, Config.masterServerSocketPort);
                            ObjectOutputStream outToMaster = new ObjectOutputStream(masterRequestSocket.getOutputStream());

                            synchronized (rooms) {

                                outToMaster.writeInt(5);
                                outToMaster.flush();

                                outToMaster.writeObject(rooms);
                                outToMaster.flush();
                                rooms.clear();
                            }

                            synchronized ((Object) workerNodesSent) {
                                workerNodesSent = 0;
                            }

                        } catch (UnknownHostException unknownHost) {
                            System.err.println("You are trying to connect to an unknown host!");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            } catch(IOException e){
                throw new RuntimeException(e);
            } catch(ClassNotFoundException e){
                throw new RuntimeException(e);
            }


    }

}
