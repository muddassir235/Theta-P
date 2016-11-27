package com.thetacab.hp.cargar;

import java.sql.Time;

/**
 * Created by gul on 7/25/16.
 */
public class TimeObject {
    public long mins;
    public long sec;
    public long milliSeconds;
    public TimeObject(){}
    public TimeObject(int mins, int sec , int milliSeconds){
        this.mins=mins;
        this.sec=sec;
        this.milliSeconds=milliSeconds;
    }

}
