package com.example.android.trackme.model;



/**
 * Created by tanujanuj on 06/10/17.
 */

public class MeetingDetails {
    private String activity;

    public String getClientn() {
        return clientn;
    }

    private String clientn;
    private String date;
    private String start;
    private String end;
    private String flag;
    private String approve;
    private String time;
    private String location;

    public MeetingDetails() {
    }

    public MeetingDetails(String date, String activity,String clientn, String time,String start,String end,String flag,String location,String  approve) {
        this.date = date;
        this.activity = activity;
        this.clientn=clientn;
        this.time = time;
        this.start=start;
        this.end=end;
        this.flag=flag;
        this.location=location;
        this.approve=approve;
    }


    public String getActivity() {
        return activity;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getEnd() {
        return end;
    }

    public String getFlag() {
        return flag;
    }

    public String getStart() {
        return start;
    }

    public String getLocation() {
        return location;
    }

    public String getApprove(){
        return approve;
    }



}
