package com.acework.shabaretailer.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.databinding.ActivityPlaceOrderBinding;
import com.acework.shabaretailer.dialog.ChooseBoxDialog;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity {
    ActivityPlaceOrderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());
        binding.chooseBox.setOnClickListener(v -> chooseBox());

        initializeOrderChoices();
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

    private void chooseBox() {
        ChooseBoxDialog dialog = ChooseBoxDialog.newInstance(box -> {

        }, binding.orderTypeField.getText().toString().trim());
        dialog.show(getSupportFragmentManager(), ChooseBoxDialog.TAG);
    }
}