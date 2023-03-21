package com.acework.shabaretailer.catalog;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.acework.shabaretailer.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageFragment extends Fragment {
    private ImageView image;
    private CircularProgressIndicator indicator;
    private String sku, name;
    private int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image = view.findViewById(R.id.image);
        indicator = view.findViewById(R.id.indicator);
        if (getArguments() != null) {
            sku = getArguments().getString("sku");
            pos = getArguments().getInt("pos", 0);
            name = getArguments().getString("name", "Handbag");
            if (sku != null && pos > 0) {
                loadImage();
            }
        }
        image.setOnClickListener(v -> imageClicked());
    }

    public void loadImage() {
        String rn = sku + "_0" + pos + "s.jpg";
        indicator.setVisibility(View.VISIBLE);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");

        shabaCSR.child(rn).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(image).load(task.getResult()).error(R.drawable.image_not_found).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        indicator.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        indicator.setVisibility(View.GONE);
                        return false;
                    }
                }).into(image);
            } else {
                indicator.setVisibility(View.VISIBLE);
                image.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.image_not_found));
            }
        });
    }

    private void imageClicked() {
        if (sku != null && pos > 0) {
            String rn = sku + "_0" + pos + ".jpg";
            Intent intent = new Intent(requireContext(), PreviewActivity.class);
            intent.putExtra("itemName", name);
            intent.putExtra("link", rn);
            startActivity(intent);
        }
    }
}
