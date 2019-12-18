package pucmm.temas.especiales.e_commerce_app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pucmm.temas.especiales.e_commerce_app.asynctasks.Response;

public class FirebaseNetwork {
    private static final String TAG = "FirebaseStorageUtils";

    private static final String URL_DOWNLOAD = "gs://ecommerceapp-fc0d8.appspot.com/images/e_commerce/";
    private static final String PATH_UPLOAD = "images/e_commerce/";
    private static final long ONE_MEGABYTE = 1024 * 1024;

    private static FirebaseNetwork sInstance;

    public static FirebaseNetwork obtain() {
        if (sInstance == null) {
            sInstance = new FirebaseNetwork();
        }
        return sInstance;
    }

    public FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance();
    }

    public StorageReference getStorageReference() {
        return getStorage().getReference();
    }

    public void delete(final String key, final Response.Listener response, final Response.ErrorListener errorListener) {
        final StorageReference reference = getStorageReference().child(PATH_UPLOAD + key);
        reference.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "delete:onSuccess");
                    response.onResponse("Successfully deleted on Firebase");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "delete:onFailure");
                    errorListener.onErrorResponse(e);
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "delete:onComplete");
            }
        }).addOnCanceledListener(() -> Log.i(TAG, "delete:onCanceled"));
        // imageCache.deleteBitmapFromCache(key);
    }

    public void upload(final Uri uri, final String key, final Response.Listener response, final Response.ErrorListener errorListener) {
        final StorageReference reference = getStorageReference().child(PATH_UPLOAD + key);

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "upload:onSuccess");
                    taskSnapshot.getUploadSessionUri();

                    taskSnapshot.getStorage().getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(bytes -> {
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                // imageCache.addBitmapToCache(key, bitmap);
                            });

                    response.onResponse(key);
                }).addOnCanceledListener(() -> Log.i(TAG, "upload:onCanceled")).addOnCompleteListener(task -> Log.i(TAG, "upload:onComplete")).addOnFailureListener(e -> {
            Log.e(TAG, "upload:onFailure");
            errorListener.onErrorResponse(e);
        }).addOnPausedListener(taskSnapshot -> Log.i(TAG, "upload:onPaused")).addOnProgressListener(taskSnapshot -> Log.i(TAG, "upload:onProgress"));
    }


    public void download(final String key, final Response.Listener response, final Response.ErrorListener errorListener) {

        Log.e(TAG, "Bitmap not in memory");
        final StorageReference reference = getStorage().getReferenceFromUrl(URL_DOWNLOAD + key);

        reference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Log.i(TAG, "download:onSuccess");
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    // imageCache.addBitmapToCache(key, bitmap);
                    response.onResponse(bitmap);
                })
                .addOnCanceledListener(() -> Log.i(TAG, "download:onCanceled"))
                .addOnCompleteListener(task -> Log.i(TAG, "download:onComplete"))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "download:onFailure");
                    errorListener.onErrorResponse(e);
                });
    }
}
