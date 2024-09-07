package gr.aueb.BookingApp.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class Reservation implements Serializable {

    private final String reservationName;
    private final LocalDate from;
    private final LocalDate to;

    public Reservation(String reservationName, LocalDate from, LocalDate to) {
        this.reservationName = reservationName;
        this.from = from;
        this.to = to;
    }

    public String getReservationName() {
        return reservationName;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Reservation under the name " + reservationName +
                " from " + from +
                " until " + to;
    }
}
