package com.acework.shabaretailer.catalog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class PreviewActivity extends AppCompatActivity {
    private MaterialButton back, close;
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
        close = findViewById(R.id.close);
        title = findViewById(R.id.title);
        image = findViewById(R.id.image);
        animation = findViewById(R.id.animation);
        loadingLayout = findViewById(R.id.loading_layout);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        close.setOnClickListener(v -> finish());
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
                            Snackbar.make(back, "Pinch to zoom in and out", Snackbar.LENGTH_LONG).setAnchorView(close).show();
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
            Snackbar.make(back, "There was an error loading the image. Try again later.", Snackbar.LENGTH_LONG).setAnchorView(close).show();
        }
    }
}