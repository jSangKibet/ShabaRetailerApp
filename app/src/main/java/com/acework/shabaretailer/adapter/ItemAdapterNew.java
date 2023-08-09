package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.model.ItemNew;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapterNew extends RecyclerView.Adapter<ItemAdapterNew.ItemViewHolder> {
    private final LayoutInflater inflater;
    private final ObjectHandler<ItemNew> viewItemHandler;
    private final Lifecycle lifecycle;
    private List<ItemNew> items;

    public ItemAdapterNew(Context context, ObjectHandler<ItemNew> viewItemHandler, Lifecycle lifecycle) {
        this.inflater = LayoutInflater.from(context);
        this.viewItemHandler = viewItemHandler;
        items = new ArrayList<>();
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_item_new, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.images.registerLifecycle(lifecycle);

        ItemNew item = items.get(position);
        holder.name.setText(item.name);
        holder.view.setOnClickListener(v -> viewItemHandler.handle(item));

        loadImages(holder.images, item.sku);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ItemNew> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void loadImages(ImageCarousel carousel, String sku) {
        String image1Link = sku + "_01_t.jpg";
        String image2Link = sku + "_02_t.jpg";
        String image3Link = sku + "_03_t.jpg";

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
                    carousel.setData(carouselItems);
                });
            });
        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageCarousel images;
        private final MaterialButton view;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            images = itemView.findViewById(R.id.carousel);
            view = itemView.findViewById(R.id.view);
        }
    }
}
