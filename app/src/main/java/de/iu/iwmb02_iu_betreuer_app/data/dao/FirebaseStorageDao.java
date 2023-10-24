package de.iu.iwmb02_iu_betreuer_app.data.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.iu.iwmb02_iu_betreuer_app.util.Callback;

public class FirebaseStorageDao implements FileStorageDao{
    private static FirebaseStorageDao instance;
    private static final String TAG = "FirebaseStorageDao";
    private static final long MAX_DOWNLOADSIZE = 1024 * 1024;

    private final StorageReference storageRef;

    public static FirebaseStorageDao getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageDao();
        }
        return instance;
    }

    private FirebaseStorageDao() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    @Override
    public void downloadImage(String imageUrl, Callback<Bitmap> callback) {
        StorageReference imageRef = storageRef.child(imageUrl);
        imageRef.getBytes(MAX_DOWNLOADSIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG, "Image dowloaded: "+imageUrl);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                callback.onCallback(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to download image: ", e);
            }
        });
    }

    @Override
    public void uploadExpose(Uri file, Callback<String> callback) {
        StorageReference riversRef = storageRef.child("exposes/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Expose upload failed", e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Expose successfully uploaded");
                callback.onCallback(taskSnapshot.getStorage().getPath());
            }
        });
    }

}
