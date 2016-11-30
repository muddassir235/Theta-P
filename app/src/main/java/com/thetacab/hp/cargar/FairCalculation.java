package com.thetacab.hp.cargar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by hp on 7/17/2016.
 */
public class FairCalculation {
    private int cabType;
    private int bikeBaseRate;
    private int bikeDistanceCoefficient;
    private int bikeTimeCoefficient;
    private int carBaseRate;
    private int carDistanceCoefficent;
    private int carTimeCoefficient;

    public FairCalculation(int cabType){
        this.cabType = cabType;
        String bikeBaseRateKey = "bikeBaseRate";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(bikeBaseRateKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    bikeBaseRate = Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String bikeDistanceCoefficientKey = "bikeDistanceCoefficient";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(bikeDistanceCoefficientKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    bikeDistanceCoefficient = Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String bikeTimeCoefficientKey = "bikeTimeCoefficient";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(bikeTimeCoefficientKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    bikeTimeCoefficient = Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String carBaseRateKey = "carBaseRate";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(carBaseRateKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    carBaseRate = Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String carDistanceCoefficientKey = "carDistanceCoefficient";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(carDistanceCoefficientKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    carDistanceCoefficent =Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String carTimeCoefficientKey = "carTimeCoefficient";
        FirebaseDatabase.getInstance().getReference().child("FairCalculationParams").child(carTimeCoefficientKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    carTimeCoefficient = Integer.valueOf(String.valueOf((Long) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bikeBaseRate = 20;
        bikeDistanceCoefficient = 15;
        bikeTimeCoefficient = 0;
        carBaseRate = 80;
        carDistanceCoefficent = 16;
        carTimeCoefficient = 2;
    }

    public int getFairEstimate(int totalDistanceInM, int etaInSeconds){
        double distanceInKm = ((double)totalDistanceInM)/((double) 1000);
        int KMs = (int) (distanceInKm+0.5);
        double etaInMinutes = ((double) etaInSeconds)/((double)60);
        int Mins = (int) (etaInMinutes + 0.5);

        int fairEstimate = 50;

        if(cabType == 0){
            if(KMs<=2){
                fairEstimate = (2*bikeDistanceCoefficient)+(Mins*bikeTimeCoefficient)+bikeBaseRate;
            }else if(KMs>20){
                fairEstimate = (20*bikeDistanceCoefficient)+(Mins*bikeTimeCoefficient)+bikeBaseRate;
            }else {
                fairEstimate = (KMs*bikeDistanceCoefficient)+(Mins*bikeTimeCoefficient)+bikeBaseRate;
            }
        }else if(cabType ==1){
            fairEstimate = (KMs*carDistanceCoefficent)+(Mins*carTimeCoefficient)+carBaseRate;
        }else {
            fairEstimate = 50;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(fairEstimate<50){
            fairEstimate = 50;
        }
        return fairEstimate;
    }

    public void setCabType(int cabType){
        this.cabType = cabType;
    }
}
