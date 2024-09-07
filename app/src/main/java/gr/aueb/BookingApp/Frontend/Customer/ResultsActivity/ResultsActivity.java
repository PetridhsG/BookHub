package gr.aueb.BookingApp.Frontend.Customer.ResultsActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import gr.aueb.BookingApp.R;
import gr.aueb.BookingApp.domain.Room;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ArrayList<Room> rooms = new ArrayList<>();
        Intent intent = getIntent();
        int size = 0;
        if (intent != null) {
            byte[] results = intent.getByteArrayExtra("rooms");
            if (results != null) {
                try {
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(results);
                    ObjectInputStream objectStream = new ObjectInputStream(byteStream);
                    rooms = (ArrayList<Room>) objectStream.readObject();
                    objectStream.close();
                    byteStream.close();
                    if(rooms != null){
                        size = rooms.size();
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("this",String.valueOf(size));
        if (rooms != null){
            ListAdapter listAdapter = new ListAdapter(this, rooms);
            ListView listView = findViewById(R.id.rooms_list);
            listView.setAdapter(listAdapter);
        }
    }
}