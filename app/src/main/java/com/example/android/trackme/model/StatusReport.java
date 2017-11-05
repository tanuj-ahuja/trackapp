package com.example.android.trackme.model;

import java.util.Date;

/**
 * Created by tanujanuj on 30/09/17.
 */

public class StatusReport {
    private String date;
    private String clientName;
    private String clientLocation;
    private String status;

    public StatusReport(){

    }

    public StatusReport(String clientName, String date, String clientLocation, String status) {
        this.clientName = clientName;
        this.date = date;
        this.clientLocation = clientLocation;
        this.status = status;
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

    public String getDate() {
        return date;
    }

    public void setDate( String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
