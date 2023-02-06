package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.model.EmailChangeRequest;
import com.acework.shabaretailer.model.Retailer;
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
    private TextInputLayout code, password;
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
        password = findViewById(R.id.password);
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

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        //noinspection ConstantConditions
        FirebaseAuth.getInstance().signInWithEmailAndPassword(u.getEmail(), password.getEditText().getText().toString()).addOnCompleteListener(signInTask -> {
            if (signInTask.isSuccessful()) {
                ecr.setStatus("Completed");
                DatabaseReference shabaRealtimeDbRef = FirebaseDatabase.getInstance().getReference();
                shabaRealtimeDbRef.child("ECR").child(ecr.getId()).setValue(ecr).addOnCompleteListener(updateEcrTask -> {
                    if (updateEcrTask.isSuccessful()) {
                        shabaRealtimeDbRef.child("RetailersV2").child(u.getUid()).get().addOnCompleteListener(getRetailerTask -> {
                            if (getRetailerTask.isSuccessful()) {
                                Retailer retailer = getRetailerTask.getResult().getValue(Retailer.class);
                                if (retailer == null) {
                                    sd.dismiss();
                                    Snackbar.make(back, "Your email could not be updated at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                                    if (updateEcrTask.getException() != null) {
                                        updateEcrTask.getException().printStackTrace();
                                    }
                                } else {
                                    retailer.setEmail(ecr.getEmail());
                                    shabaRealtimeDbRef.child("RetailersV2").child(u.getUid()).setValue(retailer).addOnCompleteListener(updateRetailerTask -> {
                                        if (updateRetailerTask.isSuccessful()) {
                                            u.updateEmail(ecr.getEmail()).addOnCompleteListener(updateEmailTask -> {
                                                sd.dismiss();
                                                if (updateEmailTask.isSuccessful()) {
                                                    sd = StatusDialog.newInstance(R.raw.success, "Your email was changed successfully. Please log in with your new email.", true, this::emailUpdated);
                                                    sd.show(getSupportFragmentManager(), StatusDialog.TAG);
                                                } else {
                                                    Snackbar.make(back, "Your email could not be updated at the moment. Please submit another request.", Snackbar.LENGTH_LONG).show();
                                                    if (updateEmailTask.getException() != null) {
                                                        updateEmailTask.getException().printStackTrace();
                                                    }
                                                }
                                            });
                                        } else {
                                            sd.dismiss();
                                            Snackbar.make(back, "Your email could not be updated at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                                            if (updateEcrTask.getException() != null) {
                                                updateEcrTask.getException().printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } else {
                                sd.dismiss();
                                Snackbar.make(back, "Your email could not be updated at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                                if (updateEcrTask.getException() != null) {
                                    updateEcrTask.getException().printStackTrace();
                                }
                            }
                        });

                    } else {
                        sd.dismiss();
                        Snackbar.make(back, "Your email could not be updated at the moment. Please try again later.", Snackbar.LENGTH_LONG).show();
                        if (updateEcrTask.getException() != null) {
                            updateEcrTask.getException().printStackTrace();
                        }
                    }
                });
            } else {
                sd.dismiss();
                Snackbar.make(back, "There was an error confirming your current details. Please confirm your password and try again.", Snackbar.LENGTH_LONG).show();
                if (signInTask.getException() != null) {
                    signInTask.getException().printStackTrace();
                }
            }
        });
    }


    @SuppressWarnings("ConstantConditions")
    private boolean validateInput() {
        hideKeyboard();
        code.setError(null);
        if (code.getEditText().getText().toString().trim().equals(ecr.getCode())) {
            if (password.getEditText().getText().toString().length() < 8) {
                password.setError("Your password is at least 8 characters long");
                return false;
            } else {
                return true;
            }
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