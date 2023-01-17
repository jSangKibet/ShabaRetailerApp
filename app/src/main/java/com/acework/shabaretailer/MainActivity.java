package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            if (user.isEmailVerified()) {
                user.reload();
                startActivity(new Intent(this, CatalogActivity.class));
            } else {
                startActivity(new Intent(this, AccountVerificationActivity.class));
            }
        }
        finish();
    }
}