package com.example.floodmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Value;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class selectbrgy extends AppCompatActivity {
    DatabaseReference reference = FirebaseDatabase.getInstance("https://floodmonitoring-6c3ec-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("area");
    Spinner spinner;
    TextView submit;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectbrgy);
        spinner=(Spinner)findViewById(R.id.spinnerdata);
        submit = findViewById(R.id.btnSubmit);
        list = new ArrayList<String>();
        adapter= new ArrayAdapter<>(selectbrgy.this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setPrompt("Tap here");
        spinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));

        fetchData();
//        FirebaseMessaging.getInstance().subscribeToTopic("Bambang")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "Subscribed for updates on Pinagbuhatan";
//                        Toast.makeText(selectbrgy.this, msg, Toast.LENGTH_SHORT).show();
//
//                        if (!task.isSuccessful()) {
//                            msg = "Subscribe failed";
//                        }
//
//
//                    }
//                });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast

                        System.out.println(token);
                        //Toast.makeText(Dashboard.this, "Your device registration token is " + token, Toast.LENGTH_SHORT).show();

                    }
                });


        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String selectedValue = spinner.getSelectedItem().toString();
                Intent intent = new Intent(selectbrgy.this, historicaldata.class);
                intent.putExtra("SELECTED_VALUE", selectedValue);
                startActivity(intent);            }
        });
    }
    public void fetchData(){
    listener=reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot mydata : snapshot.getChildren())
                list.add(mydata.child("area_name").getValue().toString());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }
    private AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            ((TextView) parent.getChildAt(0)).setTextSize(9);

        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}