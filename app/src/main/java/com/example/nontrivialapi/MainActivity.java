package com.example.nontrivialapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView latitude,longtitude,countryname,locality,address;
    private ProgressBar progressBar;
    private Button buttongetcurrentposition;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressbar);
        latitude = findViewById(R.id.Latitude);
        longtitude = findViewById(R.id.Longtitude);
        countryname = findViewById(R.id.CountryName);
        locality = findViewById(R.id.Locability);
        address = findViewById(R.id.Address);
        buttongetcurrentposition = findViewById(R.id.buttonGetCurrentPosition);
        //initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        buttongetcurrentposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
    }

    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    44);
        }
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback(){
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
//                if(locationResult != null && locationResult.getLocations().size() > 0){
//                    int lastestLocationIndex = locationResult.getLocations().size() -1;
//                    //set latitude
//                    latitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Latitude:</b" +
//                            "></font>" + locationResult.getLocations().get(lastestLocationIndex).getLatitude()));
//                    //set longtitude
//                    longtitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Longtitude:</b" +
//                            "></font>" + locationResult.getLocations().get(lastestLocationIndex).getLongitude()));
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//        },Looper.getMainLooper());
       fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if(location != null){
                    try{
                        //initialize geocoder
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        //initialize address list
                        List<Address> addressList =
                                geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(),1);
                        //set latitude
                        latitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Latitude:</b></font>" + addressList.get(0).getLatitude()));
                        //set longtitude
                        longtitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Longtitude:</b></font>" + addressList.get(0).getLongitude()));
                        //set countryname
                        countryname.setText(Html.fromHtml("<font color = '#6200EE'><b>Country " +
                                "Name:</b></font>" + addressList.get(0).getCountryName()));
                        //set locality
                        locality.setText(Html.fromHtml("<font color = '#6200EE'><b>Locality:</b></font>" + addressList.get(0).getLocality()));
                        //set address
                        address.setText(Html.fromHtml("<font color ='#6200EE'><b>Address:</b></font>" + addressList.get(0).getAddressLine(0)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}