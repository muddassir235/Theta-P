package com.thetacab.hp.cargar.storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;

import com.thetacab.hp.cargar.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gul on 10/14/16.
 */
public class ImagePicker {
    public static ImagePicker object;
    public Activity context;
    private CopyOnWriteArrayList<LocalImageListener> imageListeners;
    public interface LocalImageListener{
        public void onSuccess(Uri uri, int mediumType);
    }
    public ImagePicker(Activity context){
        this.context=context;
        imageListeners=new CopyOnWriteArrayList<LocalImageListener>();
    }
    public static ImagePicker getInstance(Activity context){
        if(object==null){
            object=new ImagePicker(context);
        }
        return object;
    }
    public void setImageListener(LocalImageListener listener){
        this.imageListeners.add(listener);
    }
    public ImagePicker takePictureFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivityForResult(takePicture, Constants.TAKE_IMAGE_FROM_CAMERA);
        return this;
    }

    public ImagePicker takePictureFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(pickPhoto , Constants.TAKE_IMAGE_FROM_GALLERY);
        return this;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_IMAGE_FROM_CAMERA:
                    if(imageReturnedIntent!=null) {
                        Bitmap image = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        Uri uri = getImageUri(context.getApplicationContext(), image);
                        for (ListIterator<LocalImageListener> it = imageListeners.listIterator(); it.hasNext(); ) {
                            LocalImageListener listener = it.next();
                            listener.onSuccess(uri, Constants.TAKE_IMAGE_FROM_CAMERA);
                            imageListeners.remove(listener);
                        }
                    }else{
                        for (ListIterator<LocalImageListener> it = imageListeners.listIterator(); it.hasNext(); ) {
                            LocalImageListener listener = it.next();
                            imageListeners.remove(listener);
                        }
                    }
                    break;
                case Constants.TAKE_IMAGE_FROM_GALLERY:
                    Log.e("Image Taken", "lol");
                    for (ListIterator<LocalImageListener> it = imageListeners.listIterator(); it.hasNext(); ) {
                        LocalImageListener listener = it.next();
                        //Log.e("URI",imageReturnedIntent.getData().toString());
                        if (imageReturnedIntent != null) {
                            listener.onSuccess(imageReturnedIntent.getData(), Constants.TAKE_IMAGE_FROM_GALLERY);
                        }
                        imageListeners.remove(listener);
                    }
                    break;
                default:
                    break;
            }
        }else{
            for (ListIterator<LocalImageListener> it = imageListeners.listIterator(); it.hasNext(); ) {
                LocalImageListener listener = it.next();
                imageListeners.remove(listener);
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.e("I am here","no way");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path1 = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "cargar-image", null);
        Bitmap newBitmap=setImage(Uri.parse(path1),inImage,inContext);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), newBitmap, "cargar-image", null);
        return Uri.parse(path);
    }
    private Bitmap rotateImage(Bitmap source, float angle) {
        Log.e("if function","");
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }
    private Bitmap setImage(Uri path, Bitmap bitmap,Context context){
        Log.e("if Function 3","he");
        ExifInterface ei;
        File file = new File(path.getPath());
        try {
            ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                default:
                    Log.e("Picture is not rotated",String.valueOf(orientation));
            }
        } catch (IOException e) {
            Log.e("opening issue","haha");
           // e.printStackTrace();
        }

        return bitmap;
    }

}
