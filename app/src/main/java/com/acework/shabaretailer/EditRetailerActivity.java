package com.acework.shabaretailer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class EditRetailerActivity extends AppCompatActivity {
    private TextInputLayout name, bizName, county, street;
    private MaterialButton saveChanges, changePassword, changeEmail, back;
    private Retailer currentRetailer;
    private StatusDialog statusDialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_retailer);
        bindViews();
        setListeners();
        initializeCounties();
        loadRetailer();
    }

    private void bindViews() {
        name = findViewById(R.id.name);
        bizName = findViewById(R.id.biz_name);
        county = findViewById(R.id.county_input);
        street = findViewById(R.id.street);
        saveChanges = findViewById(R.id.save_changes);
        saveChanges.setOnClickListener(v -> saveChanges());
        back = findViewById(R.id.back_button);
        changePassword = findViewById(R.id.change_password);
        changeEmail = findViewById(R.id.change_email);
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        changePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));
        changeEmail.setOnClickListener(v -> changeEmailClicked());
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeCounties() {
        List<String> countyList = new ArrayList<>();
        countyList.add("Baringo");
        countyList.add("Bomet");
        countyList.add("Bungoma");
        countyList.add("Busia");
        countyList.add("Elgeyo Marakwet");
        countyList.add("Embu");
        countyList.add("Garissa");
        countyList.add("Homa Bay");
        countyList.add("Isiolo");
        countyList.add("Kajiado");
        countyList.add("Kakamega");
        countyList.add("Kericho");
        countyList.add("Kiambu");
        countyList.add("Kilifi");
        countyList.add("Kirinyaga");
        countyList.add("Kisii");
        countyList.add("Kisumu");
        countyList.add("Kitui");
        countyList.add("Kwale");
        countyList.add("Laikipia");
        countyList.add("Lamu");
        countyList.add("Machakos");
        countyList.add("Makueni");
        countyList.add("Mandera");
        countyList.add("Marsabit");
        countyList.add("Meru");
        countyList.add("Migori");
        countyList.add("Mombasa");
        countyList.add("Murang'a");
        countyList.add("Nairobi");
        countyList.add("Nakuru");
        countyList.add("Nandi");
        countyList.add("Narok");
        countyList.add("Nyamira");
        countyList.add("Nyandarua");
        countyList.add("Nyeri");
        countyList.add("Samburu");
        countyList.add("Siaya");
        countyList.add("Taita/Taveta");
        countyList.add("Tana River");
        countyList.add("Tharaka-Nithi");
        countyList.add("Trans Nzoia");
        countyList.add("Turkana");
        countyList.add("Uasin Gishu");
        countyList.add("Vihiga");
        countyList.add("Wajir");
        countyList.add("West Pokot");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, countyList);
        ((AutoCompleteTextView) county.getEditText()).setAdapter(adapter);
    }

    private void loadRetailer() {
        statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching our information", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            statusDialog.dismiss();
            Snackbar.make(name, "There was an error retrieving your information. Exit the application and try again.", Snackbar.LENGTH_LONG).show();
        } else {
            FirebaseDatabase.getInstance().getReference().child("Retailers").child(user.getUid()).get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    currentRetailer = task.getResult().getValue(Retailer.class);
                    setValues();
                } else {
                    Snackbar.make(name, "There was an error retrieving your information. Exit the application and try again.", Snackbar.LENGTH_LONG).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });

        }
    }

    private void saveChanges() {
        if (validate()) {
            Retailer retailer = getRetailer();
            if (areSimilar(retailer)) {
                Snackbar.make(name, "You have not made any changes.", Snackbar.LENGTH_LONG).show();
            } else {
                statusDialog = StatusDialog.newInstance(R.raw.loading, "Updating your information", false, null);
                statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

                FirebaseDatabase.getInstance().getReference().child("Retailers").child(user.getUid()).setValue(retailer).addOnCompleteListener(task -> {
                    statusDialog.dismiss();
                    if (task.isSuccessful()) {
                        statusDialog = StatusDialog.newInstance(R.raw.success, "Your information has been updated. You will see the changes the next time you launch the application.", true, () -> {
                            setResult(RESULT_OK);
                            finish();
                        });
                        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);
                    } else {
                        Snackbar.make(name, "There was an error updating your information. Exit the application and try again.", Snackbar.LENGTH_LONG).show();
                        if (task.getException() != null) task.getException().printStackTrace();
                    }
                });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validate() {
        clearErrors();
        hideKeyboard();

        if (name.getEditText().getText().toString().trim().isEmpty()) {
            name.setError("This field is required");
            return false;
        }

        if (bizName.getEditText().getText().toString().trim().isEmpty()) {
            bizName.setError("This field is required");
            return false;
        }

        if (county.getEditText().getText().toString().isEmpty()) {
            county.setError("This field is required");
            return false;
        }

        if (street.getEditText().getText().toString().trim().isEmpty()) {
            street.setError("This field is required");
            return false;
        }

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void setValues() {
        name.getEditText().setText(currentRetailer.getName());
        bizName.getEditText().setText(currentRetailer.getBusinessName());
        ((AutoCompleteTextView) county.getEditText()).setText(currentRetailer.getCounty(), false);
        street.getEditText().setText(currentRetailer.getStreet());
        saveChanges.setEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private Retailer getRetailer() {
        return new Retailer(
                name.getEditText().getText().toString().trim(),
                bizName.getEditText().getText().toString().trim(),
                currentRetailer.getTelephone(),
                user.getEmail(),
                county.getEditText().getText().toString(),
                street.getEditText().getText().toString().trim(),
                "-");
    }

    private boolean areSimilar(Retailer retailer) {
        if (!currentRetailer.getName().equals(retailer.getName())) return false;
        if (!currentRetailer.getBusinessName().equals(retailer.getBusinessName())) return false;
        if (!currentRetailer.getCounty().equals(retailer.getCounty())) return false;
        return currentRetailer.getStreet().equals(retailer.getStreet());
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }

    private void clearErrors() {
        name.setError(null);
        bizName.setError(null);
        county.setError(null);
        street.setError(null);
    }

    private void changeEmailClicked() {
        downloadLatestECR();
    }

    private void downloadLatestECR() {
        statusDialog = StatusDialog.newInstance(R.raw.loading, "Checking your requests", false, null);
        statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

        String uid = FirebaseAuth.getInstance().getUid();

        DatabaseReference shabaRtDbRef = FirebaseDatabase.getInstance().getReference().child("ECR");
        shabaRtDbRef.orderByChild("uid").equalTo(uid).limitToLast(1).get().addOnCompleteListener(task -> {
            statusDialog.dismiss();
            if (task.isSuccessful()) {
                if (task.getResult().getChildrenCount() > 0) {
                    EmailChangeRequest ecr = task.getResult().getChildren().iterator().next().getValue(EmailChangeRequest.class);
                    if (ecr == null) {
                        Snackbar.make(back, "Could not get your requests. Try again later.", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (ecr.getStatus().equals("Denied")) {
                            Toast.makeText(this, "TODO: Display denial message & update Firebase", Toast.LENGTH_SHORT).show();
                        } else if (ecr.getStatus().equals("Pending")) {
                            Toast.makeText(this, "TODO: Display option to enter a code or cancel request", Toast.LENGTH_SHORT).show();
                        } else {
                            showECRRequestDialog();
                        }
                    }
                } else {
                    showECRRequestDialog();
                }
            } else {
                Snackbar.make(back, "Could not get your requests. Try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) {
                    task.getException().printStackTrace();
                }
            }
        });
    }

    private void showECRRequestDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Request email change").
                setMessage("Would you like to change your email address? If so, please press 'request' to submit a request for your email to be updated.").
                setPositiveButton("Request", (dialog, which) -> startActivity(new Intent(this, RequestEmailChangeActivity.class))).
                setNegativeButton("Cancel", null).show();
    }
}