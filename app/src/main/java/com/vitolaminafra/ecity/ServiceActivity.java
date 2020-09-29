package com.vitolaminafra.ecity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServiceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Locations currentPosition;
    private MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "";

    private ImageView serviceIcon;
    private TextView addressText;
    private Button favBtn, dirBtn, resBtn;
    private String lid, lat, lng, address;

    private Geocoder geocoder;
    private List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapServiceView);
        mapView.getMapAsync(this);
        mapView.onCreate(mapViewBundle);

        geocoder = new Geocoder(this, Locale.getDefault());

        serviceIcon = findViewById(R.id.serviceIcon);
        addressText = findViewById(R.id.addressText);
        favBtn = findViewById(R.id.favoriteBtn);
        favBtn.setTag("set");
        dirBtn = findViewById(R.id.directionsBtn);
        resBtn = findViewById(R.id.reservationBtn);
        resBtn.setTag("set");

        lid = getIntent().getStringExtra("lid");
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");

        for(int i = 0; i < MainActivity.getFavorites().size(); i++) {
            if(MainActivity.getFavorites().get(i).equals(lid)) {
                favBtn.setText("Rimuovi preferito");
                favBtn.setBackgroundResource(R.drawable.remfav);
                favBtn.setTag("rem");
            }
        }

        for(int i = 0; i < MainActivity.getBooked().size(); i++) {
            String[] info = MainActivity.getBooked().get(i).split("/");
            if(info[0].equals(lid)) {
                resBtn.setText("Annulla prenotazione");
                resBtn.setBackgroundResource(R.drawable.remres);
                resBtn.setTag("rem");
            }
        }

        try {
            addresses = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lng), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = addresses.get(0).getAddressLine(0);
        String[] onlyAddress = address.split(",");
        if(onlyAddress[0].startsWith("Unnamed")) {
            address = "Campus";
        } else {
            address = onlyAddress[0] + onlyAddress[1];
        }


        if(getIntent().hasCategory("bike")) {
            serviceIcon.setImageResource(R.drawable.bikeicon);
            addressText.setText(address);

        } else {
            serviceIcon.setImageResource(R.drawable.coffee);
            addressText.setText(address);
            resBtn.setVisibility(View.INVISIBLE);

        }

        currentPosition = MainActivity.getCurrentLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(getIntent().hasCategory("bike")) {
            map.addMarker(new MarkerOptions().position(
                    new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                    .icon(BitmapDescriptorFactory.defaultMarker(202))
            );

            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(lat), Double.valueOf(lng))));
            map.setMinZoomPreference(15.0f);
            map.getUiSettings().setAllGesturesEnabled(false);
        } else {
            map.addMarker(new MarkerOptions().position(
                    new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                    .icon(BitmapDescriptorFactory.defaultMarker(154))
            );

            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(lat), Double.valueOf(lng))));
            map.setMinZoomPreference(15.0f);
            map.getUiSettings().setAllGesturesEnabled(false);
        }

        map.addMarker(new MarkerOptions().position(
                new LatLng(Double.valueOf(currentPosition.getLat()), Double.valueOf(currentPosition.getLng())))
        );

    }

    public void getDirections(View view) {
        Uri gmmIntentUri = Uri.parse("geo:" + 0 + "," + 0 + "?q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void favorite(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(view.getTag().equals("set")){
            Map<String, Object> fav = new HashMap<>();
            fav.put("uid", MainActivity.getLoggedUser().getUid());
            fav.put("lid", lid);
            db.collection("favs").document()
                    .set(fav)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ECITY", "DocumentSnapshot successfully written!");
                            favBtn.setText("Rimuovi preferito");
                            favBtn.setTag("rem");
                            favBtn.setBackgroundResource(R.drawable.remfav);
                            MainActivity.updateFavorites();

                            Toast.makeText(ServiceActivity.this, "Preferito aggiunto", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ECITY", "Error writing document", e);
                        }
                    });

        } else {

            db.collection("favs")
                    .whereEqualTo("uid", MainActivity.getLoggedUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("ECITYFAVS", document.getId() + " => " + document.getData());

                                    if(document.getData().get("lid").toString().equals(lid)) {
                                        deleteFav(document.getId());
                                    }

                                }
                            } else {
                                Log.d("CITY", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }

    }


    private void deleteFav(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favs").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        favBtn.setTag("set");
                        favBtn.setText("Preferito");
                        favBtn.setBackgroundResource(R.drawable.fav);

                        MainActivity.updateFavorites();

                        Toast.makeText(ServiceActivity.this, "Preferito rimosso", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteBook(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        resBtn.setTag("set");
                        resBtn.setText("Prenota");
                        resBtn.setBackgroundResource(R.drawable.res);

                        MainActivity.updateBooked();

                        Toast.makeText(ServiceActivity.this, "Prenotazione annullata", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void booking(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(view.getTag().equals("set")){
            Date date = new Date();
            Map<String, Object> fav = new HashMap<>();
            fav.put("uid", MainActivity.getLoggedUser().getUid());
            fav.put("lid", lid);
            fav.put("ts", date.getTime());
            fav.put("active", true);
            db.collection("books").document()
                    .set(fav)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ECITY", "DocumentSnapshot successfully written!");
                            resBtn.setText("Annulla prenotazione");
                            resBtn.setTag("rem");
                            resBtn.setBackgroundResource(R.drawable.remres);
                            MainActivity.updateBooked();


                            Toast.makeText(ServiceActivity.this, "Prenotazione effettuata", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ECITY", "Error writing document", e);
                        }
                    });

        } else {

            db.collection("books")
                    .whereEqualTo("uid", MainActivity.getLoggedUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("ECITYBOOKS", document.getId() + " => " + document.getData());

                                    if(document.getData().get("lid").toString().equals(lid)) {
                                        deleteBook(document.getId());
                                    }

                                }
                            } else {
                                Log.d("CITY", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }

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
        mapView.onStart();
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

}
