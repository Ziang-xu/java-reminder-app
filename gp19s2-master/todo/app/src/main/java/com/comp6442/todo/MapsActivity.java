package com.comp6442.todo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {


    private TextView mTapTextView;
    private Button saveButton;
    private GoogleMap mMap;
    double latitudeMap;
    double longitudeMap;
    private String itemLocation;
    LocationManager locationManager;
    public static final int MY_LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //initialize the variables, button and other views.
        locationManager = (LocationManager) getSystemService(Context.
                LOCATION_SERVICE);
        mTapTextView = findViewById(R.id.tap_text);
        saveButton = findViewById(R.id.save_bt_map);
        itemLocation = "";

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET,
                    Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,}, MY_LOCATION_REQUEST_CODE);
            Toast.makeText(this, "Please let this application to enable the GPS and WI-FI!", Toast.LENGTH_SHORT).show();
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudeMap = locationGPS.getLatitude();
        longitudeMap = locationGPS.getLongitude();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**
         * send the latitude and longitude and the address back to the added or edit activity.
         */
        saveButton.setOnClickListener((e) -> {
            getAddress();
            Intent intent = new Intent();
            intent.putExtra("latitude", latitudeMap);
            intent.putExtra("longitude", longitudeMap);
            intent.putExtra("address", itemLocation);
            setResult(RESULT_OK, intent);
            finish();
        });

    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

    }

    @Override
    public void onMapClick(LatLng point) {
        mTapTextView.setText("tapped, point=" + point);
        latitudeMap = point.latitude;
        longitudeMap = point.longitude;
    }

    @Override
    public void onMapLongClick(LatLng point) {
//        mTapTextView.setText("long pressed, point=" + point);
        LatLng sydney = new LatLng(latitudeMap, longitudeMap);
        mMap.addMarker(new MarkerOptions().position(sydney).title("User's position"));
    }

    /**
     * check if all the permissions have got. if not, there will be a toast to notify the users give the permission.
     *
     * @param requestCode the code to apply for the permission.
     * @param permissions the permissions that this application should get.
     * @param grantResults the permissions that this application have get.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 8) {
                if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        permissions[1].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        permissions[2].equals(Manifest.permission.ACCESS_WIFI_STATE) &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        permissions[3].equals(Manifest.permission.ACCESS_NETWORK_STATE) &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        permissions[4].equals(Manifest.permission.CHANGE_WIFI_STATE) &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        permissions[5].equals(Manifest.permission.INTERNET) &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                        permissions[6].equals(Manifest.permission.READ_CALENDAR) &&
                        grantResults[6] == PackageManager.PERMISSION_GRANTED &&
                        permissions[7].equals(Manifest.permission.WRITE_CALENDAR) &&
                        grantResults[7] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "The location can work!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    /**
     * transfer the latitude and longitude to the address.
     */
    private void getAddress() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitudeMap, longitudeMap, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("location", latitudeMap + ", " + longitudeMap);
        Log.i("locationadd", locationList.toString());
        Address address = locationList.get(0);
        String TAG = "namelocation";
        String countryName = address.getCountryName();//get the conutry name
        Log.i(TAG, "countryName = " + countryName);
        String locality = address.getLocality();//city name
        Log.i(TAG, "locality = " + locality);
        for (int i = 0; address.getAddressLine(i) != null; i++) {
            String addressLine = address.getAddressLine(i);//ge the adress
            itemLocation += addressLine;
            Log.i(TAG, "addressLine = " + addressLine);
        }
    }

}


