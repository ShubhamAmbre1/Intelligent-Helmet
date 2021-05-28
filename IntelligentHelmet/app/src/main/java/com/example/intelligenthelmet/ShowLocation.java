package com.example.intelligenthelmet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowLocation extends AppCompatActivity {

    private static final String TAG = "Location";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseAuth fAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        getLocationPermission();
    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current Location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: found loaction");

//                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                DEFAULT_ZOOM);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Long.parseLong(mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("gps").child("latitude").getKey()),
                               Long.parseLong(mDatabase.child(String.valueOf(fAuth.getCurrentUser())).child("gps").child("longitude").getKey())), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "onComplete: current Loction is null");
                        Toast.makeText(getApplicationContext(), "unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(getApplicationContext(), "Map working", Toast.LENGTH_SHORT).show();
                mMap = googleMap;

                if (mLocationPermissionGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for(int i=0;i<grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                //initialize our map
                Toast.makeText(this, "Creating Map thorugh OnRequestMethod", Toast.LENGTH_SHORT).show();
                initMap();
            }
        }
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            return true;

        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOk: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}