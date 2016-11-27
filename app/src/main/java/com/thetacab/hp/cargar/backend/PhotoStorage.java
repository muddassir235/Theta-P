package com.thetacab.hp.cargar.backend;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.thetacab.hp.cargar.Constants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by hp on 8/29/2016.
 */
public class PhotoStorage {

    static PhotoStorage object;

    private Target imageTarget;

    public interface ImageUploadedListener {
        public void onFailure(Exception exception);
        public void onSuccess(Uri uri);
    }

    public PhotoStorage(){
        imageUploadedListeners= new ArrayList<ImageUploadedListener>();
        activity=new Activity();
    }

    public interface LocalImageEventListener{
        public void onSuccess(Uri uri);
    }
    public ArrayList<ImageUploadedListener> imageUploadedListeners;
    public ArrayList<LocalImageEventListener> localImageListeners;

    public  Activity activity;

    public PhotoStorage uploadImage(Activity activity, int requestCode){
        this.activity=activity;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent,"Select Picture"), requestCode);
        return this;
    }


    //http://stackoverflow.com/questions/21780252/how-to-use-onactivityresult-method-from-other-than-activity-class
    //PLease go through upper given link to find out how to use this method
    public void activityResult(int requestCode, int resultCode, Intent data){
       if(resultCode==Activity.RESULT_OK) {
           if (requestCode == Constants.FIREBASE_OPERATION_SELECT_PICTURE) {
               Uri selectedImageUri = data.getData();
               for(LocalImageEventListener listen : localImageListeners ){
                   listen.onSuccess(selectedImageUri);
                   localImageListeners.remove(listen);
               }
           }
       }
    }


    public String getImagePath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void uploadImageToFirebase(final StorageReference storageReference, Uri selectedImagePath){
        imageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("onBitmapLoaded: ","Image Loaded ");
                ByteArrayOutputStream bit= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
                byte[] bits=bit.toByteArray();
                UploadTask task= storageReference.putBytes(bits);
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        int index = 0;
                        for(ListIterator<ImageUploadedListener> it = imageUploadedListeners.listIterator(); it.hasNext(); ){
                            ImageUploadedListener aListener = it.next();
                            aListener.onFailure(exception);
                            imageUploadedListeners.remove(index);
                            index++;
                        }

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        int index = 0;
                        for(ListIterator<ImageUploadedListener> it = imageUploadedListeners.listIterator(); it.hasNext(); ){
                            ImageUploadedListener aListener = it.next();
                            aListener.onSuccess(downloadUrl);
                            imageUploadedListeners.remove(index);
                            index ++;
                        }

                        Log.e("ui", downloadUrl.toString());
                    }
                });

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("Error","bitmapFailed\\");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepared","I am here thumbnail");

            }
        };
        Picasso.with(activity.getApplicationContext()).load(selectedImagePath).resize(500,500).centerCrop().into(imageTarget);
    }

    public void setLocalImageListener(LocalImageEventListener listen){
        this.localImageListeners.add(listen);
    }

    public void setImageUploadedListener(ImageUploadedListener listener){
        this.imageUploadedListeners.add(listener);
    }

    public PhotoStorage uploadImageTask(String id, String folder, Uri imageUri){
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://project-2162072630908720035.appspot.com/images/");
        uploadImageToFirebase(imageRef.child(id).child(folder),imageUri);
        return this;
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static PhotoStorage getInstance(){

        if(object==null){
           object= new PhotoStorage();
        }
        return object;
    }

}
