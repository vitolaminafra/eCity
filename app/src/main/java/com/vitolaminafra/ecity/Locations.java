package com.vitolaminafra.ecity;

public class Locations {
    private String lid, lat, lng;
    private Boolean bike, booked;
    private Float distance;

    private String ts;

    public Boolean getBike() {
        return bike;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Locations(String lid, String lat, String lng, Boolean bike, Boolean booked) {
        this.lid = lid;
        this.lat = lat;
        this.lng = lng;
        this.bike = bike;
        this.booked = booked;
    }

    public Locations(String lid, String lat, String lng, Boolean bike, Boolean booked, String ts) {
        this.lid = lid;
        this.lat = lat;
        this.lng = lng;
        this.bike = bike;
        this.booked = booked;
        this.ts = ts;
    }

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

    public Boolean isBike() {
        return bike;
    }

    public void setBike(Boolean bike) {
        this.bike = bike;
    }

    public Boolean getBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }
}
