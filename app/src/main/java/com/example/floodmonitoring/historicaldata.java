package com.example.floodmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class historicaldata extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private DatabaseReference areasReference;
    private DatabaseReference historicalReference;
    private Intent intent = getIntent();
    private MyAdapter myAdapter;
    private ArrayList<Model> dataList;
    private boolean isSubscribed = false;

    TextView back, brgy, getUpdatesButton;
    ImageView print;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicaldata);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://floodmonitoring-6c3ec-default-rtdb.asia-southeast1.firebasedatabase.app/");
        areasReference = database.getReference("area");
        historicalReference = database.getReference("historical");

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        Intent intent = getIntent();
        String selectedValue = intent.getStringExtra("SELECTED_VALUE");
        dataList = new ArrayList<>();
        myAdapter = new MyAdapter(this, dataList);
        recyclerView.setAdapter(myAdapter);
        back = findViewById(R.id.btnBack);
        brgy = findViewById(R.id.brgytxt);
        getUpdatesButton = findViewById(R.id.btnSubscribe);
        print = findViewById(R.id.print);
        brgy.setText(selectedValue);


        updateText();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), selectbrgy.class);

                startActivity(intent);


            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://floodmonitoring-6c3ec.web.app/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                startActivity(intent);
            }
        });

        getUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the subscription status
                isSubscribed = !isSubscribed;

                // Subscribe or unsubscribe from the dynamically determined topic
                if (isSubscribed) {
                    getUpdatesButton.setText("Subscribe");
                    String msg = "Subscribed to latest news of Brgy. " + selectedValue;
                    Toast.makeText(historicaldata.this, msg, Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic(selectedValue);
                } else {
                    getUpdatesButton.setText("Unsubscribe");
                    Toast.makeText(historicaldata.this, "Unsubscribed", Toast.LENGTH_SHORT).show();

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(selectedValue);
                }

                // Update the text on the Button
                updateText();
            }
        });

        areasReference.orderByChild("area_name").equalTo(selectedValue)

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String historicalDataBank = areaSnapshot.child("his_sub_data_bank").getValue(String.class);
                            queryHistoricalData(historicalDataBank);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Handle the query when the user submits it
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
// Handle the query as the user types

                    // Otherwise, perform the search
                    filterData(newText);

                return true;
            }
        });

    }

        private void queryHistoricalData(String historicalDataBank) {
            // Query the "historical" branch based on historical_data_bank
            historicalReference.child(historicalDataBank)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataList.clear(); // Clear existing data

                            for (DataSnapshot historicalSnapshot : dataSnapshot.getChildren()) {

                                String uniqueId = historicalSnapshot.getKey();
                                if ("placeholder".equals(uniqueId)) {
                                    continue;
                                }

                                // Get the values of Date, Time, level_1, level_2, level_3, water_level
                                String date = historicalSnapshot.child("Date").getValue(String.class);
                                String time = historicalSnapshot.child("Time").getValue(String.class);
                                String level1 = historicalSnapshot.child("level_1").getValue(String.class);
                                String level2 = historicalSnapshot.child("level_2").getValue(String.class);
                                String level3 = historicalSnapshot.child("level_3").getValue(String.class);
                                String waterLevel = historicalSnapshot.child("water_level").getValue(String.class);

                                // Create a Model object and add it to the dataList
                                Model model = new Model();
                                model.setDate(date);
                                model.setTime(time);
                                model.setLevel_1(level1);
                                model.setLevel_2(level2);
                                model.setLevel_3(level3);
                                model.setWater_level(waterLevel);

                                dataList.add(model);
                            }

                            Collections.reverse(dataList);

                            //Notify the adapter that the data has changed
                            myAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                        }
                    });

        }
    private void filterData(String query) {
        // Perform the filtering based on the search query
        ArrayList<Model> filteredList = new ArrayList<>();

        for (Model model : dataList) {
            // Add logic here to check if the model matches the search query
            if (modelContainsQuery(model, query)) {
                filteredList.add(model);
            }
        }
        if(filteredList.isEmpty()){
//          Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            myAdapter.setData(filteredList);
        }

        // Update the adapter with the filtered data

    }

    // Add a method to check if the model contains the search query
    private boolean modelContainsQuery(Model model, String query) {
        String formattedDate = formatDateForComparison(model.getDate());

        // Check if the formatted date contains the query (month name)
        return formattedDate.toLowerCase().contains(query.toLowerCase())
                || model.getTime().toLowerCase().contains(query.toLowerCase())
                || model.getLevel_1().toLowerCase().contains(query.toLowerCase())
                || model.getLevel_2().toLowerCase().contains(query.toLowerCase())
                || model.getLevel_3().toLowerCase().contains(query.toLowerCase())
                || model.getWater_level().toLowerCase().contains(query.toLowerCase());
    }

    private String formatDateForComparison(String originalDate) {
        // Customize this method based on your date format
        // Example: "December 1, 2023" -> "December"
        // You may need to parse the date or handle different formats here
        // For simplicity, this example assumes the month is at the beginning of the date string.
        return originalDate.split(" ", 2)[0];
    }





    private void updateText() {
        // Update the Button text based on the subscription status
        if (isSubscribed) {
            getUpdatesButton.setText("Unsubscribe");

        } else {
            getUpdatesButton.setText("Subscribe");



        }
    }


}





