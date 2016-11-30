package com.thetacab.hp.cargar.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.Utils;

import java.io.IOException;

public class StartupActivity extends AppCompatActivity {
    private static final String TAG = "StartupActivity: ";

    private static final int RC_SIGN_IN = 235;
    private static final int DATA_ENTRY = 236;
    FirebaseAuth auth;

    TextView mLoadingTV;
    ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
        }

        if(!Utils.isConnected(getApplicationContext())){
            mLoadingTV = (TextView) findViewById(R.id.loading_status);
            mLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar);

            mLoadingTV.setText("No Internet :(");
            mLoadingProgressBar.setVisibility(View.GONE);
        }

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            final FirebaseUser user  = auth.getCurrentUser();

            final String token = FirebaseInstanceId.getInstance().getToken();
            if(token == null){
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Log.v(TAG, " Token: "+token);
                FirebaseDatabase.getInstance().getReference().
                        child("MapUIDtoInstanceID").
                        child(user.getUid()).setValue(token);
            }

            if(isUserDataEntered(getApplicationContext(),user.getUid())) {
                // user data has been entered
                startCustomerActivity();

            }else {

                FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    userDataHasBeenEnteredForId(getApplicationContext(),user.getUid());
                                    startCustomerActivity();
                                }else{
                                    startDataEntryActivity();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }else{
            Log.v(TAG,"7. user not signed in, signing user in using firebaseUI-auth");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER,
                                    AuthUI.FACEBOOK_PROVIDER)
                            .setTheme(R.style.ColoredFirebaseUI)
                            .setLogo(R.mipmap.cargar_logo_1)
                            .setTosUrl("https://docs.google.com/document/d/1BtRqS9hYfe-6hVmqAuJHYpSJiF8RBbnmDOC9QtH1YxU/edit?usp=sharing")
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN || requestCode == DATA_ENTRY) {
            if (resultCode == RESULT_OK) {

                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if(firebaseUser!=null) {
                    if (isUserDataEntered(getApplicationContext(), firebaseUser.getUid())) {
                        startCustomerActivity();
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(firebaseUser.getUid()).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    userDataHasBeenEnteredForId(getApplicationContext(),firebaseUser.getUid());
                                    startCustomerActivity();
                                }else{
                                    startDataEntryActivity();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            } else {
                Log.v(TAG, "14. user sign in failed or cancelled trying again");
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(
                                        AuthUI.EMAIL_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.FACEBOOK_PROVIDER)
                                .setTheme(R.style.ColoredFirebaseUI)
                                .setLogo(R.mipmap.cargar_logo_1)
                                .setTosUrl("https://docs.google.com/document/d/1BtRqS9hYfe-6hVmqAuJHYpSJiF8RBbnmDOC9QtH1YxU/edit?usp=sharing")
                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }


    void startCustomerActivity(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void startDataEntryActivity(){
        Intent intent = new Intent(getApplicationContext(), UserDataEntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public static void saveAsDriverOrCustomer(Context context,int userType){
        Log.v(TAG, "15. saving user type to shared preferences: "+userType);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt("Type",userType);
        sharedPreferencesEditor.commit();
    }

    public static boolean isUserDataEntered(Context context,String Id){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(Id,false);
    }

    public static void userDataHasBeenEnteredForId(Context context,String Id){
        Log.v(TAG,"16. customerId: "+Id+" data has been entered for this customer id.");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(Id,true);
        sharedPreferencesEditor.commit();
    }

    public static void userDataHasNotBeenEnteredForId(Context context,String Id){
        Log.v(TAG,"16. customerId: "+Id+" data has been entered for this customer id.");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(Id,false);
        sharedPreferencesEditor.commit();
    }

}
