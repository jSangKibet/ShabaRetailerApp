package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("ConstantConditions")
public class ChangeEmailActivity extends AppCompatActivity {
    StatusDialog sd;
    private MaterialButton back, submit;
    private TextInputLayout email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        submit = findViewById(R.id.submit);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        submit.setOnClickListener(v -> submitClicked());
    }

    private void submitClicked() {
        if (validateInput()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm change of email").
                    setMessage("Would you like to change your email address? If you change your mind, you can reverse this change from your inbox.").
                    setPositiveButton("Yes", (dialog, which) -> applyChanges()).
                    setNegativeButton("Cancel", null).show();
        }
    }


    private boolean validateInput() {
        hideKeyboard();
        email.setError(null);
        password.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString().trim()).matches()) {
            email.setError("Invalid email address");
            return false;
        }
        if (password.getEditText().getText().toString().length() < 8) {
            password.setError("Your password is at least 8 characters long");
            return false;
        }
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }

    private void applyChanges() {
        sd = StatusDialog.newInstance(R.raw.loading, "Updating your email address", false, null);
        sd.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(u.getEmail(), password.getEditText().getText().toString()).addOnCompleteListener(signInTask -> {
            if (signInTask.isSuccessful()) {
                u.updateEmail(email.getEditText().getText().toString().trim()).addOnCompleteListener(updateEmailTask -> {
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
                Snackbar.make(back, "There was an error confirming your current details. Please confirm your password and try again.", Snackbar.LENGTH_LONG).show();
                if (signInTask.getException() != null) {
                    signInTask.getException().printStackTrace();
                }
            }
        });
    }

    private void emailUpdated() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}