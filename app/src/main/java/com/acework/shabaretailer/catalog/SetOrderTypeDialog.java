package com.acework.shabaretailer.catalog;

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
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.atlas.ObjectHandler;
import com.acework.shabaretailer.databinding.DialogSetOrderTypeBinding;

@SuppressWarnings("ConstantConditions")
public class SetOrderTypeDialog extends DialogFragment {
    public static final String TAG = "set_order_type_dialog";
    private DialogSetOrderTypeBinding binding;
    private int currentOrderType;
    private ObjectHandler<Integer> orderTypeSelectedHandler;

    public SetOrderTypeDialog() {
    }

    public static SetOrderTypeDialog newInstance(int currentOrderType, ObjectHandler<Integer> orderTypeSelectedHandler) {
        SetOrderTypeDialog dialog = new SetOrderTypeDialog();
        dialog.currentOrderType = currentOrderType;
        dialog.orderTypeSelectedHandler = orderTypeSelectedHandler;
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
        binding = DialogSetOrderTypeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeOrderTypes();
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
        binding.okay.setOnClickListener(v -> setOrderType());
        binding.cancel.setOnClickListener(v -> dismiss());
        setOrderTypeListener();
    }

    private void setOrderType() {
        int orderType = 0;
        String selectedType = binding.orderTypeLayout.getEditText().getText().toString();
        if (selectedType.equals("Consignment")) {
            orderType = 1;
        }
        if (selectedType.equals("Commission")) {
            orderType = 2;
        }
        orderTypeSelectedHandler.handle(orderType);
        dismiss();
    }

    private void initializeOrderTypes() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.order_types));
        binding.orderType.setAdapter(adapter);
        binding.orderType.setText(Atlas.getOrderTypeAsString(currentOrderType), false);
    }

    private void setOrderTypeListener() {
        binding.orderTypeLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.okay.setEnabled(!s.toString().equals("Please set an order type"));
            }
        });
    }
}
