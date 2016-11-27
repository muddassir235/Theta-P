package com.thetacab.hp.cargar.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.hujiaweibujidao.wava.Techniques;
import com.github.hujiaweibujidao.wava.YoYo;
import com.google.android.gms.appindexing.AppIndexApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.thetacab.hp.cargar.Animations;
import com.thetacab.hp.cargar.Constants;
import com.thetacab.hp.cargar.DriverLocation;
import com.thetacab.hp.cargar.FairCalculation;
import com.thetacab.hp.cargar.GoogleDirectionsApiWrapper;
import com.thetacab.hp.cargar.GoogleReverseGeocodingApiWrapper;
import com.thetacab.hp.cargar.Order;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.SendNotif;
import com.thetacab.hp.cargar.SystemBarTintManager;
import com.thetacab.hp.cargar.User;
import com.thetacab.hp.cargar.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsFragment extends Fragment implements
        OnMapReadyCallback, LocationListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MapsFragment";
    private static boolean animateCurrLocationButton;

    private GoogleMap mMap;
    static Context context;
    static MapsFragment fragment;

    static GoogleDirectionsApiWrapper googleDirectionsApiWrapper;
    FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    android.location.Location mLastLocation;

    static String uId;

    //Fields
    int noOfAnimationsRunning;
    boolean movingUpAnimation;
    boolean movingDownAnimation;
    static boolean sourceEntered;
    static boolean destinationEntered;
    boolean canTapSelectionIcon;
    boolean driverIsArriving;
    boolean sourceLatLngRetrieved;
    boolean inTrip;
    private int currentAppState;
    private String driverId;
    private LatLng cabLatLng;
    private String instanceId;
    private boolean cabFoundScreenHadBeenShown;
    private LatLng sourceLatLng;
    private LatLng destinationLatLng;
    private Marker sourceMarker;
    private static Marker destinationMarker;
    private Marker cabMarker;
    public LatLng centerOfMapLatLng;
    private String DIRECTION_API_KEY;
    private String sourceAddress;
    private String destinationAddress;
    private ArrayList<LatLng> mTripPathData;
    public FairCalculation fairCalculation;
    boolean activateFairEstimation;
    private LocationRequest mLocationRequest;
    private final String phoneNo = "+923330627462";
    private int currentCabSelection;
    //AcceptCallListener
    private ValueEventListener accepCallListener;
    private ValueEventListener acceptCallDriverLocationListener;
    private ValueEventListener tripEndedListener;
    private ValueEventListener cabArrivedListener;
    private ValueEventListener tripStartedListener;
    /////////////////////////////
    View rootView;


    //Views
    @InjectView(R.id.notify_selection_toast_text_view)
    TextView notifySelectionToastTV;
    static ImageButton currLocButton;
    @InjectView(R.id.bike_selection_image_button)
    ImageButton bikeSelectionIB;
    @InjectView(R.id.cab_selection_card_view)
    CardView cabSelectionCV;
    @InjectView(R.id.cab_type_selection_layout)
    LinearLayout cabSelectionLayout;
    static CardView markerButton;
    static CardView searchSouceCardView;
    static CardView searchDestinationCardView;
    @InjectView(R.id.source_destination_selection_layout)
    CardView sourceDestinationSelectionLayoutCV;
    static TextView markerButtonTextView;
    static ImageView markerAtCenterOfMapIV;
    @InjectView(R.id.source_bar_cross_image_button)
    ImageButton sourceBarCrossIB;
    static ImageButton destinationBarCrossIB;
    static Button requestCabButton;
    static TextView fairQuoteTV;
    CardView tripCodeCV;
    TextView tripCodeTV;
    ImageView vehicleImageIV;
    ImageView driverProfilePicIV;
    TextView liscenseNoTV;
    ProgressBar mSourceProgressBar;
    ProgressBar mDestinationProgressBar;
    @InjectView(R.id.blurView)
    FrameLayout blurView;
    @InjectView(R.id.finding_taxi_text_view)
    TextView findingTaxiTV;
    @InjectView(R.id.finding_taxi_animation_view)
    AVLoadingIndicatorView findCabAnimView;
    @InjectView(R.id.eta_of_cab_text_view)
    TextView etaOfCabTV;
    @InjectView(R.id.driver_card_display_frame_layout)
    FrameLayout driverCardHolderFL;
    @InjectView(R.id.driver_card)
    CardView driverCard;
    @InjectView(R.id.cancel_trip_button)
    Button cancelTripButton;
    @InjectView(R.id.cab_has_arrived_text_view)
    TextView cabHasArrivedTV;
    @InjectView(R.id.cab_arrived_animation)
    AVLoadingIndicatorView cabHasArrivedAnimView;
    @InjectView(R.id.help_call_button)
    LinearLayout mHelpCallButton;
    @InjectView(R.id.help_sms_button)
    LinearLayout mHelpSMSButton;
    @InjectView(R.id.name_of_drive_text_view)
    TextView mNameOfDriverTV;
    @InjectView(R.id.driver_rating_bar_in_card)
    RatingBar mDriverRatingBar;
    @InjectView(R.id.driver_rating_text_view)
    TextView mDriverRatingTV;
    @InjectView(R.id.driver_number_of_ratings_text_view)
    TextView mDriverTotalRatingsTV;


    static DialogFragment ratingDialogFragment;

    static PlaceAutocompleteFragment sourceAddressAutocCompleteFragment;
    static PlaceAutocompleteFragment destinationAddressAutoCompleteFragment;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsFragment newInstance(String param1, String param2) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.inject(this, rootView);
        fragment = this;
        animateCurrLocationButton = true;
        defineAllListeners();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setupAllViews();
        initializeFields();
        setupGooglePlacesAPI();
        googleDirectionsApiWrapper.setEtaTV(etaOfCabTV);
        googleDirectionsApiWrapper.setFairEstimateTV(fairQuoteTV);
        setStatusBarTranslucent(true, rootView);
        getLocationPermissions();
        setTripEndedListener();
        markerButton.setCardBackgroundColor(Color.parseColor("#ddF9BA32"));
        checkingLayout(markerButton);
        return rootView;
    }

    private void checkingLayout(View v) {
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    void setupGooglePlacesAPI() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTripEndedListener();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UID-SharedPref", 0);
        String uId = sharedPref.getString("UID", null);
        if (uId != null) {
            FirebaseDatabase.getInstance().getReference().child("AppStatus").child(uId).setValue(1);
        }
    }


    void getLocationPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_BLUETOOTH is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent, View rootView) {
        View v = rootView.findViewById(R.id.map);
        View v1 = rootView.findViewById(R.id.source_destination_selection_layout);
        if (v != null && v1 != null) {
            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0;
            RelativeLayout.LayoutParams mapLayoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
            RelativeLayout.LayoutParams searchCardLayoutParams = (RelativeLayout.LayoutParams) v1.getLayoutParams();
            mapLayoutParams.topMargin -= paddingTop;
            searchCardLayoutParams.topMargin += paddingTop;
            int paddingLeftOfETATV = etaOfCabTV.getPaddingLeft();
            int paddingRightOfETATV = etaOfCabTV.getPaddingRight();
            int paddingTopOfETATV = etaOfCabTV.getPaddingTop();
            int paddingBottomOfETATV = etaOfCabTV.getPaddingBottom();

            paddingTopOfETATV += getStatusBarHeight();
            etaOfCabTV.setPadding(
                    paddingLeftOfETATV,
                    paddingTopOfETATV,
                    paddingRightOfETATV,
                    paddingBottomOfETATV
            );
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarAlpha(0.2f);
        tintManager.setNavigationBarAlpha(0.2f);
        tintManager.setTintAlpha(0.2f);
        tintManager.setStatusBarTintResource(R.drawable.selected);
        tintManager.setTintColor(Color.parseColor("#007DC0"));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLastLocation = location;
            Log.v("LocationChanged", " Entered On location changed");
            FirebaseDatabase.getInstance().getReference().child("PassengerLocation").
                    child(uId).child("lat").setValue(location.getLatitude());
            FirebaseDatabase.getInstance().getReference().child("PassengerLocation").
                    child(uId).child("lng").setValue(location.getLongitude());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.v(TAG, " code callback: "+requestCode);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("GoogleApiClient: ", "Connection Failed. Trying to reconnect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("GoogleApiClient: ", "Connection Suspended. Trying to reconnect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /**
         * He we will get the user's current
         * location and setup the map camera
         * to that location
         */
        //startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            getLocationPermissions();
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng currentLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().
                            target(currentLoc).
                            zoom((float) 15.5).
                            tilt((float) 70).
                            build()
            ));
        }else{
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            Log.e("Location: ", " Location was null");
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    void initializeFields(){
        DIRECTION_API_KEY = getResources().getString(R.string.google_directions_api_key);
        googleDirectionsApiWrapper = new GoogleDirectionsApiWrapper(DIRECTION_API_KEY,fragment);
        fairCalculation = new FairCalculation(0);
        googleDirectionsApiWrapper.setCabType(0);
        googleDirectionsApiWrapper.changeEstimateWhenCabTypeChanged();
        canTapSelectionIcon = true;
        currentCabSelection = Constants.SELECT_BIKE;
        noOfAnimationsRunning = 0;
        movingUpAnimation = false;
        movingDownAnimation = false;
        sourceEntered = false;
        destinationEntered = false;
        cabFoundScreenHadBeenShown = false;
        driverIsArriving = false;
        inTrip = false;
        activateFairEstimation = false;
        sourceLatLngRetrieved = false;
    }

    void launchAppropriateAppState(){
        Log.v("AppState: ",""+currentAppState);
        Log.v("MapsActivity: ","Launch Appropriate App State");
        if(currentAppState == 1){
            // do nothing
        }else if(currentAppState == Constants.FINDING_CAB_STATE){
            Animations.makeVisible(findCabAnimView,findingTaxiTV,blurView,cancelTripButton);
            Animations.makeInvisible(
                    requestCabButton,cabSelectionCV,notifySelectionToastTV,
                    sourceDestinationSelectionLayoutCV,etaOfCabTV,driverCard,driverCardHolderFL,cabHasArrivedAnimView,
                    cabHasArrivedTV,markerAtCenterOfMapIV,markerButton,currLocButton
            );
            setOnAcceptCallListner();
        }else if(currentAppState == Constants.CAB_ARRIVING_STATE){
            Animations.makeInvisible(
                    findingTaxiTV,requestCabButton,cabSelectionCV,
                    notifySelectionToastTV,sourceDestinationSelectionLayoutCV,
                    blurView,cabHasArrivedAnimView,cabHasArrivedTV,
                    markerAtCenterOfMapIV,markerButton,currLocButton

            );
            Animations.makeVisible(
                    etaOfCabTV,driverCard,
                    driverCardHolderFL,cancelTripButton
            );
            Animations.remove(findCabAnimView);
            FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        driverId = (String) dataSnapshot.getValue();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    User driver = dataSnapshot.getValue(User.class);
                                    mNameOfDriverTV.setText(driver.name);
                                    FirebaseDatabase.getInstance().getReference().child("DriverRating").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                float rating = 0.0f;
                                                int numberOfRatings = 0;
                                                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                                    float rating1 = Float.valueOf(String.valueOf(dataSnapshot1.child("rating").getValue()));
                                                    rating+=rating1;
                                                    numberOfRatings++;
                                                }
                                                DecimalFormat df = new DecimalFormat("#.#");
                                                df.setRoundingMode(RoundingMode.CEILING);
                                                rating = Float.valueOf(df.format(rating/numberOfRatings));
                                                mDriverRatingTV.setText(""+rating);
                                                mDriverRatingBar.setRating(rating);
                                                if(numberOfRatings == 1) {
                                                    mDriverTotalRatingsTV.setText("One ride completed");
                                                }else {
                                                    mDriverTotalRatingsTV.setText(""+numberOfRatings+" rides completed");
                                                }
                                            }else{
                                                mDriverRatingTV.setText("n/a");
                                                mDriverRatingBar.setRating(0f);
                                                mDriverTotalRatingsTV.setText("No Rides Completed Yet");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    if(driver.profileImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.profileImageURL).resize(getPXfromDP(70f),getPXfromDP(70f)).centerCrop().into(driverProfilePicIV);
                                    }
                                    if(driver.bikeImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.bikeImageURL).into(vehicleImageIV);
                                    }
                                    if(driver.licsencePlateNumber!=null) {
                                        liscenseNoTV.setText(driver.licsencePlateNumber);
                                    }
                                }
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

            setOnAcceptCallListner();
        }else if(currentAppState == Constants.CAB_ARRIVED_STATE) {
            Animations.makeInvisible(
                    findingTaxiTV,requestCabButton,cabSelectionCV,
                    notifySelectionToastTV,sourceDestinationSelectionLayoutCV,
                    blurView,cabHasArrivedAnimView,cabHasArrivedTV,
                    markerAtCenterOfMapIV,markerButton,currLocButton,cancelTripButton,etaOfCabTV
            );

            Animations.makeVisible(driverCard,
                    driverCardHolderFL
            );
            Animations.remove(findCabAnimView);
            FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        driverId = (String) dataSnapshot.getValue();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    User driver = dataSnapshot.getValue(User.class);
                                    mNameOfDriverTV.setText(driver.name);
                                    FirebaseDatabase.getInstance().getReference().child("DriverRating").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                float rating = 0.0f;
                                                int numberOfRatings = 0;
                                                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                                    float rating1 = Float.valueOf(String.valueOf(dataSnapshot1.child("rating").getValue()));
                                                    rating+=rating1;
                                                    numberOfRatings++;
                                                }
                                                DecimalFormat df = new DecimalFormat("#.#");
                                                df.setRoundingMode(RoundingMode.CEILING);
                                                rating = Float.valueOf(df.format(rating/numberOfRatings));
                                                mDriverRatingTV.setText(""+rating);
                                                mDriverRatingBar.setRating(rating);
                                                if(numberOfRatings == 1) {
                                                    mDriverTotalRatingsTV.setText("One ride completed");
                                                }else {
                                                    mDriverTotalRatingsTV.setText(""+numberOfRatings+" rides completed");
                                                }
                                            }else{
                                                mDriverRatingTV.setText("n/a");
                                                mDriverRatingBar.setRating(0f);
                                                mDriverTotalRatingsTV.setText("No Rides Completed Yet");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    if(driver.profileImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.profileImageURL).resize(getPXfromDP(70f),getPXfromDP(70f)).centerCrop().into(driverProfilePicIV);
                                    }
                                    if(driver.bikeImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.bikeImageURL).into(vehicleImageIV);
                                    }
                                    if(driver.licsencePlateNumber!=null) {
                                        liscenseNoTV.setText(driver.licsencePlateNumber);
                                    }

