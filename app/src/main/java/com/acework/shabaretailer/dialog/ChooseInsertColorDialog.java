package com.acework.shabaretailer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.databinding.DialogChooseInsertColorBinding;

@SuppressWarnings("ConstantConditions")
public class ChooseInsertColorDialog extends DialogFragment {
    public static final String TAG = "choose_insert_color_dialog";
    private DialogChooseInsertColorBinding binding;
    private ObjectHandler<String> colorSelectionHandler;

    public ChooseInsertColorDialog() {
    }

    public static ChooseInsertColorDialog newInstance(ObjectHandler<String> colorSelectionHandler) {
        ChooseInsertColorDialog dialog = new ChooseInsertColorDialog();
        dialog.colorSelectionHandler = colorSelectionHandler;
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
        binding = DialogChooseInsertColorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = requireDialog().getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.BOTTOM;
        requireDialog().getWindow().setAttributes(lp);
        requireDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void setListeners() {
        binding.mustard.setOnClickListener(v -> colorSelected("Mustard"));
        binding.maroon.setOnClickListener(v -> colorSelected("Maroon"));
        binding.darkBrown.setOnClickListener(v -> colorSelected("Dark brown"));
        binding.dustyPink.setOnClickListener(v -> colorSelected("Dusty pink"));
        binding.taupe.setOnClickListener(v -> colorSelected("Taupe"));
        binding.cancel.setOnClickListener(v -> dismissGracefully());
    }

    private void colorSelected(String color) {
        colorSelectionHandler.handle(color);
        dismissGracefully();
    }

    private void dismissGracefully() {
        if (isVisible()) {
            dismissAllowingStateLoss();
        }
    }
}
