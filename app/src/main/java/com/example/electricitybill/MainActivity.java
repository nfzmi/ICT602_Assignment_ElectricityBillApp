package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.cardview.widget.CardView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private TextInputLayout layoutUnits;
    private TextInputEditText editUnits;
    private Slider sliderRebate;
    private TextView labelRebate;
    private MaterialButton btnCalculate, btnHistory, btnAbout;
    private CardView cardResult;
    private TextView tvTotalCharges, tvFinalCost;

    private DBHelper dbHelper;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        initViews();
        setupMonthSpinner();
        setupRebateSlider();
        setupListeners();
    }

    private void initViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth);
        layoutUnits = findViewById(R.id.layoutUnits);
        editUnits = findViewById(R.id.editUnits);
        sliderRebate = findViewById(R.id.sliderRebate);
        labelRebate = findViewById(R.id.labelRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnHistory = findViewById(R.id.btnHistory);
        btnAbout = findViewById(R.id.btnAbout);
        cardResult = findViewById(R.id.cardResult);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
    }

    private void setupMonthSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, MONTHS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
    }

    private void setupRebateSlider() {
        updateRebateLabel(0);
        sliderRebate.addOnChangeListener((slider, value, fromUser) -> updateRebateLabel(value));
    }

    private void updateRebateLabel(float value) {
        labelRebate.setText(getString(R.string.label_rebate) + "  →  " + (int) value + "%");
    }

    private void setupListeners() {
        btnCalculate.setOnClickListener(v -> calculateBill());
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AboutActivity.class)));
    }


    private void calculateBill() {
        layoutUnits.setError(null);

        String unitsText = editUnits.getText() != null ? editUnits.getText().toString().trim() : "";

        // Validate empty input
        if (unitsText.isEmpty()) {
            layoutUnits.setError(getString(R.string.error_units_empty));
            editUnits.requestFocus();
            return;
        }

        double units;
        try {
            units = Double.parseDouble(unitsText);
        } catch (NumberFormatException e) {
            layoutUnits.setError(getString(R.string.error_units_invalid));
            editUnits.requestFocus();
            return;
        }

        // Validate range 1 - 1000
        if (units < 1 || units > 1000) {
            layoutUnits.setError(getString(R.string.error_units_range));
            editUnits.requestFocus();
            return;
        }

        String month = spinnerMonth.getSelectedItem().toString();
        float rebatePercent = sliderRebate.getValue();

        // Perform calculation
        double totalCharges = BillCalculator.calculateTotalCharges(units);
        double finalCost = BillCalculator.calculateFinalCost(totalCharges, rebatePercent);

        // Display results
        tvTotalCharges.setText(String.format(Locale.getDefault(), "RM %.2f", totalCharges));
        tvFinalCost.setText(String.format(Locale.getDefault(), "RM %.2f", finalCost));
        cardResult.setVisibility(View.VISIBLE);

        // Save to local database
        long resultId = dbHelper.insertRecord(month, units, totalCharges, rebatePercent, finalCost);

        if (resultId != -1) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.success_saved, Snackbar.LENGTH_SHORT).show();
        }
    }
}