//                                    FirebaseDatabase.getInstance().getReference().child("Orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if(dataSnapshot.exists()){
//                                                Order order = dataSnapshot.getValue(Order.class);
//                                                tripCodeTV.setText(order.tripCode);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
                                }
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
            setOnCabArrivedListener();

        }else if(currentAppState==Constants.IN_TRIP_STATE){
            Animations.makeInvisible(findingTaxiTV,cabHasArrivedAnimView,cabSelectionCV,notifySelectionToastTV,
                    sourceDestinationSelectionLayoutCV,driverCard,requestCabButton,cancelTripButton,driverCardHolderFL,
                    cabHasArrivedAnimView,markerButton,markerAtCenterOfMapIV,blurView,currLocButton);
            Animations.makeVisible(etaOfCabTV,mHelpSMSButton,mHelpCallButton);
            inTrip = true;
            setOnTripStartedListener();
            FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        driverId = (String) dataSnapshot.getValue();
                        FirebaseDatabase.getInstance().getReference().child("Order").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Order order = dataSnapshot.getValue(Order.class);
                                    destinationLatLng = new LatLng(
                                            Double.valueOf(order.destLat),
                                            Double.valueOf(order.destLong)
                                    );
                                    FirebaseDatabase.getInstance().getReference().child("DriverLocation").child(driverId).
                                            addValueEventListener(acceptCallDriverLocationListener);
                                }
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

        }else if(currentAppState == Constants.TRIP_ENDED_STATE){
            Animations.makeInvisible(
                    findingTaxiTV,cabHasArrivedAnimView,cabSelectionCV,notifySelectionToastTV,
                    sourceDestinationSelectionLayoutCV,driverCard,requestCabButton,cancelTripButton,
                    driverCardHolderFL,cabHasArrivedAnimView,markerButton,markerAtCenterOfMapIV,
                    blurView,currLocButton,etaOfCabTV,mHelpSMSButton,mHelpCallButton
            );
            showRatingDialog();
        }else{
            //do nothing
        }
    }

    /**
     * Called when a fragment is first attached to its activity.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param activity
     * @deprecated See {@link #onAttach(Context)}.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context=activity;
    }

    RatingBar ratingBar;

    void showRatingDialog() {
        ImageView driverProfileIV;
        TextView finalFairTextView;
        final TextView ratingPromptTV;

        if(getActivity()==null){
            Log.e("Hello world","nullptr");
        }
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Rating")
                .customView(R.layout.dialog_rating, true)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final float rating = ratingBar.getRating();
                        FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    String driverId = (String) dataSnapshot.getValue();

                                    HashMap<String,Object> ratingData = new HashMap<String, Object>();
                                    ratingData.put("customerKey",uId);
                                    ratingData.put("rating",rating);

                                    FirebaseDatabase.getInstance().getReference().
                                            child("DriverRating").
                                            child(driverId).
                                            push().
                                            setValue(ratingData);

                                    FirebaseDatabase.getInstance().
                                            getReference().
                                            child("State").
                                            child(uId).
                                            setValue(1);

                                    FirebaseDatabase.getInstance().
                                            getReference().
                                            child("AcceptedOrders").
                                            child(uId).
                                            setValue(null);


                                    removeEventListeners();
                                    MapsFragment.this.onDestroy();
                                    Intent intent = new Intent(context, MapsActivity.class);
                                    MapsActivity.activity.finish();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    MapsActivity.activity.startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                        );
                        dialog.dismiss();
                    }
                }).build();

        ratingBar = (RatingBar) dialog.getCustomView().findViewById(R.id.driver_rating_bar);
        driverProfileIV = (ImageView) dialog.getCustomView().findViewById(R.id.driver_image_view);
        finalFairTextView = (TextView) dialog.getCustomView().findViewById(R.id.final_fair_tv);
        ratingPromptTV = (TextView) dialog.getCustomView().findViewById(R.id.rating_prompt_tv);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null) {
            final TextView finalFairTextView1 = finalFairTextView;
            FirebaseDatabase.getInstance().getReference().child("FairEstimate").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        finalFairTextView1.setText(""+((Long)dataSnapshot.getValue()));
                    }else{
                        finalFairTextView1.setText(""+50);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        dialog.show();
    }

    public void showDistanceNotSupportedDialog(){
        new MaterialDialog.Builder(getActivity()).title("Trip Too Long")
                .content("We currently don't support journeys more than 20km out side NUST premises.")
                .titleColorRes(R.color.primary_dark)
                .backgroundColor(Color.WHITE)
                .positiveColorRes(R.color.primary)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        destinationMarker.setVisible(false);
                        destinationBarCrossIB.setVisibility(View.GONE);
                        destinationAddressAutoCompleteFragment.setText("");
                        destinationAddressAutoCompleteFragment.setHint("Enter Destination");
                        destinationEntered = false;
                        if (!sourceEntered) {
                            elevateSourceBar();
                            makeDestinationBarBlank();
                            turnMarkerIntoSetSourceMarker();
                        } else {
                            googleDirectionsApiWrapper.removePath();
                            sourceEnteredHighLightDestinationBar();
                            turnMarkerIntoSetDestinationMarker();
                            animateRequestCabView(false);
                        }
                    }
                })
                .show();
    }



    void setupAllViews() {
        linkViews();

        setAllOnClickListeners();


        setOnPlaceSelectedListenerOnSourceBar();
        setOnPlaceSelectedListnerOnDestinationBar();

        //Set the hints in the source and destination bars.
        sourceAddressAutocCompleteFragment.setHint("Enter Source");
        destinationAddressAutoCompleteFragment.setHint("Enter Destination");

        //Set Max Card Elevation for source and destination Address Cards
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //ex. if ics is met then do this
            searchDestinationCardView.setMaxCardElevation(getPixelsFromDPs(20));
            searchSouceCardView.setMaxCardElevation(getPixelsFromDPs(20));
            cabSelectionCV.setMaxCardElevation(getPixelsFromDPs(30));
            cabSelectionCV.setCardElevation(getPixelsFromDPs(10));
        }else{
            //if api is not ics then do this
        }

    }

    /**
     * Link Java Fields to XML
     */
    void linkViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            sourceAddressAutocCompleteFragment = (PlaceAutocompleteFragment)
                    getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_source);
            destinationAddressAutoCompleteFragment = (PlaceAutocompleteFragment)
                    getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);
        }
        markerButton = (CardView) rootView.findViewById(R.id.button_on_top_of_marker);
        currLocButton = (ImageButton) rootView.findViewById(R.id.current_location_button);
        searchSouceCardView = (CardView) rootView.findViewById(R.id.search_source_card_view);
        searchDestinationCardView = (CardView) rootView.findViewById(R.id.search_destination_card_view);
        markerAtCenterOfMapIV = (ImageView) rootView.findViewById(R.id.marker_at_center_of_map_image_view);
        markerAtCenterOfMapIV = (ImageView) rootView.findViewById(R.id.marker_at_center_of_map_image_view);
        destinationBarCrossIB = (ImageButton) rootView.findViewById(R.id.destination_bar_cross_image_button);
        requestCabButton = (Button) rootView.findViewById(R.id.request_cab_button);
        fairQuoteTV = (TextView) rootView.findViewById(R.id.request_cab_price_text_view);
        markerButtonTextView = (TextView) rootView.findViewById(R.id.marker_button_text_view);
        tripCodeCV = (CardView) rootView.findViewById(R.id.trip_code_card_view);
        tripCodeTV = (TextView) rootView.findViewById(R.id.trip_code_text_view);
        driverProfilePicIV = (ImageView) rootView.findViewById(R.id.driver_image_view);
        vehicleImageIV = (ImageView) rootView.findViewById(R.id.car_image_view);
        liscenseNoTV = (TextView) rootView.findViewById(R.id.car_liscence_plate_number);
        mSourceProgressBar = (ProgressBar) rootView.findViewById(R.id.source_progress_bar);
        mDestinationProgressBar = (ProgressBar) rootView.findViewById(R.id.destination_progress_bar);
    }

    void setOnPlaceSelectedListenerOnSourceBar(){
        sourceAddressAutocCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                if(isSourceLatLngWithinBounds(place.getLatLng())) {
                    GoogleReverseGeocodingApiWrapper apiWrapper = new GoogleReverseGeocodingApiWrapper(DIRECTION_API_KEY);
                    apiWrapper.setLatLng(place.getLatLng()).requestAddress().setOnAddressRetrievedListener(new GoogleReverseGeocodingApiWrapper.OnAddressRetrievedListener() {
                        @Override
                        public void onAddressRetrieved(String resultingAddress) {
                            if (resultingAddress.contains("Islamabad") || resultingAddress.contains("Rawalpindi")) {
                                sourceBarCrossIB.setVisibility(View.VISIBLE);
                                searchSouceCardView.setClickable(false);
                                searchSouceCardView.setFocusable(false);
                                if (destinationEntered && sourceEntered) {
                                    animateCurrLocationButton = false;
                                } else {
                                    sourceEntered = true;
                                }
                                if (!destinationEntered) {
                                    // this is the source location
                                    sourceLatLng = place.getLatLng();

                                    // make the source bar show the complete address of the place
                                    sourceAddressAutocCompleteFragment.setText(place.getAddress());
                                    sourceAddress = place.getAddress().toString();

                                    // animate the camera to the source location
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), (float) 15.5));

                                    // add a marker to the current location indicating that it is the source
                                    if (sourceMarker != null) {
                                        sourceMarker.remove();
                                    }
                                    sourceMarker = mMap.addMarker(new MarkerOptions().
                                            position(place.getLatLng()).
                                            icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)).
                                            title("Source").
                                            visible(true)
                                    );

                                    sourceEnteredHighLightDestinationBar();

                                    /**
                                     * as the source have been entered turn the marker
                                     * and button to set destination mode
                                     */
                                    turnMarkerIntoSetDestinationMarker();
                                } else {
                                    // this is the source location
                                    sourceLatLng = place.getLatLng();

                                    // make the source bar show the complete address of the place
                                    sourceAddressAutocCompleteFragment.setText(place.getAddress());
                                    sourceAddress = place.getAddress().toString();

                                    // animate the camera to the source location
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), (float) 15.5));

                                    // add a marker to the current location indicating that it is the source
                                    if (sourceMarker != null) {
                                        sourceMarker.remove();
                                    }
                                    sourceMarker = mMap.addMarker(new MarkerOptions().
                                            position(place.getLatLng()).
                                            icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)).
                                            title("Source").
                                            visible(true)
                                    );

                                    // draw path between source and destination as both of them have been entered
                                    if (googleDirectionsApiWrapper.pathPresent()) {
                                        googleDirectionsApiWrapper.removePath();
                                    }
                                    googleDirectionsApiWrapper.from(sourceLatLng).to(destinationLatLng).retreiveDirections().setShow20Warning(true).setTextView(fairQuoteTV).setMap(mMap).drawPathOnMap();

                                    // make sourcebar green and make marker disappear
                                    makeSourceBarGreen();
                                    makeMarkerDisappear();

                                    // show the request cab UI
                                    animateRequestCabView(true);
                                }
                            } else {
                                sourceAddressAutocCompleteFragment.setText(null);
                                Toast.makeText(getActivity(), "Currently we only support rides in the twin cities", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sourceAddressAutocCompleteFragment.setText(null);
                        }
                    },200);
                    Toast.makeText(getActivity(),"Currently, pick up points can only be in premisis of nust",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(ratingDialogFragment!=null) {
            ratingDialogFragment.dismiss();
        }
    }

    void setOnPlaceSelectedListnerOnDestinationBar(){
        destinationAddressAutoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                GoogleReverseGeocodingApiWrapper googleReverseGeocodingApiWrapper = new GoogleReverseGeocodingApiWrapper(DIRECTION_API_KEY);
                googleReverseGeocodingApiWrapper.setLatLng(place.getLatLng()).requestAddress().setOnAddressRetrievedListener(new GoogleReverseGeocodingApiWrapper.OnAddressRetrievedListener() {
                    @Override
                    public void onAddressRetrieved(String resultingAddress) {
                        if(resultingAddress.contains("Islamabad")||resultingAddress.contains("Rawalpindi")) {
                            destinationBarCrossIB.setVisibility(View.VISIBLE);
                            if (sourceEntered && destinationEntered) {
                                animateCurrLocationButton = false;
                            } else {
                                destinationEntered = true;
                            }
                            if (!sourceEntered) {
                                // this is the destination location
                                destinationLatLng = place.getLatLng();

                                // make the destination bar show the complete address of the place
                                destinationAddressAutoCompleteFragment.setText(place.getAddress());
                                destinationAddress = place.getAddress().toString();

                                // animate the camera to the destination location
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), (float) 15.5));

                                // add a marker to the current location indicating that it is the destination
                                if (destinationMarker != null) {
                                    destinationMarker.remove();
                                }
                                destinationMarker = mMap.addMarker(new MarkerOptions().
                                        position(place.getLatLng()).
                                        icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)).
                                        title("Destination").
                                        visible(true)
                                );

                                destinationEnteredHighlightSourceBar();

                                /**
                                 * as the destination has been entered turn the marker
                                 * and button to set source mode
                                 */
                                turnMarkerIntoSetSourceMarker();
                            } else {
                                // this is the destination location
                                destinationLatLng = place.getLatLng();

                                // make the destination bar show the complete address of the place
                                destinationAddressAutoCompleteFragment.setText(place.getAddress());
                                destinationAddress = place.getAddress().toString();

                                // animate the camera to the destination location
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), (float) 15.5));

                                // add a marker to the current location indicating that it is the destination
                                if (destinationMarker != null) {
                                    destinationMarker.remove();
                                }
                                destinationMarker = mMap.addMarker(new MarkerOptions().
                                        position(place.getLatLng()).
                                        icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)).
                                        title("Destination").
                                        visible(true)
                                );

                                if (googleDirectionsApiWrapper.pathPresent()) {
                                    googleDirectionsApiWrapper.removePath();
                                }
                                // draw path between source and destination as both of them have been entered
                                googleDirectionsApiWrapper.from(sourceLatLng).to(destinationLatLng).retreiveDirections().setShow20Warning(true).setTextView(fairQuoteTV).setMap(mMap).drawPathOnMap();


                                makeDestinationBarBlue();
                                makeMarkerDisappear();

                                // show request cab UI
                                animateRequestCabView(true);
                            }
                        }else{
                            destinationAddressAutoCompleteFragment.setText(null);
                            Toast.makeText(getActivity(),"Currently we only support rides in twin cities",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    void setAllOnClickListeners() {
        setOnClickListenersOnCabTypeSelection();
        setOnClickListnerForMarkerButton();
        setOnCancelListenerForSourceCard();
        setOnCancelListenerForDestinationCard();
        setOnClickListenerOnRequestCabButton();
        setOnClickListenerOnCancelTripButton();
        setOCLOnHelpCallButton();
        setOCLOnHelpSMSButton();
        setOnClickListenerOnCurrLocationButton();
    }

    void setOCLOnHelpCallButton(){
        mHelpCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+ phoneNo ));

                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    //intent.setPackage("com.android.phone");
                    //intent.putExtra("simSlot", 0);
                    startActivity(intent);
                    showToast("Permission Granted");
                }else{
                    showToast("Permission Denied");
                }
            }
        });
    }

    void setOCLOnHelpSMSButton(){
        mHelpSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Help me i am in trouble. To view my Location at the time of this SMS click the following link "
                        +"http://maps.google.com/?q="+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()
                        + " . To view my current location click the following link "+
                        "http://cargar-survey987.rhcloud.com/FindYourPeer.html";

                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);
                showToast("SMS Sent");
            }
        });
    }

    public void showToast(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }


    void setOnClickListenerOnCancelTripButton(){
        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancellTrip();
            }
        });
    }

    void setTripEndedListener(){
        FirebaseDatabase.getInstance().getReference().child("EndTrip").child(uId).addValueEventListener(tripEndedListener);
    }

    public static class RatingDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View rootView = inflater.inflate(R.layout.dialog_rating, null);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(rootView);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            return builder.create();
        }
    }


    void setOnClickListenerOnRequestCabButton(){
        requestCabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Order").child(uId).setValue(new Order(
                        sourceAddress,
                        destinationAddress,
                        String.valueOf(sourceLatLng.latitude),
                        String.valueOf(sourceLatLng.longitude),
                        String.valueOf(destinationLatLng.latitude),
                        String.valueOf(destinationLatLng.longitude),
                        currentCabSelection,null,
                        Utils.extractRequiredKey(System.currentTimeMillis())
                ));

                mDatabase.child("State").child(uId).setValue(Constants.FINDING_CAB_STATE);
                // move the souce and destination selection card view up
                animateSourceDestinatonSelectionLayoutUp();

                // move the request button with it's text view and the cab selection card view down.
                animateCarTypeSelectionAndRequestLayoutDown();

                //show finding cab animation
                showFindingCabScreen();
                setOnAcceptCallListner();
            }
        });
    }

    void setOnAcceptCallListner(){
        FirebaseDatabase.getInstance().getReference().
                child("AcceptedOrders").
                child(uId).
                addValueEventListener(accepCallListener);
        Log.e("THis","yes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        //attach listener which server uses to notify if call has been cancelled and implement canceltripbutton logic here
        FirebaseDatabase.getInstance().getReference().child("NotifyDriverAbsence").child(user.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            boolean status= (boolean) dataSnapshot.getValue();

                            if(!status){
                                FirebaseDatabase.getInstance().getReference().child("NotifyDriverAbsence")
                                        .child(uId).setValue(null);
                                //cancellOrder
                               // Toast.makeText(getActivity().getApplicationContext(),"No driver Available",Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
                                final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.activity, R.style.MyAlertDialogStyle).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("We are sorry, there is no driver available in your area currently :(");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if(alertDialog!=null || alertDialog.isShowing()){
                                                alertDialog.dismiss();
                                                }
                                                cancellTrip();
                                            }
                                        });
                                alertDialog.show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void setOnCabArrivedListener(){
        FirebaseDatabase.getInstance().getReference().child("CabArrived").child(uId).addValueEventListener(cabArrivedListener);
    }

    void setOnTripStartedListener(){
        FirebaseDatabase.getInstance().getReference().child("StartTrip").child(uId).addValueEventListener(tripStartedListener);
    }

    void showPathBetweenCabAndPassenger(LatLng currentCabLatLng,LatLng passengerLatLng){
        if(!cabFoundScreenHadBeenShown) {
            googleDirectionsApiWrapper.
                    animateMapToShowFullPath(true).
                    from(currentCabLatLng).
                    to(passengerLatLng).
                    retreiveDirections().setShow20Warning(false).
                    setMap(mMap).
                    drawPathOnMap();
        }else{
            googleDirectionsApiWrapper.
                    animateMapToShowFullPath(false).
                    from(currentCabLatLng).
                    to(passengerLatLng).
                    retreiveDirections().setShow20Warning(false).
                    setMap(mMap).
                    drawPathOnMap();
        }
    }

    void animateToCabsPositon(LatLng currCabLatLng){
        if(cabMarker!=null){
            cabMarker.remove();
        }

        googleDirectionsApiWrapper.removePathThatIsVisible();

        cabMarker = mMap.addMarker(new MarkerOptions().
                title("Cab").
                position(currCabLatLng).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.cab_icon))
        );
    }

    int getPXfromDP(float dp){
        return (int)(getResources().getDisplayMetrics().density*dp);
    }

    void removeFindingCabScreen(){
        YoYo.Builder builder = YoYo.with(Techniques.FadeOut).duration(1000).interpolate(new AccelerateDecelerateInterpolator());
        builder.playOn(findingTaxiTV);

        Handler handlerForDelayingCabFoundAnimation = new Handler();
        handlerForDelayingCabFoundAnimation.postDelayed(new Runnable() {
            @Override
            public void run() {
                findingTaxiTV.setText("RIDER FOUND");
                findingTaxiTV.setTextColor(Color.parseColor("#F9BA32"));
                YoYo.with(Techniques.FadeIn).duration(300).interpolate(new AccelerateDecelerateInterpolator()).playOn(findingTaxiTV);

                Handler waitToReachTheCabAndTheShowThePath = new Handler();
                waitToReachTheCabAndTheShowThePath.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animations.makeVisible(etaOfCabTV);
                        YoYo.with(Techniques.SlideInDown).duration(500).playOn(etaOfCabTV);

                        Animations.makeVisible(driverCard,driverCardHolderFL);
                        Animations.playYoYoAnimOnMultipleViews(Techniques.SlideInUp,1000,driverCardHolderFL,driverCard);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    User driver = dataSnapshot.getValue(User.class);
                                    mNameOfDriverTV.setText(driver.name);
                                    FirebaseDatabase.getInstance().getReference().child("DriverRating").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                float rating = 0.0f;
                                                int numberOfRatings = 0;
                                                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                                    float rating1 = Float.valueOf(String.valueOf(dataSnapshot1.child("rating").getValue()));
                                                    rating+=rating1;
                                                    numberOfRatings++;
                                                }
                                                DecimalFormat df = new DecimalFormat("#.#");
                                                df.setRoundingMode(RoundingMode.CEILING);
                                                rating = Float.valueOf(df.format(rating/numberOfRatings));
                                                mDriverRatingTV.setText(""+rating);
                                                mDriverRatingBar.setRating(rating);
                                                if(numberOfRatings == 1) {
                                                    mDriverTotalRatingsTV.setText("One ride completed");
                                                }else {
                                                    mDriverTotalRatingsTV.setText(""+numberOfRatings+" rides completed");
                                                }
                                            }else{
                                                mDriverRatingTV.setText("n/a");
                                                mDriverRatingBar.setRating(0f);
                                                mDriverTotalRatingsTV.setText("No Rides Completed Yet");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    if(driver.profileImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.profileImageURL).resize(getPXfromDP(70f),getPXfromDP(70f)).centerCrop().into(driverProfilePicIV);
                                    }
                                    if(driver.bikeImageURL!=null) {
                                        Picasso.with(getActivity()).load(driver.bikeImageURL).into(vehicleImageIV);
                                    }
                                    if(driver.licsencePlateNumber!=null) {
                                        liscenseNoTV.setText(driver.licsencePlateNumber);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        Animations.makeVisible(cancelTripButton);
                        YoYo.with(Techniques.SlideInUp).duration(500).playOn(cancelTripButton);
                        Animations.remove(findingTaxiTV,findCabAnimView);
                    }
                },1000);

            }
        },350);

        YoYo.Builder hideView = builder.delay(1650);
        hideView.playOn(findingTaxiTV);
        hideView.playOn(findCabAnimView);
        hideView.playOn(blurView);
    }

    void animateSourceDestinatonSelectionLayoutUp(){
        YoYo.with(Techniques.SlideOutUp).duration(1000).playOn(sourceDestinationSelectionLayoutCV);
    }

    void showFindingCabScreen(){

        Animations.makeVisible(findCabAnimView,findingTaxiTV,blurView,cancelTripButton);
        // show loading animation
        Animations.playYoYoAnimOnMultipleViews(Techniques.FadeIn,1000,findCabAnimView,findingTaxiTV,blurView);
        YoYo.with(Techniques.SlideOutRight).duration(1000).playOn(currLocButton);
        YoYo.with(Techniques.SlideInUp).duration(500).playOn(cancelTripButton);
        LatLng sourceLatLng = sourceMarker.getPosition();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
    }


    void animateCarTypeSelectionAndRequestLayoutDown(){
        YoYo.with(Techniques.SlideOutDown).duration(1000).playOn(cabSelectionCV);
        YoYo.with(Techniques.SlideOutDown).duration(1000).playOn(notifySelectionToastTV);
        YoYo.with(Techniques.SlideOutDown).duration(1000).playOn(fairQuoteTV);
        YoYo.with(Techniques.SlideOutDown).duration(1000).playOn(requestCabButton);
    }


    void setOnCancelListenerForDestinationCard(){
        destinationBarCrossIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCurrLocationButton = true;
                destinationMarker.setVisible(false);
                destinationBarCrossIB.setVisibility(View.GONE);
                destinationAddressAutoCompleteFragment.setText("");
                destinationAddressAutoCompleteFragment.setHint("Enter Destination");
                destinationEntered = false;
                if(!sourceEntered){
                    elevateSourceBar();
                    makeDestinationBarBlank();
                    turnMarkerIntoSetSourceMarker();
                }else{
                    googleDirectionsApiWrapper.removePath();
                    sourceEnteredHighLightDestinationBar();
                    turnMarkerIntoSetDestinationMarker();
                    animateRequestCabView(false);
                }
            }
        });
    }

    void setOnClickListenersOnCabTypeSelection(){
        bikeSelectionIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCabSelection = Constants.SELECT_BIKE;
                googleDirectionsApiWrapper.setCabType(currentCabSelection);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bikeSelectionIB.setImageTintList(ColorStateList.valueOf(Color.parseColor("#007DC0")));
                }
                animateSelectionToast("YOU HAVE SELECTED A BIKE");
            }
        });
    }

    void setOnClickListnerForMarkerButton() {
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerOfMapLatLng = mMap.getCameraPosition().target;
                boolean isInBounds = true;
                if(!sourceEntered){
                    if(!isSourceLatLngWithinBounds(centerOfMapLatLng)){
                        isInBounds = false;
                    }
                }

                if(!sourceEntered){
                    if(isInBounds) {
                        mSourceProgressBar.setVisibility(View.VISIBLE);
                    }
                }else if(!destinationEntered){
                    mDestinationProgressBar.setVisibility(View.VISIBLE);
                }
                if(isInBounds) {
                    GoogleReverseGeocodingApiWrapper googleReverseGeocodingApiWrapper = new GoogleReverseGeocodingApiWrapper(DIRECTION_API_KEY);
                    googleReverseGeocodingApiWrapper.setLatLng(centerOfMapLatLng).
                            requestAddress().setOnAddressRetrievedListener(new GoogleReverseGeocodingApiWrapper.OnAddressRetrievedListener() {
                        @Override
                        public void onAddressRetrieved(String resultingAddress) {
                            mSourceProgressBar.setVisibility(View.GONE);
                            mDestinationProgressBar.setVisibility(View.GONE);
                            if (resultingAddress.contains("Islamabad") || resultingAddress.contains("Rawalpindi")) {
                                if (!sourceEntered && !destinationEntered) {
                                    /**
                                     * make the cross button on the source card visible
                                     * so that the user can cancel the source that they
                                     * have set if they want to enter a different one.
                                     */
                                    sourceBarCrossIB.setVisibility(View.VISIBLE);

                                    /**
                                     * put the current address in the source bar as the
                                     * user has selected this place as thier source
                                     */
                                    sourceAddressAutocCompleteFragment.setText(resultingAddress);
                                    sourceAddress = resultingAddress;

                                    /**
                                     * Adjust the view for entering destination mode
                                     */
                                    turnMarkerIntoSetDestinationMarker();
                                    sourceEnteredHighLightDestinationBar();

                                    // Add a marker a the source indicating that it is the source
                                    sourceMarker = mMap.addMarker(new MarkerOptions()
                                            .position(centerOfMapLatLng)
                                            .title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)));
                                    sourceMarker.setVisible(true);

                                    // the source has been entered
                                    sourceEntered = true;

                                    // this is the source LatLng
                                    sourceLatLng = centerOfMapLatLng;
                                } else if (sourceEntered && !destinationEntered) {
                                    /**
                                     * make the cross button on the destination bar
                                     * visible so that the user cancel the destination
                                     * if they want to enter a different one.
                                     */
                                    destinationBarCrossIB.setVisibility(View.VISIBLE);

                                    /**
                                     * Put the current place's address as the destination in
                                     * the destination bar as the user has selected this
                                     * place as thier destination
                                     */
                                    destinationAddressAutoCompleteFragment.setText(resultingAddress);
                                    destinationAddress = resultingAddress;

                                    /**
                                     * make marker and button disappear as both source and destination have
                                     * been entered.
                                     */
                                    makeMarkerDisappear();

                                    makeDestinationBarBlue();

                                    // this is the destination LatLng
                                    destinationLatLng = centerOfMapLatLng;

                                    // draw path between source and destination
                                    googleDirectionsApiWrapper.from(sourceLatLng).to(destinationLatLng).retreiveDirections().setShow20Warning(true).setTextView(fairQuoteTV).setMap(mMap).drawPathOnMap();

                                    /**
                                     * Add a marker at this place clearly indication that it is
                                     * the destination.
                                     */
                                    destinationMarker = mMap.addMarker(new MarkerOptions()
                                            .position(centerOfMapLatLng)
                                            .title("Destination").icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)));
                                    destinationMarker.setVisible(true);

                                    // the destination has been entered
                                    destinationEntered = true;

                                    /**
                                     * as both source and destination have been entered
                                     * make the request cab UI visible to the user.
                                     */

                                    animateRequestCabView(true);

                                } else if (!sourceEntered && destinationEntered) {
                                    /**
                                     * make the cross button on the source card visible
                                     * so that the user can cancel the source that they
                                     * have set if they want to enter a different one.
                                     */
                                    sourceBarCrossIB.setVisibility(View.VISIBLE);

                                    /**
                                     * put the current address in the source bar as the
                                     * user has selected this place as thier source
                                     */
                                    sourceAddressAutocCompleteFragment.setText(resultingAddress);
                                    sourceAddress = resultingAddress;

                                    /**
                                     * make marker and button disappear as both source and destination have
                                     * been entered.
                                     */
                                    makeMarkerDisappear();

                                    makeSourceBarGreen();

                                    // this is the source LatLng
                                    sourceLatLng = centerOfMapLatLng;

                                    // draw path between source and destination
                                    googleDirectionsApiWrapper.from(sourceLatLng).to(destinationLatLng).retreiveDirections().setShow20Warning(true).setTextView(fairQuoteTV).setMap(mMap).drawPathOnMap();

                                    // Add a marker a the source indicating that it is the source
                                    sourceMarker = mMap.addMarker(new MarkerOptions()
                                            .position(centerOfMapLatLng)
                                            .title("Source").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)));
                                    sourceMarker.setVisible(true);

                                    /**
                                     * as both source and destination have been entered
                                     * make the request cab UI visible to the user.
                                     */
                                    animateRequestCabView(true);

                                    // the source has been entered
                                    sourceEntered = true;


                                } else {
                                    /**
                                     * This condition is impossible as when
                                     * both the source and destination have
                                     * been entered the marker button cannot
                                     * be clicked
                                     */

                                }
                            } else {
                                Toast.makeText(getActivity(), "Currently, we only support rides in Twin Cities.", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }else{
                    Toast.makeText(getActivity(),"Currently, pick up points can only be in premisis of nust",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isSourceLatLngWithinBounds(LatLng centerOfMapLatLng) {
        return centerOfMapLatLng.latitude<=33.662271&&
                        centerOfMapLatLng.longitude>=72.970657
                        &&centerOfMapLatLng.latitude>=33.636449
                        &&centerOfMapLatLng.longitude<=73.047456;
    }

    void setOnCancelListenerForSourceCard(){
        sourceBarCrossIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCurrLocationButton = true;
                sourceBarCrossIB.setVisibility(View.GONE);
                sourceMarker.setVisible(false);
                sourceAddressAutocCompleteFragment.setText("");
                sourceAddressAutocCompleteFragment.setHint("Enter Source");
                sourceEntered = false;
                if(!destinationEntered){
                    elevateSourceBar();
                    makeDestinationBarBlank();
                    turnMarkerIntoSetSourceMarker();
                }else{
                    googleDirectionsApiWrapper.removePath();
                    destinationEnteredHighlightSourceBar();
                    turnMarkerIntoSetSourceMarker();
                    animateRequestCabView(false);
                }
            }
        });
    }

    void destinationEnteredHighlightSourceBar(){
        elevateSourceBar();
        makeDestinationBarBlue();
    }


    static void turnMarkerIntoSetSourceMarker(){
        markerButton.setVisibility(View.VISIBLE);
        markerAtCenterOfMapIV.setVisibility(View.VISIBLE);
        markerButtonTextView.setText("SET SOURCE");
        markerButton.setCardBackgroundColor(Color.parseColor("#ddF9BA32"));
        markerAtCenterOfMapIV.setImageResource(R.mipmap.marker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            markerAtCenterOfMapIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F9BA32")));
        }
    }


    static void makeDestinationBarBlank(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchDestinationCardView.setCardElevation(getPixelsFromDPs((float) 1));
        }
        searchDestinationCardView.setCardBackgroundColor(Color.parseColor("#aaffffff"));
    }

    static void elevateSourceBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchSouceCardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchSouceCardView.setCardElevation(getPixelsFromDPs((6)));
        }
    }

    void makeDestinationBarBlue(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchDestinationCardView.setCardElevation(getPixelsFromDPs((float) 1));
        }
        searchDestinationCardView.setCardBackgroundColor(Color.parseColor("#cc426E86"));
    }

    static void animateRequestCabView(boolean up){
        if(up){
            Animations.makeVisible(requestCabButton,fairQuoteTV);
            Animations.playYoYoAnimOnMultipleViews(Techniques.SlideInUp,1000,requestCabButton,fairQuoteTV);
            if(animateCurrLocationButton) {
                RelativeLayout.LayoutParams currLocationButtonParams = (RelativeLayout.LayoutParams) currLocButton.getLayoutParams();
                currLocationButtonParams.bottomMargin += requestCabButton.getHeight();
                currLocationButtonParams.bottomMargin += fairQuoteTV.getHeight();
            }
        }else{
            Animations.makeVisible(fairQuoteTV,requestCabButton);
            Animations.playYoYoAnimOnMultipleViews(Techniques.SlideOutDown,1000,fairQuoteTV,requestCabButton);
            RelativeLayout.LayoutParams currLocationButtonParams = (RelativeLayout.LayoutParams) currLocButton.getLayoutParams();
            currLocationButtonParams.bottomMargin-=requestCabButton.getHeight();
            currLocationButtonParams.bottomMargin-=fairQuoteTV.getHeight();
        }

    }

    void setOnClickListenerOnCurrLocationButton(){
        currLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastLocation!=null){
                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude()
                                    ),15.5f)
                    );
                }
            }
        });
    }

    void makeMarkerDisappear(){
        markerButton.setVisibility(View.INVISIBLE);
        markerAtCenterOfMapIV.setVisibility(View.INVISIBLE);
    }

    static void turnMarkerIntoSetDestinationMarker(){
        markerButton.setVisibility(View.VISIBLE);
        markerAtCenterOfMapIV.setVisibility(View.VISIBLE);
        markerButtonTextView.setText("SET DESTINATION");
        markerButton.setCardBackgroundColor(Color.parseColor("#dd426E86"));
        markerAtCenterOfMapIV.setImageResource(R.mipmap.flag);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            markerAtCenterOfMapIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#426E86")));
        }
    }

    static void sourceEnteredHighLightDestinationBar(){
        makeSourceBarGreen();
        elevateDestinationBar();
    }

    static void makeSourceBarGreen(){
        searchSouceCardView.setCardBackgroundColor(Color.parseColor("#ccF9BA32"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchSouceCardView.setCardElevation(getPixelsFromDPs(1));
        }
    }

    static void elevateDestinationBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchDestinationCardView.setCardElevation(getPixelsFromDPs((float) 6));
        }
        searchDestinationCardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
    }

    protected static int getPixelsFromDPs(float dps){
        Resources r = context.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    void animateSelectionToast(String message) {
        if(noOfAnimationsRunning>=1){
            noOfAnimationsRunning--;
        }

        notifySelectionToastTV.setText(message);
        Animations.makeVisible(notifySelectionToastTV);
        if(!movingDownAnimation) {
            YoYo.with(Techniques.SlideInUp).listen(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    movingUpAnimation = true;
                    noOfAnimationsRunning++;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    movingUpAnimation = false;
                }
            }).duration(500).playOn(notifySelectionToastTV);
        }
        if(noOfAnimationsRunning == 1) {
            YoYo.with(Techniques.SlideOutDown).listen(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    movingDownAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    movingDownAnimation = false;
                    if(noOfAnimationsRunning>0){
                        noOfAnimationsRunning--;
                    }
                }
            }).duration(500).delay(2500).playOn(notifySelectionToastTV);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLngBounds latLngBounds = new LatLngBounds(new LatLng(33.547869, 73.275766),new LatLng(33.925381, 72.742947));
        //mMap.setLatLngBoundsForCameraTarget(latLngBounds);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void defineAllListeners(){
        defineAcceptCallListener();
        defineAcceptDriverLocationListener();
        defineTripEndedListener();
        defineCabArrivedListener();
        defineTripStartedListener();
    }
    private ValueEventListener defineAcceptCallListener(){
        accepCallListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    driverIsArriving = true;
                    driverId = (String) dataSnapshot.getValue();

                    FirebaseDatabase.getInstance().getReference().child("DriverLocation").child(driverId).
                            addValueEventListener(acceptCallDriverLocationListener);
                    setOnCabArrivedListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return accepCallListener;
    }
    private ValueEventListener defineAcceptDriverLocationListener() {
        acceptCallDriverLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(driverIsArriving) {
                    if (dataSnapshot.exists()) {
                        Log.v("DriverLocation:::: ", "datasnapshot exists.");

                        if (!cabFoundScreenHadBeenShown) {
                            removeFindingCabScreen();
                            cabFoundScreenHadBeenShown = true;
                        }
                        HashMap cabLatLngHashMap = (HashMap) dataSnapshot.getValue();
                        if(cabLatLngHashMap!=null) {
                            if (cabLatLngHashMap.get("Lat") != null && cabLatLngHashMap.get("Long") != null) {
                                double cabLat = Double.valueOf((String) cabLatLngHashMap.get("Lat"));
                                double cabLong = Double.valueOf((String) cabLatLngHashMap.get("Long"));
                                cabLatLng = new LatLng(cabLat, cabLong);
                                animateToCabsPositon(cabLatLng);
                                if (cabLatLng != null) {
                                    Log.v("CabLatLng:", "" + cabLatLng.latitude + " " + cabLatLng.longitude);
                                } else {
                                    Log.v("CabLatLng:", " NULL");
                                }
                                if (!sourceLatLngRetrieved) {
                                    FirebaseDatabase.getInstance().getReference().child("Order").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Order order = dataSnapshot.getValue(Order.class);
                                                sourceLatLng = new LatLng(
                                                        Double.valueOf(order.sourceLat),
                                                        Double.valueOf(order.sourceLong)
                                                );
                                                sourceMarker = mMap.addMarker(
                                                        new MarkerOptions().
                                                                icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)).
                                                                position(sourceLatLng)
                                                );
                                                sourceMarker.setVisible(true);
                                                showPathBetweenCabAndPassenger(sourceLatLng, cabLatLng);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child("Order").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Order order = dataSnapshot.getValue(Order.class);
                                                sourceLatLng = new LatLng(
                                                        Double.valueOf(order.sourceLat),
                                                        Double.valueOf(order.sourceLong)
                                                );

                                                destinationLatLng = new LatLng(
                                                        Double.valueOf(order.destLat),
                                                        Double.valueOf(order.destLong)
                                                );
                                                sourceMarker = mMap.addMarker(
                                                        new MarkerOptions().
                                                                icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_green)).
                                                                position(sourceLatLng)
                                                );
                                                destinationMarker = mMap.addMarker(
                                                        new MarkerOptions().
                                                                icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)).
                                                                position(destinationLatLng)
                                                );
                                                sourceMarker.setVisible(true);
                                                destinationMarker.setVisible(true);
                                                showPathBetweenCabAndPassenger(sourceLatLng, cabLatLng);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    showPathBetweenCabAndPassenger(sourceLatLng, cabLatLng);
                                }
                            }
                        }
                    } else {
                        Log.v("DriverLocation:::: ", "datasnapshot doesn't exist.");
                    }
                } else if( inTrip ){
                    HashMap cabLatLngHashMap = (HashMap) dataSnapshot.getValue();
                    if(cabLatLngHashMap!=null) {
                        if(cabLatLngHashMap.get("Lat")!=null&&cabLatLngHashMap.get("Long")!=null) {
                            double cabLat = Double.valueOf((String) cabLatLngHashMap.get("Lat"));
                            double cabLong = Double.valueOf((String) cabLatLngHashMap.get("Long"));
                            cabLatLng = new LatLng(cabLat, cabLong);

                            mTripPathData = new ArrayList<LatLng>();
                            FirebaseDatabase.getInstance().getReference().child("TripPath").child(uId + driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.hasChildren()) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                DriverLocation location = snapshot.getValue(DriverLocation.class);
                                                LatLng latLng = new LatLng(
                                                        Double.valueOf(location.Lat),
                                                        Double.valueOf(location.Long)
                                                );
                                                mTripPathData.add(latLng);
                                            }
                                            final int distanceCoveredInMeters = Utils.getDistanceInMetersFromLatLngData(mTripPathData);
                                            FirebaseDatabase.getInstance().getReference().child("StartTripTime").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    long startTime = (Long) dataSnapshot.getValue();
                                                    long currTime = System.currentTimeMillis();
                                                    long timePassed = currTime - startTime;
                                                    long timePassedSeconds = timePassed / 1000;
                                                    fairCalculation.setCabType(currentCabSelection);
                                                    int fairEstimate = fairCalculation.getFairEstimate(distanceCoveredInMeters, Integer.valueOf(Long.toString(timePassedSeconds)));
                                                    int distanceInKm = distanceCoveredInMeters / 1000;
                                                    int timeInMinutes = Integer.valueOf(Long.toString(timePassedSeconds)) / 60;
                                                    if (activateFairEstimation) {
                                                        etaOfCabTV.setText(
                                                                "" + distanceInKm + "km has been covered in " + timeInMinutes + "minute, "
                                                                        + "the current fair estimate is " + fairEstimate + "PKR"
                                                        );
                                                    }

                                                    googleDirectionsApiWrapper.removePath();
                                                    googleDirectionsApiWrapper.animateMapToShowFullPath(false).
                                                            setEtaTV(null).
                                                            from(cabLatLng).
                                                            to(destinationLatLng).
                                                            retreiveDirections().
                                                            setMap(mMap).
                                                            drawPathOnMap();

                                                    cabMarker.setPosition(cabLatLng);
                                                    cabMarker.hideInfoWindow();

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return  accepCallListener;
    }
    private ValueEventListener defineTripEndedListener(){
        tripEndedListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    FirebaseDatabase.getInstance().getReference().child("EndTrip").child(uId).setValue(null);
                    removeEventListeners();
                    showRatingDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return tripEndedListener;
    }
    private ValueEventListener defineCabArrivedListener(){
        cabArrivedListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("CabArrived", " Inside on cab arrived listener");
                if(dataSnapshot.exists()) {
                    final Order order = dataSnapshot.getValue(Order.class);
                    FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                driverId = (String) dataSnapshot.getValue();
                                if(driverId!=null) {
                                    currentAppState = Constants.CAB_ARRIVED_STATE;
                                    driverIsArriving = false;
                                    FirebaseDatabase.getInstance().getReference().child("DriverLocation").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                driverIsArriving = false;
                                                HashMap cabLatLngHashMap = (HashMap) dataSnapshot.getValue();
                                                if(cabLatLngHashMap!=null) {
                                                    if(cabLatLngHashMap.get("Lat")!=null&&cabLatLngHashMap.get("Long")!=null) {
                                                        double cabLat = Double.valueOf((String) cabLatLngHashMap.get("Lat"));
                                                        double cabLong = Double.valueOf((String) cabLatLngHashMap.get("Long"));
                                                        if (cabMarker == null) {
                                                            Log.v("CabMarker", " cab  marker was null");
                                                            cabMarker = mMap.addMarker(
                                                                    new MarkerOptions().
                                                                            icon(BitmapDescriptorFactory.fromResource(R.mipmap.cab_icon)).
                                                                            position(new LatLng(cabLat, cabLong))
                                                            );
                                                        }
                                                        if (destinationMarker == null) {
                                                            destinationMarker = mMap.addMarker(
                                                                    new MarkerOptions().
                                                                            icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag_blue)).
                                                                            position(new LatLng(
                                                                                    Double.valueOf(order.destLat),
                                                                                    Double.valueOf(order.destLong)
                                                                            ))
                                                            );

                                                        } else {
                                                            googleDirectionsApiWrapper.removePath();
                                                            destinationMarker.setPosition(new LatLng(
                                                                    Double.valueOf(order.destLat),
                                                                    Double.valueOf(order.destLong)
                                                            ));
                                                        }
                                                        cabMarker.setPosition(new LatLng(cabLat, cabLong));


                                                        googleDirectionsApiWrapper.animateMapToShowFullPath(true).
                                                                from(new LatLng(cabLat, cabLong)).
                                                                to(new LatLng(Double.valueOf(order.destLat), Double.valueOf(order.destLong))).
                                                                retreiveDirections().
                                                                setMap(mMap).
                                                                drawPathOnMap();

                                                        cabMarker.setTitle("Your Rider has arrived");
                                                        cabMarker.showInfoWindow();
                                                        Animations.remove(etaOfCabTV, cancelTripButton);
                                                        Animations.makeVisible(driverCard, driverCardHolderFL);
                                                        setOnTripStartedListener();

                                                        tripCodeCV.setVisibility(View.VISIBLE);
                                                        tripCodeTV.setText(order.tripCode);

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    Log.v("CabArrived", " data snapshot doesn't exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return cabArrivedListener;
    }
    private ValueEventListener defineTripStartedListener(){
        tripStartedListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    inTrip = true;
                    Animations.makeVisible(mHelpCallButton,mHelpSMSButton);
                    Animations.playYoYoAnimOnMultipleViews(Techniques.FadeIn,1000,mHelpSMSButton,mHelpCallButton);
                    Order order = dataSnapshot.getValue(Order.class);
                    Animations.playYoYoAnimOnMultipleViews(Techniques.SlideOutDown,1000,driverCard,driverCardHolderFL);
                    Animations.makeVisible(etaOfCabTV);
                    YoYo.with(Techniques.SlideInDown).duration(1000).playOn(etaOfCabTV);
                    etaOfCabTV.setText("Have a safe journey.");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activateFairEstimation = true;
                        }
                    },3000);

                    tripCodeCV.setVisibility(View.GONE);
                }else{
                    Log.e("StartTrip", " datasnapshot doesn't exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return tripStartedListener;
    }
    private void removeEventListeners(){
        if(driverId!=null) {
            FirebaseDatabase.getInstance().getReference().child("DriverLocation").child(driverId).
                    removeEventListener(acceptCallDriverLocationListener);
        }
        if(uId!=null) {
            Log.e("dick ptr exception","trip end");
            FirebaseDatabase.getInstance().getReference().child("CabArrived").child(uId).
                    removeEventListener(cabArrivedListener);
            FirebaseDatabase.getInstance().getReference().child("StartTrip").child(uId).
                    removeEventListener(tripStartedListener);
            FirebaseDatabase.getInstance().getReference().
                    child("AcceptedOrders").
                    child(uId).
                    removeEventListener(accepCallListener);
            FirebaseDatabase.getInstance().getReference().child("EndTrip").child(uId).
                    removeEventListener(tripEndedListener);
        }


    }
    public void cancellTrip(){
        if(uId!=null) {
            FirebaseDatabase.getInstance().getReference().child("AcceptedOrders").child(uId).setValue(null);
        }

        if(driverId!=null) {
            FirebaseDatabase.getInstance().getReference().
                    child("CanceledTripsServer").
                    child(uId).
                    setValue(driverId);
            FirebaseDatabase.getInstance().getReference().
                    child("CanceledTripsDriver").
                    child(uId).
                    setValue(driverId);
            FirebaseDatabase.getInstance().getReference().child("DriverState").
                    child(driverId).
                    setValue(0);
        }else{
            FirebaseDatabase.getInstance().getReference().
                    child("CanceledTripsServer").
                    child(uId).
                    setValue("null");
            FirebaseDatabase.getInstance().getReference().
                    child("CanceledTripsDriver").
                    child(uId).
                    setValue("null");

        }
        FirebaseDatabase.getInstance().getReference().
                child("State").child(uId).setValue(Constants.SET_SOURCE_STATE);
        //1)find driver state 2)if app open don't do anything else send notification
        if(driverId!=null) {
            FirebaseDatabase.getInstance().getReference().child("AppStatus").child(driverId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int status = Integer.valueOf(String.valueOf((long) dataSnapshot.getValue()));
                                if (status == 0) {
                                    FirebaseDatabase.getInstance().getReference().child("MapUIDtoInstanceID").child(driverId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        instanceId = dataSnapshot.getValue().toString();
                                                        SendNotif send = new SendNotif(context,SendNotif.PASSENGER_TO_DRIVER, SendNotif.CAB_ARRIVED_NOTIF,driverId);
                                                        send.setTitle("The Trip has been Cancelled");
                                                        send.send();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        Intent intent = new Intent(getActivity(),MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        removeEventListeners();
        getActivity().finish();
        startActivity(intent);
    }
}
