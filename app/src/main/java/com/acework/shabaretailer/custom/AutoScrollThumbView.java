package com.acework.shabaretailer.custom;

import android.content.Context;
import android.transition.AutoTransition;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AutoScrollThumbView extends ConstraintLayout {
    private final ImageView image1, image2, image3;
    private final ConstraintLayout root;
    private final AutoTransition transition = new AutoTransition();
    private int currentImage = 1;
    private boolean autoScrolling = false;

    public AutoScrollThumbView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_auto_scroll_thumb_view_1, this, true);
        image1 = findViewById(R.id.image_1);
        image2 = findViewById(R.id.image_2);
        image3 = findViewById(R.id.image_3);
        root = findViewById(R.id.root);
        transition.setDuration(600);
    }

    private void scroll12() {
        scroll(R.layout.layout_auto_scroll_thumb_view_2);
        currentImage = 2;
        autoScroll();
    }

    private void scroll23() {
        scroll(R.layout.layout_auto_scroll_thumb_view_3);
        currentImage = 3;
        autoScroll();
    }

    private void scroll31() {
        scroll(R.layout.layout_auto_scroll_thumb_view_1);
        currentImage = 1;
        autoScroll();
    }

    private void scroll(int constraintLayoutId) {
        ConstraintSet set1 = new ConstraintSet();
        set1.clone(root);
        ConstraintSet set2 = new ConstraintSet();
        set2.clone(getContext(), constraintLayoutId);
        TransitionManager.beginDelayedTransition(root, transition);
        set2.applyTo(root);
        autoScrolling = false;
    }

    public void loadImages(String sku, int position) {
        String image1Link = sku + "_01_t.jpg";
        String image2Link = sku + "_02_t.jpg";
        String image3Link = sku + "_03_t.jpg";

        if (position == 1) {
            image1Link = sku + "_02_t.jpg";
            image2Link = sku + "_03_t.jpg";
            image3Link = sku + "_01_t.jpg";
        }

        if (position == 2) {
            image1Link = sku + "_03_t.jpg";
            image2Link = sku + "_01_t.jpg";
            image3Link = sku + "_02_t.jpg";
        }

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
        autoScroll();
    }


    public void autoScroll() {
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
