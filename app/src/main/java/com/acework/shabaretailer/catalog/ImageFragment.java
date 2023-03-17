package com.acework.shabaretailer.catalog;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageFragment extends Fragment {
    private ImageView image;

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
        if (getArguments() != null) {
            String rn = getArguments().getString("rn", "-");
            if (!rn.equals("-")) {
                loadImage(rn);
            }
        }
    }

    public void loadImage(String rn) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");

        shabaCSR.child(rn).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(image).load(task.getResult()).error(R.drawable.image_not_found).into(image);
            } else {
                image.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.image_not_found));
            }
        });
    }
}
