package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private MyAdapter adapter;


    private LocationListener locationListener;
    private final long MIN_TIME = 1000;
    private final long MIN_DIST = 5;
    private LocationManager locationManager;
    private DatabaseReference databaseReference;
    private DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.SEND_SMS},PackageManager.PERMISSION_GRANTED);

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {

                    DatabaseReference childRefLat=FirebaseDatabase.getInstance().getReference("Location").child("Latitude");
                    DatabaseReference childRefLon=FirebaseDatabase.getInstance().getReference("Location").child("Longitude");
                    childRefLat.removeValue();
                    childRefLon.removeValue();

                    databaseReference.child("Latitude").push().setValue(Double.toString(location.getLatitude()));
                    databaseReference.child("Longitude").push().setValue(Double.toString(location.getLongitude()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }



        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIST,locationListener);


        } catch (Exception e) {
            e.printStackTrace();
        }


        recyclerView=(RecyclerView)findViewById(R.id.recview4);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<RequestModel> options =new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Couriers"),RequestModel.class).build();
        adapter=new MyAdapter(options,this);
        recyclerView.setAdapter(adapter);

    }
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }




}