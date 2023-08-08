package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.databinding.ActivitySignupBinding;
import com.acework.shabaretailer.model.RetailerNew;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    FirebaseAuth firebaseAuth;
    private StatusDialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initializeCounties();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        binding.back.setOnClickListener(v -> finish());
        binding.join.setOnClickListener(v -> join());
        binding.viewTc.setOnClickListener(v -> viewTc());
        binding.tc.setOnCheckedChangeListener((buttonView, isChecked) -> hideKeyboard());
    }

    @SuppressWarnings("ConstantConditions")
    private void join() {
        if (validate()) {
            statusDialog = StatusDialog.newInstance(R.raw.loading, "Attempting to sign you up", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            RetailerNew retailer = getRetailer();
            String emailString = binding.emailField.getText().toString().trim();
            String passwordString = binding.passwordField.getText().toString().trim();

            firebaseAuth.fetchSignInMethodsForEmail(emailString).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                createRetailer(retailer);
                            } else {
                                statusDialog.dismiss();
                                Snackbar.make(binding.back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        statusDialog.dismiss();
                        Snackbar.make(binding.back, "The provided email address is already in use.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    statusDialog.dismiss();
                    Snackbar.make(binding.back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validate() {
        clearErrors();
        hideKeyboard();
        if (binding.nameField.getText().toString().trim().isEmpty()) {
            binding.name.setError("This field is required");
            scroll(binding.name);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailField.getText().toString().trim()).matches()) {
            binding.email.setError("Invalid email address");
            return false;
        }
        if (binding.passwordField.getText().toString().length() < 8) {
            binding.password.setError("Password must be at least 8 characters long");
            return false;
        }
        if (!binding.confirmPasswordField.getText().toString().equals(binding.passwordField.getText().toString())) {
            binding.confirmPassword.setError("Passwords do not match");
            return false;
        }
        if (!binding.tc.isChecked()) {
            Snackbar.make(binding.back, "You must read and accept the terms and conditions", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (binding.countyField.getText().toString().isEmpty()) {
            binding.county.setError("This field is required");
            return false;
        }
        if (binding.townField.getText().toString().trim().isEmpty()) {
            binding.town.setError("This field is required");
            return false;
        }
        return true;
    }

    private void clearErrors() {
        binding.name.setError(null);
        binding.email.setError(null);
        binding.county.setError(null);
        binding.town.setError(null);
        binding.password.setError(null);
        binding.confirmPassword.setError(null);
    }

    private void scroll(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.scrollView.scrollToDescendant(view);
        } else {
            binding.scrollView.fullScroll(View.FOCUS_UP);
        }
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
        binding.countyField.setAdapter(adapter);
    }

    @SuppressWarnings("ConstantConditions")
    private RetailerNew getRetailer() {
        RetailerNew retailer = new RetailerNew();
        retailer.name = binding.nameField.getText().toString().trim();
        retailer.email = binding.emailField.getText().toString().trim();
        retailer.county = binding.countyField.getText().toString().trim();
        retailer.town = binding.townField.getText().toString().trim();
        return retailer;
    }

    @SuppressWarnings("ConstantConditions")
    private void createRetailer(RetailerNew retailer) {
        String uid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch writeBatch = db.batch();

        writeBatch.set(db.collection("retailers").document(uid), retailer);

        writeBatch.commit().addOnCompleteListener(task -> {
            statusDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> toAccountVerification());
            } else {
                Snackbar.make(binding.back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
                FirebaseAuth.getInstance().getCurrentUser().delete();
            }
        });
    }

    private void toAccountVerification() {
        Intent intent = new Intent(this, AccountVerificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void viewTc() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.theshaba.com/terms-of-use"));
        startActivity(browserIntent);
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(binding.back.getWindowToken(), 0);
    }
}