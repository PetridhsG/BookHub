package gr.aueb.BookingApp.Frontend.Manager;

import gr.aueb.BookingApp.backend.Client.Client;
import gr.aueb.BookingApp.domain.Reservation;
import gr.aueb.BookingApp.domain.Room;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

/**
 *  This class implements the console app for the manager
 */
public class ManagerApp {

    public static void main(String[] args) {

        Client client = new Client();
        Scanner in = new Scanner(System.in);
        ArrayList<Room> rooms;
        System.out.println("Welcome to manager app!");
        while (true) {
            ArrayList<Integer> filterOptions = new ArrayList<>();
            ArrayList<Object> filters = new ArrayList<>();
            System.out.println("---------------------------------");
            System.out.println("0. Exit");
            System.out.println("1. Add rooms.");
            System.out.println("2. Show your rooms.");
            System.out.println("3. Add available dates for your rooms.");
            System.out.println("4. Show reservations for your rooms.");
            System.out.println("5. Show statistics.");
            System.out.println("---------------------------------");
            switch (Integer.parseInt(in.nextLine())) {
                case 0:
                    System.out.println("Exiting...");
                    in.close();
                    System.exit(0);
                case 1:
                    System.out.print("Please give the JSON file name:");
                    String fileName = in.nextLine();
                    if(client.addRooms(fileName) == 0){
                        System.out.println("Rooms added successfully");
                    }
                    else{
                        System.out.println("There was an error.Please try again.");
                    }
                    break;
                case 2:
                    filterOptions.add(1);
                    filters.add("");
                    rooms = client.search(filterOptions,filters);
                    if(rooms != null){
                        if (!rooms.isEmpty()) {
                            for (Room room : rooms) {
                                System.out.println(room);
                            }
                        }
                        else{
                            System.out.println("No rooms added yet.");
                        }
                    }
                    break;
                case 3:
                    System.out.print("Please choose your room by room name:");
                    String roomName2 = in.nextLine();
                    ArrayList<LocalDate[]> dates = new ArrayList<>();
                    while(true) {
                        System.out.print("Please give the date you want your room to be available from (YYYY-MM-DD):");
                        LocalDate from = LocalDate.parse((in.nextLine()));
                        System.out.print("Please give the date you want your room to be available until (YYYY-MM-DD):");
                        LocalDate to = LocalDate.parse((in.nextLine()));
                        if (!dates.isEmpty()){
                            for (LocalDate[] date: dates){
                                if (from.equals(date[0]) && to.equals(date[1])){
                                    System.out.println("This date is already added.");
                                }
                                else if ((from.isAfter(date[0]) && from.isBefore(date[1]) ||
                                        (to.isAfter(date[0]) && to.isBefore(date[1])))){
                                    System.out.println("This date intervenes in another.");
                                }
                                else{
                                    dates.add(new LocalDate[]{from , to});
                                }
                                break;
                            }
                        }
                        else{
                            dates.add(new LocalDate[]{from , to});
                        }
                        System.out.print("To stop adding dates press 'N'.");
                        String answer = in.nextLine();
                        if (answer.equals("N")) {
                            break;
                        }
                    }
                    switch(client.addAvailableDates(roomName2, dates)){
                        case -1:
                            System.out.println("There was an error.Please try again.");
                            break;
                        case 0:
                            System.out.println("Dates added successfully!");
                            break;
                        case 1:
                            System.out.println("Room wasn't found.");
                            break;
                        case 2:
                            System.out.println("One of the dates was already on available dates.");
                            break;
                        case 3:
                            System.out.println("One of the dates intervenes in another.");
                            break;
                    }
                    break;
                case 4:
                    filterOptions.add(1);
                    filters.add("");
                    rooms = client.search(filterOptions,filters);
                    if(rooms != null){
                        if (!rooms.isEmpty()) {
                            for (Room room : rooms) {
                                System.out.println("---------------------------------");
                                System.out.println(room.getRoomName());
                                if(!room.getReservations().isEmpty()){
                                    for(Reservation reservation : room.getReservations()) {
                                        System.out.println(reservation);
                                    }
                                }
                                else{
                                    System.out.println("No reservations to this room");
                                }
                            }
                        }
                        else{
                            System.out.println("No rooms added yet.");
                        }
                    }
                    break;

                case 5:
                    System.out.print("Please give the from date(YYYY-MM-DD):");
                    LocalDate from = LocalDate.parse((in.nextLine()));
                    System.out.print("Please give the to date(YYYY-MM-DD):");
                    LocalDate to = LocalDate.parse((in.nextLine()));
                    HashMap<String,Integer> map = new HashMap<>();
                    filterOptions.add(1);
                    filters.add("");
                    rooms = client.search(filterOptions,filters);
                    if(rooms != null){
                        if (!rooms.isEmpty()) {
                            for (Room room : rooms) {
                                if(!room.getReservations().isEmpty()){
                                    for(Reservation reservation : room.getReservations()) {
                                        if(((reservation.getFrom().equals(from)) || reservation.getFrom().isAfter(from)) &&
                                                    (reservation.getTo().equals(to) || reservation.getTo().isBefore(to))){

                                            if(! map.containsKey(room.getArea())){
                                                map.put(room.getArea(),0);
                                            }
                                            map.put(room.getArea(),map.get(room.getArea()) + 1);

                                        }
                                    }
                                }

                            }
                        }
                        else{
                            System.out.println("No rooms added yet.");
                        }
                    }

                    if(map.size() != 0) {
                        for (HashMap.Entry<String, Integer> entry : map.entrySet()) {
                            String key = entry.getKey();
                            int value = entry.getValue();
                            System.out.println(key + ": " + value);
                        }
                    }
                    else{
                        System.out.println("No reservations found in the given dates.");
                    }

                    break;
                default:
                    System.out.println("Please choose a valid option.");
                    break;
            }
        }
    }

}
