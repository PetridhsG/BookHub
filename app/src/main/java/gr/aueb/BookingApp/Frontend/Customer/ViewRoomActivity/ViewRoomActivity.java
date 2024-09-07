package gr.aueb.BookingApp.Frontend.Customer.ViewRoomActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import gr.aueb.BookingApp.Frontend.Customer.SearchActivity.SearchActivity;
import gr.aueb.BookingApp.Frontend.Customer.Threads.RateThread;
import gr.aueb.BookingApp.Frontend.Customer.Threads.ReservationThread;
import gr.aueb.BookingApp.R;
import gr.aueb.BookingApp.domain.Room;

public class ViewRoomActivity extends AppCompatActivity {

    private Room room;

    public Handler ratingHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            createRatingToastMessage(message.arg1);
            Intent intent = new Intent(ViewRoomActivity.this, SearchActivity.class);
            startActivity(intent);
            return false;
        }
    });

    public Handler reservationHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            createReservationToastMessage(message.arg1);
            Intent intent = new Intent(ViewRoomActivity.this, SearchActivity.class);
            startActivity(intent);
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room);

        Room room = (Room) getIntent().getSerializableExtra("room");
        this.room = room;
        ImageView roomImage = findViewById(R.id.imageView);
        TextView roomNameText = findViewById(R.id.room_name_text);
        TextView roomAreaText = findViewById(R.id.room_area_text);
        TextView roomStarsText = findViewById(R.id.room_stars_text);
        TextView roomRatingsText = findViewById(R.id.room_ratings_text);
        TextView roomPriceText = findViewById(R.id.room_price_text);
        TextView roomNoOfPeopleText = findViewById(R.id.room_number_of_people_text);
        TextView datesText = findViewById(R.id.room_dates_text);

        String dateString = "";
        ArrayList<LocalDate[]> roomDates = room.getDates();
        for(LocalDate[] dates: roomDates){
            dateString += dates[0] + " until " + dates[1] + "\n" ;
        }

        String imageName = room.getRoomImage().trim();
        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        roomImage.setImageResource(resourceId);
        roomNameText.setText(room.getRoomName());
        roomAreaText.setText(room.getArea());
        roomStarsText.setText(String.valueOf(room.getStars()));
        roomRatingsText.setText(String.valueOf(room.getNumberOfReviews()));
        roomPriceText.setText(String.valueOf(room.getPrice()));
        roomNoOfPeopleText.setText(String.valueOf(room.getNumberOfPeople()));
        datesText.setText(dateString);

        Button fromDateButton = findViewById(R.id.reservation_from_date_button);
        TextView fromDateTextView = findViewById(R.id.reservation_from_date_text);
        fromDateButton.setOnClickListener(v -> showDatePickerDialog(fromDateTextView));
        Button toDateButton = findViewById(R.id.reservation_to_date_button);
        TextView toDateTextView = findViewById(R.id.reservation_to_date_text);
        toDateButton.setOnClickListener(v -> showDatePickerDialog(toDateTextView));

        Button reserveButton = findViewById(R.id.reserve_button);
        reserveButton.setOnClickListener(v -> reserve());

        Button rateButton = findViewById(R.id.rate_button);
        rateButton.setOnClickListener(v -> rate());
    }

    public void rate(){
        float rating = ((RangeSlider)findViewById(R.id.rating_slider)).getValues().get(0);
        String roomName = room.getRoomName();
        RateThread thread = new RateThread(ratingHandler,roomName,rating);
        thread.start();
    }

    private void reserve(){
        String from = ((TextView)findViewById(R.id.reservation_from_date_text)).getText().toString().trim();
        String to = ((TextView)findViewById(R.id.reservation_to_date_text)).getText().toString().trim();
        String roomName = room.getRoomName();
        String reservationName = ((EditText)findViewById(R.id.reservation_name_text)).getText().toString().trim();
        int counter = 0;
        if(reservationName.equals("")){
            Toast.makeText(ViewRoomActivity.this,"Reservation name is empty!",Toast.LENGTH_SHORT).show();
            counter++;
        }
        if(from.equals("None")){
            Toast.makeText(ViewRoomActivity.this,"From date is empty!",Toast.LENGTH_SHORT).show();
            counter++;
        }
        if(to.equals("None")){
            Toast.makeText(ViewRoomActivity.this,"To date is empty!",Toast.LENGTH_SHORT).show();
            counter++;
        }
        if(counter > 0){
            Toast.makeText(ViewRoomActivity.this,"There was an error with your details!",Toast.LENGTH_SHORT).show();
        }
        else{
            ReservationThread thread = new ReservationThread(reservationHandler,roomName,reservationName,
                    new LocalDate[]{LocalDate.parse(from), LocalDate.parse(to)});
            thread.start();
        }

    }

    private void showDatePickerDialog(TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = selectedDate.format(formatter);
                    dateTextView.setText(formattedDate);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void createRatingToastMessage(int response) {

        String message = "";
        switch (response) {
            case -1:
                message = "There was an error.Please try again.";
                break;
            case 0:
                message = "Your rating submitted successfully!";
                break;
            case 1:
                message = "Room wasn't found.";
                break;
        }
        Toast.makeText(ViewRoomActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    private void createReservationToastMessage(int response) {

        String message = "";
        switch (response) {
            case -1:
                message = "There was an error.Please try again.";
                break;
            case 0:
                message = "The room was reserved successfully.";
                break;
            case 1:
                message = "Room wasn't found.";
                break;
            case 2:
                message = "No dates found for this room.";
                break;
            case 3:
                message = "There are no available dates for this room.";
                break;
        }
        Toast.makeText(ViewRoomActivity.this,message,Toast.LENGTH_SHORT).show();
    }

}