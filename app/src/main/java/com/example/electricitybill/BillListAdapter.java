package com.example.electricitybill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class BillListAdapter extends ArrayAdapter<BillRecord> {

    private final Context context;
    private final List<BillRecord> records;

    public BillListAdapter(Context context, List<BillRecord> records) {
        super(context, R.layout.item_bill_record, records);
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_bill_record, parent, false);
        }

        BillRecord record = records.get(position);

        TextView tvMonth = view.findViewById(R.id.tvItemMonth);
        TextView tvFinalCost = view.findViewById(R.id.tvItemFinalCost);

        tvMonth.setText(record.getMonth());
        tvFinalCost.setText(String.format(Locale.getDefault(), "RM %.2f", record.getFinalCost()));

        return view;
    }
}
