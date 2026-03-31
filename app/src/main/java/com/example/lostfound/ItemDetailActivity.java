package com.example.lostfound;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.DBHelper;
import com.example.lostfound.util.TimeUtils;

public class ItemDetailActivity extends AppCompatActivity {

    // Intent extra keys — referenced by ItemAdapter when launching this screen
    public static final String EXTRA_ID          = "id";
    public static final String EXTRA_TITLE       = "title";
    public static final String EXTRA_TYPE        = "type";
    public static final String EXTRA_PHONE       = "phone";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_DATE        = "date";
    public static final String EXTRA_LOCATION    = "location";
    public static final String EXTRA_IMAGE_URI   = "imageUri";
    public static final String EXTRA_TIMESTAMP   = "timestamp";

    private TextView txtName, txtType, txtPhone, txtDescription,
            txtDate, txtLocation, txtTimestamp;

    private ImageView imageViewItem, btnBack;
    private Button btnRemove;

    private DBHelper dbHelper;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        bindViews();
        dbHelper = new DBHelper(this);

        loadData();
        setupActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void bindViews() {
        txtName        = findViewById(R.id.txtName);
        txtType        = findViewById(R.id.txtType);
        txtPhone       = findViewById(R.id.txtPhone);
        txtDescription = findViewById(R.id.txtDescription);
        txtDate        = findViewById(R.id.txtDate);
        txtLocation    = findViewById(R.id.txtLocation);
        txtTimestamp   = findViewById(R.id.txtTimestamp);

        imageViewItem = findViewById(R.id.imageViewItem);
        btnBack       = findViewById(R.id.btnBack);
        btnRemove     = findViewById(R.id.btnRemove);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());
        btnRemove.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadData() {

        itemId = getIntent().getIntExtra(EXTRA_ID, -1);

        String title       = getIntent().getStringExtra(EXTRA_TITLE);
        String type        = getIntent().getStringExtra(EXTRA_TYPE);
        String phone       = getIntent().getStringExtra(EXTRA_PHONE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String date        = getIntent().getStringExtra(EXTRA_DATE);
        String location    = getIntent().getStringExtra(EXTRA_LOCATION);
        String timestamp   = getIntent().getStringExtra(EXTRA_TIMESTAMP);
        String imageUri    = getIntent().getStringExtra(EXTRA_IMAGE_URI);

        txtName.setText(safe(title));
        txtType.setText(safe(type));
        txtLocation.setText(safe(location));
        txtDate.setText(safe(date));
        txtDescription.setText(safe(description));
        txtPhone.setText(safe(phone));
        txtTimestamp.setText(TimeUtils.getTimeAgo(timestamp));

        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                imageViewItem.setImageURI(Uri.parse(imageUri));
            } catch (Exception ignored) {
                imageViewItem.setImageDrawable(null);
            }
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Post")
                .setMessage("Are you sure you want to remove this item?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        if (itemId != -1) {
            int result = dbHelper.deleteItem(itemId);

            if (result > 0) {
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Remove failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}