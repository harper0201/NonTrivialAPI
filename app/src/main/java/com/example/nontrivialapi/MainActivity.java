package com.example.nontrivialapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView latitude,longtitude,country,locality,address;
    private ProgressBar progressBar;
    private Button buttongetcurrentposition;
    private Button buttonnavigatetoMap;
    private int Position[];
    Intent intent;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressbar);
        latitude = findViewById(R.id.Latitude);
        longtitude = findViewById(R.id.Longtitude);
        country = findViewById(R.id.CountryName);
        locality = findViewById(R.id.Locability);
        address = findViewById(R.id.Address);
        buttongetcurrentposition = findViewById(R.id.buttonGetCurrentPosition);
        buttonnavigatetoMap = findViewById(R.id.buttonNavigatetoGoogleMap);
        //initialize position
        Position = new int[2];
        //initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        buttongetcurrentposition.setOnClickListener(new View.OnClickListener(){
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
        buttonnavigatetoMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("Latitude",Position[0]);
                intent.putExtra("Longtitude",Position[1]);
                startActivity(intent);
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
                        Position[0] = (int) addressList.get(0).getLatitude();
                        //set longtitude
                        longtitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Longtitude:</b></font>" + addressList.get(0).getLongitude()));
                        Position[1] = (int) addressList.get(0).getLongitude();
                        //set countryname
                        country.setText(Html.fromHtml("<font color = '#6200EE'><b>Country " +
                                ":</b></font>" + addressList.get(0).getCountryName()));
                        //set locality
                        locality.setText(Html.fromHtml("<font color = '#6200EE'><b>Locality:</b></font>" + addressList.get(0).getLocality()));
                        //set address
                        address.setText(Html.fromHtml("<font color ='#6200EE'><b>Address:</b></font>" + addressList.get(0).getAddressLine(0)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                buttonnavigatetoMap.setVisibility(View.VISIBLE);
            }
        });
    }
}