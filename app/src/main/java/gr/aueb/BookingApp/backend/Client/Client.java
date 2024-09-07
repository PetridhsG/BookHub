package gr.aueb.BookingApp.backend.Client;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Reservation;
import gr.aueb.BookingApp.domain.Room;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


/**
 *  This class implements the Client
 *  The Client communicates via App and Master
 *  The Client gets the requests from the app, provides them to Master,
 *  gets the response from Master and provides it to the App
 */
public class Client {
    private final int masterServerSocket;
    private final String masterHostIP;

    public Client(){
        this.masterHostIP = Config.masterHostIP;
        this.masterServerSocket = Config.masterServerSocketPort;
    }

    public int addRooms(String fileName){
        try {
            Socket requestSocket = new Socket(this.masterHostIP, this.masterServerSocket);     // Connect to Master
            ObjectOutputStream outToMaster = new ObjectOutputStream(requestSocket.getOutputStream());      // Output stream to Master

            outToMaster.writeInt(0);    // Action 0 : Add rooms
            outToMaster.flush();

            outToMaster.writeObject(createRooms(fileName));     // Write rooms to Master
            outToMaster.flush();

            return 0 ;

        } catch (UnknownHostException unknownHost) {
            System.err.println("There was an error on the Server.Please try again later.");
        } catch (IOException ioException) {
            System.out.println("There was an error on the Server.Please try again later.");
        }
        return -1;
    }

