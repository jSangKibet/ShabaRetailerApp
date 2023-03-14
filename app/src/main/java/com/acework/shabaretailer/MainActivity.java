package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.acework.shabaretailer.model.Retailer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            if (user.isEmailVerified()) {
                startActivity(new Intent(this, CatalogActivity.class));
                getRetailer(user);
            } else {
                startActivity(new Intent(this, AccountVerificationActivity.class));
            }
        }
        finish();
    }

    private void getRetailer(FirebaseUser u) {
        FirebaseDatabase.getInstance().getReference().child("RetailersV2").child(u.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Retailer r = task.getResult().getValue(Retailer.class);
                if (r != null) {
                    if (r.getEmail().equals(u.getEmail())) {
                        Log.i("UserEmail", "Sync not necessary");
                    } else {
                        r.setEmail(u.getEmail());
                        updateRetailer(u.getUid(), r);
                    }
                }
            }
        });
    }

    private void updateRetailer(String uid, Retailer r) {
        FirebaseDatabase.getInstance().getReference().child("RetailersV2").child(uid).setValue(r).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("UserEmail", "Updated!");
            } else {
                Log.e("UserEmail", "Sync failed!");
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            }
        });
    }
}