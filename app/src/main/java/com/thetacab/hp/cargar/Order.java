package com.thetacab.hp.cargar;

/**
 * Created by gul on 6/28/16.
 */
public class Order {
    public String source;
    public String destination;
    public String sourceLat;
    public String sourceLong;
    public String destLat;
    public String destLong;
    public int cabType;
    public String promoId;
    public String tripCode;

    public Order(){}
    public Order(String source, String destination,String sourceLat,String sourceLong,String destLat,String destLong ,int cabType){
        this.source=source;
        this.destination=destination;
        this.cabType=cabType;
        this.sourceLat=sourceLat;
        this.sourceLong=sourceLong;
        this.destLat=destLat;
        this.destLong=destLong;
    }

    public Order(String source, String destination, String sourceLat, String sourceLong, String destLat, String destLong, int cabType, String promoId) {
        this.source = source;
        this.destination = destination;
        this.sourceLat = sourceLat;
        this.sourceLong = sourceLong;
        this.destLat = destLat;
        this.destLong = destLong;
        this.cabType = cabType;
        this.promoId = promoId;
    }

    public Order(String source, String destination, String sourceLat, String sourceLong, String destLat, String destLong, int cabType, String promoId, String tripCode) {
        this.source = source;
        this.destination = destination;
        this.sourceLat = sourceLat;
        this.sourceLong = sourceLong;
        this.destLat = destLat;
        this.destLong = destLong;
        this.cabType = cabType;
        this.promoId = promoId;
        this.tripCode = tripCode;
    }
}
