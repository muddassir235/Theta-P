package com.thetacab.hp.cargar.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.TextTrackStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.thetacab.hp.cargar.Constants;
import com.thetacab.hp.cargar.R;
import com.thetacab.hp.cargar.User;
import com.thetacab.hp.cargar.Utils;
import com.thetacab.hp.cargar.storage.ImageGetterSetter;
import com.thetacab.hp.cargar.storage.ImagePicker;

public class UserDataEntryActivity extends AppCompatActivity {

    public static final int RESULT_OK = 0;
    private static final String TAG = "UserDataEntryActivity: " ;

    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 011;

    Button mEnterDataButton;
    AutoCompleteTextView mPhoneTV;
    RadioGroup mGenderChoice;
    RadioButton mMaleRadioButton;
    RadioButton mFemaleRadioButton;
    RadioGroup mTypeChoice;
    RadioButton mDriverRadioButton;
    RadioButton mCustomerRadioButton;

    EditText mDriverCity;
    EditText mDriverAddress;
    EditText mCNIC;
    EditText mDriverReferenceName;
    EditText mDriverReferencePhone;
    EditText mDriverLiscenceNumber;
    EditText mDriverBikeLiscenceNumber;
    ProgressBar mDataEntryProgressBar;

    LinearLayout mDivider1;
    LinearLayout mDivider2;
    LinearLayout mDivider3;

    FrameLayout mCityFL;
    FrameLayout mAddressFL;
    FrameLayout mCNICFL;
    FrameLayout mRefNameFL;
    FrameLayout mRefPhoneFL;
    FrameLayout mDriverLicenseFL;
    FrameLayout mBikePlateFL;

    RelativeLayout mProfileImageRL;
    RelativeLayout mBikeImageRL;

    ImageView mProfileIV;
    ImageView mBikeIV;

    ImageButton mAddPIThroughCameraIB;
    ImageButton mAddPIThroughGalleryIB;
    ImageButton mAddBIThroughCameraIB;
    ImageButton mAddBIThroughGalleryIB;

    TextView mPINameTV;
    TextView mBINameTV;

    TextView mPhoneValidationTV;
    TextView mRefPhonValidationTV;
    TextView mCNICValidationTV;

    Uri mProfileImageURI;
    Uri mBikeImageURI;

    Button mAddProfileImageB;
    Button mAddBikeImageB;

    ProgressDialog progressDialog;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data_entry);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        bindViews();

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

            Log.v(TAG, " permission not granted");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
        }else {
            Log.v(TAG, " permission granted");
        }

        mCustomerRadioButton.setChecked(true);
        mEnterDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterData();
            }

        });

        mPhoneTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    enterData();
                }
                return false;
            }
        });

        mDriverBikeLiscenceNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    enterData();
                }
                return false;
            }
        });

        mDriverRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mDivider1.setVisibility(View.VISIBLE);
                    mDivider2.setVisibility(View.VISIBLE);
                    mDivider3.setVisibility(View.VISIBLE);

                    mCityFL.setVisibility(View.VISIBLE);
                    mAddressFL.setVisibility(View.VISIBLE);
                    mRefNameFL.setVisibility(View.VISIBLE);
                    mRefPhoneFL.setVisibility(View.VISIBLE);
                    mDriverLicenseFL.setVisibility(View.VISIBLE);
                    mBikePlateFL.setVisibility(View.VISIBLE);
                    mBikeImageRL.setVisibility(View.VISIBLE);
                }else{
                    mDivider1.setVisibility(View.GONE);
                    mDivider2.setVisibility(View.GONE);
                    mDivider3.setVisibility(View.GONE);

                    mCityFL.setVisibility(View.GONE);
                    mAddressFL.setVisibility(View.GONE);
                    mRefNameFL.setVisibility(View.GONE);
                    mRefPhoneFL.setVisibility(View.GONE);
                    mDriverLicenseFL.setVisibility(View.GONE);
                    mBikePlateFL.setVisibility(View.GONE);
                    mBikeImageRL.setVisibility(View.GONE);
                }
            }
        });

