package com.example.nontrivialapi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
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
    private double Position[];
    private ImageView icSearch;
    private ImageView icEmail;
    private String addressLine;
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
        icSearch = findViewById(R.id.ic_search);
        icEmail = findViewById(R.id.ic_email);
        //initialize position
        Position = new double[2];
        //initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        buttongetcurrentposition.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    if(isLocationEnabled()){
                        //execute when Permission and Location is avaliable
                        getCurrentLocation();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Please turn on your location",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
                else{
                    requestPermissions();
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

        icSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchAPlace.class);
                startActivity(intent);
            }
        });
        //send email of address and google map url
        icEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                String[] To = {""};
                send.putExtra(Intent.EXTRA_EMAIL,To);
                String subject = "My Position";
                send.putExtra(Intent.EXTRA_SUBJECT,subject);
                send.putExtra(Intent.EXTRA_TEXT, addressLine + "\n" + "https://www.google.com/maps/search/?api=1&query" +
                        "=" + Position[0] + "," + Position[1]);
                try{
                    startActivity(Intent.createChooser(send,"Choose an Email client"));
                }catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(MainActivity.this,"There is no email client installed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
            else{
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
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
                        Position[0] = addressList.get(0).getLatitude();
                        //set longtitude
                        longtitude.setText(Html.fromHtml("<font color = '#6200EE'><b>Longtitude:</b></font>" + addressList.get(0).getLongitude()));
                        Position[1] = addressList.get(0).getLongitude();
                        //set countryname
                        country.setText(Html.fromHtml("<font color = '#6200EE'><b>Country " +
                                ":</b></font>" + addressList.get(0).getCountryName()));
                        //set locality
                        locality.setText(Html.fromHtml("<font color = '#6200EE'><b>Locality:</b></font>" + addressList.get(0).getLocality()));
                        //set address
                        address.setText(Html.fromHtml("<font color ='#6200EE'><b>Address:</b></font>" + addressList.get(0).getAddressLine(0)));
                        addressLine = addressList.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
                buttonnavigatetoMap.setVisibility(View.VISIBLE);
                icSearch.setVisibility(View.VISIBLE);
                icEmail.setVisibility(View.VISIBLE);
            }
        });
    }
}