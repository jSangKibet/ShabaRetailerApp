package com.acework.shabaretailer.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.adapter.InsertColorChoicesAdapter;
import com.acework.shabaretailer.atlas.Atlas;
import com.acework.shabaretailer.databinding.ActivityChooseInsertColorsBinding;
import com.acework.shabaretailer.dialog.ChooseInsertColorOptionDialog;
import com.acework.shabaretailer.model.InsertColorChoice;
import com.acework.shabaretailer.model.OrderItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChooseInsertColorsActivity extends AppCompatActivity {
    private final List<InsertColorChoice> choices = new ArrayList<>();
    private final ActivityResultLauncher<Intent> confirmOrderLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    });
    private ActivityChooseInsertColorsBinding binding;
    private InsertColorChoicesAdapter adapter;
    private int twende;
    private int wahura;
    private String orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseInsertColorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());
        binding.addColorChoice.setOnClickListener(v -> toAddInsertColorChoice());
        binding.toSummary.setOnClickListener(v -> toConfirmOrder());

        wahura = getIntent().getIntExtra("wahura", 0);
        twende = getIntent().getIntExtra("twende", 0);
        orderType = getIntent().getStringExtra("orderType");

        adapter = new InsertColorChoicesAdapter(this, this::insertColorChoiceRemoved, choices);
        binding.list.setAdapter(adapter);
        updateUI();
    }

    private void toAddInsertColorChoice() {
        ChooseInsertColorOptionDialog dialog = ChooseInsertColorOptionDialog.newInstance(getRemainingWahura(), getRemainingTwende(), this::insertColorChoiceAdded);
        dialog.show(getSupportFragmentManager(), ChooseInsertColorOptionDialog.TAG);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void insertColorChoiceAdded(InsertColorChoice choice) {
        boolean choiceConsumed = false;
        for (InsertColorChoice alreadyChosen : choices) {
            if (choice.wahura > 0) {
                if (alreadyChosen.wahura > 0 && alreadyChosen.color.equals(choice.color)) {
                    alreadyChosen.wahura += choice.wahura;
                    choiceConsumed = true;
                    break;
                }
            } else {
                if (alreadyChosen.twende > 0 && alreadyChosen.color.equals(choice.color)) {
                    alreadyChosen.twende += choice.twende;
                    choiceConsumed = true;
                    break;
                }
            }
        }

        if (!choiceConsumed) choices.add(choice);
        adapter.notifyDataSetChanged();
        updateUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void insertColorChoiceRemoved(InsertColorChoice choice) {
        choices.remove(choice);
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private int getRemainingWahura() {
        int totalWahura = 0;
        for (InsertColorChoice choice : choices) {
            totalWahura += choice.wahura;
        }
        return wahura - totalWahura;
    }

    private int getRemainingTwende() {
        int totalTwende = 0;
        for (InsertColorChoice choice : choices) {
            totalTwende += choice.twende;
        }
        return twende - totalTwende;
    }

    private void updateUI() {
        int remainingWahura = getRemainingWahura();
        int remainingTwende = getRemainingTwende();

        if (remainingWahura > 0) {
            binding.wahura.setText(getString(R.string.box_wahura_ph, remainingWahura));
            binding.wahura.setVisibility(View.VISIBLE);
        } else {
            binding.wahura.setVisibility(View.GONE);
        }
        if (remainingTwende > 0) {
            binding.twende.setText(getString(R.string.box_twende_ph, remainingTwende));
            binding.twende.setVisibility(View.VISIBLE);
        } else {
            binding.twende.setVisibility(View.GONE);
        }
        if ((remainingTwende + remainingWahura) == 0) {
            binding.title.setText(R.string.all_bags_set);
            binding.toSummary.setEnabled(true);
        } else {
            binding.title.setText(R.string.unaccounted_bags);
            binding.toSummary.setEnabled(false);
        }
    }

    private void toConfirmOrder() {
        Intent intent = new Intent(this, ConfirmOrderActivity.class);
        intent.putExtra("orderType", orderType);
        intent.putExtra("orderItems", new Gson().toJson(getOrderItems()));
        confirmOrderLauncher.launch(intent);
    }

    private List<OrderItem> getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        for (InsertColorChoice choice : choices) {
            OrderItem item = new OrderItem();
            item.insertColor = choice.color;
            if (choice.wahura > 0) {
                item.sku = "3";
                item.quantity = choice.wahura;
                item.price = Atlas.getWahuraPrice(orderType);
            } else {
                item.sku = "1";
                item.quantity = choice.twende;
                item.price = Atlas.getTwendePrice(orderType);
            }
            orderItems.add(item);
        }
        return orderItems;
    }
}