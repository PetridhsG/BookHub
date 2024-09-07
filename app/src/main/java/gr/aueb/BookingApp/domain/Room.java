package gr.aueb.BookingApp.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;


/**
 *  This class implements the logic of a Room
 */
public class Room implements Serializable{

    private int numberOfPeople;
    private int numberOfReviews;
    private int price;
    private float stars;
    private String roomName;
    private String area;
    private String roomImage;
    private ArrayList<LocalDate[]> dates = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();

    public Room(){}

    // This constructor handles Json strings
    public Room(String roomName,int price,int numberOfPeople,String area,float stars, int numberOfReviews,
                ArrayList<LocalDate[]> dates,ArrayList<Reservation> reservations, String roomImage) {
        this.roomName = roomName;
        this.price = price;
        this.numberOfPeople = numberOfPeople;
        this.area = area;
        this.stars = stars;
        this.numberOfReviews = numberOfReviews;
        this.dates = dates;
        this.reservations = reservations;
        this.roomImage = roomImage;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public int getPrice() {
        return price;
    }

    public int getStars() {
        return Math.round(stars);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getArea() {
        return area;
    }

    public String getRoomImage(){
        return roomImage;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public void addDate(LocalDate from, LocalDate to){
        dates.add(new LocalDate[]{from , to});
    }

    public void addDates(ArrayList<LocalDate[]> dates){ this.dates.addAll(dates);}

    public ArrayList<LocalDate[]> getDates(){
        return this.dates;
    }

    public void addReservation(Reservation reservation){
        reservations.add(reservation);
    }

    public ArrayList<Reservation> getReservations(){
        return this.reservations;
    }

    @Override
    public String toString() {

        String roomString = "---------------------------------------\n" +
                roomName + "\n" +
                "Number Of People = " + numberOfPeople +
                " | Price = " + price +
                " | Stars = " + Math.round(stars) + "(" + numberOfReviews + ")" +
                " | Area = " + area;
        if(! getDates().isEmpty()){
            roomString += "\n" + "Available Dates:";
            for(LocalDate[] date: getDates()){

                String from = date[0].toString();
                String to = date[1].toString();
                roomString += "\nFrom:" + from + " | To:" + to ;
            }
        }
        else{
            roomString += "\n" + "No available dates for this room";
        }

        return roomString;

    }

}
