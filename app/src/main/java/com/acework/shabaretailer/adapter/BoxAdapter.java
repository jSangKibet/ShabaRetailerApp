package com.acework.shabaretailer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.model.Box;

import java.util.ArrayList;
import java.util.List;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.BoxVH> {
    private final LayoutInflater inflater;
    private final Context context;
    private final ObjectHandler<Box> boxSelectionHandler;
    private List<Box> boxes;
    private final String orderType;

    public BoxAdapter(Context context, ObjectHandler<Box> boxSelectionHandler, String orderType) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.boxSelectionHandler = boxSelectionHandler;
        this.orderType = orderType;
        boxes = new ArrayList<>();
    }

    @NonNull
    @Override
    public BoxVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_box, parent, false);
        return new BoxVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxVH holder, int position) {
        Box box = boxes.get(position);
        holder.name.setText(context.getString(R.string.box_name_ph, box.number, box.name));
        if (box.wahura > 0) {
            holder.wahura.setText(context.getString(R.string.box_wahura_ph, box.wahura));
            holder.wahura.setVisibility(View.VISIBLE);
        } else {
            holder.wahura.setVisibility(View.GONE);
        }
        if (box.twende > 0) {
            holder.twende.setText(context.getString(R.string.box_twende_ph, box.twende));
            holder.twende.setVisibility(View.VISIBLE);
        } else {
            holder.twende.setVisibility(View.GONE);
        }
        holder.root.setOnClickListener(v -> boxSelectionHandler.handle(box));

        int total = (Atlas.getWahuraPrice(orderType) * box.wahura) + (Atlas.getTwendePrice(orderType) * box.twende);
        holder.total.setText(context.getString(R.string.box_total_ph, orderType, total));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return boxes.size();
    }


    public static class BoxVH extends RecyclerView.ViewHolder {
        private final TextView name, wahura, twende, total;
        private final ConstraintLayout root;

        public BoxVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            wahura = itemView.findViewById(R.id.wahura);
            twende = itemView.findViewById(R.id.twende);
            total = itemView.findViewById(R.id.total);
            root = itemView.findViewById(R.id.root);
        }
    }
}
