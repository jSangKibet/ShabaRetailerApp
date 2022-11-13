package com.acework.shabaretailer;

import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {
    private MaterialButton back, join;
    private TextInputLayout businessName, name, telephone, email, password, confirmPassword;
    private CheckBox tc;
    private View readTc;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        join = findViewById(R.id.join_button);
        businessName = findViewById(R.id.business_name_input);
        name = findViewById(R.id.name_input);
        telephone = findViewById(R.id.telephone_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        confirmPassword = findViewById(R.id.password_confirmation_input);
        tc = findViewById(R.id.tc_checkbox);
        readTc = findViewById(R.id.read_tc_view);
        scrollView = findViewById(R.id.scroll_view);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        join.setOnClickListener(v -> join());
        readTc.setOnClickListener(v -> readTc());
    }

    private void join() {
        if (validate()) {
            Toast.makeText(this, "Join!", Toast.LENGTH_SHORT).show();
        }
    }

    private void readTc() {
        Toast.makeText(this, "Read T&C!", Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validate() {
        clearErrors();
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
        if (!telephone.getEditText().getText().toString().trim().matches("\\d{10}")) {
            telephone.setError("Invalid telephone number");
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
        return true;
    }

    private void clearErrors() {
        businessName.setError(null);
        name.setError(null);
        telephone.setError(null);
        email.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
    }

    private void scroll(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scrollView.scrollToDescendant(view);
        } else {
            scrollView.fullScroll(View.FOCUS_UP);
        }
    }
}