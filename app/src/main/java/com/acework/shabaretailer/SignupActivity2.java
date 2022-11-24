package com.acework.shabaretailer;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("ConstantConditions")
public class SignupActivity2 extends AppCompatActivity {
    private MaterialButton back;
    private TextInputLayout emailInput;
    private MaterialButton continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        emailInput = findViewById(R.id.email_input);
        continueBtn = findViewById(R.id.continue_btn);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        continueBtn.setOnClickListener(v -> validateEmail());
    }

    private void validateEmail() {
        hideKeyboard();
        String email = emailInput.getEditText().getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(null);
            checkIfInUse(email);
        } else {
            emailInput.setError("Invalid email address");
        }
    }

    private void checkIfInUse(String email) {
        continueBtn.setEnabled(false);
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getSignInMethods().isEmpty()) {
                    Toast.makeText(SignupActivity2.this, "Email available!", Toast.LENGTH_SHORT).show();
                    continueBtn.setEnabled(true);
                } else {
                    Snackbar.make(emailInput, "The email address is already in use", Snackbar.LENGTH_SHORT).show();
                    continueBtn.setEnabled(true);
                }
            } else {
                Snackbar.make(emailInput, "There was an error. Please try again later.", Snackbar.LENGTH_SHORT).show();
                continueBtn.setEnabled(true);
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }
}