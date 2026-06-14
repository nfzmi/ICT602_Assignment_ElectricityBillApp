package com.example.electricitybill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectricityBill.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bill_records";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNITS = "units";
    public static final String COL_TOTAL_CHARGES = "total_charges";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL_COST = "final_cost";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT NOT NULL, " +
                COL_UNITS + " REAL NOT NULL, " +
                COL_TOTAL_CHARGES + " REAL NOT NULL, " +
                COL_REBATE + " REAL NOT NULL, " +
                COL_FINAL_COST + " REAL NOT NULL" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertRecord(String month, double units, double totalCharges,
                              double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }


    public List<BillRecord> getAllRecords() {
        List<BillRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
                COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                BillRecord record = new BillRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                record.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)));
                record.setUnits(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_UNITS)));
                record.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES)));
                record.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REBATE)));
                record.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }


    public BillRecord getRecordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        BillRecord record = null;

        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            record = new BillRecord();
            record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            record.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH)));
            record.setUnits(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_UNITS)));
            record.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES)));
            record.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_REBATE)));
            record.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST)));
        }
        cursor.close();
        db.close();
        return record;
    }


    public int updateRecord(int id, String month, double units, double totalCharges,
                             double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);

        int rows = db.update(TABLE_NAME, values, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}
