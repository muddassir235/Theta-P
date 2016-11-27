package com.thetacab.hp.cargar;

/**
 * Created by hp on 6/28/2016.
 */
public class DriverLocation {
    public String Lat;
    public String Long;
    public long time;
    public DriverLocation(){}
    public DriverLocation(String Lat, String Long){
        this.Lat=Lat;
        this.Long=Long;
        this.time = System.currentTimeMillis();
    }

    public DriverLocation(String Lat, String Long, long time){
        this.Lat=Lat;
        this.Long=Long;
        this.time = time;
    }
}
