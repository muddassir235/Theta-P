package com.thetacab.hp.cargar.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.thetacab.hp.cargar.Constants;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.User;
import com.thetacab.hp.cargar.Utils;
import com.thetacab.hp.cargar.storage.ImageGetterSetter;
import com.thetacab.hp.cargar.storage.ImagePicker;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getName()+": ";

    LinearLayout mFieldsLayout;
    ProgressBar mLoadingAnim;

    ImageButton mCloseButton;

    ImageView mProfileImage;
    TextView mProfileImageNameTV;
    ImageButton mPickProfileImageFromCamera;
    ImageButton mPickProfileImageFromGallery;

    ProgressDialog progressDialog;

    Uri mProfileImageURI;
    boolean mProfileImageEdited;
    Uri mBikeImageURL;
    boolean mBikeImageEdited;

    RelativeLayout mBikeImageRL;
    ImageView mBikeImage;
    TextView mBikeImageNameTV;
    ImageButton mPickBikeImageFromCamera;
    ImageButton mPickBikeImageFromGallery;

    FrameLayout mCellNumberFL;
    AutoCompleteTextView mPhone;
    TextView mPhoneNoValidationTV;

    FrameLayout mCNICFL;
    EditText mCNICET;
    TextView mCNICValidationTV;

    FrameLayout mHelpNumberFL;
    EditText mHelpNumberET;
    TextView mHelpNumberValidationTV;

    LinearLayout mDivider1;

    FrameLayout mCityFL;
    EditText mCity;

    FrameLayout mAddressFL;
    EditText mAddress;

    LinearLayout mDivider2;

    FrameLayout mDrivingLiscenseNumberFL;
    EditText mDrivingLiscenseNumber;

    FrameLayout mBikeLiscenseNumberFL;
    EditText mBikeLiscense;

    Button mMakeEditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        bindViews();

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImagePicker.object = null;

        mProfileImageEdited = false;
        mBikeImageEdited = false;

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        getExternalStoragePermissions();

        if(user!=null) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        final User currentUser = dataSnapshot.getValue(User.class);

                        mLoadingAnim.setVisibility(View.GONE);
                        mFieldsLayout.setVisibility(View.VISIBLE);

                        mPhone.setText(currentUser.phone);
                        mCNICET.setText(currentUser.cNIC);
                        mHelpNumberET.setText(currentUser.helpNumber);
                        mCity.setText(currentUser.city);
                        mAddress.setText(currentUser.address);
                        mDrivingLiscenseNumber.setText(currentUser.licsenceNumber);
                        mBikeLiscense.setText(currentUser.licsencePlateNumber);

                        mPhone.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                int phoneNumberStatus = Utils.phoneNumberIsValid(mPhone.getText().toString());
                                if(phoneNumberStatus == Utils.EMPTY){
                                    mPhoneNoValidationTV.setVisibility(View.GONE);
                                }else if(phoneNumberStatus == Utils.INVALID) {
                                    mPhoneNoValidationTV.setVisibility(View.VISIBLE);
                                    mPhoneNoValidationTV.setText("Phone number is not valid :(");
                                    mPhoneNoValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                                }else if(phoneNumberStatus == Utils.VALID){
                                    mPhoneNoValidationTV.setVisibility(View.VISIBLE);
                                    mPhoneNoValidationTV.setText("Phone number is valid :)");
                                    mPhoneNoValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        mHelpNumberET.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                int phoneNumberStatus = Utils.phoneNumberIsValid(mHelpNumberET.getText().toString());
                                if(phoneNumberStatus == Utils.EMPTY){
                                    mHelpNumberValidationTV.setVisibility(View.GONE);
                                }else if(phoneNumberStatus == Utils.INVALID) {
                                    mHelpNumberValidationTV.setVisibility(View.VISIBLE);
                                    mHelpNumberValidationTV.setText("Phone number is not valid :(");
                                    mHelpNumberValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                                }else if(phoneNumberStatus == Utils.VALID){
                                    mHelpNumberValidationTV.setVisibility(View.VISIBLE);
                                    mHelpNumberValidationTV.setText("Phone number is valid :)");
                                    mHelpNumberValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        mCNICET.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                int cnicStatus = Utils.isCNICValid(mCNICET.getText().toString());
                                if(cnicStatus == Utils.EMPTY){
                                    mCNICValidationTV.setVisibility(View.GONE);
                                }else if(cnicStatus == Utils.INVALID) {
                                    mCNICValidationTV.setVisibility(View.VISIBLE);
                                    mCNICValidationTV.setText("CNIC number is not valid :(");
                                    mCNICValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                                }else if(cnicStatus == Utils.VALID){
                                    mCNICValidationTV.setVisibility(View.VISIBLE);
                                    mCNICValidationTV.setText("CNIC number is valid :-)");
                                    mCNICValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        currentUser.type = "customer";

                        if(currentUser.type.equals("driver")){
                            mHelpNumberFL.setVisibility(View.GONE);

                            if(currentUser.bikeImageURL!=null){
                                mBikeImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                                Picasso.with(getApplicationContext()).load(currentUser.bikeImageURL).resize(getPXfromDP(120f),getPXfromDP(120f)).into(mBikeImage);
                                mBikeImageNameTV.setText("../BikeImage/1.jpg");
                            }else {
                                mBikeImageNameTV.setText("No Image Uploaded");
                            }
                        }else if(currentUser.type.equals("customer")){
                            mBikeImageRL.setVisibility(View.GONE);
                            mCityFL.setVisibility(View.GONE);
                            mDivider1.setVisibility(View.GONE);
                            mAddressFL.setVisibility(View.GONE);
                            mDivider2.setVisibility(View.GONE);
                            mDrivingLiscenseNumberFL.setVisibility(View.GONE);
                            mBikeLiscenseNumberFL.setVisibility(View.GONE);
                        }

                        if(currentUser.profileImageURL!=null) {
                            mProfileImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                            Picasso.with(getApplicationContext()).load(currentUser.profileImageURL).resize(getPXfromDP(120f),getPXfromDP(120f)).into(mProfileImage);
                            mProfileImageNameTV.setText("../ProfileImage/1.jpg");
                        }else{
                            mProfileImageNameTV.setText("No Image Uploaded");
                        }

                        mPickProfileImageFromCamera.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImagePicker.getInstance(EditProfileActivity.this).takePictureFromCamera().setImageListener(new ImagePicker.LocalImageListener() {
                                    @Override
                                    public void onSuccess(Uri uri, int mediumType) {
                                        Log.v(TAG, uri.toString());
                                        mProfileImageURI = uri;
                                        mProfileImageEdited = true;

                                        Log.v(TAG, " uri: "+ mProfileImageURI.toString());

                                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mProfileImage);
                                        mProfileImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                                        if (mProfileImageURI.getScheme().equals("file")) {
                                            mProfileImageNameTV.setText(mProfileImageURI.getLastPathSegment());
                                        } else {
                                            Cursor cursor = null;
                                            try {
                                                cursor = getContentResolver().query(mProfileImageURI, new String[]{
                                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                                }, null, null, null);

                                                if (cursor != null && cursor.moveToFirst()) {
                                                    mProfileImageNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                                                }
                                            } finally {

                                                if (cursor != null) {
                                                    cursor.close();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });

                        mPickProfileImageFromGallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImagePicker.getInstance(EditProfileActivity.this).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
                                    @Override
                                    public void onSuccess(Uri uri, int mediumType) {
                                        Log.v(TAG, uri.toString());
                                        mProfileImageURI = uri;
                                        mProfileImageEdited = true;

                                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mProfileImage);
                                        mProfileImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                                        if (mProfileImageURI.getScheme().equals("file")) {
                                            mProfileImageNameTV.setText(mProfileImageURI.getLastPathSegment());
                                        } else {
                                            Cursor cursor = null;
                                            try {
                                                cursor = getContentResolver().query(mProfileImageURI, new String[]{
                                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                                }, null, null, null);

                                                if (cursor != null && cursor.moveToFirst()) {
                                                    mProfileImageNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                                                }
                                            } finally {

                                                if (cursor != null) {
                                                    cursor.close();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });

                        mPickBikeImageFromCamera.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImagePicker.getInstance(EditProfileActivity.this).takePictureFromCamera().setImageListener(new ImagePicker.LocalImageListener() {
                                    @Override
                                    public void onSuccess(Uri uri, int mediumType) {
                                        Log.v(TAG, uri.toString());
                                        mBikeImageURL = uri;
                                        mBikeImageEdited = true;

                                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mBikeImage);
                                        mBikeImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                                        if (mBikeImageURL.getScheme().equals("file")) {
                                            mBikeImageNameTV.setText(mBikeImageURL.getLastPathSegment());
                                        } else {
                                            Cursor cursor = null;
                                            try {
                                                cursor = getContentResolver().query(mBikeImageURL, new String[]{
                                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                                }, null, null, null);

                                                if (cursor != null && cursor.moveToFirst()) {
                                                    mBikeImageNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                                                }
                                            } finally {

                                                if (cursor != null) {
                                                    cursor.close();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });

                        mPickBikeImageFromGallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImagePicker.getInstance(EditProfileActivity.this).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
                                    @Override
                                    public void onSuccess(Uri uri, int mediumType) {
                                        Log.v(TAG, uri.toString());
                                        mBikeImageURL = uri;
                                        mBikeImageEdited = true;

                                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mBikeImage);
                                        mBikeImage.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                                        if (mBikeImageURL.getScheme().equals("file")) {
                                            mBikeImageNameTV.setText(mBikeImageURL.getLastPathSegment());
                                        } else {
                                            Cursor cursor = null;
                                            try {
                                                cursor = getContentResolver().query(mBikeImageURL, new String[]{
                                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                                }, null, null, null);

                                                if (cursor != null && cursor.moveToFirst()) {
                                                    mBikeImageNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                                                }
                                            } finally {

                                                if (cursor != null) {
                                                    cursor.close();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });


                        mMakeEditsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                makeEdits(user,currentUser);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    void makeEdits(final FirebaseUser user, final User currentUser){
        boolean allDataEntered = true;
        View focusView = null;

        if(currentUser.type.equals("driver")){
            if(TextUtils.isEmpty(mBikeLiscense.getText().toString())){
                allDataEntered = false;
                mBikeLiscense.setError("Please enter your bike's liscense plate number");
                focusView = mBikeLiscense;
            }else {
                currentUser.licsencePlateNumber = mBikeLiscense.getText().toString();
            }

            if(TextUtils.isEmpty(mDrivingLiscenseNumber.getText().toString())){
                allDataEntered = false;
                mDrivingLiscenseNumber.setError("Please enter your driver liscense number");
                focusView = mDrivingLiscenseNumber;
            }else {
                currentUser.licsenceNumber = mDrivingLiscenseNumber.getText().toString();
            }

            if(TextUtils.isEmpty(mAddress.getText().toString())){
                allDataEntered = false;
                mAddress.setError("Please enter your address");
                focusView = mAddress;
            }else {
                currentUser.address = mAddress.getText().toString();
            }

            if(TextUtils.isEmpty(mCity.getText().toString())){
                allDataEntered = false;
                mCity.setError("Please enter your city");
                focusView = mCity;
            }else {
                currentUser.city = mCity.getText().toString();
            }

            if(TextUtils.isEmpty(mCNICET.getText().toString())){
                allDataEntered = false;
                mCNICET.setError("Please enter a valid CNIC number");
                focusView = mCNICET;
            }else {
                currentUser.cNIC = mCNICET.getText().toString();
            }

            if(TextUtils.isEmpty(mPhone.getText().toString())){
                allDataEntered = false;
                mPhone.setError("Please enter a valid phone number");
                focusView = mPhone;
            }else {
                currentUser.phone = mPhone.getText().toString();
            }


        }else if(currentUser.type.equals("customer")){
            if(TextUtils.isEmpty(mHelpNumberET.getText().toString()) || Utils.phoneNumberIsValid(mHelpNumberET.getText().toString())!=Utils.VALID){
                allDataEntered = false;
                mHelpNumberET.setError("Please enter a valid phone number");
                focusView = mHelpNumberET;
            }else {
                currentUser.helpNumber = mHelpNumberET.getText().toString();
            }

            if(TextUtils.isEmpty(mCNICET.getText().toString()) || Utils.isCNICValid(mCNICET.getText().toString())!=Utils.VALID){
                allDataEntered = false;
                mCNICET.setError("Please enter a valid CNIC number");
                focusView = mCNICET;
            }else {
                currentUser.cNIC = mCNICET.getText().toString();
            }

            if(TextUtils.isEmpty(mPhone.getText().toString()) || Utils.phoneNumberIsValid(mPhone.getText().toString()) != Utils.VALID){
                allDataEntered = false;
                mPhone.setError("Please enter a valid phone number");
                focusView = mPhone;
            }else {
                currentUser.phone = mPhone.getText().toString();
            }
        }

        if(allDataEntered) {
            if (mProfileImageEdited && mBikeImageEdited) {
                editBothProfileAndBikeImage(user, currentUser);
            } else if (mProfileImageEdited) {
                editProfileImage(user, currentUser);
            } else if (mBikeImageEdited) {
                editBikeImage(user, currentUser);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).setValue(currentUser);
                Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }else {
            if(focusView!=null){
                focusView.requestFocus();
            }
        }
    }

    void editProfileImage(final FirebaseUser user,final User currentUser){
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Entering Images and Data");
        progressDialog.show();

        ImageGetterSetter.getInstance(EditProfileActivity.this).
                setImage(mProfileImageURI, Constants.DRIVER_PROFILE_PIC,
                        Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                        user.getUid()
                ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
            @Override
            public void onSuccess(final Uri uri) {
                currentUser.profileImageURL = uri.toString();
                Log.v(TAG, " profile Image Url: " +uri.toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).setValue(currentUser);
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    void editBothProfileAndBikeImage(final FirebaseUser user,final User currentUser){
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Entering Images and Data");
        progressDialog.show();

        ImageGetterSetter.getInstance(EditProfileActivity.this).
                setImage(mProfileImageURI, Constants.DRIVER_PROFILE_PIC,
                        Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                        user.getUid()
                ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
            @Override
            public void onSuccess(final Uri profileURI) {
                ImageGetterSetter.getInstance(EditProfileActivity.this).
                        setImage(mBikeImageURL, Constants.DRIVER_BIKE_IMAGE,
                                Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                                user.getUid()
                        ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                    @Override
                    public void onSuccess(final Uri bikeURI) {
                        currentUser.profileImageURL = profileURI.toString();
                        currentUser.bikeImageURL = bikeURI.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).setValue(currentUser);
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
                        finish();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    void editBikeImage(final FirebaseUser user,final User currentUser){
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Entering Images and Data");
        progressDialog.show();

        ImageGetterSetter.getInstance(EditProfileActivity.this).
                setImage(mBikeImageURL, Constants.DRIVER_BIKE_IMAGE,
                        Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                        user.getUid()
                ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
            @Override
            public void onSuccess(final Uri uri) {
                currentUser.bikeImageURL = uri.toString();
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).setValue(currentUser);
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.getInstance(this).onActivityResult(requestCode,resultCode,data);
    }

    int getPXfromDP(float dp){
        return (int)(getResources().getDisplayMetrics().density*dp);
    }

    void bindViews(){
        mProfileImage = (ImageView) findViewById(R.id.profile_picture_image_view);
        mProfileImageNameTV = (TextView) findViewById(R.id.profile_image_name_text_view);
        mPickProfileImageFromCamera = (ImageButton) findViewById(R.id.add_profile_image_through_camera_button);
        mPickProfileImageFromGallery = (ImageButton) findViewById(R.id.add_profile_image_through_gallery_button);

        mBikeImageRL = (RelativeLayout) findViewById(R.id.bike_image_layout);
        mBikeImage = (ImageView) findViewById(R.id.bike_picture_image_view);
        mBikeImageNameTV = (TextView) findViewById(R.id.bike_image_name_text_view);
        mPickBikeImageFromCamera = (ImageButton) findViewById(R.id.add_bike_image_through_camera_button);
        mPickBikeImageFromGallery = (ImageButton) findViewById(R.id.add_bike_image_through_gallery_button);

        mCellNumberFL = (FrameLayout) findViewById(R.id.cell_number_frame_layout);
        mPhone = (AutoCompleteTextView) findViewById(R.id.phone);
        mPhoneNoValidationTV = (TextView) findViewById(R.id.phone_number_validation_text_view);

        mCNICFL = (FrameLayout) findViewById(R.id.cnic_frame_layout);
        mCNICET = (EditText) findViewById(R.id.cnic);
        mCNICValidationTV = (TextView) findViewById(R.id.cnic_validation_text_view);

        mHelpNumberFL = (FrameLayout) findViewById(R.id.help_number_frame_layout);
        mHelpNumberET = (EditText) findViewById(R.id.help_number);
        mHelpNumberValidationTV = (TextView) findViewById(R.id.help_number_validation_text_view);

        mDivider1 = (LinearLayout) findViewById(R.id.divider1);

        mCityFL = (FrameLayout) findViewById(R.id.city_frame_layout);
        mCity = (EditText) findViewById(R.id.city);

        mAddressFL = (FrameLayout) findViewById(R.id.address_frame_layout);
        mAddress = (EditText) findViewById(R.id.address);

        mDivider2 = (LinearLayout) findViewById(R.id.divider2);

        mDrivingLiscenseNumberFL = (FrameLayout) findViewById(R.id.driver_license_frame_layout);
        mDrivingLiscenseNumber = (EditText) findViewById(R.id.liscence_number);

        mBikeLiscenseNumberFL = (FrameLayout) findViewById(R.id.bike_plate_frame_layout);
        mBikeLiscense = (EditText) findViewById(R.id.liscence_plate_number);

        mMakeEditsButton = (Button) findViewById(R.id.make_edits_button);

        mFieldsLayout = (LinearLayout) findViewById(R.id.fields_layout);
        mLoadingAnim = (ProgressBar) findViewById(R.id.loading_data);
        mCloseButton = (ImageButton) findViewById(R.id.close_button);
    }

    void getExternalStoragePermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.MY_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_BLUETOOTH is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.v(TAG, " code callback: "+requestCode);
        switch (requestCode) {
            case Constants.MY_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.

                } else {
                    mPickProfileImageFromCamera.setClickable(false);
                    mPickProfileImageFromCamera.setOnClickListener(null);
                    mPickProfileImageFromCamera.setFocusable(false);
                    mPickProfileImageFromCamera.setVisibility(View.INVISIBLE);

                    Toast.makeText(getApplicationContext(),"To take image from camera external storage permission required",Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
