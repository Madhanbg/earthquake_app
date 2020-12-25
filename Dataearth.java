package com.example.android.quakereport;

public class Dataearth {
    private double mmagnitude;
    private String mplace;
    private long mdate;
    private String mUrl;



    public Dataearth(double magnitude, String place, long date,String url) {
        mmagnitude = magnitude;
        mplace = place;
        mdate = date;
        mUrl = url;
    }
    public double getMmagnitude() {
        return mmagnitude;
    }

    public String getMplace() {
        return mplace;
    }

    public long getMdate() {
        return mdate;
    }

    public String getUrl() {
        return mUrl;
    }

}


