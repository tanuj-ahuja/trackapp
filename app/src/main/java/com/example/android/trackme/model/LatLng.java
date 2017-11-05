package com.example.android.trackme.model;

/**
 * Created by tanujanuj on 09/09/17.
 */

public class LatLng {
    private Double latitude;
    private Double longitude;

    public LatLng() {}

    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public LatLng(Double latitude,Double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }
}
