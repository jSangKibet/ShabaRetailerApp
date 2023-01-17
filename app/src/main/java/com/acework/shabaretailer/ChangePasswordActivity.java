package com.acework.shabaretailer;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("ConstantConditions")
public class ChangePasswordActivity extends AppCompatActivity {
    private MaterialButton back, saveChanges;
    private TextInputLayout currentPassword, newPassword, confirmPassword;
    private StatusDialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back);
        saveChanges = findViewById(R.id.save_changes);
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        saveChanges.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        if (validateInput()) {
            statusDialog = StatusDialog.newInstance(R.raw.loading, "Changing your password...", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String email = firebaseAuth.getCurrentUser().getEmail();

            firebaseAuth.signInWithEmailAndPassword(email, currentPassword.getEditText().getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updatePassword(newPassword.getEditText().getText().toString()).addOnCompleteListener(innerTask -> {
                        statusDialog.dismiss();
                        if (innerTask.isSuccessful()) {
                            statusDialog = StatusDialog.newInstance(R.raw.success, "Your password was changed successfully!", true, this::finish);
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
    }

    private boolean validateInput() {
        hideKeyboard();
        clearErrors();
        if (currentPassword.getEditText().getText().toString().length() < 8) {
            currentPassword.setError("Password must be at least 8 characters long");
            return false;
        }
        if (newPassword.getEditText().getText().toString().length() < 8) {
            newPassword.setError("Password must be at least 8 characters long");
            return false;
        }
        if (!newPassword.getEditText().getText().toString().equals(confirmPassword.getEditText().getText().toString())) {
            confirmPassword.setError("Your new passwords do not match");
            return false;
        }
        return true;
    }

    private void clearErrors() {
        currentPassword.setError(null);
        newPassword.setError(null);
        confirmPassword.setError(null);
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }

    private void showSnackbar(String message) {
        Snackbar.make(back, message, Snackbar.LENGTH_LONG).show();
    }
}