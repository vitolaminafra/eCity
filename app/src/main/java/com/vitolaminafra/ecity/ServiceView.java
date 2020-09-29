package com.vitolaminafra.ecity;

import android.content.Context;
import android.view.View;

public class ServiceView extends View {
    private Context context;

    public ServiceView(Context context) {
        super(context);
        this.context = context;
    }

    public ServiceView(Context context, String add, String sub, Boolean bike, String lid, String lat, String lng) {
        super(context);
        this.add = add;
        this.sub = sub;
        this.bike = bike;

        this.lat = lat;
        this.lng = lng;
        this.lid = lid;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Boolean getBike() {
        return bike;
    }

    public void setBike(Boolean bike) {
        this.bike = bike;
    }

    private String add, sub;
    private Boolean bike;

    private String lid, lat, lng;

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

}
