package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivityEditRetailerBinding;
import com.acework.shabaretailer.model.RetailerNew;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EditRetailerActivity extends AppCompatActivity {
    ActivityEditRetailerBinding binding;
    private RetailerNew retailer;
    private StatusDialog statusDialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditRetailerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initializeCounties();
        loadRetailer();
    }

    private void setListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        binding.changePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        binding.saveChanges.setOnClickListener(v -> saveChanges());
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeCounties() {
        List<String> countyList = new ArrayList<>();
        countyList.add("Baringo");
        countyList.add("Bomet");
        countyList.add("Bungoma");
        countyList.add("Busia");
        countyList.add("Elgeyo Marakwet");
        countyList.add("Embu");
        countyList.add("Garissa");
        countyList.add("Homa Bay");
        countyList.add("Isiolo");
        countyList.add("Kajiado");
        countyList.add("Kakamega");
        countyList.add("Kericho");
        countyList.add("Kiambu");
        countyList.add("Kilifi");
        countyList.add("Kirinyaga");
        countyList.add("Kisii");
        countyList.add("Kisumu");
        countyList.add("Kitui");
        countyList.add("Kwale");
        countyList.add("Laikipia");
        countyList.add("Lamu");
        countyList.add("Machakos");
        countyList.add("Makueni");
        countyList.add("Mandera");
        countyList.add("Marsabit");
        countyList.add("Meru");
        countyList.add("Migori");
        countyList.add("Mombasa");
        countyList.add("Murang'a");
        countyList.add("Nairobi");
        countyList.add("Nakuru");
        countyList.add("Nandi");
        countyList.add("Narok");
        countyList.add("Nyamira");
        countyList.add("Nyandarua");
        countyList.add("Nyeri");
        countyList.add("Samburu");
        countyList.add("Siaya");
        countyList.add("Taita/Taveta");
        countyList.add("Tana River");
        countyList.add("Tharaka-Nithi");
        countyList.add("Trans Nzoia");
        countyList.add("Turkana");
        countyList.add("Uasin Gishu");
        countyList.add("Vihiga");
        countyList.add("Wajir");
        countyList.add("West Pokot");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, countyList);
        binding.county.setAdapter(adapter);
    }

    private void loadRetailer() {
        statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching your information", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            loadingRetailerFailed();
        } else {
            FirebaseFirestore.getInstance().collection("retailers").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (statusDialog.isVisible()) statusDialog.dismissAllowingStateLoss();
                retailer = documentSnapshot.toObject(RetailerNew.class);
                if (retailer == null) {
                    Log.e("FirebaseError", "Null");
                    loadingRetailerFailed();
                } else {
                    setValues();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                loadingRetailerFailed();
            });
        }
    }

    private void saveChanges() {
        if (validate()) {
            updateRetailer();
            {
                statusDialog = StatusDialog.newInstance(R.raw.loading, "Updating your information", false, null);
                statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

                FirebaseFirestore.getInstance().collection("retailers").document(user.getUid()).set(retailer).addOnCompleteListener(task -> {
                    statusDialog.dismiss();
                    if (task.isSuccessful()) {
                        statusDialog = StatusDialog.newInstance(R.raw.success, "Your information has been updated.", true, () -> {
                            setResult(RESULT_OK);
                            finish();
                        });
                        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
                    } else {
                        Snackbar.make(binding.backButton, "There was an error updating your information. Please try again later.", Snackbar.LENGTH_LONG).show();
                        if (task.getException() != null) task.getException().printStackTrace();
                    }
                });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validate() {
        clearErrors();
        hideKeyboard();

        if (binding.name.getEditText().getText().toString().trim().isEmpty()) {
            binding.name.setError("This field is required");
            return false;
        }

        if (binding.county.getText().toString().isEmpty()) {
            binding.countyInput.setError("This field is required");
            return false;
        }

        if (binding.street.getEditText().getText().toString().trim().isEmpty()) {
            binding.street.setError("This field is required");
            return false;
        }

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void setValues() {
        binding.name.getEditText().setText(retailer.name);
        binding.county.setText(retailer.county, false);
        binding.street.getEditText().setText(retailer.town);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateRetailer() {
        retailer.name = binding.name.getEditText().getText().toString().trim();
        retailer.county = binding.county.getText().toString();
        retailer.town = binding.street.getEditText().getText().toString().trim();
    }


    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(binding.backButton.getWindowToken(), 0);
    }

    private void clearErrors() {
        binding.name.setError(null);
        binding.county.setError(null);
        binding.street.setError(null);
    }

    private void loadingRetailerFailed() {
        if (statusDialog.isVisible()) statusDialog.dismissAllowingStateLoss();
        StatusDialog dialog = StatusDialog.newInstance(R.raw.failed, "There was an error getting your details. Please try again later, or contact support.", true, this::finish);
        dialog.show(getSupportFragmentManager(), StatusDialog.TAG);
    }
}