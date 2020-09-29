package com.vitolaminafra.ecity;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NearFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Location position;
    private MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Map<String, Object> locations;

    private Geocoder geocoder;
    private List<Address> addresses;

    private Button v1, v2, v3, b1, b2, b3;

    private Locations loc;
    private static ArrayList<Locations> serviceLocations = new ArrayList<>();

    public NearFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_near, container, false);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        mapView = v.findViewById(R.id.mapview);
        mapView.getMapAsync(this);
        mapView.onCreate(mapViewBundle);

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        mAuth = FirebaseAuth.getInstance();

        v1 = v.findViewById(R.id.v1);
        v2 = v.findViewById(R.id.v2);
        v3 = v.findViewById(R.id.v3);
        b1 = v.findViewById(R.id.b1);
        b2 = v.findViewById(R.id.b2);
        b3 = v.findViewById(R.id.b3);


        return v;
    }

    public static ArrayList<Locations> getAllLocations() {
        return serviceLocations;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getNavBar().setTabIndex(0, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        mapView.onStart();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            position = location;
                            Log.d("ECITYPOS", position.toString());

                            LatLng currentPosition = new LatLng(position.getLatitude(), position.getLongitude());
                            map.addMarker(new MarkerOptions().position(currentPosition));
                            map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                            map.setMinZoomPreference(15.0f);
                            map.getUiSettings().setAllGesturesEnabled(false);

                            MainActivity.setCurrentLocation(String.valueOf(location.getLatitude()), String.valueOf(position.getLongitude()));
                            getLocations();
                        }
                    }
                });
    }

    private void getLocations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        serviceLocations = new ArrayList<>();

        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ECITY", document.getId() + " => " + document.getData());

                                locations = document.getData();

                                loc = new Locations(document.getId(),
                                        locations.get("lat").toString(),
                                        locations.get("long").toString(),
                                        Boolean.valueOf(locations.get("bike").toString()),
                                        Boolean.valueOf(locations.get("booked").toString()));


                                try {
                                    addresses = geocoder.getFromLocation(Double.valueOf(loc.getLat()), Double.valueOf(loc.getLng()), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String address = addresses.get(0).getAddressLine(0);
                                String[] onlyAddress = address.split(",");
                                if(onlyAddress[0].startsWith("Unnamed")) {
                                    address = "Campus";
                                } else {
                                    address = onlyAddress[0] + onlyAddress[1];
                                }

                                Log.d("DIOCANE", "id, " + loc.getLat() + ", " +
                                        loc.getLng() + ", " + loc.getBike() + ", " + address);

                                if(loc.isBike()) {
                                     Marker bikeMarker = map.addMarker(new MarkerOptions().position(
                                            new LatLng(Double.valueOf(loc.getLat()), Double.valueOf(loc.getLng())))
                                            .icon(BitmapDescriptorFactory.defaultMarker(202))
                                            .title(address)
                                            .snippet("Bicicletta")
                                    );
                                    bikeMarker.setTag(loc.getLid());
                                    //bikeMarkers.put(loc, bikeMarker);
                                } else {
                                    Marker vendingMarker = map.addMarker(new MarkerOptions().position(
                                            new LatLng(Double.valueOf(loc.getLat()), Double.valueOf(loc.getLng())))
                                            .icon(BitmapDescriptorFactory.defaultMarker(154))
                                            .title(address)
                                            .snippet("Distributore")
                                    );
                                    vendingMarker.setTag(loc.getLid());
                                    //vendingMarkers.put(loc, vendingMarker);
                                }

                                serviceLocations.add(loc);

                                setDistances();

                            }

                            addButtons();
                        } else {
                            Log.d("ECITY", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setDistances() {

        Double lat1 = position.getLatitude();
        Double long1 = position.getLongitude();

        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(long1);

        Location loc2 = new Location("");

        loc2.setLatitude(Double.valueOf(loc.getLat()));
        loc2.setLongitude(Double.valueOf(loc.getLng()));

        Float dis = loc1.distanceTo(loc2);

        loc.setDistance(dis);

    }

    private void addButtons() {
        ArrayList<Locations> bikeDis = new ArrayList<>();
        ArrayList<Locations> vendDis = new ArrayList<>();

        for(int i = 0; i < serviceLocations.size(); i++) {
            if(serviceLocations.get(i).getBike()) {
                bikeDis.add(serviceLocations.get(i));
            } else {
                vendDis.add(serviceLocations.get(i));
            }
        }

        Locations[] bikeDisArray = bikeDis.toArray(new Locations[bikeDis.size()]);
        Locations[] vendDisArray = vendDis.toArray(new Locations[vendDis.size()]);

        bikeDisArray = sort(bikeDisArray);
        vendDisArray = sort(vendDisArray);


        v1.setText(Math.round(vendDisArray[0].getDistance()) + " m");
        v1.setOnClickListener(new BottomButtonsListener(vendDisArray[0]));

        v2.setText(Math.round(vendDisArray[1].getDistance()) + " m");
        v2.setOnClickListener(new BottomButtonsListener(vendDisArray[1]));

        v3.setText(Math.round(vendDisArray[2].getDistance()) + " m");
        v3.setOnClickListener(new BottomButtonsListener(vendDisArray[2]));

        b1.setText(Math.round(bikeDisArray[0].getDistance()) + " m");
        b1.setOnClickListener(new BottomButtonsListener(bikeDisArray[0]));

        b2.setText(Math.round(bikeDisArray[1].getDistance()) + " m");
        b2.setOnClickListener(new BottomButtonsListener(bikeDisArray[1]));

        b3.setText(Math.round(bikeDisArray[2].getDistance()) + " m");
        b3.setOnClickListener(new BottomButtonsListener(bikeDisArray[2]));

    }

    public static Locations[] sort(Locations[] a) {
        boolean sorted = false;
        Locations temp;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i].getDistance() > a[i+1].getDistance()) {
                    temp = a[i];
                    a[i] = a[i+1];
                    a[i+1] = temp;
                    sorted = false;
                }
            }
        }
        return a;
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String lid = marker.getTag().toString();
                String lat = Double.toString(marker.getPosition().latitude);
                String lng = Double.toString(marker.getPosition().longitude);

                if(marker.getSnippet().equals("Bicicletta")){
                    Intent serviceIntent = new Intent(getContext(), ServiceActivity.class);
                    serviceIntent.addCategory("bike");
                    serviceIntent.putExtra("lat", lat);
                    serviceIntent.putExtra("lng", lng);
                    serviceIntent.putExtra("lid", lid);
                    startActivity(serviceIntent);
                } else {
                    Intent serviceIntent = new Intent(getContext(), ServiceActivity.class);
                    serviceIntent.putExtra("lat", lat);
                    serviceIntent.putExtra("lng", lng);
                    serviceIntent.putExtra("lid", lid);
                    startActivity(serviceIntent);
                }

            }
        });

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            position = location;
            map.addMarker(new MarkerOptions().position(currentPosition)
                    .title(""));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        }
    };

    private class BottomButtonsListener implements View.OnClickListener {

        Locations loc;

        public BottomButtonsListener(Locations loc) {
            this.loc = loc;
        }

        @Override
        public void onClick(View view) {
            Log.d("BUTTON", loc.getLid());

            String lid = loc.getLid();
            String lat = loc.getLat();
            String lng = loc.getLng();

            if(loc.getBike()){
                Intent serviceIntent = new Intent(getContext(), ServiceActivity.class);
                serviceIntent.addCategory("bike");
                serviceIntent.putExtra("lat", lat);
                serviceIntent.putExtra("lng", lng);
                serviceIntent.putExtra("lid", lid);
                startActivity(serviceIntent);
            } else {
                Intent serviceIntent = new Intent(getContext(), ServiceActivity.class);
                serviceIntent.putExtra("lat", lat);
                serviceIntent.putExtra("lng", lng);
                serviceIntent.putExtra("lid", lid);
                startActivity(serviceIntent);
            }
        }


    }
}
