package gr.aueb.BookingApp.Frontend.Customer.SearchActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import gr.aueb.BookingApp.Frontend.Customer.ResultsActivity.ResultsActivity;
import gr.aueb.BookingApp.Frontend.Customer.Threads.SearchThread;
import gr.aueb.BookingApp.R;
import gr.aueb.BookingApp.domain.Room;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<Integer> filterOptions = new ArrayList<>();
    private ArrayList<Object> filters = new ArrayList<>();

    public Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            ArrayList<Room> rooms = (ArrayList<Room>) message.obj;
            Intent intent = new Intent(SearchActivity.this, ResultsActivity.class);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = null;
            try {
                objectStream = new ObjectOutputStream(byteStream);
                objectStream.writeObject(rooms);
                objectStream.flush();
                byte[] results = byteStream.toByteArray();
                objectStream.close();
                objectStream.close();
                intent.putExtra("rooms", results);
                startActivity(intent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        filterOptions.add(1);
        filters.add("");

        Spinner areaSpinner = findViewById(R.id.area_spinner);
        String[] areas = new String[]{"NONE","ATH","SKG","VOL","KVA","JMK","CHQ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, areas);
        areaSpinner.setAdapter(adapter);

        Button fromDateButton = findViewById(R.id.search_from_date_button);
        TextView fromDateTextView = findViewById(R.id.search_from_date_text);
        fromDateButton.setOnClickListener(v -> showDatePickerDialog(fromDateTextView));
        Button toDateButton = findViewById(R.id.search_to_date_button);
        TextView toDateTextView = findViewById(R.id.search_to_date_text);
        toDateButton.setOnClickListener(v -> showDatePickerDialog(toDateTextView));

        Button minusButton = findViewById(R.id.minus_button);
        minusButton.setOnClickListener(v -> changeNoOfPeopleValue(false));
        Button plusButton = findViewById(R.id.plus_button);
        plusButton.setOnClickListener(v -> changeNoOfPeopleValue(true));

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> clearFilters());

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> search());

    }

    private void search(){
        String area = ((Spinner)findViewById(R.id.area_spinner)).getSelectedItem().toString().trim();
        String from = ((TextView)findViewById(R.id.search_from_date_text)).getText().toString().trim();
        String to = ((TextView)findViewById(R.id.search_to_date_text)).getText().toString().trim();
        String noOfPeople = ((TextView)findViewById(R.id.number_of_people)).getText().toString().trim();
        int minPrice =Math.round(((RangeSlider)findViewById(R.id.price_slider)).getValues().get(0));
        int maxPrice = Math.round(((RangeSlider)findViewById(R.id.price_slider)).getValues().get(1));
        float stars = ((RangeSlider)findViewById(R.id.rating_slider)).getValues().get(0);

        if(filterOptions.size() > 1 && filterOptions.contains(1)){
            filterOptions.clear();
            filters.clear();
        }
        if(!area.equals("NONE")){
            filterOptions.add(2);
            filters.add(area);
        }
        if(!from.equals("None") && !to.equals("None")){
            filterOptions.add(3);
            filters.add(new LocalDate[]{
                    LocalDate.parse(from),LocalDate.parse(to)
            });
        }
        if(!noOfPeople.equals("None")){
            filterOptions.add(4);
            filters.add(Integer.parseInt(noOfPeople));
        }
        if(!(minPrice == 0 && maxPrice == 800)){
            filterOptions.add(5);
            filters.add(new int[]{minPrice,maxPrice});
        }
        if(stars != 0.f){
            filterOptions.add(6);
            filters.add(stars);
        }
        SearchThread thread = new SearchThread(handler,filterOptions,filters);
        thread.start();
    }

    private void clearFilters(){
        filterOptions.clear();
        filters.clear();
        filterOptions.add(1);
        filters.add("");
        ((Spinner)findViewById(R.id.area_spinner)).setSelection(0);
        ((TextView)findViewById(R.id.search_from_date_text)).setText("None");
        ((TextView)findViewById(R.id.search_to_date_text)).setText("None");
        ((TextView)findViewById(R.id.number_of_people)).setText("None");
        ((RangeSlider)findViewById(R.id.price_slider)).setValues(0f,800f);
        ((RangeSlider)findViewById(R.id.rating_slider)).setValues(0f);
    }

    private void changeNoOfPeopleValue(boolean add){
        TextView noOfPeopleText = findViewById(R.id.number_of_people);
        String noOfPeople =noOfPeopleText.getText().toString().trim();
        String outText;
        if(noOfPeople.equals("None")){
            if(add){
                outText = "1";
            }
            else{
                outText = "0";
            }
        }
        else{
            int number = Integer.parseInt(noOfPeople);
            if(add){
                number++;
            }
            else{
                if (number > 0){
                    number--;
                }
            }
            outText = Integer.toString(number);
        }
        noOfPeopleText.setText(outText);
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

}