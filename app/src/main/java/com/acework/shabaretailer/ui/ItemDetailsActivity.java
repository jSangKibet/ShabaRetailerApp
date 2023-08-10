package com.acework.shabaretailer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.catalog.PreviewActivity;
import com.acework.shabaretailer.databinding.ActivityItemDetailsBinding;
import com.acework.shabaretailer.dialog.FeaturesDialogNew;
import com.acework.shabaretailer.model.ItemNew;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity {
    private ActivityItemDetailsBinding binding;
    private ItemNew item;
    private String sku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());
        binding.more.setOnClickListener(v -> showSpecifications());

        sku = getIntent().getStringExtra("sku");
        if (sku == null) {
            finish();
        } else {
            getItem(sku);
        }
    }

    private void getItem(String sku) {
        StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Getting bag info...", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseFirestore.getInstance().collection("items").document(sku).get().addOnSuccessListener(documentSnapshot -> {
            statusDialog.dismiss();
            item = documentSnapshot.toObject(ItemNew.class);
            setValues();
        }).addOnFailureListener(e -> {
            statusDialog.dismiss();
            Snackbar.make(binding.back, "There was an error getting the bag", Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> getItem(sku))
                    .show();
            e.printStackTrace();
        });
    }

    private void setValues() {
        if (item == null) {
            Snackbar.make(binding.back, "There was an error getting the bag", Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> getItem(sku))
                    .show();
        } else {
            binding.name.setText(item.name);
            binding.wsPrice.setText(getString(R.string.ksh_ph, item.priceWholesale));
            binding.consPrice.setText(getString(R.string.ksh_ph, item.priceConsignment));
            binding.commPrice.setText(getString(R.string.ksh_ph, item.priceShaba));
            loadImages();
        }
    }

    private void showSpecifications() {
        if (item != null) {
            FeaturesDialogNew dialog = FeaturesDialogNew.newInstance(item);
            dialog.show(getSupportFragmentManager(), FeaturesDialogNew.TAG);
        }
    }

    private void loadImages() {
        String image1Link = sku + "_01s.jpg";
        String image2Link = sku + "_02s.jpg";
        String image3Link = sku + "_03s.jpg";

        List<CarouselItem> carouselItems = new ArrayList<>();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference shabaCSR = firebaseStorage.getReference().child("item_images");


        shabaCSR.child(image1Link).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                carouselItems.add(new CarouselItem(task.getResult().toString()));
            }
            shabaCSR.child(image2Link).getDownloadUrl().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    carouselItems.add(new CarouselItem(task2.getResult().toString()));
                }
                shabaCSR.child(image3Link).getDownloadUrl().addOnCompleteListener(task3 -> {
                    if (task3.isSuccessful()) {
                        carouselItems.add(new CarouselItem(task3.getResult().toString()));
                    }
                    binding.carousel.setData(carouselItems);
                    startCarousel();
                });
            });
        });
    }

    private void startCarousel() {
        binding.carousel.registerLifecycle(getLifecycle());
        binding.carousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                imageClicked(i + 1);
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });
    }

    private void imageClicked(int pos) {
        if (item != null) {
            String rn = sku + "_0" + pos + ".jpg";
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra("itemName", item.name);
            intent.putExtra("link", rn);
            startActivity(intent);
        }
    }
}