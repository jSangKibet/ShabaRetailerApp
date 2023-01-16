package com.acework.shabaretailer.custom;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.acework.shabaretailer.R;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AutoScrollImageView extends ConstraintLayout {
    private final ImageView image1, image2, image3;
    private final MaterialButton scrollLeft, scrollRight;
    private final ConstraintLayout root;
    private int currentImage = 1;
    private boolean autoScroll = false;
    private boolean autoScrolling = false;

    public AutoScrollImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_auto_scroll_image_view_1, this, true);
        image1 = findViewById(R.id.image_1);
        image2 = findViewById(R.id.image_2);
        image3 = findViewById(R.id.image_3);
        scrollLeft = findViewById(R.id.scroll_left);
        scrollRight = findViewById(R.id.scroll_right);
        root = findViewById(R.id.root);
        scrollLeft.setOnClickListener(v -> scroll31());
        scrollRight.setOnClickListener(v -> scroll12());
    }

    private void scroll12() {
        scroll(R.layout.layout_auto_scroll_image_view_2);
        scrollRight.setOnClickListener(v -> scroll23());
        scrollLeft.setOnClickListener(v -> scroll21());
        currentImage = 2;
        autoScroll();
    }

    private void scroll13() {
        scroll(R.layout.layout_auto_scroll_image_view_3);
        scrollRight.setOnClickListener(v -> scroll31());
        scrollLeft.setOnClickListener(v -> scroll32());
        currentImage = 3;
        autoScroll();
    }


    private void scroll23() {
        scroll(R.layout.layout_auto_scroll_image_view_3);
        scrollRight.setOnClickListener(v -> scroll31());
        scrollLeft.setOnClickListener(v -> scroll32());
        currentImage = 3;
        autoScroll();
    }

    private void scroll21() {
        scroll(R.layout.layout_auto_scroll_image_view_1);
        scrollRight.setOnClickListener(v -> scroll12());
        scrollLeft.setOnClickListener(v -> scroll13());
        currentImage = 1;
        autoScroll();
    }

    private void scroll31() {
        scroll(R.layout.layout_auto_scroll_image_view_1);
        scrollRight.setOnClickListener(v -> scroll12());
        scrollLeft.setOnClickListener(v -> scroll13());
        currentImage = 1;
        autoScroll();
    }


    private void scroll32() {
        scroll(R.layout.layout_auto_scroll_image_view_2);
        scrollRight.setOnClickListener(v -> scroll23());
        scrollLeft.setOnClickListener(v -> scroll21());
        currentImage = 2;
        autoScroll();
    }


    private void scroll(int constraintLayoutId) {
        ConstraintSet set1 = new ConstraintSet();
        set1.clone(root);
        ConstraintSet set2 = new ConstraintSet();
        set2.clone(getContext(), constraintLayoutId);
        TransitionManager.beginDelayedTransition(root);
        set2.applyTo(root);
        autoScrolling = false;
    }

    public void loadImages(String sku) {
        String image1Link = sku + "_01.jpg";
        String image2Link = sku + "_02.jpg";
        String image3Link = sku + "_03.jpg";

        image1.setImageResource(R.drawable.image_loading);
        image2.setImageResource(R.drawable.image_loading);
        image3.setImageResource(R.drawable.image_loading);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");

        shabaCSR.child(image1Link).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(image1).load(task.getResult()).error(R.drawable.image_not_found).into(image1);
            } else {
                image1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image_not_found));
            }
        });

        shabaCSR.child(image2Link).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(image2).load(task.getResult()).error(R.drawable.image_not_found).into(image2);
            } else {
                image2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image_not_found));
            }
        });

        shabaCSR.child(image3Link).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(image3).load(task.getResult()).error(R.drawable.image_not_found).into(image3);
            } else {
                image3.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image_not_found));
            }
        });

        autoScroll = true;
        autoScroll();
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public void autoScroll() {
        if (autoScroll) {
            if (!autoScrolling) {
                autoScrolling = true;
                image1.postDelayed(() -> {
                    if (currentImage == 1) {
                        scroll12();
                    } else if (currentImage == 2) {
                        scroll23();
                    } else {
                        scroll31();
                    }
                }, 3000);
            }
        }
    }
}
