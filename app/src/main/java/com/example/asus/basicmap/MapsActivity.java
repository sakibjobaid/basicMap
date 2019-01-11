package com.example.asus.basicmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //sakib
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location lastlocation;
    private boolean permit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
        setUpMap();
//        LatLng myPlace =new  LatLng(40.73, -73.99);  // this is New York
//        map.addMarker(new MarkerOptions().position(myPlace).title("My Favorite City"));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace,15f));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else permit = true;
        if (permit) {
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //map.setTrafficEnabled(true);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {


                                lastlocation = location;
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions mk= new MarkerOptions();
                                mk.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                //mk.title();
                                map.addMarker(mk.position(currentLatLng).title(getAddress(currentLatLng)));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                            }
                        }
                    });
        }
    }

    public String getAddress(LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses= new ArrayList<>();
        //val address: Address?
        Address address;
        String addressText = "";

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if ( addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0);
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                     if (i == 0) addressText +=address.getAddressLine(i) ;
                     else addressText += "\n" + address.getAddressLine(i);
                    //addressFragments.add(address.getAddressLine(i));
                }
//                for (i in 0 until address.maxAddressLineIndex) {
//                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
//                }
            }
        } catch (IOException e) {
        //Log.e("MapsActivity", e.localizedMessage);
    }

        return addressText;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permit = true;
            } else return;
        } else
            return;
    }
}
