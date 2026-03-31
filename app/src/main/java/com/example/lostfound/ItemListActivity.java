package com.example.lostfound;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostfound.adapter.ItemAdapter;
import com.example.lostfound.database.DBHelper;
import com.example.lostfound.model.LostItem;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView btnBack;
    private Button btnAll, btnLost, btnFound;
    private EditText etSearch;

    private ItemAdapter adapter;
    private DBHelper dbHelper;

    private final List<LostItem> fullList = new ArrayList<>();
    private final List<LostItem> displayList = new ArrayList<>();

    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        bindViews();
        setupRecycler();
        setupActions();

        dbHelper = new DBHelper(this);
        loadItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack);
        btnAll = findViewById(R.id.btnAll);
        btnLost = findViewById(R.id.btnLost);
        btnFound = findViewById(R.id.btnFound);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(displayList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupActions() {

        btnBack.setOnClickListener(v -> finish());

        btnAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            setActive(btnAll);
            applyFilters();
        });

        btnLost.setOnClickListener(v -> {
            currentFilter = "Lost";
            setActive(btnLost);
            applyFilters();
        });

        btnFound.setOnClickListener(v -> {
            currentFilter = "Found";
            setActive(btnFound);
            applyFilters();
        });

        setActive(btnAll);

        // 🔍 SEARCH (TITLE ONLY)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setActive(Button active) {
        btnAll.setSelected(false);
        btnLost.setSelected(false);
        btnFound.setSelected(false);
        active.setSelected(true);
    }

    private void loadItems() {
        fullList.clear();
        List<LostItem> items = dbHelper.getAllItems();
        if (items != null) fullList.addAll(items);

        applyFilters();
        updateStats();
    }
    // runs on every filter button tap and on every keystroke in the search bar
    private void applyFilters() {

        displayList.clear();

        String query = etSearch.getText().toString().toLowerCase().trim();

        for (LostItem item : fullList) {

            boolean matchesType =
                    currentFilter.equals("ALL") ||
                            item.getType().equalsIgnoreCase(currentFilter);

            boolean matchesSearch =
                    query.isEmpty() ||
                            (item.getTitle() != null &&
                                    item.getTitle().toLowerCase().contains(query));

            if (matchesType && matchesSearch) {
                displayList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void updateStats() {

        int lost = 0;
        int found = 0;

        for (LostItem item : fullList) {
            if ("Lost".equalsIgnoreCase(item.getType())) lost++;
            if ("Found".equalsIgnoreCase(item.getType())) found++;
        }

        int total = fullList.size();

        btnAll.setText("ALL (" + total + ")");
        btnLost.setText("LOST (" + lost + ")");
        btnFound.setText("FOUND (" + found + ")");
    }
}