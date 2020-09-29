package com.vitolaminafra.ecity;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookedFragment extends Fragment {

    private ArrayList<String> books;
    private List<ServiceView> booksViews = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;


    public BookedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_booked, container, false);

        recyclerView = v.findViewById(R.id.recViewBooked);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        updateList();

        return v;
    }

    public void updateList() {
        books = new ArrayList<>();
        booksViews = new ArrayList<>();

        books = MainActivity.getBooked();

        for(int i = 0; i < books.size(); i++) {
            for(int j = 0; j < NearFragment.getAllLocations().size(); j++){
                String[] info = MainActivity.getBooked().get(i).split("/");
                if(NearFragment.getAllLocations().get(j).getLid().equals(info[0])) {
                    ArrayList<Locations> locs = NearFragment.getAllLocations();
                    String address, distance = String.valueOf(Math.round(locs.get(j).getDistance()));
                    Geocoder geocoder;
                    List<Address> addresses = new ArrayList<>();
                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(Double.valueOf(locs.get(j).getLat()), Double.valueOf(locs.get(j).getLng()), 1);
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


                    final String lid = locs.get(j).getLid(),
                            lat = locs.get(j).getLat(),
                            lng = locs.get(j).getLng();

                    Long etaInMillis = Long.valueOf(info[1]) + 1800000;

                    DateFormat df = new SimpleDateFormat("HH:mm");
                    String sub = distance + " m - Scade alle " + df.format(etaInMillis);

                    if(etaInMillis < new Date().getTime()) {
                        sub = distance + " m - Scaduta";
                    }

                    ServiceView sv = new ServiceView(getContext(), address, sub, locs.get(j).getBike(), lid, lat, lng);
                    booksViews.add(sv);
                    break;
                }
            }
        }

        adapter = new ServiceFavAdapter(booksViews, this.getContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getNavBar().setTabIndex(2, false);

        updateList();
    }
}
