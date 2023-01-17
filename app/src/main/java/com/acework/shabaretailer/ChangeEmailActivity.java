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
    private MaterialButton back, saveChanges;
    private TextInputLayout newEmail, password;
    private StatusDialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        saveChanges = findViewById(R.id.save_changes);
        newEmail = findViewById(R.id.new_email);
        password = findViewById(R.id.password);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        saveChanges.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        if (validateInput()) {
            new MaterialAlertDialogBuilder(this).
                    setTitle("Change email").
                    setMessage("Changing your email address will require verification and automatically log you out. Are you sure you want to change your email?").
                    setPositiveButton("Yes", (dialog, which) -> changeEmail()).
                    setNegativeButton("No", null).show();
        }
    }

    private boolean validateInput() {
        hideKeyboard();
        newEmail.setError(null);
        password.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail.getEditText().getText().toString().trim()).matches()) {
            newEmail.setError("Invalid email address");
            return false;
        }
        if (password.getEditText().getText().toString().length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return false;
        }
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }

    private void showSnackbar(String message) {
        Snackbar.make(back, message, Snackbar.LENGTH_LONG).show();
    }

    private void changeEmail() {
        statusDialog = StatusDialog.newInstance(R.raw.loading, "Changing your email...", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(firebaseAuth.getCurrentUser().getEmail(), password.getEditText().getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user.updateEmail(newEmail.getEditText().getText().toString().trim()).addOnCompleteListener(innerTask -> {
                    statusDialog.dismiss();
                    if (innerTask.isSuccessful()) {
                        statusDialog = StatusDialog.newInstance(R.raw.success, "Your email was changed successfully.", true, this::toAccountVerification);
                        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
                    } else {
                        showSnackbar("There was an error changing your password. Please try again later.");
                    }
                });
            } else {
                statusDialog.dismiss();
                showSnackbar("There was an error changing your password, please check your credentials and try again.");
            }
        });
    }

    private void toAccountVerification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}