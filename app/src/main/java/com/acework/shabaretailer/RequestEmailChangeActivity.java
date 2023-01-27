package com.acework.shabaretailer;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.model.EmailChangeRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestEmailChangeActivity extends AppCompatActivity {
    private MaterialButton back, submit;
    private TextInputLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_email_change);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        submit = findViewById(R.id.submit);
        email = findViewById(R.id.email);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> submitClicked());
    }

    private void submitClicked() {
        if (validateInput()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm email change request").
                    setMessage("Would you like to submit a request to change your email to the one you have provided?").
                    setPositiveButton("Yes", (dialog, which) -> submitRequest()).
                    setNegativeButton("Cancel", null).show();
        }
    }

    private void submitRequest() {
        StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Submitting request", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        String uid = FirebaseAuth.getInstance().getUid();
        //noinspection ConstantConditions
        EmailChangeRequest ecr = new EmailChangeRequest(uid, email.getEditText().getText().toString().trim(), "Pending", "", "", System.currentTimeMillis());

        DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("ECR");
        shabaRealtimeDbRef.child(ecr.getId()).setValue(ecr).addOnCompleteListener(task -> {
            statusDialog.dismiss();
            if (task.isSuccessful()) {
                StatusDialog statusDialog2 = StatusDialog.newInstance(R.raw.success, "Request submitted. A verification code will be sent to your new email address within 48 hours.", true, this::finish);
                statusDialog2.show(getSupportFragmentManager(), StatusDialog.TAG);
            } else {
                Snackbar.make(back, "Your request could not be submitted at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    private boolean validateInput() {
        hideKeyboard();
        email.setError(null);
        //noinspection ConstantConditions
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