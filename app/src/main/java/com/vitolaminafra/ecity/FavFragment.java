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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment {

    private ArrayList<String> favs;
    private List<ServiceView> favsViews = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_fav, container, false);

        recyclerView = v.findViewById(R.id.favRecView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        updateList();

        return v;
    }

    public void updateList() {
        favs = new ArrayList<>();
        favsViews = new ArrayList<>();

        favs = MainActivity.getFavorites();

        for(int i = 0; i < favs.size(); i++) {
            for(int j = 0; j < NearFragment.getAllLocations().size(); j++){
                if(NearFragment.getAllLocations().get(j).getLid().equals(favs.get(i))) {
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


                    ServiceView sv = new ServiceView(getContext(), address, distance + " m", locs.get(j).getBike(), lid, lat, lng);
                    favsViews.add(sv);
                    break;
                }
            }
        }

        adapter = new ServiceFavAdapter(favsViews, this.getContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getNavBar().setTabIndex(1, false);

        updateList();
    }


}
