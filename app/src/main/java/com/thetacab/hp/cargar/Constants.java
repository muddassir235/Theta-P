package com.thetacab.hp.cargar;

/**
 * Created by hp on 7/14/2016.
 */
public class Constants {

    public static final int DONT_KNOW_USER_TYPE = 3;

    public static final int TYPE_DRIVER = 0;

    public static final int TYPE_CUSTOMER = 1;

    public static final int SELECT_BIKE = 0;

    public static final int SELECT_SEDAN = 1;

    public static final int SELECT_SUV = 2;

    public static final int CANCEL_TRIP=6;

    public static final int SET_SOURCE_STATE = 1;

    public static final int FINDING_CAB_STATE = 2;

    public static final int CAB_ARRIVING_STATE = 3;

    public static final int CAB_ARRIVED_STATE = 4;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    public static final int IN_TRIP_STATE = 5;

    public static final int TRIP_ENDED_STATE = 6;

    public static final int D_IDLE_STATE=0;

    public static final int D_CALL_ACCEPTED=1;

    public static final int D_CAB_ARRIVED=2;

    public static final int D_TRIP_START=3;

    public static final int DRIVER_NOT_VERIFIED = 0;

    public static final int DRIVER_VERIFICATION_UNDER_PROCESS = 1;

    public static final int DRIVER_VERIFIED = 2;

    public static final int FIREBASE_OPERATION_SELECT_PICTURE = 0;

    public static final int TAKE_IMAGE_FROM_GALLERY = 0;

    public static final int TAKE_IMAGE_FROM_CAMERA = 1;

    public static final String DRIVER_PROFILE_PIC="ProfilePic/";

    public static final String FIREBASE_STORAGE_URL="gs://project-2162072630908720035.appspot.com/";

    public static final String FIREBASE_STORAGE_DRIVER_REFERENCE="Driver/";

    public static final String DRIVER_BIKE_IMAGE = "BikeImage/";

    public static final int MY_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
}
