package com.acework.shabaretailer.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.databinding.DialogFeaturesBinding;
import com.acework.shabaretailer.model.Item;

public class FeaturesDialog extends DialogFragment {
    public static final String TAG = "features_dialog";
    private DialogFeaturesBinding binding;
    private Item item;
    private LayoutInflater layoutInflater;

    public FeaturesDialog() {
    }

    public static FeaturesDialog newInstance(Item item) {
        FeaturesDialog dialog = new FeaturesDialog();
        dialog.item = item;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.WindowAnimationUpDown;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFeaturesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutInflater = LayoutInflater.from(requireContext());
        binding.close.setOnClickListener(v -> dismiss());
        setItemDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height=getResources().getDisplayMetrics().heightPixels;
        WindowManager.LayoutParams lp = requireDialog().getWindow().getAttributes();
        lp.width = width;
        lp.height=height;
        lp.gravity = Gravity.BOTTOM;
        requireDialog().getWindow().setAttributes(lp);
    }

    private void setItemDetails() {
        binding.description.setText(item.getDescription());
        binding.size.setText(item.getSize());
        binding.material.setText(item.getMaterial());
        binding.weaving.setText(item.getWeaving());
        binding.color.setText(item.getColour());
        binding.strap.setText(item.getStrap());
        binding.insert.setText(item.getInsert());
        binding.weight.setText(getString(R.string.weight_formatted, item.getWeight()));
        binding.sku.setText(item.getSku());
        binding.strapLength.setText(item.getStrapLength());
        setFeatures();
    }

    private void setFeatures() {
        binding.features.removeAllViews();
        for (String feature : item.getFeatures()) {
            @SuppressLint("InflateParams")
            TextView textView = (TextView) layoutInflater.inflate(R.layout.view_textview, null);
            textView.setText(getString(R.string.bullet_item, feature));
            binding.features.addView(textView);
        }
    }
}
