package com.thetacab.hp.cargar.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.thetacab.hp.cargar.Constants;
import com.thetacab.hp.cargar.OfflineFragment;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.User;
import com.thetacab.hp.cargar.Utils;

public class MapsActivity
        extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapsFragment.OnFragmentInteractionListener, OfflineFragment.OnFragmentInteractionListener {

    private static final String TAG = "MapActivity";

    public static MapsActivity activity;

    ImageButton mOpenDrawerButton;

    @Override
    protected void onStart() {
        super.onStart();
        activity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mOpenDrawerButton = (ImageButton) findViewById(R.id.open_nav_drawer);
        activity = this;
        FirebaseMessaging.getInstance().subscribeToTopic("notif");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setOnClickListenerOnOpenDrawerButton();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_passenger_map);

        TextView nameTV = (TextView) headerView.findViewById(R.id.current_user_name);
        TextView emailTV = (TextView) headerView.findViewById(R.id.current_user_email);
        final ImageView profileImage = (ImageView) headerView.findViewById(R.id.imageView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            nameTV.setText(user.getDisplayName());
            emailTV.setText(user.getEmail());

            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User driver = dataSnapshot.getValue(User.class);
                        if(driver.profileImageURL != null) {
                            Picasso.with(getApplicationContext()).load(driver.profileImageURL).resize(getPXfromDP(70f), getPXfromDP(70f)).centerCrop().into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        FrameLayout currFragment = (FrameLayout) findViewById(R.id.curr_fragment_frame);

        if (savedInstanceState == null) {
            Fragment newFragment = new MapsFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(currFragment.getId(), newFragment).commit();
        }


    }

    int getPXfromDP(float dps){
        return (int)(getResources().getDisplayMetrics().density*dps);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Utils.getCurrUser()!=null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("AppStatus")
                    .child(Utils.getUid())
                    .setValue(0);
        }
    }

    void setOnClickListenerOnOpenDrawerButton(){
        mOpenDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_out) {
            // Handle the camera action
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), StartupActivity.class));
                            StartupActivity.saveAsDriverOrCustomer(getApplicationContext(), Constants.DONT_KNOW_USER_TYPE);
                            if(user!=null) {
                                FirebaseDatabase.getInstance().getReference().child("MapUIDtoInstanceID").child(user.getUid()).setValue(null);
                            }
                            finish();
                        }
                    });
        }else if(id == R.id.edit_profile){
            Intent intent = new Intent(this,EditProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
