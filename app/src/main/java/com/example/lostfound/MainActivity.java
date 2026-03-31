package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostfound.database.DBHelper;
import com.example.lostfound.model.LostItem;

import java.util.List;

// Home screen with navigation + stats
public class MainActivity extends AppCompatActivity {

    private Button btnCreate, btnViewItems;
    private TextView txtStatsLost, txtStatsFound, txtLastEntry;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreate = findViewById(R.id.btnCreate);
        btnViewItems = findViewById(R.id.btnViewItems);

        txtStatsLost = findViewById(R.id.txtStatsLost);
        txtStatsFound = findViewById(R.id.txtStatsFound);
        txtLastEntry = findViewById(R.id.txtLastEntry);

        dbHelper = new DBHelper(this);

        // Navigate to Add screen
        btnCreate.setOnClickListener(v ->
                startActivity(new Intent(this, AddItemActivity.class))
        );

        // Navigate to list screen
        btnViewItems.setOnClickListener(v ->
                startActivity(new Intent(this, ItemListActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {

        List<LostItem> items = dbHelper.getAllItems();

        int lost = 0;
        int found = 0;
        String lastEntry = "-";

        for (LostItem item : items) {

            if ("Lost".equalsIgnoreCase(item.getType())) {
                lost++;
            }

            if ("Found".equalsIgnoreCase(item.getType())) {
                found++;
            }

            // First item is newest (DESC order)
            if (lastEntry.equals("-") && item.getTimestamp() != null) {

                String ts = item.getTimestamp();

                // 🔥 Extract date only (before space)
                if (ts.contains(" ")) {
                    lastEntry = ts.split(" ")[0];
                } else {
                    lastEntry = ts;
                }
            }
        }

        txtStatsLost.setText(lost + " Lost items");
        txtStatsFound.setText(found + " Found items");
        txtLastEntry.setText("Latest • " + lastEntry);
    }
}