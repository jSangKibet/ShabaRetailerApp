package com.acework.shabaretailer;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

@SuppressWarnings("ConstantConditions")
public class Login extends AppCompatActivity {
    private TextInputLayout usernameLayout, passwordLayout;
    private TextInputEditText usernameField, passwordField;
    private MaterialButton loginButton, forgotPasswordButton, signUpButton;

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
            Toast.makeText(this, "Login action!", Toast.LENGTH_SHORT).show();
        }
    }

    private void forgotPassword() {
        Toast.makeText(this, "Forgot password!", Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        Toast.makeText(this, "Sign up action", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput() {
        passwordLayout.setError(null);
        usernameLayout.setError(null);

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
        if (Patterns.EMAIL_ADDRESS.matcher(usernameField.getText().toString()).matches()) {
            return true;
        }
        usernameLayout.setError("Invalid email address or telephone number");
        return false;
    }
}