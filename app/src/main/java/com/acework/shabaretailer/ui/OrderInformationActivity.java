package com.acework.shabaretailer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.StatusDialog;
import com.acework.shabaretailer.adapter.DeliveryNoteAdapterNew;
import com.acework.shabaretailer.adapter.ItemInOrderAdapterNew;
import com.acework.shabaretailer.atlas.BackgroundExecutor;
import com.acework.shabaretailer.databinding.ActivityOrderInformationBinding;
import com.acework.shabaretailer.model.OrderNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderInformationActivity extends AppCompatActivity {
    private static boolean modified = false;
    ActivityOrderInformationBinding binding;
    private ItemInOrderAdapterNew adapter;
    private OrderNew order;
    private final ActivityResultLauncher<Intent> l = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                loadRetailer(data.getData());
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        initializeList();
        loadOrder();
        if (modified) {
            setResult(RESULT_OK);
        }
    }

    private void setListeners() {
        binding.back.setOnClickListener(v -> finish());
        binding.cancel.setOnClickListener(v -> confirmCanceling());
        binding.received.setOnClickListener(v -> confirmReceived());
        binding.download.setOnClickListener(v -> confirmDownloadingNote());
    }

    private void initializeList() {
        adapter = new ItemInOrderAdapterNew(this);
        binding.itemList.setAdapter(adapter);
    }

    private void loadOrder() {
        String orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            finish();
        } else {
            StatusDialog statusDialog = StatusDialog.newInstance(R.raw.loading, "Fetching order information...", false, null);
            statusDialog.show(getSupportFragmentManager(), StatusDialog.TAG);

            FirebaseFirestore.getInstance().collection("orders").document(orderId).get().addOnCompleteListener(task -> {
                statusDialog.dismiss();
                if (task.isSuccessful()) {
                    OrderNew order = task.getResult().toObject(OrderNew.class);
                    if (order == null) {
                        Snackbar.make(binding.back, "There was an error loading the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                        Log.e("FirebaseError", "Null");
                    } else {
                        displayOrder(order);
                    }
                } else {
                    Snackbar.make(binding.back, "There was an error loading the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                    if (task.getException() != null) task.getException().printStackTrace();
                }
            });
        }
    }

    private void displayOrder(OrderNew order) {
        this.order = order;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String formattedDate = dateFormatter.format(new Date(order.timestamp));

        binding.orderNum.setText(getString(R.string.order_num, order.id));
        binding.type.setText(order.orderType);
        binding.date.setText(formattedDate);
        binding.orderStatus.setText(order.status);
        binding.total.setText(getString(R.string.ksh_ph, order.getTotal()));
        binding.transport.setText(order.county.equals("Nairobi") ? "250" : "500");
        binding.deliveryPoint.setText(getString(R.string.delivery_point, order.county, order.town));

        adapter.setItems(order.orderItems);

        if (order.status.equals("Pending")) {
            binding.cancel.setVisibility(View.VISIBLE);
        }
        if (order.status.equals("Dispatched")) {
            binding.received.setVisibility(View.VISIBLE);
        }
        if (order.status.equals("Received")) {
            binding.download.setVisibility(View.VISIBLE);
            if (modified) confirmDownloadingNote();
        }
        modified = false;
    }

    private void confirmCanceling() {
        new MaterialAlertDialogBuilder(this).setTitle("Cancel order").setMessage("Are you sure you want to cancel this order? This action is irreversible.").setPositiveButton("Yes", (dialogInterface, i) -> cancel()).setNegativeButton("No", null).show();
    }

    private void confirmReceived() {
        new MaterialAlertDialogBuilder(this).setTitle("Order received").setMessage("Have the items in the order been delivered to you?").setPositiveButton("Yes", (dialogInterface, i) -> received()).setNegativeButton("No", null).show();
    }

    private void cancel() {
        StatusDialog cd = StatusDialog.newInstance(R.raw.loading, "Canceling order", false, null);
        cd.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseFirestore.getInstance().collection("orders").document(order.id).update("status", "Canceled").addOnCompleteListener(task -> {
            cd.dismiss();
            if (task.isSuccessful()) {
                StatusDialog sd = StatusDialog.newInstance(R.raw.success, "Order canceled", true, () -> {
                    modified = true;
                    recreate();
                });
                sd.show(getSupportFragmentManager(), StatusDialog.TAG);
            } else {
                Snackbar.make(binding.back, "There was an error canceling the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void received() {
        StatusDialog cd = StatusDialog.newInstance(R.raw.loading, "Updating order", false, null);
        cd.show(getSupportFragmentManager(), StatusDialog.TAG);

        FirebaseFirestore.getInstance().collection("orders").document(order.id).update("status", "Received").addOnCompleteListener(task -> {
            cd.dismiss();
            if (task.isSuccessful()) {
                StatusDialog sd = StatusDialog.newInstance(R.raw.success, "Order completed", true, () -> {
                    modified = true;
                    recreate();
                });
                sd.show(getSupportFragmentManager(), StatusDialog.TAG);
            } else {
                Snackbar.make(binding.back, "There was an error updating the order. Please try again later.", Snackbar.LENGTH_LONG).show();
                if (task.getException() != null) task.getException().printStackTrace();
            }
        });
    }

    private void confirmDownloadingNote() {
        new MaterialAlertDialogBuilder(this).setTitle("Download delivery note").setMessage("Would you like to download this order's delivery note as a PDF document? You can click the download icon at the top right to download later.").setPositiveButton("Yes", (dialogInterface, i) -> downloadDocument()).setNegativeButton("No", null).show();
    }

    private void downloadDocument() {
        getNoteDownloadPath();
    }

    private void getNoteDownloadPath() {
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/pdf");
        i.putExtra(Intent.EXTRA_TITLE, order.id + ".pdf");
        l.launch(i);
    }

    private void loadRetailer(Uri u) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("retailers").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                String name = (String) documentSnapshot.get("name");
                String email = user.getEmail();

                prepareDeliveryNote(u, name, email);
            });
        }
    }

    private void prepareDeliveryNote(Uri u, String name, String email) {
        LayoutInflater li = LayoutInflater.from(this);
        View v = li.inflate(R.layout.delivery_note, null);
        populateView(v, name, email);

        v.measure(View.MeasureSpec.makeMeasureSpec(1240, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(1754, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, 1240, 1754);

        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        PdfDocument d = new PdfDocument();
        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(1240, 1754, 1).create();

        PdfDocument.Page p = d.startPage(pi);
        p.getCanvas().drawBitmap(b, 0, 0, null);
        d.finishPage(p);

        saveDeliveryNoteToFile(d, u);
    }

    private void saveDeliveryNoteToFile(PdfDocument d, Uri u) {
        BackgroundExecutor.execute(() -> {
            try {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(u, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                d.writeTo(fos);
                d.close();
                fos.close();
                pfd.close();
                runOnUiThread(() -> Snackbar.make(binding.back, "Delivery note saved", Snackbar.LENGTH_LONG).setAction("OPEN", view -> openSavedDeliveryNote(u)).setActionTextColor(getResources().getColor(R.color.primaryTextColor)).show());
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Snackbar.make(binding.back, "There was an error saving your note. Please try again later.", Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            }
        });
    }

    private void openSavedDeliveryNote(Uri noteUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(noteUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void populateView(View view, String name, String email) {
        TextView orderType = view.findViewById(R.id.order_type);
        TextView retailerName = view.findViewById(R.id.ret_name);
        TextView retailerEmail = view.findViewById(R.id.ret_email);
        TextView retailerLocation = view.findViewById(R.id.loc);
        RecyclerView itemList = view.findViewById(R.id.list);
        TextView itemTotal = view.findViewById(R.id.itm_total);
        TextView transportCost = view.findViewById(R.id.trans);
        TextView totalCost = view.findViewById(R.id.total);
        TextView orderStatus = view.findViewById(R.id.status);

        orderType.setText(order.orderType);
        retailerName.setText(name);
        retailerEmail.setText(email);
        retailerLocation.setText(String.format(Locale.getDefault(), "%s, %s", order.town, order.county));

        DeliveryNoteAdapterNew noteAdapter = new DeliveryNoteAdapterNew(this);
        itemList.setAdapter(noteAdapter);
        noteAdapter.setItems(order.orderItems);

        itemTotal.setText(getString(R.string.kes, order.getBagTotal()));
        transportCost.setText(order.county.equals("Nairobi") ? "250" : "500");
        totalCost.setText(getString(R.string.kes, order.getTotal()));
        orderStatus.setText(order.status);
    }
}