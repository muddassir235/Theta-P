package com.thetacab.hp.cargar;

/**
 * Created by hp on 9/6/2016.
 */
public class User {
    public String id;
    public String email;
    public String name;
    public String phone;
    public String type;
    public String gender;
    public String cNIC;
    public String address;
    public String referenceName;
    public String referenceNumber;
    public String city;
    public String licsencePlateNumber;
    public String licsenceNumber;
    public String rating;
    public String profileImageURL;
    public String bikeImageURL;
    public String helpNumber;
    public int verification;

    public User() {
    }

    public User(String id, String email, String name, String phone, String type, String gender, String cNIC, String address, String referenceName, String referenceNumber, String city, String licsencePlateNumber, String licsenceNumber, String rating,int verification) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.gender = gender;
        this.cNIC = cNIC;
        this.address = address;
        this.referenceName = referenceName;
        this.referenceNumber = referenceNumber;
        this.city = city;
        this.licsencePlateNumber = licsencePlateNumber;
        this.licsenceNumber = licsenceNumber;
        this.rating = rating;
        this.verification = verification;
    }

    public User(String id, String email, String name, String phone, String type, String gender, String cNIC, String address, String referenceName, String referenceNumber, String city, String licsencePlateNumber, String licsenceNumber, String rating, String profileImageURL, String bikeImageURL, int verification) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.gender = gender;
        this.cNIC = cNIC;
        this.address = address;
        this.referenceName = referenceName;
        this.referenceNumber = referenceNumber;
        this.city = city;
        this.licsencePlateNumber = licsencePlateNumber;
        this.licsenceNumber = licsenceNumber;
        this.rating = rating;
        this.profileImageURL = profileImageURL;
        this.bikeImageURL = bikeImageURL;
        this.verification = verification;
    }

    public User(String id, String email, String name, String phone, String type, String gender, String cNIC, String address, String referenceName, String referenceNumber, String city, String licsencePlateNumber, String licsenceNumber, String rating, String profileImageURL, String bikeImageURL) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.gender = gender;
        this.cNIC = cNIC;
        this.address = address;
        this.referenceName = referenceName;
        this.referenceNumber = referenceNumber;
        this.city = city;
        this.licsencePlateNumber = licsencePlateNumber;
        this.licsenceNumber = licsenceNumber;
        this.rating = rating;
        this.profileImageURL = profileImageURL;
        this.bikeImageURL = bikeImageURL;
    }
}
