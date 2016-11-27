package com.thetacab.hp.cargar;

/**
 * Created by gul on 6/23/16.
 */
public class FinalUserObject {
    public String Email;
    public String Name;
    public String Birthday;
    public String Gender;
    public String CNIC;
    public String Cell;
    public String Type;
    public FinalUserObject(){}
    public FinalUserObject(String Email,String Name,String Birthday,String Gender, String CNIC,String Cell,String Type){
        this.Email=Email;
        this.Name=Name;
        this.Birthday=Birthday;
        this.Gender=Gender;
        this.CNIC=CNIC;
        this.Cell=Cell;
        this.Type=Type;
    }
}
