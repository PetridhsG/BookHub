package gr.aueb.BookingApp.Frontend.Customer.ResultsActivity;

import java.io.File;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

import gr.aueb.BookingApp.Frontend.Customer.ViewRoomActivity.ViewRoomActivity;
import gr.aueb.BookingApp.R;
import gr.aueb.BookingApp.domain.Room;

public class ListAdapter extends ArrayAdapter<Room> {

    private Context mContext;
    private ArrayList<Room> mRooms;

    public ListAdapter(Context context, ArrayList<Room> rooms) {
        super(context, 0, rooms);
        mContext = context;
        mRooms = rooms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        ImageView roomImage = convertView.findViewById(R.id.results_room_image);
        TextView roomNameText = convertView.findViewById(R.id.results_room_name_text);
        TextView roomAreaText = convertView.findViewById(R.id.results_room_area_text);
        TextView roomStarsText = convertView.findViewById(R.id.results_room_stars_text);
        TextView roomRatingsText = convertView.findViewById(R.id.results_room_ratings_text);
        TextView roomPriceText = convertView.findViewById(R.id.results_room_price_text);
        TextView roomNoOfPeopleText = convertView.findViewById(R.id.results_room_noOfPeople_text);

        String imageName = room.getRoomImage().trim();
        int resourceId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        roomImage.setImageResource(resourceId);
        roomNameText.setText(room.getRoomName());
        roomAreaText.setText(room.getArea());
        roomStarsText.setText(String.valueOf(room.getStars()));
        roomRatingsText.setText(String.valueOf(room.getNumberOfReviews()));
        roomPriceText.setText(String.valueOf(room.getPrice()));
        roomNoOfPeopleText.setText(String.valueOf(room.getNumberOfPeople()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewRoomActivity.class);
                intent.putExtra("room",room);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

}
