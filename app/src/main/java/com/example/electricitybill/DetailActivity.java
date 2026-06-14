package com.example.electricitybill;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private Spinner spinnerMonthDetail;
    private TextInputLayout layoutUnitsDetail;
    private TextInputEditText editUnitsDetail;
    private Slider sliderRebateDetail;
    private TextView labelRebateDetail;
    private TextView tvTotalChargesDetail, tvFinalCostDetail;
    private MaterialButton btnEdit, btnSave, btnDelete;

    private DBHelper dbHelper;
    private int recordId;
    private boolean isEditing = false;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DBHelper(this);
        recordId = getIntent().getIntExtra("record_id", -1);

        initViews();
        setupMonthSpinner();
        loadRecordData();
        setupListeners();
    }

    private void initViews() {
        spinnerMonthDetail = findViewById(R.id.spinnerMonthDetail);
        layoutUnitsDetail = findViewById(R.id.layoutUnitsDetail);
        editUnitsDetail = findViewById(R.id.editUnitsDetail);
        sliderRebateDetail = findViewById(R.id.sliderRebateDetail);
        labelRebateDetail = findViewById(R.id.labelRebateDetail);
        tvTotalChargesDetail = findViewById(R.id.tvTotalChargesDetail);
        tvFinalCostDetail = findViewById(R.id.tvFinalCostDetail);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupMonthSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, MONTHS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonthDetail.setAdapter(adapter);
    }

    private void loadRecordData() {
        if (recordId == -1) {
            finish();
            return;
        }

        BillRecord record = dbHelper.getRecordById(recordId);
        if (record == null) {
            finish();
            return;
        }

        int monthIndex = Arrays.asList(MONTHS).indexOf(record.getMonth());
        if (monthIndex >= 0) {
            spinnerMonthDetail.setSelection(monthIndex);
        }

        editUnitsDetail.setText(String.format(Locale.getDefault(), "%.0f", record.getUnits()));
        sliderRebateDetail.setValue((float) record.getRebate());
        updateRebateLabel((float) record.getRebate());

        tvTotalChargesDetail.setText(String.format(Locale.getDefault(), "RM %.2f", record.getTotalCharges()));
        tvFinalCostDetail.setText(String.format(Locale.getDefault(), "RM %.2f", record.getFinalCost()));

        sliderRebateDetail.addOnChangeListener((slider, value, fromUser) -> updateRebateLabel(value));
    }

    private void updateRebateLabel(float value) {
        labelRebateDetail.setText(getString(R.string.label_rebate_detail) + "  " + (int) value + "%");
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> enableEditMode());
        btnSave.setOnClickListener(v -> saveChanges());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }


    private void enableEditMode() {
        isEditing = true;
        spinnerMonthDetail.setEnabled(true);
        editUnitsDetail.setEnabled(true);
        sliderRebateDetail.setEnabled(true);

        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);

        Snackbar.make(findViewById(android.R.id.content),
                "Edit mode enabled. Update values and tap Save.", Snackbar.LENGTH_SHORT).show();
    }

    private void saveChanges() {
        layoutUnitsDetail.setError(null);

        String unitsText = editUnitsDetail.getText() != null ? editUnitsDetail.getText().toString().trim() : "";

        if (unitsText.isEmpty()) {
            layoutUnitsDetail.setError(getString(R.string.error_units_empty));
            return;
        }

        double units;
        try {
            units = Double.parseDouble(unitsText);
        } catch (NumberFormatException e) {
            layoutUnitsDetail.setError(getString(R.string.error_units_invalid));
            return;
        }

        if (units < 1 || units > 1000) {
            layoutUnitsDetail.setError(getString(R.string.error_units_range));
            return;
        }

        String month = spinnerMonthDetail.getSelectedItem().toString();
        float rebatePercent = sliderRebateDetail.getValue();

        double totalCharges = BillCalculator.calculateTotalCharges(units);
        double finalCost = BillCalculator.calculateFinalCost(totalCharges, rebatePercent);

        int rows = dbHelper.updateRecord(recordId, month, units, totalCharges, rebatePercent, finalCost);

        if (rows > 0) {
            tvTotalChargesDetail.setText(String.format(Locale.getDefault(), "RM %.2f", totalCharges));
            tvFinalCostDetail.setText(String.format(Locale.getDefault(), "RM %.2f", finalCost));

            // Disable editing again
            isEditing = false;
            spinnerMonthDetail.setEnabled(false);
            editUnitsDetail.setEnabled(false);
            sliderRebateDetail.setEnabled(false);
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);

            Snackbar.make(findViewById(android.R.id.content),
                    R.string.update_success, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> deleteRecord())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteRecord() {
        int rows = dbHelper.deleteRecord(recordId);
        if (rows > 0) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }
}
