package com.vitolaminafra.ecity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends FragmentActivity{

    private FirebaseAuth mAuth;
    private static FirebaseUser currentUser;

    private static User loggedUser;

    private static NavigationTabStrip navigationTabStrip;

    private static final int NUM_PAGES = 4;
    private static ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private static Context mContext;

    public static NavigationTabStrip getNavBar() {
        return navigationTabStrip;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    private static Locations currentLocation;

    private static ArrayList<String> booked = new ArrayList<>();

    public static ArrayList<String> getBooked() {
        return booked;
    }

    private static ArrayList<String> favorites = new ArrayList<>();

    public static ArrayList<String> getFavorites() {
        return favorites;
    }

    public static void updateFavorites() {
        favorites = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("favs")
                .whereEqualTo("uid", loggedUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ECITYFAVS", document.getId() + " => " + document.getData());

                                favorites.add(String.valueOf(document.getData().get("lid")));
                            }
                        } else {
                            Log.d("CITY", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void updateBooked() {
        booked = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("books")
                .whereEqualTo("uid", loggedUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                booked.add(document.getData().get("lid") + "/" + document.getData().get("ts"));
                            }
                        }
                    }
                });
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(mContext, "Logout effettuato", Toast.LENGTH_SHORT).show();
        Intent splashIntent = new Intent(mContext, SplashScreenActivity.class);
        splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(splashIntent);
    }

    public static void setCurrentLocation(String lat, String lng) {
        currentLocation = new Locations("", lat, lng, false, false);
    }

    public static Locations getCurrentLocation() {
        return currentLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        navigationTabStrip = findViewById(R.id.navbar);
        navigationTabStrip.setTitles("VICINO", "PREFERITI️", "PRENOTATI", "PROFILO");
        navigationTabStrip.setTabIndex(0, true);


        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                viewPager.setCurrentItem(index);
            }

            @Override
            public void onEndTabSelected(String title, int index) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        Log.d("ECITY", currentUser.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ECITY", "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> userData = document.getData();

                        loggedUser = new User(currentUser.getUid(), userData.get("first").toString(), userData.get("last").toString(), currentUser.getEmail());

                    } else {
                        Log.d("ECITY", "No such document");
                    }
                    updateFavorites();
                    updateBooked();
                } else {
                    Log.d("ECITY", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);

        }


        @Override
        public Fragment createFragment(int position) {
            switch(position) {
                case 0:
                    return new NearFragment();
                case 1:
                    return new FavFragment();
                case 2:
                    return new BookedFragment();
                case 3:
                    return new ProfileFragment();
            }

            return null;

        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
