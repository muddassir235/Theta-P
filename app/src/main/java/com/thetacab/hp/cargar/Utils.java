package com.thetacab.hp.cargar;

import android.content.Context;
import android.content.res.Resources;
import android.location.*;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by hp on 7/13/2016.
 */
public class Utils {

    static FirebaseUser user;

    public static FirebaseUser getCurrUser(){
        if(user == null){
            user = FirebaseAuth.getInstance().getCurrentUser();
            return user;
        }else {
            return user;
        }
    }

    public static String getUid(){
        if(user == null){
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                return null;
            }else{
                return user.getUid();
            }
        }else {
            return user.getUid();
        }
    }

    public static float fromDpToPx(float dp) {

        try {
            return dp * Resources.getSystem().getDisplayMetrics().density;
        } catch (Exception e) {
            return dp;
        }
    }

    public static int getDistanceInMetersFromLatLngData(ArrayList<LatLng> data){
        int meters=0;
        int length = data.size();
        int i=0;
        for(LatLng latLng:data){
            if(i < (length-1)) {
                LatLng a = latLng;
                LatLng b = data.get(i + 1);
                float[] results = new float[3];
                Location.
                        distanceBetween(
                                a.latitude,
                                b.longitude,
                                b.latitude,
                                b.longitude,
                                results
                        );
                meters += results[0];
            }
            i++;
        }
        return meters;
    }

    private static int getNumber(long realNumber, int index){
        String realNumberString=String.valueOf(realNumber);
        Log.e("Numbers -> string",realNumberString);
        char neededNumber=realNumberString.charAt(index);
        int needInteger=Integer.valueOf(String.valueOf(neededNumber));
        return needInteger;
    }
    public static String extractRequiredKey(long timeStamp){
        String key="";
        Log.e("Numbers -> time",String.valueOf(timeStamp));
        for(int j=5; j<11; j++){
            key=key+String.valueOf(getNumber(timeStamp,j));
        }
        Log.e("Numbers -> key",String.valueOf(key));
        return key;
    }

    public static boolean containsUnwantedCharsPhoneNumber(String string){
        for(int i=0;i<string.length();i++){
            if(
                    string.charAt(i) != '0' &&
                    string.charAt(i) != '1' &&
                    string.charAt(i) != '2' &&
                    string.charAt(i) != '3' &&
                    string.charAt(i) != '4' &&
                    string.charAt(i) != '5' &&
                    string.charAt(i) != '6' &&
                    string.charAt(i) != '7' &&
                    string.charAt(i) != '8' &&
                    string.charAt(i) != '9' &&
                    string.charAt(i) != '+'
                    ){
                return true;
            }
        }
        return false;
    }

    public static boolean containsUnwantedCharsCNIC(String string){
        for(int i=0;i<string.length();i++){
            if(
                    string.charAt(i) != '0' &&
                            string.charAt(i) != '1' &&
                            string.charAt(i) != '2' &&
                            string.charAt(i) != '3' &&
                            string.charAt(i) != '4' &&
                            string.charAt(i) != '5' &&
                            string.charAt(i) != '6' &&
                            string.charAt(i) != '7' &&
                            string.charAt(i) != '8' &&
                            string.charAt(i) != '9'
                    ){
                return true;
            }
        }
        return false;
    }

    public static int phoneNumberIsValid(String phoneNumber){
        String phoneNumberEntry = phoneNumber.replace("-","");
        phoneNumberEntry = phoneNumberEntry.replace(" ","");
        if(phoneNumberEntry.equals("")){
            return EMPTY;
        }else if(phoneNumberEntry.length()<11) {
            return INVALID;
        }else if(
                phoneNumberEntry.substring(0,4).equals("+923") &&
                        phoneNumberEntry.length() == 13 &&
                        !Utils.containsUnwantedCharsPhoneNumber(phoneNumberEntry)
                ){
            return VALID;
        }else if(
                phoneNumberEntry.substring(0,2).equals("03") &&
                        phoneNumberEntry.length() == 11 &&
                        !Utils.containsUnwantedCharsPhoneNumber(phoneNumberEntry)
                ){
            return VALID;
        }else {
            return INVALID;
        }
    }

    public static int isCNICValid(String cnic){
        String cleanedCNIC = cnic.replace(" ","");
        cleanedCNIC = cleanedCNIC.replace("-","");
        if(cleanedCNIC.equals("")){
            return EMPTY;
        }else if(containsUnwantedCharsCNIC(cleanedCNIC)){
            return INVALID;
        }else if(cleanedCNIC.length()!=13){
            return INVALID;
        }else {
            return VALID;
        }
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static final int VALID = 2;
    public static final int INVALID = 1;
    public static final int EMPTY = 0;
}
