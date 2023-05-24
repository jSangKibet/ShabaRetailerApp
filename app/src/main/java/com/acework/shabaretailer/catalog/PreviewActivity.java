package com.acework.shabaretailer.catalog;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ortiz.touchview.TouchImageView;

import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewActivity extends AppCompatActivity {
    private MaterialButton back, download;
    // this object will handle the result of the storage permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            prepareToDownloadImageP();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Storage permission denied")
                    .setMessage("Shaba Retailer requires access to your device's storage to download the image." +
                            " Please grant the permission from the application's settings to proceed.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    });
    private TextView title;
    private LottieAnimationView animation;
    private TouchImageView image;
    private ConstraintLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        bindViews();
        setListeners();
        loadImage();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        download = findViewById(R.id.download);
        title = findViewById(R.id.title);
        image = findViewById(R.id.image);
        animation = findViewById(R.id.animation);
        loadingLayout = findViewById(R.id.loading_layout);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        download.setOnClickListener(v -> prepareToDownloadImage());
    }

    private void loadImage() {
        String name = getIntent().getStringExtra("itemName");
        String link = getIntent().getStringExtra("link");
        if (name == null || link == null) {
            finish();
        } else {
            title.setText(name);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");

            shabaCSR.child(link).getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Glide.with(this).load(task.getResult()).error(R.drawable.image_not_found).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            hideAnimation(true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            hideAnimation(false);
                            return false;
                        }
                    }).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            image.setImageDrawable(resource);
                            Snackbar.make(back, "Pinch to zoom in and out", Snackbar.LENGTH_LONG).setAnchorView(download).show();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                } else {
                    hideAnimation(true);
                    image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.image_not_found));
                }
            });
        }
    }

    private void hideAnimation(boolean failed) {
        animation.pauseAnimation();
        loadingLayout.setVisibility(View.GONE);
        if (failed) {
            Snackbar.make(back, "There was an error loading the image. Try again later.", Snackbar.LENGTH_LONG).setAnchorView(download).show();
        } else {
            download.setEnabled(true);
        }
    }

    // download the image to local storage
    private void downloadImage(Uri destinationUri) {
        // show progress dialog
        StatusDialog dialog = StatusDialog.newInstance(R.raw.loading, "Downloading image", false, null);
        dialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        // download image to memory
        String link = getIntent().getStringExtra("link");
        FirebaseStorage.getInstance().getReference().child("item_images").child(link).getBytes(10485760).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                try {

                    // save image to file
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(destinationUri, "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                    fileOutputStream.write(task.getResult());
                    fileOutputStream.close();
                    parcelFileDescriptor.close();

                    // notify of completion
                    Snackbar.make(back, "Image downloaded. Please check your gallery.", Snackbar.LENGTH_LONG).show();
                } catch (IOException ioException) {

                    // notify of failure
                    Snackbar.make(back, "Download failed. Try again later.", Snackbar.LENGTH_LONG).show();
                    ioException.printStackTrace();
                }
            } else {
                Snackbar.make(back, "Download failed. Try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void prepareToDownloadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            prepareToDownloadImageQ();
        } else {
            checkForStoragePermission();
        }
    }

    // prepare to download the image (get uri basically)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void prepareToDownloadImageQ() {
        // Add a specific media item
        ContentResolver resolver = getApplicationContext().getContentResolver();

        // Find all photo files on the primary external storage device
        Uri imageCollection;
        imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        // Save a new image
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, getFileName());

        // Keep a handle to the new image's URI in case you need to modify it later
        Uri newImageUri = resolver.insert(imageCollection, newImageDetails);

        downloadImage(newImageUri);
    }

    private void prepareToDownloadImageP() {
        // Add a specific media item
        ContentResolver resolver = getApplicationContext().getContentResolver();

        // Find all photo files on the primary external storage device
        Uri imageCollection;
        imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Save a new image
        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, getFileName());

        // Keep a handle to the new image's URI in case you need to modify it later
        Uri newImageUri = resolver.insert(imageCollection, newImageDetails);

        downloadImage(newImageUri);
    }

    // check for storage permission (android P and below)
    private void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            prepareToDownloadImageP();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    // get download file name
    private String getFileName() {
        String name = getIntent().getStringExtra("itemName");
        String link = getIntent().getStringExtra("link");
        String[] linkComponents = link.split("\\.");
        return name + getInsertColor(linkComponents[0]) + ".jpg";
    }

    // get insert color from image number
    private String getInsertColor(String number) {
        if (number.endsWith("1")) return " mustard ";
        if (number.endsWith("2")) return " maroon ";
        return " dark brown ";
    }
}