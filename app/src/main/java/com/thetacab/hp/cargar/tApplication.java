package com.thetacab.hp.cargar;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gul on 6/20/16.
 */
public class tApplication extends MultiDexApplication {
    public static Context context;
    private String uid;
    public boolean authStateListenerNotActive;
    @Override
    public void onCreate()
    {

        context = getApplicationContext();
        super.onCreate();
        Log.e("App","is created");

        if (!FirebaseApp.getApps(this).isEmpty()) {
            Log.e("app ","is empty");
            FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        }

       /* authStateListenerNotActive = false;
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });
*/

    }


    public void putUid(String S)
    {
        uid=S;
    }
    public String getUid(){
        return uid;
    }
    public void handleUncaughtException(Thread thread, Throwable e){
        Log.e("Uncaught Exception:",thread.toString()+"::"+e.toString());
        
    }
}
