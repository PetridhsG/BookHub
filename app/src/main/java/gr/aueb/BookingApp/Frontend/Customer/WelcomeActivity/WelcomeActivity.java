package gr.aueb.BookingApp.Frontend.Customer.WelcomeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import gr.aueb.BookingApp.Frontend.Customer.SearchActivity.SearchActivity;
import gr.aueb.BookingApp.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.enter_button).setOnClickListener( v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }
}