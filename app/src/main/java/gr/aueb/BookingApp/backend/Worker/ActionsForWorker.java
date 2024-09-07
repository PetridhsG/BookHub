package gr.aueb.BookingApp.backend.Worker;

import gr.aueb.BookingApp.Config.Config;
import gr.aueb.BookingApp.domain.Reservation;
import gr.aueb.BookingApp.domain.Room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class handles requests to Worker
 */
public class ActionsForWorker extends Thread {
    private ObjectInputStream inFromMaster;
    private ObjectOutputStream outToMaster;
    private ArrayList<Room> rooms;
    private int workerID;

    private Socket connection;

    ActionsForWorker(Socket connection, int workerID, ArrayList<Room> rooms) {
        try {
            this.connection = connection;
            this.outToMaster = new ObjectOutputStream(connection.getOutputStream());
            this.inFromMaster = new ObjectInputStream(connection.getInputStream());
            this.workerID = workerID;
            this.rooms = rooms;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            int action = inFromMaster.readInt();  // Read action from Client
            switch (action){
                case 0:     // Add room
                    addRoom();
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method adds a room to Worker rooms
     */
    private void addRoom() {
        Room room ;
        try {
            room = (Room) inFromMaster.readObject();    // Get room from master
            synchronized (this.rooms){                  // Synchronize Worker rooms in case another thread is trying to write
                rooms.add(room);
            }
            System.out.println("Successfully added " + room.getRoomName() + " to Worker " + workerID);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This method receive a request from Master with the filters given by the user,
     * processes the data and send them to Reducer
     * This method performs the MapReduce process
     */
    private void search(){

        ArrayList<Room> filteredRooms = new ArrayList<>();
        try {
            ArrayList<Integer> filterOptions = (ArrayList<Integer>) inFromMaster.readObject();
            ArrayList<Object> filters = (ArrayList<Object>) inFromMaster.readObject();

            synchronized (this.rooms){              // Synchronize Worker rooms in case another thread is trying to write or read data that is currently changing
                ArrayList<Room> tempRooms = new ArrayList<>(this.rooms);

                for (Integer action : filterOptions) {
                    Object filter = null ;
                    if (filters != null){
                        filter = filters.remove(0);
                    }
                    ArrayList<Room> temp = new ArrayList<>();
                    filteredRooms.clear();
                    switch (action) {
                        case 1:                                 // No filter
                            for (Room room : tempRooms) {
                                temp.add(room);
                                filteredRooms.add(room);
                            }
                            break;
                        case 2:                                 // Filter by area
                            for (Room room : tempRooms) {
                                if (room.getArea().equals(filter.toString())) {
                                    temp.add(room);
                                    filteredRooms.add(room);
                                }
                            }
                            break;
                        case 3:                                 // Filter by dates
                            LocalDate[] dates = (LocalDate[]) filter;
                            LocalDate from = dates[0];
                            LocalDate to = dates[1];
                            for (Room room : tempRooms) {
                                for (LocalDate[] roomDates : room.getDates()) {
                                    if (from.equals(roomDates[0]) && to.equals(roomDates[1])) {
                                        filteredRooms.add(room);
                                        temp.add(room);
                                        break;
                                    }
                                }
                            }
                            break;
                        case 4:                                  // Filter by number of people
                            for (Room room : tempRooms) {
                                if (room.getNumberOfPeople() == (int) filter) {
                                    filteredRooms.add(room);
                                    temp.add(room);
                                }
                            }
                            break;
                        case 5:                                 // Filter by price
                            int[] prices = (int[]) filter;
                            int min = prices[0];
                            int max = prices[1];
                            for (Room room : tempRooms) {
                                if (room.getPrice() >= min && room.getPrice() <= max) {
                                    filteredRooms.add(room);
                                    temp.add(room);
                                }
                            }
                            break;
                        case 6:                                 // Filter by rating

                            for (Room room : tempRooms) {
                                if (room.getStars() == Math.round((float) filter)) {
                                    System.out.println(room.getRoomName() + " " + room.getArea());
                                    filteredRooms.add(room);
                                    temp.add(room);
                                }
                            }
                            break;
                    }

                    tempRooms.clear();
                    tempRooms.addAll(temp);
                    temp.clear();

                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println("Search finished.");
            System.out.println("Sending results to reducer from Worker " + workerID );
            Socket requestSocket = new Socket(Config.reducerHostIP, Config.reducerServerSocketPort);   // Connect to Reducer
            ObjectOutputStream outToReducer = new ObjectOutputStream(requestSocket.getOutputStream());

            outToReducer.writeObject(filteredRooms);        // Send filtered rooms to Reducer
            outToReducer.flush();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void addAvailableDates(){

        try {
            int finishCode = 1;     // Room wasn't found
            String roomName = inFromMaster.readUTF();      // Read roomName from Master
            ArrayList<LocalDate[]> dates = (ArrayList<LocalDate[]>) inFromMaster.readObject();    // Read dates from Master
            synchronized(rooms){                   // Synchronize Worker rooms in case another thread is trying to write or read data that is currently changing
                for(Room room : rooms) {                                     // For every room in Worker rooms
                    if (room.getRoomName().equals(roomName)) {               // If room name was found
                        for (LocalDate[] givenDate : dates) {                      // For every pair of dates given
                            if (!room.getDates().isEmpty()) {               // If available dates is empty, add all the dates given
                                for (LocalDate[] dataDate : room.getDates()) {   // For every date in Worker room dates
                                    if (givenDate[0].equals(dataDate[0]) && givenDate[1].equals(dataDate[1])) {  // The date already exists
                                        finishCode = 2;
                                    } else if ((givenDate[0].isAfter(dataDate[0]) && givenDate[0].isBefore(dataDate[1]) ||     // There is one day that interferes with another
                                            (givenDate[1].isAfter(dataDate[0]) && givenDate[1].isBefore(dataDate[1])))) {      // e.g. 13 - 17 interferes in 10 - 15
                                        finishCode = 3;
                                    } else {
                                        finishCode = 0;
                                        continue;   // If this day is correct continue
                                    }
                                    System.out.println("There was an error with the given dates.");
                                    break;  // If at least one day is not correct or all dates searched, break
                                }
                                if (finishCode != 0) {
                                    break;  // If at least one day is not correct break
                                }
                            } else {
                                finishCode = 0;     // All dates are correct
                                break;
                            }
                        }
                        if (finishCode == 0) {       // All the dates was correct, so we add them to available dates list
                            room.addDates(dates);
                            System.out.println("Dates successfully added.");
                        }
                        break;  // The room was found, so the loop may stop
                    }
                }
            }

            outToMaster.writeInt(finishCode);    // Send finish code back to Master
            outToMaster.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void book(){
        try {
            int finishCode = 1;     // Room wasn't found
            String roomName = inFromMaster.readUTF();           // Read roomName from Master
            String reservationName = inFromMaster.readUTF();    // Read reservation name from Master
            LocalDate[] dates = (LocalDate[]) inFromMaster.readObject();  // Read dates from Master
            LocalDate inFrom = dates[0];      // From date that user gave
            LocalDate inTo = dates[1];        // To date that user gave
            synchronized(rooms){              // Synchronize Worker rooms in case another thread is trying to write or read data that is currently changing
                for(Room room : rooms) {      // For every room in Worker rooms
                    if (room.getRoomName().equals(roomName)) {   // If room name was found
                        if (!room.getDates().isEmpty()) {        // If available dates is empty
                            for (LocalDate[] date : room.getDates()) {   // For every pair of dates in Worker room dates
                                LocalDate from = date[0];         // From date in data
                                LocalDate to = date[1];           // To date in data
                                if ((inFrom.equals(from) && inTo.equals(to))) {         // The exact dates give by the user e.g. given:10-15 exists:10-15
                                    room.getDates().remove(date);                       // Remove this date from the available dates
                                    room.addReservation(new Reservation(reservationName, inFrom, inTo));    // Create new Reservation
                                    finishCode = 0;                                     // Room successfully booked
                                } else if (inFrom.equals(from) && inTo.isBefore(to)) {  // There are remaining dates. e.g exists:10-15 given:10-13 remains:14-15
                                    room.getDates().remove(date);                       // Remove this date from the available dates
                                    room.addDate(inTo.plusDays(1), to);                      // Add the remaining date
                                    room.addReservation(new Reservation(reservationName, inFrom, inTo));    // Create new Reservation
                                    finishCode = 0;                                                     // Room successfully booked
                                } else if (inFrom.isAfter(from) && inTo.equals(to)) {        // There are remaining dates. e.g. exists:10-15 given:13-15 remains:10-12
                                    room.getDates().remove(date);                            // Remove this date from the available dates
                                    room.addDate(from, inFrom.minusDays(1));  // Add the remaining date
                                    room.addReservation(new Reservation(reservationName, inFrom, inTo));    // Create new Reservation
                                    finishCode = 0;                                                     // Room successfully booked
                                } else if (inFrom.isAfter(from) && inTo.isBefore(to)) {     //There are remaining dates. e.g. exists:10-15 given:12-13 remains:10-11 and 14-15
                                    room.getDates().remove(date);                              // Remove this date from the available dates
                                    room.addDate(from, inFrom.minusDays(1));    // Add the remaining date
                                    room.addDate(inTo.plusDays(1),to );            // Add the remaining date
                                    room.addReservation(new Reservation(reservationName, inFrom, inTo));    // Create new Reservation
                                    finishCode = 0;                                                     // Room successfully booked
                                } else {                // The given dates don't match with these dates
                                    finishCode = 2;     // Not available dates found for the given dates
                                    continue;           // Search for another dates in room data
                                }
                                System.out.println("Room successfully reserved.");
                                break;          // If at least one available day found
                            }
                        } else {
                            System.out.println("There was an error with the reservation.");
                            finishCode = 3;     // There aren't available dates to this room
                        }
                        break;      // The room was found, so the loop may stop
                    }
                }
            }

            outToMaster.writeInt(finishCode);   // Send finish code back to Master
            outToMaster.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void rate(){
        try {
            int finishCode = 1;        // Room wasn't found
            String roomName = inFromMaster.readUTF();           // Read roomName from Master
            float rating = (float) inFromMaster.readObject();   // Read rating from Master
            synchronized(rooms) {              // Synchronize Worker rooms in case another thread is trying to write or read data that is currently changing
                for (Room room : rooms) {                             // For every room in Worker rooms
                    if (room.getRoomName().equals(roomName)) {        // If room name was found
                        float mean = room.getStars();                      // Mean value : m
                        int numberOfReviews = room.getNumberOfReviews();   // NumberOfReviews : n
                        float ratingSum = mean * (float) numberOfReviews;  // RatingsSum: s = m * n (for m = s / n)
                        ratingSum += rating;                               // Add new rating
                        numberOfReviews++;                                 // Increment by 1 the number of reviews
                        mean = ratingSum / numberOfReviews;                // The new mean value (rating)
                        room.setStars(mean);                               // Set the new rating of the room
                        room.setNumberOfReviews(numberOfReviews);          // Set the new number of reviews of the room
                        finishCode = 0;    // Room was found
                        System.out.println("Rating added successfully.");
                        break;
                    }
                }
            }
            outToMaster.writeInt(finishCode);   // Send finish code back to Master
            outToMaster.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
