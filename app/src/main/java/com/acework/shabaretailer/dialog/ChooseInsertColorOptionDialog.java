package com.acework.shabaretailer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.databinding.DialogChooseInsertColorOptionBinding;
import com.acework.shabaretailer.model.InsertColorChoice;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ChooseInsertColorOptionDialog extends DialogFragment {
    public static final String TAG = "choose_insert_color_option_dialog";
    private DialogChooseInsertColorOptionBinding binding;
    private int wahura;
    private int twende;
    private ObjectHandler<InsertColorChoice> choiceSelectedHandler;

    public ChooseInsertColorOptionDialog() {
    }

    public static ChooseInsertColorOptionDialog newInstance(int wahura, int twende, ObjectHandler<InsertColorChoice> choiceSelectedHandler) {
        ChooseInsertColorOptionDialog dialog = new ChooseInsertColorOptionDialog();
        dialog.wahura = wahura;
        dialog.twende = twende;
        dialog.choiceSelectedHandler = choiceSelectedHandler;
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
        binding = DialogChooseInsertColorOptionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeBags();
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
        binding.colorLayout.setOnClickListener(v -> chooseColor());
        binding.okay.setOnClickListener(v -> confirmSelection());
        binding.cancel.setOnClickListener(v -> dismissGracefully());
        setBagChangeListener();
        setQtyChangeListener();
    }

    private void confirmSelection() {
        InsertColorChoice choice = new InsertColorChoice();
        choice.color = binding.colorName.getText().toString();
        if (binding.bag.getText().toString().equals("Wahura")) {
            choice.wahura = Integer.parseInt(binding.qty.getText().toString());
        } else {
            choice.twende = Integer.parseInt(binding.qty.getText().toString());
        }
        choiceSelectedHandler.handle(choice);
        dismissGracefully();
    }

    private void initializeBags() {
        List<String> bagChoices = new ArrayList<>();
        if (wahura > 0) bagChoices.add("Wahura");
        if (twende > 0) bagChoices.add("Twende");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, bagChoices);
        binding.bag.setAdapter(adapter);
    }

    private void setBagChangeListener() {
        binding.bag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setQuantityChoices(s.toString());
            }
        });
    }

    private void setQtyChangeListener() {
        binding.qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.okay.setEnabled(!s.toString().isEmpty());
            }
        });
    }

    private void setQuantityChoices(String bag) {
        binding.qty.setText("", false);
        List<String> qtyChoices = new ArrayList<>();
        if (bag.equals("Wahura")) {
            for (int i = 1; i <= wahura; i++) {
                qtyChoices.add(String.valueOf(i));
            }
        } else {
            for (int i = 1; i <= twende; i++) {
                qtyChoices.add(String.valueOf(i));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, qtyChoices);
        binding.qty.setAdapter(adapter);
    }

    private void chooseColor() {
        ChooseInsertColorDialog dialog = ChooseInsertColorDialog.newInstance(this::colorSelected);
        dialog.show(getChildFragmentManager(), ChooseInsertColorDialog.TAG);
    }

    private void colorSelected(String color) {
        binding.colorName.setText(color);
        switch (color) {
            case "Mustard":
                binding.colorImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.mustard_circle));
                break;
            case "Maroon":
                binding.colorImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.maroon_circle));
                break;
            case "Dark brown":
                binding.colorImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dark_brown_circle));
                break;
            case "Dusty pink":
                binding.colorImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dusty_pink_circle));
                break;
            default:
                binding.colorImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.taupe_circle));
        }
    }

    private void dismissGracefully() {
        if (isVisible()) dismissAllowingStateLoss();
    }
}
