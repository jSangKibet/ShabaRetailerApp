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
import com.acework.shabaretailer.databinding.DialogChooseBoxBinding;
import com.acework.shabaretailer.model.Box;

@SuppressWarnings("ConstantConditions")
public class ChooseBoxDialog extends DialogFragment {
    public static final String TAG = "choose_box_dialog";
    private DialogChooseBoxBinding binding;
    private ObjectHandler<Box> boxSelectedHandler;

    public ChooseBoxDialog() {
    }

    public static ChooseBoxDialog newInstance(ObjectHandler<Box> boxSelectedHandler) {
        ChooseBoxDialog dialog = new ChooseBoxDialog();
        dialog.boxSelectedHandler = boxSelectedHandler;
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
        binding = DialogChooseBoxBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        WindowManager.LayoutParams lp = requireDialog().getWindow().getAttributes();
        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.BOTTOM;
        requireDialog().getWindow().setAttributes(lp);
    }
}
