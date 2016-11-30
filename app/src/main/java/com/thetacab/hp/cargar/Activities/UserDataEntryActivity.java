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
import android.support.annotation.NonNull;
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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private static final String TAG = "UserDataEntryActivity: " ;

    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 011;

    Button mEnterDataButton;
    AutoCompleteTextView mPhoneTV;
    RadioGroup mGenderChoice;
    RadioButton mMaleRadioButton;
    RadioButton mFemaleRadioButton;

    EditText mCNIC;

    FrameLayout mCNICFL;

    RelativeLayout mProfileImageRL;

    ImageView mProfileIV;
    ImageView mBikeIV;

    ImageButton mAddPIThroughCameraIB;
    ImageButton mAddPIThroughGalleryIB;

    ImageButton mCloseButton;

    TextView mPINameTV;

    TextView mPhoneValidationTV;
    TextView mCNICValidationTV;

    Uri mProfileImageURI;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

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

        }else {
            Log.v(TAG, " permission granted");
        }

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

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }



    void close(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthUI.getInstance()
                .signOut(UserDataEntryActivity.this)
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
    }

    int getPXfromDP(float dps){
        return (int) (getResources().getDisplayMetrics().density*dps);
    }

    void enterData(){
        boolean allDataEntered = true;
        View focusView = null;

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
            String gender="";
            if(mGenderChoice.getCheckedRadioButtonId() == mMaleRadioButton.getId()){
                gender = "male";
            }else if(mGenderChoice.getCheckedRadioButtonId() == mFemaleRadioButton.getId()){
                gender = "female";
            }

            final User[] user = new User[1];

            mEnterDataButton.setText("");

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Entering Images and Data");
            progressDialog.show();


            final String finalType1 = "customer";
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
        mMaleRadioButton = (RadioButton) findViewById(R.id.male_gender_radio_button);
        mFemaleRadioButton = (RadioButton) findViewById(R.id.female_gender_radio_button);
        mCNIC = (EditText) findViewById(R.id.cnic);
        mCNICFL = (FrameLayout) findViewById(R.id.cnic_frame_layout);
        mProfileImageRL = (RelativeLayout) findViewById(R.id.profile_image_layout);
        mProfileIV = (ImageView) findViewById(R.id.profile_picture_image_view);
        mBikeIV = (ImageView) findViewById(R.id.bike_picture_image_view);
        mAddPIThroughCameraIB = (ImageButton) findViewById(R.id.add_profile_image_through_camera_button);
        mAddPIThroughGalleryIB = (ImageButton) findViewById(R.id.add_profile_image_through_gallery_button);
        mPINameTV = (TextView) findViewById(R.id.profile_image_name_text_view);
        mPhoneValidationTV = (TextView) findViewById(R.id.phone_number_validation_text_view);
        mCNICValidationTV = (TextView) findViewById(R.id.cnic_validation_text_view);
        mCloseButton = (ImageButton) findViewById(R.id.close_button);
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
                            .make(relativeLayout, "External storage access required for accessing camera!", Snackbar.LENGTH_LONG)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startInstalledAppDetailsActivity();
                                }
                            });

                    snackbar.setActionTextColor(getResources().getColor(R.color.errorColor));
                    snackbar.show();

                    mAddPIThroughCameraIB.setClickable(false);
                    mAddPIThroughCameraIB.setOnClickListener(null);
                    mAddPIThroughCameraIB.setFocusable(false);
                    mAddPIThroughCameraIB.setVisibility(View.INVISIBLE);
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
