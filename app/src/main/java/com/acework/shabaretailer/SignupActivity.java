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
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.model.Retailer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private MaterialButton back, join, viewTc;
    private TextInputLayout businessName, name, email, password, confirmPassword, county, street;
    private CheckBox tc;
    private ScrollView scrollView;
    private StatusDialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        bindViews();
        setListeners();
        initializeCounties();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        join = findViewById(R.id.join_button);
        businessName = findViewById(R.id.business_name_input);
        name = findViewById(R.id.name_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        confirmPassword = findViewById(R.id.password_confirmation_input);
        tc = findViewById(R.id.tc_checkbox);
        viewTc = findViewById(R.id.view_tc);
        scrollView = findViewById(R.id.scroll_view);
        county = findViewById(R.id.county_input);
        street = findViewById(R.id.street_input);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        join.setOnClickListener(v -> join());
        viewTc.setOnClickListener(v -> viewTc());
        tc.setOnCheckedChangeListener((buttonView, isChecked) -> hideKeyboard());
    }

    @SuppressWarnings("ConstantConditions")
    private void join() {
        if (validate()) {
            statusDialog = StatusDialog.newInstance(R.raw.loading, "Attempting to sign you up", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            Retailer retailer = getRetailer();
            String emailString = email.getEditText().getText().toString().trim();
            String passwordString = password.getEditText().getText().toString().trim();

            firebaseAuth.fetchSignInMethodsForEmail(emailString).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                createRetailer(retailer);
                            } else {
                                statusDialog.dismiss();
                                Snackbar.make(back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        statusDialog.dismiss();
                        Snackbar.make(back, "The provided email address is already in use.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    statusDialog.dismiss();
                    Snackbar.make(back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validate() {
        clearErrors();
        hideKeyboard();
        if (businessName.getEditText().getText().toString().trim().isEmpty()) {
            businessName.setError("This field is required");
            scroll(businessName);
            return false;
        }
        if (name.getEditText().getText().toString().trim().isEmpty()) {
            name.setError("This field is required");
            scroll(name);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString().trim()).matches()) {
            email.setError("Invalid email address");
            return false;
        }
        if (password.getEditText().getText().toString().length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return false;
        }
        if (!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) {
            confirmPassword.setError("Passwords do not match");
            return false;
        }
        if (!tc.isChecked()) {
            Snackbar.make(tc, "You must read and accept the terms and conditions", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (county.getEditText().getText().toString().isEmpty()) {
            county.setError("This field is required");
            return false;
        }
        if (street.getEditText().getText().toString().trim().isEmpty()) {
            street.setError("This field is required");
            return false;
        }
        return true;
    }

    private void clearErrors() {
        businessName.setError(null);
        name.setError(null);
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        county.setError(null);
        street.setError(null);
    }

    private void scroll(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.scrollToDescendant(view);
        } else {
            scrollView.fullScroll(View.FOCUS_UP);
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
        ((AutoCompleteTextView) county.getEditText()).setAdapter(adapter);
    }

    @SuppressWarnings("ConstantConditions")
    private Retailer getRetailer() {
        return new Retailer(
                name.getEditText().getText().toString().trim(),
                businessName.getEditText().getText().toString().trim(),
                "-",
                county.getEditText().getText().toString(),
                street.getEditText().getText().toString().trim(),
                email.getEditText().getText().toString().trim());
    }

    @SuppressWarnings("ConstantConditions")
    private void createRetailer(Retailer retailer) {
        String uid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("retailers").document(uid).set(retailer).addOnCompleteListener(task -> {
            statusDialog.dismiss();
            if (task.isSuccessful()) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> toAccountVerification());
            } else {
                Snackbar.make(back, "Could not sign you up now. Please try again later.", Snackbar.LENGTH_LONG).show();
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
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }
}