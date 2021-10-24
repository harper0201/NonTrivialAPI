package com.example.nontrivialapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    boolean isPermissionGranter;
    GoogleMap googleMap;
    ImageView imageViewSearch;
    EditText inputlocation;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        imageViewSearch = findViewById(R.id.imageViewSearch);
        inputlocation = findViewById(R.id.inputLocation);

        checkPermission();
        if (isPermissionGranter) {
            if (checkGooglePlayService()) {
                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.map, supportMapFragment).commit();
                supportMapFragment.getMapAsync(this);
            } else {
                Toast.makeText(this, "Google Service available", Toast.LENGTH_SHORT).show();
            }
        }

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = inputlocation.getText().toString();
                if (location == null) {
                    Toast.makeText(MapActivity.this, "Type any location name.", Toast.LENGTH_LONG).show();
                } else {
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    try {
                        List<Address> listAddress = geocoder.getFromLocationName(location, 1);
                        if (listAddress.size() > 0) {
                            latitude = listAddress.get(0).getLatitude();
                            longitude = listAddress.get(0).getLongitude();
                            UpdatePosition(latitude, longitude);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private boolean checkGooglePlayService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(MapActivity.this, "User Canceled Dialog", Toast.LENGTH_SHORT).show();
                        }
                    });
            dialog.show();
        }
        return false;
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranter = true;
                Toast.makeText(MapActivity.this, "Permission Granter", Toast.LENGTH_SHORT).show();
                ;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent map = getIntent();
        latitude = map.getDoubleExtra("Latitude", 0);
        longitude =  map.getDoubleExtra("Longtitude", 0);
        LatLng latLng = new LatLng(map.getDoubleExtra("Latitude", 0), map.getDoubleExtra("Longtitude",
                0));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Position");
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NoneMap:
                UpdateMap(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.NormalMap:
                UpdateMap(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.SatelliteMap:
                UpdateMap(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.HybridMap:
                UpdateMap(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.TerrianMap:
                UpdateMap(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdateMap(int MAP_TYPE) {
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(MAP_TYPE);
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Position");
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                googleMap.animateCamera(cameraUpdate);
            }
        });
    }

    public boolean UpdatePosition(double latitude, double longitude){
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, supportMapFragment).commit();
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Position");
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                googleMap.animateCamera(cameraUpdate);

            }
        });
        return true;
    }

}