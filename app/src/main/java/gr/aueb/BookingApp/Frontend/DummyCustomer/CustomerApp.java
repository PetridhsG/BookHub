package gr.aueb.BookingApp.Frontend.DummyCustomer;

import gr.aueb.BookingApp.backend.Client.Client;
import gr.aueb.BookingApp.domain.Room;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;

/**
 *  This class implements the console app for a customer
 */
public class
CustomerApp {

    public static void main(String[] args){

        Client client = new Client();
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("---------------------------------");
            System.out.println("0. Exit");
            System.out.println("1. Search for rooms.");
            System.out.println("2. Reserve a room.");
            System.out.println("3. Rate a room.");
            System.out.println("---------------------------------");
            switch(Integer.parseInt(in.nextLine())){
                case 0 :
                    System.out.println("Exiting...");
                    in.close();
                    System.exit(0);
                case 1:
                    ArrayList<Integer> filterOptions = new ArrayList<>();
                    ArrayList<Object> filters = new ArrayList<>();
                    while(true) {
                        System.out.println("---------------------------------");
                        System.out.println("Please choose the filtering option.");
                        System.out.println("1. None.");
                        System.out.println("2. Area.");
                        System.out.println("3. Date.");
                        System.out.println("4. Number of people.");
                        System.out.println("5. Price.");
                        System.out.println("6. Stars.");
                        System.out.println("---------------------------------");
                        int filterOption = Integer.parseInt(in.nextLine());
                        Object filter = null;
                        if(filterOptions.contains(filterOption)){
                            System.out.println("Filter option already added!");
                            continue;
                        }
                        switch (filterOption) {
                            case 1:
                                filter = "";
                                break;
                            case 2:
                                System.out.print("Please give the area:");
                                filter = in.nextLine();
                                break;
                            case 3:
                                System.out.print("Please give the date you want to rent your room from (YYYY-MM-DD):");
                                LocalDate from = LocalDate.parse((in.nextLine()));
                                System.out.print("Please give the date you want to rent your room until (YYYY-MM-DD):");
                                LocalDate to = LocalDate.parse((in.nextLine()));
                                filter = new LocalDate[]{from, to};
                                break;
                            case 4:
                                System.out.print("Please give the number of people:");
                                filter = Integer.parseInt(in.nextLine());
                                break;
                            case 5:
                                System.out.print("Please give the minimum price:");
                                int min = Integer.parseInt(in.nextLine());
                                System.out.print("Please give the maximum price:");
                                int max = Integer.parseInt(in.nextLine());
                                filter = new int[]{min, max};
                                break;
                            case 6:
                                System.out.print("Please give the stars(1-5):");
                                filter = Float.parseFloat(in.nextLine());
                                break;
                        }
                        filterOptions.add(filterOption);
                        filters.add(filter);
                        if(filterOption == 1){
                            filterOptions.clear();
                            filters.clear();
                            filterOptions.add(1);
                            filters.add("");
                            break;
                        }
                        System.out.print("Do you want to add another filter (Y/N);");
                        String answer = in.nextLine();
                        if(answer.equals("Y")) {
                            continue;
                        }
                        break;
                    }
                    printRooms(client.search(filterOptions, filters));
                    break;
                case 2:
                    System.out.print("Please choose a room by typing its name:");
                    String roomName = in.nextLine();
                    System.out.print("Please give your name:");
                    String reservationName = in.nextLine();
                    System.out.print("Please give the date you want to rent your room from (YYYY-MM-DD):");
                    LocalDate from = LocalDate.parse((in.nextLine()));
                    System.out.print("Please give the date you want to rent your room until (YYYY-MM-DD):");
                    LocalDate to = LocalDate.parse((in.nextLine()));
                    switch (client.book(roomName, reservationName , new LocalDate[]{from, to})){
                        case -1:
                            System.out.println("There was an error.Please try again.");
                            break;
                        case 0:
                            System.out.println("The room was reserved successfully.");
                            break;
                        case 1:
                            System.out.println("Room wasn't found.");
                            break;
                        case 2:
                            System.out.println("No dates found for this room.");
                            break;
                        case 3:
                            System.out.println("There are no available dates for this room.");
                            break;
                    }
                    break;
                case 3:
                    System.out.print("Please choose a room by typing its name:");
                    String roomName1 = in.nextLine();
                    System.out.print("Give your rating(1-5):");
                    float roomRating = Float.parseFloat(in.nextLine());
                    switch(client.rate(roomName1,roomRating)){
                        case -1:
                            System.out.println("There was an error.Please try again.");
                            break;
                        case 0:
                            System.out.println("Your rating submitted successfully!");
                            break;
                        case 1:
                            System.out.println("Room wasn't found.");
                            break;
                    }
                    break;

                default:
                    System.out.println("Please choose a valid option.");
                    break;
            }
        }
    }

    public static void printRooms(ArrayList<Room> rooms){
        if (! rooms.isEmpty()){
            for (Room room : rooms) {
                System.out.println(room);
            }
        }
        else{
            System.out.println("There weren't any rooms by your search preference!");
        }
    }
}
