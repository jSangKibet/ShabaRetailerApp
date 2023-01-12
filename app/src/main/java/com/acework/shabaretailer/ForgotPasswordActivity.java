package com.acework.shabaretailer;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("ConstantConditions")
public class ForgotPasswordActivity extends AppCompatActivity {
    private MaterialButton back, reset;
    private TextInputLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        reset = findViewById(R.id.reset);
        email = findViewById(R.id.email_input);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        reset.setOnClickListener(v -> reset());
    }

    private void reset() {
        if (validateEmail()) {
            StatusDialog dialog = StatusDialog.newInstance(R.raw.loading, "Checking...", false, null);
            dialog.show(getSupportFragmentManager(), StatusDialog.TAG);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getEditText().getText().toString().trim()).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    StatusDialog dialog2 = StatusDialog.newInstance(R.raw.success, "An email with instructions on how to reset your password has been sent to you. If you cannot see it, check your spam folder.", true, this::finish);
                    dialog2.show(getSupportFragmentManager(), StatusDialog.TAG);
                } else {
                    Snackbar.make(back, "There was an error resetting your password. Confirm your email address and try again.", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validateEmail() {
        hideKeyboard();
        email.setError(null);
        if (Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString().trim()).matches()) {
            return true;
        } else {
            email.setError("Invalid email address");
            return false;
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }
}