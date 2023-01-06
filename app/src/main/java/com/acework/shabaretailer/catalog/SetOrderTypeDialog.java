package com.acework.shabaretailer.catalog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.viewmodel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

@SuppressWarnings("ConstantConditions")
public class SetOrderTypeDialog extends DialogFragment {
    public static final String TAG = "set_order_type_dialog";
    private MaterialButton okay, cancel;
    private TextInputLayout orderTypeLayout;
    private CartViewModel cartViewModel;

    public SetOrderTypeDialog() {
    }

    public static SetOrderTypeDialog newInstance(CartViewModel cartViewModel) {
        SetOrderTypeDialog dialog = new SetOrderTypeDialog();
        dialog.cartViewModel = cartViewModel;
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
        return inflater.inflate(R.layout.dialog_set_order_type, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
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

    private void bindViews(View view) {
        orderTypeLayout = view.findViewById(R.id.order_type_layout);
        okay = view.findViewById(R.id.okay);
        cancel = view.findViewById(R.id.cancel);
    }

    private void setListeners() {
        okay.setOnClickListener(v -> setOrderType());
        cancel.setOnClickListener(v -> dismiss());
    }

    private void setOrderType() {
        int orderType = CartViewModel.ORDER_TYPE_WHOLESALE;
        String selectedType = orderTypeLayout.getEditText().getText().toString();
        if (selectedType.equals("Consignment")) {
            orderType = CartViewModel.ORDER_TYPE_CONSIGNMENT;
        }
        if (selectedType.equals("Commission")) {
            orderType = CartViewModel.ORDER_TYPE_COMMISSION;
        }
        cartViewModel.setOrderType(orderType);
        dismiss();
    }

    private void initializeOrderTypes() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.order_types));
        ((AutoCompleteTextView) orderTypeLayout.getEditText()).setAdapter(adapter);
        ((AutoCompleteTextView) orderTypeLayout.getEditText()).setText(getCurrentOrderType(), false);
    }

    private String getCurrentOrderType() {
        switch (cartViewModel.getOrderType().getValue()) {
            case 2:
                return "Commission";
            case 1:
                return "Consignment";
            default:
                return "Wholesale";
        }
    }
}
