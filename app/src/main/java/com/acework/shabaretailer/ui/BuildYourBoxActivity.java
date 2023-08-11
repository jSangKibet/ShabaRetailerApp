package com.acework.shabaretailer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.ActivityBuildYourBoxBinding;

import java.util.ArrayList;
import java.util.List;

public class BuildYourBoxActivity extends AppCompatActivity {
    ActivityBuildYourBoxBinding binding;
    private int wahura = 0;
    private int twende = 0;
    private String orderType = "Wholesale";
    private int sink = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuildYourBoxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initializeOrderChoices();
        setOrderTypeChangeListener();
        updateUI();
    }

    private void setListeners() {
        binding.back.setOnClickListener(v -> finish());
        binding.toChooseIc.setOnClickListener(v -> toChooseInsertColors());
        binding.wahuraMinus.setOnClickListener(v -> wahuraMinus());
        binding.wahuraPlus.setOnClickListener(v -> wahuraPlus());
        binding.twendeMinus.setOnClickListener(v -> twendeMinus());
        binding.twendePlus.setOnClickListener(v -> twendePlus());
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
                calculateTotal();
            }
        });
    }

    private void calculateTotal() {
        int total = (Atlas.getWahuraPrice(orderType) * wahura) + (Atlas.getTwendePrice(orderType) * twende);
        binding.total.setText(getString(R.string.box_total_ph, orderType, total));
    }

    private void wahuraMinus() {
        wahura--;
        updateUI();
    }

    private void wahuraPlus() {
        wahura++;
        updateUI();
    }

    private void twendeMinus() {
        twende--;
        updateUI();
    }

    private void twendePlus() {
        twende++;
        updateUI();
    }

    private void updateUI() {
        binding.wahura.setText(String.valueOf(wahura));
        binding.twende.setText(String.valueOf(twende));

        sink = (2 * wahura) + twende;
        binding.progress.setProgressCompat(getProgress(), true);
        calculateTotal();

        int remainder = 8 - sink;
        binding.wahuraPlus.setEnabled(remainder >= 2);
        binding.twendePlus.setEnabled(remainder >= 1);

        binding.wahuraMinus.setEnabled(wahura >= 1);
        binding.twendeMinus.setEnabled(twende >= 1);

        binding.toChooseIc.setEnabled(sink == 8);
    }

    private int getProgress() {
        switch (sink) {
            case 0:
                return 0;
            case 1:
                return 13;
            case 2:
                return 25;
            case 3:
                return 38;
            case 4:
                return 50;
            case 5:
                return 63;
            case 6:
                return 75;
            case 7:
                return 88;
            case 8:
                return 100;
        }
        return 0;
    }

    private void toChooseInsertColors() {
        Intent intent = new Intent(this, ChooseInsertColorsActivity.class);
        intent.putExtra("orderType", orderType);
        intent.putExtra("wahura", wahura);
        intent.putExtra("twende", twende);
        startActivity(intent);
    }
}