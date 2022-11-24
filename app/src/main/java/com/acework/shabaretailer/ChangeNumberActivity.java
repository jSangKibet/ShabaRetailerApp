package com.acework.shabaretailer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthProvider;

@SuppressWarnings("ConstantConditions")
public class ChangeNumberActivity extends AppCompatActivity {
    private MaterialButton back, get, set;
    private TextInputLayout number, code;
    private TextView setNumber;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        back = findViewById(R.id.back_button);
        get = findViewById(R.id.get_code);
        set = findViewById(R.id.set);
        number = findViewById(R.id.number_input);
        code = findViewById(R.id.code_input);
        setNumber = findViewById(R.id.set_number);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        get.setOnClickListener(v -> verifyNumber());
        set.setOnClickListener(v -> verifyCode());
    }

    private void verifyNumber() {
        number.setError(null);
        hideKeyboard();
        String numberEntered = number.getEditText().getText().toString().trim();
        if (numberEntered.matches("\\d{10}")) {
            getCode(numberEntered);
        } else {
            number.setError("Invalid phone number");
        }
    }

    private void getCode(String number) {
        get.setEnabled(false);
        String finalNumber = number;
        number = "+254" + number.substring(1);
    }

    private void switchToCodeInput(String numberEntered) {
        number.setVisibility(View.GONE);
        get.setVisibility(View.GONE);
        setNumber.setText(numberEntered);
        setNumber.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        set.setVisibility(View.VISIBLE);
    }

    private void verifyCode() {

    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(back.getWindowToken(), 0);
    }

    private void changeNumber(String finalNumber) {
    }
}