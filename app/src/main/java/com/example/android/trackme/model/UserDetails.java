package com.example.android.trackme.model;

import android.content.Intent;

import com.example.android.trackme.utils.Constants;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by at199 on 9/5/2017.
 */

public class UserDetails {
    private String name;
    private String mobile;
    private String email;
    private String department;
    private String designation;
    private LatLng latLng;
    private Integer flagPic;
    private Integer countPic;


    private Integer meetingcount;
    private Integer countStatusReport;
    private Integer  userMC;
    private HashMap<String,Object> timestampCreated;

    public UserDetails() {
    }

    public Integer getMeetingcount() {
        return meetingcount;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Integer getCountPic(){ return countPic;}

    public Integer getCountStatusReport(){ return countStatusReport;}

    public Integer getUserMC(){
        return userMC;
    }


    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public UserDetails(String name, String mobile, String email, String department, String designation, LatLng latLng,Integer flagPic,Integer countPic,Integer meetingcount,Integer countStatusReport ,HashMap<String,Object> timeStampCreated,Integer userMC){
        this.name = name;
        this.mobile = mobile;
        this.email= email;
        this.department=department;
        this.designation=designation;
        this.latLng=latLng;
        this.flagPic=flagPic;
        this.countPic=countPic;
        this.meetingcount=meetingcount;
        this.userMC=userMC;
        this.countStatusReport=countStatusReport;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampCreated=timestampNowObject;

    }

    public Integer getFlagPic() {
        return flagPic;
    }
}
