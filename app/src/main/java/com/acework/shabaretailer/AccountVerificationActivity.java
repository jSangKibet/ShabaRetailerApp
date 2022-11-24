package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("ConstantConditions")
public class AccountVerificationActivity extends AppCompatActivity {
    private MaterialButton resendEmail, checkVerification, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        resendEmail = findViewById(R.id.resend_email);
        checkVerification = findViewById(R.id.check_verification);
        logout = findViewById(R.id.logout);
    }

    private void setListeners() {
        resendEmail.setOnClickListener(v -> resendEmail());
        checkVerification.setOnClickListener(v -> checkVerification());
        logout.setOnClickListener(v -> confirmLoggingOut());
    }

    private void resendEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(resendEmail, "Email sent. Check your inbox or spam folder.", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(resendEmail, "Could not send email, please try again later", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVerification() {
        FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(task -> {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Snackbar.make(resendEmail, "Account verified", Snackbar.LENGTH_SHORT).show();
                resendEmail.postDelayed(() -> {
                    startActivity(new Intent(AccountVerificationActivity.this, CatalogActivity.class));
                    finish();
                }, 1000);
            } else {
                Snackbar.make(resendEmail, "Your account has not yet been verified", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmLoggingOut() {
        new MaterialAlertDialogBuilder(this).
                setTitle("Confirm logging out").
                setMessage("Do you want to log out of Shaba Retailers?").
                setPositiveButton("Yes", (dialog, which) -> logout()).
                setNegativeButton("Cancel", null).show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}