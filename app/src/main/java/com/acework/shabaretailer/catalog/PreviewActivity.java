package com.acework.shabaretailer.catalog;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.acework.shabaretailer.R;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ortiz.touchview.TouchImageView;

import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewActivity extends AppCompatActivity {
    private MaterialButton back, download;
    private TextView title;
    private LottieAnimationView animation;
    private TouchImageView image;
    private ConstraintLayout loadingLayout;

    // this object will handle the download destination provided by the user, if any
    private final ActivityResultLauncher<Intent> chooseDestinationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    downloadImage(uri);
                }
            }
        }
    });

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
        download.setOnClickListener(v -> chooseDownloadDestination());
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

    private void chooseDownloadDestination() {
        // prepare data
        String name = getIntent().getStringExtra("itemName");
        String link = getIntent().getStringExtra("link");
        String fileName = name.replace(" ", "_") + "_" + link;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        chooseDestinationLauncher.launch(intent);
    }

    private void downloadImage(Uri destinationUri) {
        download.setEnabled(false);
        String link = getIntent().getStringExtra("link");

        FirebaseStorage.getInstance().getReference().child("item_images").child(link).getBytes(10485760).addOnCompleteListener(task -> {
            download.setEnabled(true);
            if (task.isSuccessful()) {
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(destinationUri, "w");
                    FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                    fileOutputStream.write(task.getResult());
                    fileOutputStream.close();
                    parcelFileDescriptor.close();

                    Snackbar.make(back, "Image downloaded.", Snackbar.LENGTH_LONG).show();
                } catch (IOException ioException) {
                    Snackbar.make(back, "Download failed. Try again later.", Snackbar.LENGTH_LONG).show();
                    ioException.printStackTrace();
                }
            } else {
                Snackbar.make(back, "Download failed. Try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }
}