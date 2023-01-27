package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.model.EmailChangeRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class ConfirmEmailChangeActivity extends AppCompatActivity {
    private MaterialButton back, submit;
    private TextInputLayout code;
    private EmailChangeRequest ecr;
    private StatusDialog sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email_change);
        bindViews();
        setListeners();
        String ecrString = getIntent().getStringExtra("ecr");
        if (ecrString == null) {
            finish();
        } else {
            ecr = new Gson().fromJson(ecrString, EmailChangeRequest.class);
            if (ecr == null) {
                finish();
            }
        }
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        submit = findViewById(R.id.submit);
        code = findViewById(R.id.code);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> submitClicked());
    }

    private void submitClicked() {
        if (validateInput()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm email change").
                    setMessage("Are you sure you want to change your email address to " + ecr.getEmail() + "?").
                    setPositiveButton("Yes", (dialog, which) -> applyChanges()).
                    setNegativeButton("Cancel", null).show();
        }
    }

    private void applyChanges() {
        sd = StatusDialog.newInstance(R.raw.loading, "Updating your email address", false, null);
        sd.show(getSupportFragmentManager(), StatusDialog.TAG);

        ecr.setStatus("Completed");
        DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference().child("ECR");
        shabaRealtimeDbRef.child(ecr.getId()).setValue(ecr).addOnCompleteListener(t1 -> {
            if (t1.isSuccessful()) {
                FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                if (u == null) {
                    sd.dismiss();
                    Snackbar.make(back, "Your email could not be updated at the moment. Please submit another request.", Snackbar.LENGTH_LONG).show();
                } else {
                    u.updateEmail(ecr.getEmail()).addOnCompleteListener(t2 -> {
                        sd.dismiss();
                        if (t2.isSuccessful()) {
                            sd = StatusDialog.newInstance(R.raw.success, "Your email was changed successfully. Please log in with your new email.", true, this::emailUpdated);
                            sd.show(getSupportFragmentManager(), StatusDialog.TAG);
                        } else {
                            Snackbar.make(back, "Your email could not be updated at the moment. Please submit another request.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                sd.dismiss();
                Snackbar.make(back, "Your email could not be updated at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (t1.getException() != null) {
                    t1.getException().printStackTrace();
                }
            }
        });
    }

    private boolean validateInput() {
        hideKeyboard();
        code.setError(null);
        //noinspection ConstantConditions
        if (code.getEditText().getText().toString().trim().equals(ecr.getCode())) {
            return true;
        } else {
            code.setError("Invalid verification code provided. Please check your email again.");
            return false;
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }

    private void emailUpdated() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}