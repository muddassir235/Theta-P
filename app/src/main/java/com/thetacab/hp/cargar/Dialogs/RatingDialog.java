package com.thetacab.hp.cargar.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thetacab.hp.cargar.Activities.MapsActivity;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.User;
import com.thetacab.hp.cargar.Utils;

import java.util.HashMap;

/**
 * Created by hp on 11/29/2016.
 */
public class RatingDialog extends DialogFragment {
    Context context;

    public RatingDialog(){
    }

    @SuppressLint("ValidFragment")
    public RatingDialog(Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyRatingDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.dialog_rating, null);

        builder.setView(rootView);

        final RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.driver_rating_bar);
        final TextView finalFairTextView = (TextView) rootView.findViewById(R.id.final_fair_tv);

        final TextView ratingPromptTV = (TextView) rootView.findViewById(R.id.rating_prompt_tv);

        if(Utils.getCurrUser() == null){return null;}

        FirebaseDatabase.getInstance().getReference()
                .child("FairEstimate")
                .child(Utils.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            finalFairTextView.setText(""+((Long)dataSnapshot.getValue()));
                        }else{
                            finalFairTextView.setText(""+50);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


        FirebaseDatabase.getInstance().getReference()
                .child("AcceptedOrders")
                .child(Utils.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String driverId = (String) dataSnapshot.getValue();

                            FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User driver = dataSnapshot.getValue(User.class);
                                    ratingPromptTV.setText("How much do you rate "+driver.name+"?");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final float rating = ratingBar.getRating();
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("AcceptedOrders")
                        .child(Utils.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){



                                    String driverId = (String) dataSnapshot.getValue();

                                    HashMap<String,Object> ratingData = new HashMap<String, Object>();
                                    ratingData.put("customerKey",Utils.getUid());
                                    ratingData.put("rating",rating);

                                    FirebaseDatabase.getInstance().getReference().
                                            child("DriverRating").
                                            child(driverId).
                                            push().
                                            setValue(ratingData);

                                    FirebaseDatabase.getInstance().
                                            getReference().
                                            child("State").
                                            child(Utils.getUid()).
                                            setValue(1);

                                    FirebaseDatabase.getInstance().
                                            getReference().
                                            child("AcceptedOrders").
                                            child(Utils.getUid()).
                                            setValue(null);



                                    Intent intent = new Intent(context, MapsActivity.class);
                                    MapsActivity.activity.finish();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    MapsActivity.activity.startActivity(intent);
                                    dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
        return builder.create();
    }
}