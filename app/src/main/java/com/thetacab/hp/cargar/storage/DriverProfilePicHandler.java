package com.thetacab.hp.cargar.storage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.SyncStateContract;

import com.thetacab.hp.cargar.Constants;

import java.lang.annotation.Target;
import java.sql.Driver;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gul on 10/14/16.
 */
public class DriverProfilePicHandler {
    public interface ImageGetterListener{
        void onSuccess(Bitmap bitmap);
        void onFailure(Exception e);
    }
    public interface ImageSetterListener{
        void onSuccess(Uri downloadUri, Uri localImageUri);
        void onFailure(Exception e);
    }
    private CopyOnWriteArrayList<ImageGetterListener> imageGetterListeners;
    private CopyOnWriteArrayList<ImageSetterListener> imageSetterListeners;
    private static DriverProfilePicHandler object;
    private Activity context;
    public DriverProfilePicHandler(Activity context){
        this.context=context;
        imageGetterListeners=new CopyOnWriteArrayList<ImageGetterListener>();
        imageSetterListeners=new CopyOnWriteArrayList<ImageSetterListener>();
    }

    public DriverProfilePicHandler setImageSetterGetterListener(ImageGetterListener listener){
        imageGetterListeners.add(listener);
        return this;
    }
    public DriverProfilePicHandler setImageSetterGetterListener(ImageSetterListener listener){
        imageSetterListeners.add(listener);
        return this;
    }
    public static DriverProfilePicHandler getInstance(Activity context){
        if(object==null){
            object=new DriverProfilePicHandler(context);
        }
        return object;
    }
    public DriverProfilePicHandler uploadImage(int mediumType, final String uid, final String imageType,final String userType){
        if(mediumType== Constants.TAKE_IMAGE_FROM_CAMERA){
            ImagePicker.getInstance(context).takePictureFromCamera().setImageListener(new ImagePicker.LocalImageListener() {
                @Override
                public void onSuccess(Uri uri, int mediumType) {
                    final Uri localImageUri=uri;
                    ImageGetterSetter.getInstance(context).setImage(uri,imageType,userType,uid)
                            .setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //success of user listeners
                                    for(ListIterator<ImageSetterListener> it= imageSetterListeners.listIterator();it.hasNext();){
                                        ImageSetterListener listener= it.next();
                                        listener.onSuccess(uri,localImageUri);
                                        imageSetterListeners.remove(listener);
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    //failure of user listeners
                                    for(ListIterator<ImageSetterListener> it= imageSetterListeners.listIterator();it.hasNext();){
                                        ImageSetterListener listener= it.next();
                                        listener.onFailure(e);
                                        imageSetterListeners.remove(listener);
                                    }
                                }
                            });
                }
            });
        }
        else if(mediumType== Constants.TAKE_IMAGE_FROM_GALLERY){
            ImagePicker.getInstance(context).takePictureFromGallery().setImageListener(new ImagePicker.LocalImageListener() {
                @Override
                public void onSuccess(Uri uri, int mediumType) {
                    final Uri localImageUri = uri;
                    ImageGetterSetter.getInstance(context).setImage(uri,imageType,userType,uid)
                            .setImageSetterGetterListener(new ImageGetterSetter.FirebaseSetterImageListener() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //success of user listeners
                                    for(ListIterator<ImageSetterListener> it= imageSetterListeners.listIterator();it.hasNext();){
                                        ImageSetterListener listener= it.next();
                                        listener.onSuccess(uri,localImageUri);
                                        imageSetterListeners.remove(listener);
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    //failure of user listeners
                                    for(ListIterator<ImageSetterListener> it= imageSetterListeners.listIterator();it.hasNext();){
                                        ImageSetterListener listener= it.next();
                                        listener.onFailure(e);
                                        imageSetterListeners.remove(listener);
                                    }
                                }
                            });
                }
            });
        }
        return this;
    }
    public DriverProfilePicHandler getImage(String uid, String userType, String imageType){
        ImageGetterSetter.getInstance(context).getImage(uid,userType,imageType)
                .setImageSetterGetterListener(new ImageGetterSetter.FirebaseGetterImageListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        //success
                        for(ListIterator<ImageGetterListener> it= imageGetterListeners.listIterator();it.hasNext();){
                            ImageGetterListener listener= it.next();
                            listener.onSuccess(bitmap);
                            imageGetterListeners.remove(listener);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //failure
                        for(ListIterator<ImageGetterListener> it= imageGetterListeners.listIterator();it.hasNext();){
                            ImageGetterListener listener= it.next();
                            listener.onFailure(e);
                            imageGetterListeners.remove(listener);
                        }
                    }
                });
        return this;
    }
}