//        mPhoneTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                return true;
//            }
//        });

      /*  Muddassir got beaten by Gul
      mPhoneTV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("JUst checking","hello");
                int phoneNumberStatus = Utils.phoneNumberIsValid(mPhoneTV.getText().toString());
                if(phoneNumberStatus == Utils.EMPTY){
                    mPhoneValidationTV.setVisibility(View.GONE);
                }else if(phoneNumberStatus == Utils.INVALID) {
                    mPhoneValidationTV.setVisibility(View.VISIBLE);
                    mPhoneValidationTV.setText("Phone number is not valid :(");
                    mPhoneValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                }else if(phoneNumberStatus == Utils.VALID){
                    mPhoneValidationTV.setVisibility(View.VISIBLE);
                    mPhoneValidationTV.setText("Phone number is valid :)");
                    mPhoneValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                }
                return false;
            }
        });*/
        /* New Listener */
        mPhoneTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("JUst checking","hello");
                int phoneNumberStatus = Utils.phoneNumberIsValid(mPhoneTV.getText().toString());
                if(phoneNumberStatus == Utils.EMPTY){
                    mPhoneValidationTV.setVisibility(View.GONE);
                }else if(phoneNumberStatus == Utils.INVALID) {
                    mPhoneValidationTV.setVisibility(View.VISIBLE);
                    mPhoneValidationTV.setText("Phone number is not valid :(");
                    mPhoneValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                }else if(phoneNumberStatus == Utils.VALID){
                    mPhoneValidationTV.setVisibility(View.VISIBLE);
                    mPhoneValidationTV.setText("Phone number is valid :)");
                    mPhoneValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /*GUl was here*/

        mDriverReferencePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int phoneNumberStatus = Utils.phoneNumberIsValid(mDriverReferencePhone.getText().toString());
                if(phoneNumberStatus == Utils.EMPTY){
                    mRefPhonValidationTV.setVisibility(View.GONE);
                }else if(phoneNumberStatus == Utils.INVALID) {
                    mRefPhonValidationTV.setVisibility(View.VISIBLE);
                    mRefPhonValidationTV.setText("Phone number is not valid :(");
                    mRefPhonValidationTV.setTextColor(getResources().getColor(R.color.errorColor));
                }else if(phoneNumberStatus == Utils.VALID){
                    mRefPhonValidationTV.setVisibility(View.VISIBLE);
                    mRefPhonValidationTV.setText("Phone number is valid :)");
                    mRefPhonValidationTV.setTextColor(getResources().getColor(R.color.correct_green));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCNIC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int cnicStatus = Utils.isCNICValid(mCNIC.getText().toString());
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


        mAddPIThroughCameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.getInstance(UserDataEntryActivity.this).takePictureFromCamera().setImageListener(new ImagePicker.LocalImageListener() {
                    @Override
                    public void onSuccess(Uri uri, int mediumType) {
                        Log.v(TAG, uri.toString());
                        mProfileImageURI = uri;

                        Log.v(TAG, " uri: "+ mProfileImageURI.toString());

                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mProfileIV);
                        mProfileIV.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                        if (mProfileImageURI.getScheme().equals("file")) {
                            mPINameTV.setText(mProfileImageURI.getLastPathSegment());
                        } else {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(mProfileImageURI, new String[]{
                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                }, null, null, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    mPINameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

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

        mAddPIThroughGalleryIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.getInstance(UserDataEntryActivity.this).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
                    @Override
                    public void onSuccess(Uri uri, int mediumType) {
                        Log.v(TAG, uri.toString());
                        mProfileImageURI = uri;
                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mProfileIV);
                        mProfileIV.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                        if (mProfileImageURI.getScheme().equals("file")) {
                            mPINameTV.setText(mProfileImageURI.getLastPathSegment());
                        } else {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(mProfileImageURI, new String[]{
                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                }, null, null, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    mPINameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

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

        mAddBIThroughCameraIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.getInstance(UserDataEntryActivity.this).takePictureFromCamera().setImageListener(new ImagePicker.LocalImageListener() {
                    @Override
                    public void onSuccess(Uri uri, int mediumType) {
                        Log.v(TAG, uri.toString());
                        mBikeImageURI = uri;
                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mBikeIV);
                        mBikeIV.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                        if (mBikeImageURI.getScheme().equals("file")) {
                            mBINameTV.setText(mBikeImageURI.getLastPathSegment());
                        } else {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(mBikeImageURI, new String[]{
                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                }, null, null, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    mBINameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

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

        mAddBIThroughGalleryIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.getInstance(UserDataEntryActivity.this).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
                    @Override
                    public void onSuccess(Uri uri, int mediumType) {
                        Log.v(TAG, uri.toString());
                        mBikeImageURI = uri;
                        Picasso.with(getApplicationContext()).load(uri).resize(getPXfromDP(120f),getPXfromDP(120f)).centerCrop().into(mBikeIV);
                        mBikeIV.setPadding(getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f),getPXfromDP(5f));
                        if (mBikeImageURI.getScheme().equals("file")) {
                            mBINameTV.setText(mBikeImageURI.getLastPathSegment());
                        } else {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(mBikeImageURI, new String[]{
                                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                                }, null, null, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    mBINameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

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



//        ImagePicker.getInstance(this).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
//            @Override
//            public void onSuccess(Uri uri, int mediumType) {
//
//            }
//        });
//        ImageGetterSetter.getInstance(this).setImage().setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
//            @Override
//            public void onSuccess(Uri uri) {
//
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//
//            }
//        });

    }

    int getPXfromDP(float dps){
        return (int) (getResources().getDisplayMetrics().density*dps);
    }

    void enterData(){
        boolean allDataEntered = true;
        View focusView = null;

        if(!mCustomerRadioButton.isChecked()&&!mDriverRadioButton.isChecked()){
            focusView = mTypeChoice;
            Toast.makeText(getApplicationContext()," Please select whether you want to be a customer or driver",Toast.LENGTH_SHORT).show();
            allDataEntered = false;
        }

        if(!mMaleRadioButton.isChecked()&&!mFemaleRadioButton.isChecked()){
            focusView = mGenderChoice;
            Toast.makeText(getApplicationContext()," Please select your gender.",Toast.LENGTH_SHORT).show();
            allDataEntered = false;
        }

        if(Utils.isCNICValid(mCNIC.getText().toString()) != Utils.VALID){
            focusView = mCNIC;
            mCNIC.setError("Please the valid CNIC number that belongs to you");
            allDataEntered = false;
        }

        if(Utils.phoneNumberIsValid(mPhoneTV.getText().toString()) != Utils.VALID){
            focusView = mPhoneTV;
            mPhoneTV.setError("Please enter a valid phone number that belongs to you");
            allDataEntered = false;
        }

        if(mProfileImageURI == null){
            focusView = mProfileImageRL;
            Toast.makeText(getApplicationContext(),"Please select a profile picture",Toast.LENGTH_SHORT).show();
            allDataEntered = false;
        }


        if(allDataEntered){
            String type="";

            if(mTypeChoice.getCheckedRadioButtonId()==mDriverRadioButton.getId()){
                type = "driver";
            }else if (mTypeChoice.getCheckedRadioButtonId() == mCustomerRadioButton.getId()){
                type = "customer";
            }

            if(type.equals("driver")){

                if(TextUtils.isEmpty(mDriverBikeLiscenceNumber.getText().toString())){
                    focusView = mDriverBikeLiscenceNumber;
                    mDriverBikeLiscenceNumber.setError("Please enter your bike's liscence plate number");
                    allDataEntered = false;
                }

                if (TextUtils.isEmpty(mDriverLiscenceNumber.getText().toString())) {
                    focusView = mDriverLiscenceNumber;
                    mDriverLiscenceNumber.setError("Please enter your driving liscence number");
                    allDataEntered = false;
                }

                if(Utils.phoneNumberIsValid(mDriverReferencePhone.getText().toString())!=Utils.VALID){
                    focusView = mDriverReferencePhone;
                    mDriverReferencePhone.setError("Enter a valid phone number");
                    allDataEntered = false;
                }

                if(TextUtils.isEmpty(mDriverReferenceName.getText())){
                    focusView = mDriverReferenceName;
                    mDriverReferenceName.setError("Reference's name is required");
                    allDataEntered = false;
                }

                if(TextUtils.isEmpty(mDriverAddress.getText())){
                    focusView = mDriverAddress;
                    mDriverAddress.setError("Please enter your address");
                    allDataEntered = false;
                }

                if(TextUtils.isEmpty(mDriverCity.getText())){
                    focusView = mDriverCity;
                    mDriverCity.setError("Please enter your city");
                    allDataEntered = false;
                }

                if(mBikeImageURI == null){
                    focusView = mBikeIV;
                    Toast.makeText(getApplicationContext()," Please add an Image of your bike.",Toast.LENGTH_SHORT).show();
                    allDataEntered = false;
                }

                if(mProfileIV == null){
                    focusView = mProfileIV;
                    Toast.makeText(getApplicationContext()," Please add your a clear photo of yourself.",Toast.LENGTH_SHORT).show();
                    allDataEntered = false;
                }

            }
        }

        if(allDataEntered){
            String gender="";
            if(mGenderChoice.getCheckedRadioButtonId() == mMaleRadioButton.getId()){
                gender = "male";
            }else if(mGenderChoice.getCheckedRadioButtonId() == mFemaleRadioButton.getId()){
                gender = "female";
            }

            String type="";

            if(mTypeChoice.getCheckedRadioButtonId()==mDriverRadioButton.getId()){
                type = "driver";
            }else if (mTypeChoice.getCheckedRadioButtonId() == mCustomerRadioButton.getId()){
                type = "customer";
            }
            final User[] user = new User[1];

            mDataEntryProgressBar.setVisibility(View.VISIBLE);
            mEnterDataButton.setText("");

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Entering Images and Data");
            progressDialog.show();

            if(type.equals("driver")){
                final String city = mDriverCity.getText().toString();
                final String address = mDriverAddress.getText().toString();
                final String cNIC = mCNIC.getText().toString();
                final String refName = mDriverReferenceName.getText().toString();
                final String refPhone = mDriverReferencePhone.getText().toString();
                final String drivingLisNum = mDriverLiscenceNumber.getText().toString();
                final String bikeLisPlateNum = mDriverBikeLiscenceNumber.getText().toString();

                final String finalType = type;
                final String finalGender = gender;
                ImageGetterSetter.getInstance(this).
                        setImage(mProfileImageURI,Constants.DRIVER_PROFILE_PIC,
                                Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                                firebaseUser.getUid()
                        ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String profileImageURL = uri.toString();
                        ImageGetterSetter.getInstance(UserDataEntryActivity.this).
                                setImage(mBikeImageURI,Constants.DRIVER_BIKE_IMAGE,
                                        Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                                        firebaseUser.getUid()
                                ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String bikeImageURL = uri.toString();
                                user[0] = new User(firebaseUser.getUid(),firebaseUser.getEmail(),firebaseUser.getDisplayName(),mPhoneTV.getText().toString(), finalType, finalGender
                                        ,cNIC,address,refName,refPhone,city,bikeLisPlateNum,drivingLisNum,null,profileImageURL,bikeImageURL,Constants.DRIVER_NOT_VERIFIED);

                                FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).setValue(user[0]);
                                FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(firebaseUser.getUid()).setValue(true);
                                if(finalType.equals("driver")){
                                    Log.v(TAG, " the user is a driver");
                                    FirebaseDatabase.getInstance().getReference().child("drivers").child(firebaseUser.getUid()).setValue(true);
                                    StartupActivity.saveAsDriverOrCustomer(getApplicationContext(), Constants.TYPE_DRIVER);
                                }else if(finalType.equals("customer")){
                                    Log.v(TAG, " the user is a customer");
                                    FirebaseDatabase.getInstance().getReference().child("customers").child(firebaseUser.getUid()).setValue(true);
                                    StartupActivity.saveAsDriverOrCustomer(getApplicationContext(), Constants.TYPE_CUSTOMER);
                                }
                                StartupActivity.userDataHasBeenEnteredForId(getApplicationContext(),firebaseUser.getUid());
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
            }else {
                final String finalType1 = type;
                final String finalGender1 = gender;
                ImageGetterSetter.getInstance(this).
                        setImage(mProfileImageURI,Constants.DRIVER_PROFILE_PIC,
                                Constants.FIREBASE_STORAGE_DRIVER_REFERENCE,
                                firebaseUser.getUid()
                        ).setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String profileImageURL = uri.toString();
                        user[0] = new User(
                                firebaseUser.getUid(),
                                firebaseUser.getEmail(),
                                firebaseUser.getDisplayName(),
                                mPhoneTV.getText().toString(),
                                finalType1,
                                finalGender1,mCNIC.getText().toString(),null,null,null,null,null,null,null
                                ,profileImageURL,null
                        );

                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).setValue(user[0]);
                        FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(firebaseUser.getUid()).setValue(true);
                        if(finalType1.equals("driver")){
                            Log.v(TAG, " the user is a driver");
                            FirebaseDatabase.getInstance().getReference().child("drivers").child(firebaseUser.getUid()).setValue(true);
                            StartupActivity.saveAsDriverOrCustomer(getApplicationContext(), Constants.TYPE_DRIVER);
                        }else if(finalType1.equals("customer")){
                            Log.v(TAG, " the user is a customer");
                            FirebaseDatabase.getInstance().getReference().child("customers").child(firebaseUser.getUid()).setValue(true);
                            StartupActivity.saveAsDriverOrCustomer(getApplicationContext(), Constants.TYPE_CUSTOMER);
                        }
                        StartupActivity.userDataHasBeenEnteredForId(getApplicationContext(),firebaseUser.getUid());
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
        }else {
            focusView.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ImagePicker.object = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.getInstance(this).onActivityResult(requestCode,resultCode,data);
    }

    void bindViews(){
        mEnterDataButton = (Button) findViewById(R.id.enter_data_button);
        mPhoneTV = (AutoCompleteTextView) findViewById(R.id.phone);
        mGenderChoice = (RadioGroup) findViewById(R.id.customer_gender_sign_up);
        mTypeChoice = (RadioGroup) findViewById(R.id.customerid_signup);
        mMaleRadioButton = (RadioButton) findViewById(R.id.male_gender_radio_button);
        mFemaleRadioButton = (RadioButton) findViewById(R.id.female_gender_radio_button);
        mDriverRadioButton = (RadioButton) findViewById(R.id.driver_signup);
        mCustomerRadioButton = (RadioButton) findViewById(R.id.customer_signup);
        mDataEntryProgressBar = (ProgressBar) findViewById(R.id.data_entry_progress_bar);
        mDriverCity = (EditText) findViewById(R.id.city);
        mDriverAddress = (EditText) findViewById(R.id.address);
        mCNIC = (EditText) findViewById(R.id.cnic);
        mDriverReferenceName = (EditText) findViewById(R.id.reference_name);
        mDriverReferencePhone = (EditText) findViewById(R.id.reference_phone);
        mDriverLiscenceNumber = (EditText) findViewById(R.id.liscence_number);
        mDriverBikeLiscenceNumber = (EditText) findViewById(R.id.liscence_plate_number);
        mDivider1 = (LinearLayout) findViewById(R.id.divider1);
        mDivider2 = (LinearLayout) findViewById(R.id.divider2);
        mDivider3 = (LinearLayout) findViewById(R.id.divider3);
        mCityFL = (FrameLayout) findViewById(R.id.city_frame_layout);
        mAddressFL = (FrameLayout) findViewById(R.id.address_frame_layout);
        mCNICFL = (FrameLayout) findViewById(R.id.cnic_frame_layout);
        mRefNameFL = (FrameLayout) findViewById(R.id.ref_name_frame_layout);
        mRefPhoneFL = (FrameLayout) findViewById(R.id.ref_phone_frame_layout);
        mDriverLicenseFL = (FrameLayout) findViewById(R.id.driver_license_frame_layout);
        mBikePlateFL = (FrameLayout) findViewById(R.id.bike_plate_frame_layout);
        mProfileImageRL = (RelativeLayout) findViewById(R.id.profile_image_layout);
        mBikeImageRL = (RelativeLayout) findViewById(R.id.bike_image_layout);
        mProfileIV = (ImageView) findViewById(R.id.profile_picture_image_view);
        mBikeIV = (ImageView) findViewById(R.id.bike_picture_image_view);
        mAddProfileImageB = (Button) findViewById(R.id.add_profile_image_button);
        mAddBikeImageB = (Button) findViewById(R.id.add_bike_image_button);
        mAddPIThroughCameraIB = (ImageButton) findViewById(R.id.add_profile_image_through_camera_button);
        mAddPIThroughGalleryIB = (ImageButton) findViewById(R.id.add_profile_image_through_gallery_button);
        mAddBIThroughCameraIB = (ImageButton) findViewById(R.id.add_bike_image_through_camera_button);
        mAddBIThroughGalleryIB = (ImageButton) findViewById(R.id.add_bike_image_through_gallery_button);
        mPINameTV = (TextView) findViewById(R.id.profile_image_name_text_view);
        mBINameTV = (TextView) findViewById(R.id.bike_image_name_text_view);
        mPhoneValidationTV = (TextView) findViewById(R.id.phone_number_validation_text_view);
        mRefPhonValidationTV  = (TextView) findViewById(R.id.reference_phone_number_validation_text_view);
        mCNICValidationTV = (TextView) findViewById(R.id.cnic_validation_text_view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.root_layout);
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "External storage access required for profile picture. Sign up may fail!", Snackbar.LENGTH_LONG)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startInstalledAppDetailsActivity();
                                }
                            });

                    snackbar.setActionTextColor(getResources().getColor(R.color.errorColor));
                    snackbar.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void startInstalledAppDetailsActivity() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + this.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.startActivity(i);
    }

}