    public ArrayList<Room> search(ArrayList<Integer> filterOptions, ArrayList<Object> filters){
        ArrayList<Room> rooms = null;
        try {
            Socket requestSocket = new Socket(this.masterHostIP, this.masterServerSocket);                  // Connect to Master
            ObjectOutputStream outToMaster = new ObjectOutputStream(requestSocket.getOutputStream());      // Output stream to Master
            ObjectInputStream inFromMaster = new ObjectInputStream(requestSocket.getInputStream());       // Input stream from Master

            outToMaster.writeInt(1);    // Action 1 : Search room by filter
            outToMaster.flush();

            outToMaster.writeObject(filterOptions);
            outToMaster.flush();

            outToMaster.writeObject(filters);
            outToMaster.flush();

            rooms = (ArrayList<Room>) inFromMaster.readObject();    // Master response

            return rooms;
        } catch (UnknownHostException unknownHost) {
            System.err.println("There was an error on the Server.Please try again later.");
        } catch (IOException ioException) {
            System.out.println("There was an error on the Server.Please try again later.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }

    public int addAvailableDates(String roomName, ArrayList<LocalDate[]> dates){
        try {
            Socket requestSocket = new Socket(this.masterHostIP, this.masterServerSocket);     // Connect to Master
            ObjectOutputStream outToMaster = new ObjectOutputStream(requestSocket.getOutputStream());      // Output stream to Master
            ObjectInputStream inFromMaster = new ObjectInputStream(requestSocket.getInputStream());       // Input stream from Master

            outToMaster.writeInt(2);    // Action 2 : Add available dates
            outToMaster.flush();

            outToMaster.writeUTF(roomName);
            outToMaster.flush();

            outToMaster.writeObject(dates);
            outToMaster.flush();

            return inFromMaster.readInt();  // Master response

        } catch (UnknownHostException unknownHost) {
            System.err.println("There was an error on the Server.Please try again later.");
        } catch (IOException ioException) {
            System.out.println("There was an error on the Server.Please try again later.");
        }
        return -1;
    }

    public int book(String roomName , String reservationName , LocalDate[] dates){
        try {
            Socket requestSocket = new Socket(this.masterHostIP, this.masterServerSocket);     // Connect to Master
            ObjectOutputStream outToMaster = new ObjectOutputStream(requestSocket.getOutputStream());      // Output stream to Master
            ObjectInputStream inFromMaster = new ObjectInputStream(requestSocket.getInputStream());       // Input stream from Master

            outToMaster.writeInt(3);     // Action 3 : Reserve a room
            outToMaster.flush();

            outToMaster.writeUTF(roomName);
            outToMaster.flush();

            outToMaster.writeUTF(reservationName);
            outToMaster.flush();

            outToMaster.writeObject(dates);
            outToMaster.flush();

            return inFromMaster.readInt();      // Master response

        } catch (UnknownHostException unknownHost) {
            System.err.println("There was an error on the Server.Please try again later.");
        } catch (IOException ioException) {
            System.out.println("There was an error on the Server.Please try again later.");
        }
        return -1;
    }

    public int rate(String roomName ,float rating){
        try {
            Socket requestSocket = new Socket(this.masterHostIP, this.masterServerSocket);               // Connect to Master
            ObjectOutputStream outToMaster = new ObjectOutputStream(requestSocket.getOutputStream());      // Output stream to Master
            ObjectInputStream inFromMaster = new ObjectInputStream(requestSocket.getInputStream());       // Input stream from Master

            outToMaster.writeInt(4);     // Action 4 : Rate a room
            outToMaster.flush();

            outToMaster.writeUTF(roomName);
            outToMaster.flush();

            outToMaster.writeObject(rating);
            outToMaster.flush();

            return inFromMaster.readInt();   // Master response

        } catch (UnknownHostException unknownHost) {
            System.err.println("There was an error on the Server.Please try again later.");
        } catch (IOException ioException) {
            System.out.println("There was an error on the Server.Please try again later.");
        }
        return -1;
    }

    private ArrayList<Room> createRooms(String fileName){
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File(System.getProperty("user.dir") + Config.roomDataPath + fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        ArrayList<Room> rooms = new ArrayList<>();
        String[] roomData = data.toString().split("(?<=\\})\\s*,\\s*");
        for (String room : roomData) {
            String roomName = room.split("\"roomName\":")[1].split(",")[0].replaceAll("\"", "").trim();
            int price = Integer.parseInt(room.split("\"price\":")[1].split(",")[0].trim());
            int numberOfPeople = Integer.parseInt(room.split("\"noOfPersons\":")[1].split(",")[0].trim());
            String area = room.split("\"area\":")[1].split(",")[0].replaceAll("\"", "").trim();
            float stars = Float.parseFloat(room.split("\"stars\":")[1].split(",")[0].trim());
            int numberOfReviews = Integer.parseInt(room.split("\"noOfReviews\":")[1].split(",")[0].trim());
            String datesString = room.split("\"dates\":")[1].split(",")[0].trim();
            String[] datePairs = datesString.split("\\|");
            ArrayList<LocalDate[]> dates = new ArrayList<>();
            if(datesString.length() > 20) {
                for (String pair : datePairs) {
                    String from = pair.split("from:")[1].split("/")[0].trim().replaceAll("\"", "");
                    String to = pair.split("to:")[1].trim().replaceAll("\"", "");
                    LocalDate f;
                    LocalDate t;
                    f = LocalDate.parse(from);
                    t = LocalDate.parse(to);
                    dates.add(new LocalDate[]{f, t});
                }
            }
            String reservationsString = room.split("\"reservations\":")[1].split("\"roomImage\":")[0].trim();
            String[] reservationData = reservationsString.split("\\|");
            ArrayList<Reservation> reservations = new ArrayList<>();
            if(reservationsString.length() > 20) {
                for (String reservation : reservationData) {
                    String name = reservation.split("name:")[1].split(",")[0].trim().replaceAll("\"", "");
                    String from = reservation.split("from:")[1].split("/")[0].trim().replaceAll("\"", "");
                    String toWithAdditionalText = reservation.split("to:")[1].trim().replaceAll("\"", "");
                    String to = toWithAdditionalText.split("/")[0].trim();
                    LocalDate fromDate = LocalDate.parse(from);
                    LocalDate toDate = LocalDate.parse(to);
                    reservations.add(new Reservation(name, fromDate, toDate));
                }
            }
            String roomImage = room.split("\"roomImage\":")[1].replaceAll("\"", "").trim();
            roomImage = roomImage.replaceAll("}", "");
            rooms.add(new Room(roomName, price, numberOfPeople, area, stars, numberOfReviews, dates, reservations, roomImage));
        }
        return rooms;
    }
}
