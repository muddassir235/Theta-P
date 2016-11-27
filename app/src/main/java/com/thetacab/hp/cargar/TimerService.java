package com.thetacab.hp.cargar;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;

/**
 * Created by gul on 7/25/16.
 */
public class TimerService extends Service {
    Handler handler ;
    Handler handler2;

    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int t = 1;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    String orderCustomerId;
    String orderCustomerid;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    public Runnable updateTimer = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            Log.e("Time","" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            //settime here
            /*

            time.setText("" + mins + ":" + String.format("%02d", secs) + ":"
            + String.format("%03d", milliseconds));
            time.setTextColor(Color.RED);
            handler.postDelayed(this, 0);

            */

            handler.postDelayed(this, 0);
        }};
    public Runnable updateDatabase = new Runnable() {
        public void run() {

            FirebaseDatabase.getInstance().getReference().child("TripTime").child(orderCustomerId)
                    .setValue(new TimeObject(mins,secs,milliseconds));
            handler.postDelayed(this, 2000);
        }};
    @Override
    public void onCreate() {
        //getting values from extras
        handler= new Handler();

        starttime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        handler.postDelayed(updateDatabase,2000);


    }
    @Override
    public void onDestroy(){
        //push data to firebase
        FirebaseDatabase.getInstance().getReference().child("TripTime").child(orderCustomerId)
                .setValue(new TimeObject(mins,secs,milliseconds));
        //PUSH VARIABLES INTO SHARED PREFERENCES
        timeSwapBuff=timeSwapBuff+timeInMilliseconds;

        SharedPreferences prefs=getSharedPreferences("timervariables",this.MODE_PRIVATE);
        SharedPreferences.Editor editor= prefs.edit();
        editor.putString("timeSwap",String.valueOf(timeSwapBuff));
        editor.commit();
        handler.removeCallbacks(updateTimer);


    }
    public int onStartCommand (Intent intent, int flags, int startId) {

        timeSwapBuff= Integer.valueOf((String)intent.getExtras().get("timeSwap"));
        orderCustomerId=(String)intent.getExtras().get("customerOrderId");
        starttime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimer, 0);
        addOrderCompletedListener();

        return 1;
    }
    public void addOrderCompletedListener(){
        FirebaseDatabase.getInstance().getReference().child("EndTrip").child(orderCustomerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            FirebaseDatabase.getInstance().getReference().child("TripTime").child(orderCustomerId)
                                    .setValue(new TimeObject(mins,secs,milliseconds));
                            TimerService.this.stopSelf();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}
