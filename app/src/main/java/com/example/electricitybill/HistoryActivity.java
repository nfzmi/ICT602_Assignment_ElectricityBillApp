package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private TextView tvEmptyHistory;
    private DBHelper dbHelper;
    private BillListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DBHelper(this);
        listViewHistory = findViewById(R.id.listViewHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);

        loadRecords();

        listViewHistory.setOnItemClickListener((parent, view, position, id) -> {
            BillRecord record = adapter.getItem(position);
            if (record != null) {
                Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
                intent.putExtra("record_id", record.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list every time activity becomes visible (e.g. after edit/delete)
        loadRecords();
    }

    private void loadRecords() {
        List<BillRecord> records = dbHelper.getAllRecords();

        if (records.isEmpty()) {
            listViewHistory.setVisibility(View.GONE);
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            listViewHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setVisibility(View.GONE);
            adapter = new BillListAdapter(this, records);
            listViewHistory.setAdapter(adapter);
        }
    }
}
