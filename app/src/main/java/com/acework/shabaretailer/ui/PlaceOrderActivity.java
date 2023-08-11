package com.acework.shabaretailer.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.ActivityPlaceOrderBinding;
import com.acework.shabaretailer.dialog.ChooseBoxDialog;
import com.acework.shabaretailer.model.Box;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {
    ActivityPlaceOrderBinding binding;
    private Box box;
    private String orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());
        binding.chooseBox.setOnClickListener(v -> chooseBox());

        initializeOrderChoices();
        setOrderTypeChangeListener();
    }

    private void initializeOrderChoices() {
        List<String> choices = new ArrayList<>();
        choices.add("Wholesale");
        choices.add("Consignment");
        choices.add("Commission");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, choices);
        binding.orderTypeField.setAdapter(adapter);
        binding.orderTypeField.setText(getString(R.string.wholesale), false);
    }

    private void setOrderTypeChangeListener() {
        binding.orderTypeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                orderType = s.toString();
                displayBox();
            }
        });
    }

    private void chooseBox() {
        ChooseBoxDialog dialog = ChooseBoxDialog.newInstance(this::boxChosen, binding.orderTypeField.getText().toString().trim());
        dialog.show(getSupportFragmentManager(), ChooseBoxDialog.TAG);
    }

    private void boxChosen(Box box) {
        this.box = box;
        displayBox();
    }

    private void displayBox() {
        if (box != null) {
            binding.name.setText(getString(R.string.box_name_ph, box.number, box.name));
            if (box.wahura > 0) {
                binding.wahura.setText(getString(R.string.box_wahura_ph, box.wahura));
                binding.wahura.setVisibility(View.VISIBLE);
            } else {
                binding.wahura.setVisibility(View.GONE);
            }
            if (box.twende > 0) {
                binding.twende.setText(getString(R.string.box_twende_ph, box.twende));
                binding.twende.setVisibility(View.VISIBLE);
            } else {
                binding.twende.setVisibility(View.GONE);
            }

            int total = (Atlas.getWahuraPrice(orderType) * box.wahura) + (Atlas.getTwendePrice(orderType) * box.twende);
            binding.total.setText(getString(R.string.box_total_ph, orderType, total));

            binding.boxDetails.setVisibility(View.VISIBLE);
        }
    }
}