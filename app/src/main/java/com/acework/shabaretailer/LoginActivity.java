package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.ui.CatalogActivityNew;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AppCompatActivity {
    private TextInputLayout usernameLayout, passwordLayout;
    private TextInputEditText usernameField, passwordField;
    private MaterialButton loginButton, forgotPasswordButton, signUpButton;
    private StatusDialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        usernameLayout = findViewById(R.id.username_layout);
        passwordLayout = findViewById(R.id.password_layout);
        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        loginButton = findViewById(R.id.login_button);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        signUpButton = findViewById(R.id.sign_up_button);
    }

    private void setListeners() {
        loginButton.setOnClickListener(v -> login());
        forgotPasswordButton.setOnClickListener(v -> forgotPassword());
        signUpButton.setOnClickListener(v -> signUp());
    }

    private void login() {
        if (validateInput()) {
            statusDialog = StatusDialog.newInstance(R.raw.loading, "Logging you in...", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(usernameField.getText().toString().trim(), passwordField.getText().toString()).addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        statusDialog = StatusDialog.newInstance(R.raw.success, "Welcome back!", true, () -> {
                            startActivity(new Intent(LoginActivity.this, CatalogActivityNew.class));
                            finish();
                        });
                        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
                    } else {
                        statusDialog = StatusDialog.newInstance(R.raw.next, "Welcome back! We notice that you haven't verified your email. Please do so in the next screen.", true, () -> {
                            startActivity(new Intent(LoginActivity.this, AccountVerificationActivity.class));
                            finish();
                        });
                        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
                    }
                } else {
                    Snackbar.make(usernameLayout, "Could not log you in. Check your credentials and try again.", Snackbar.LENGTH_LONG).show();
                }
            });
        }

    }

    private void forgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    private void signUp() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    private boolean validateInput() {
        passwordLayout.setError(null);
        usernameLayout.setError(null);
        hideKeyboard();

        if (validateUsername()) {
            if (passwordField.getText().toString().length() < 8) {
                passwordLayout.setError("Your password is at least 8 characters long");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean validateUsername() {
        if (usernameField.getText().toString().matches("\\d{10}")) {
            return true;
        }
        if (Patterns.EMAIL_ADDRESS.matcher(usernameField.getText().toString().trim()).matches()) {
            return true;
        }
        usernameLayout.setError("Invalid email address or telephone number");
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(usernameLayout.getWindowToken(), 0);
    }
}