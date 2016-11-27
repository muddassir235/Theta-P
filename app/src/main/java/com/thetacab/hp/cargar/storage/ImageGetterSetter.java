package com.thetacab.hp.cargar.storage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.squareup.picasso.Target;
import com.thetacab.hp.cargar.Constants;

import java.io.ByteArrayOutputStream;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by gul on 10/14/16.
 */
public class ImageGetterSetter {
    private Activity context;

    private StorageReference reference;
    private static ImageGetterSetter object;
    private Target target;
    public interface FirebaseSetterImageListener {
        public void onSuccess(Uri uri);
        public void onFailure(Exception e);
    };
    public interface FirebaseGetterImageListener{
        public void onSuccess(Bitmap bitmap);
        public void onFailure(Exception e);
    }
    private CopyOnWriteArrayList<FirebaseSetterImageListener> imageSetterListeners;
    private CopyOnWriteArrayList<FirebaseGetterImageListener> imageGetterListeners;

    public ImageGetterSetter(Activity context){
        this.context=context;
        imageSetterListeners =new CopyOnWriteArrayList<FirebaseSetterImageListener>();
        imageGetterListeners=new CopyOnWriteArrayList<FirebaseGetterImageListener>();
        defineTarget();
    }

    public void setImageSetterGetterListener(FirebaseSetterImageListener listener){
        this.imageSetterListeners.add(listener);
    }

    public void setImageSetterGetterListener(FirebaseGetterImageListener listener){
        this.imageGetterListeners.add(listener);
    }

    public static ImageGetterSetter getInstance(Activity context){
        if(object==null){
            object=new ImageGetterSetter(context);
        }
        return object;
    }
    public ImageGetterSetter setImage(Uri uri, String pictureType,String userType,String uid){
        reference= FirebaseStorage.getInstance().
                getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL+userType+uid+pictureType+"1.jpg");
        defineTarget();
        GlideExecutor set= new GlideExecutor();
        set.execute(uri);
        //Picasso.with(context.getApplicationContext()).load(uri).into(target);
        return this;
    }


    private void defineTarget(){
        target= new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("Bitmap Success","lol");
                uploadImage(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("Bitmap ","failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }



    private void uploadImage(Bitmap bitmap){
        Log.e("onBitmapLoaded: ","Image Loaded ");

        ByteArrayOutputStream bit= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
        byte[] bits=bit.toByteArray();
        UploadTask task= reference.putBytes(bits);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                for(ListIterator<FirebaseSetterImageListener> it = imageSetterListeners.listIterator(); it.hasNext();){
                    FirebaseSetterImageListener listener= it.next();
                    listener.onFailure(exception);
                    imageSetterListeners.remove(listener);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                int index = 0;
                for(ListIterator<FirebaseSetterImageListener> it = imageSetterListeners.listIterator(); it.hasNext();){
                    FirebaseSetterImageListener listener=it.next();
                    listener.onSuccess(taskSnapshot.getDownloadUrl());
                    imageSetterListeners.remove(listener);
                }
            }
        });
    }
    public ImageGetterSetter getImage(String uid, String userType, String imageType){
        reference= FirebaseStorage.getInstance().
                getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL+userType+uid+"/"+imageType+"1.jpg");
        downloadImage();
        return this;

    }
    private void downloadImage(){
        final long ONE_MEGABYTE = 1024 * 1024;
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                for(ListIterator<FirebaseGetterImageListener> it= imageGetterListeners.listIterator();it.hasNext();){
                    FirebaseGetterImageListener listener=it.next();
                    listener.onSuccess(bitmap);
                    imageGetterListeners.remove(listener);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                for(ListIterator<FirebaseGetterImageListener> it= imageGetterListeners.listIterator();it.hasNext();){
                    FirebaseGetterImageListener listener=it.next();
                    listener.onFailure(exception);
                    imageGetterListeners.remove(listener);
                }
            }
        });
    }
    private class GlideExecutor extends AsyncTask<Uri,Void,Bitmap>{
        protected Bitmap doInBackground(Uri... params) {
            Uri uri=params[0];
            Log.e("Uri glide",uri.toString());
            Bitmap bitmap= null;
            try {
               bitmap = Glide.with(context).load(uri).asBitmap().centerCrop().into(250,250).get();
            }
            catch(ExecutionException e){
                Log.e("GlideExecutor",e.toString());
            }
            catch(InterruptedException e){
                Log.e("GlideExecutor",e.toString());
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            uploadImage(result);
            }
        }

}
