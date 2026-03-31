package com.example.lostfound;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.DBHelper;
import com.example.lostfound.model.LostItem;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private ImageView btnBack, imagePreview;
    private Button btnSelectImage, btnSave;

    private TextInputLayout tilTitle, tilName, tilPhone, tilDescription, tilDate, tilLocation;

    private com.google.android.material.textfield.TextInputEditText
            etTitle, etName, etPhone, etDescription, etDate, etLocation;

    private RadioGroup radioGroupType;

    private Uri selectedImageUri = null;
    private Uri cameraOutputUri = null;
    private DBHelper dbHelper;

    private boolean hasChanges = false;

    private final ActivityResultLauncher<String> galleryPicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
                    hasChanges = true;
                }
            });

    private final ActivityResultLauncher<Uri> cameraPicker =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraOutputUri != null) {
                    selectedImageUri = cameraOutputUri;
                    imagePreview.setImageURI(cameraOutputUri);
                    hasChanges = true;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dbHelper = new DBHelper(this);

        bindViews();
        setupActions();
        setupValidation();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBack();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void bindViews() {
        btnBack        = findViewById(R.id.btnBack);
        imagePreview   = findViewById(R.id.imagePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave        = findViewById(R.id.btnSave);
        radioGroupType = findViewById(R.id.radioGroupType);

        tilTitle       = findViewById(R.id.tilTitle);
        tilName        = findViewById(R.id.tilName);
        tilPhone       = findViewById(R.id.tilPhone);
        tilDescription = findViewById(R.id.tilDescription);
        tilDate        = findViewById(R.id.tilDate);
        tilLocation    = findViewById(R.id.tilLocation);

        etTitle        = findViewById(R.id.etTitle);
        etName         = findViewById(R.id.etName);
        etPhone        = findViewById(R.id.etPhone);
        etDescription  = findViewById(R.id.etDescription);
        etDate         = findViewById(R.id.etDate);
        etLocation     = findViewById(R.id.etLocation);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> handleBack());
        btnSelectImage.setOnClickListener(v -> showImageSourceDialog());
        etDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveItem());
    }

    // Wires up focus-out validation and clears errors as the user types
    private void setupValidation() {

        // Title — required
        etTitle.addTextChangedListener(clearErrorWatcher(tilTitle));
        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etTitle.getText().toString().trim().isEmpty()) {
                tilTitle.setError("Please enter an item title");
            }
        });

        // Name — required, letters only
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString().trim();
                if (value.isEmpty()) {
                    tilName.setError(null); // handle the empty case
                } else if (!value.matches("[a-zA-Z ]+")) {
                    tilName.setError("Name must not contain numbers or symbols");
                } else {
                    tilName.setError(null);
                }
            }
        });
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String value = etName.getText().toString().trim();
                if (value.isEmpty()) {
                    tilName.setError("Please enter your name");
                } else if (!value.matches("[a-zA-Z ]+")) {
                    tilName.setError("Name must not contain numbers or symbols");
                }
            }
        });

        // Phone — required, min 6 digits
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only flag too-short once they've started typing, not on first keystroke
                if (s.length() > 0 && s.length() < 6) {
                    tilPhone.setError("Enter a valid phone number (min 6 digits)");
                } else {
                    tilPhone.setError(null);
                }
            }
        });
        etPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String value = etPhone.getText().toString().trim();
                if (value.isEmpty()) {
                    tilPhone.setError("Please enter a phone number");
                } else if (value.length() < 6) {
                    tilPhone.setError("Enter a valid phone number (min 6 digits)");
                }
            }
        });

        // Description — required
        etDescription.addTextChangedListener(clearErrorWatcher(tilDescription));
        etDescription.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etDescription.getText().toString().trim().isEmpty()) {
                tilDescription.setError("Please enter a description");
            }
        });

        // Location — required
        etLocation.addTextChangedListener(clearErrorWatcher(tilLocation));
        etLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etLocation.getText().toString().trim().isEmpty()) {
                tilLocation.setError("Please enter a location");
            }
        });
    }

    // Simple watcher that clears the error on the given layout as soon as the user types anything
    private TextWatcher clearErrorWatcher(TextInputLayout layout) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) layout.setError(null);
            }
        };
    }

    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Add Photo")
                .setItems(new String[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) launchCamera();
                    else galleryPicker.launch("image/*");
                })
                .show();
    }

    private void launchCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "lost_found_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        cameraOutputUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (cameraOutputUri != null) {
            cameraPicker.launch(cameraOutputUri);
        } else {
            Toast.makeText(this, "Could not access camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleBack() {
        if (hasChanges || hasText()) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard changes?")
                    .setMessage("You have unsaved changes.")
                    .setPositiveButton("Discard", (d, w) -> finish())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            finish();
        }
    }

    private boolean hasText() {
        return !etTitle.getText().toString().isEmpty()
                || !etName.getText().toString().isEmpty()
                || !etPhone.getText().toString().isEmpty()
                || !etDescription.getText().toString().isEmpty()
                || !etLocation.getText().toString().isEmpty();
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String date = day + "/" + (month + 1) + "/" + year;
                    etDate.setText(date);
                    tilDate.setError(null);
                    hasChanges = true;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void clearErrors() {
        tilTitle.setError(null);
        tilName.setError(null);
        tilPhone.setError(null);
        tilDescription.setError(null);
        tilDate.setError(null);
        tilLocation.setError(null);
    }

    private void saveItem() {

        // Run all field validations manually on save to catch anything not yet touched
        clearErrors();

        String title       = etTitle.getText().toString().trim();
        String name        = etName.getText().toString().trim();
        String phone       = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date        = etDate.getText().toString().trim();
        String location    = etLocation.getText().toString().trim();

        boolean valid = true;

        if (title.isEmpty()) {
            tilTitle.setError("Please enter an item title");
            valid = false;
        }
        if (name.isEmpty()) {
            tilName.setError("Please enter your name");
            valid = false;
        } else if (!name.matches("[a-zA-Z ]+")) {
            tilName.setError("Name must not contain numbers or symbols");
            valid = false;
        }
        if (phone.isEmpty()) {
            tilPhone.setError("Please enter a phone number");
            valid = false;
        } else if (phone.length() < 6) {
            tilPhone.setError("Enter a valid phone number (min 6 digits)");
            valid = false;
        }
        if (description.isEmpty()) {
            tilDescription.setError("Please enter a description");
            valid = false;
        }
        if (date.isEmpty()) {
            tilDate.setError("Please select a date");
            valid = false;
        }
        if (location.isEmpty()) {
            tilLocation.setError("Please enter a location");
            valid = false;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (!valid) return;

        int selectedId = radioGroupType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = (selectedId == R.id.radioLost) ? "Lost" : "Found";

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        LostItem item = new LostItem(title, type, name, phone, description,
                date, location, selectedImageUri.toString(), timestamp);

        dbHelper.insertItem(item);

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}