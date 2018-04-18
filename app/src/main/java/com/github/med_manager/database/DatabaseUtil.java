/*
 * Copyright 2018 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.med_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.github.med_manager.model.Medication;
import com.github.med_manager.model.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ese Udom on 4/16/2018.
 */
public class DatabaseUtil {

    /**
     * Instantiates the database helper class to gain access to database
     */
    private static MedManagerDbHelper getDb(Context context) {
        return new MedManagerDbHelper(context);
    }

    /**
     * Calls database and inserts profile data on separate thread
     */
    public static long saveProfileData(final Context context, final String name, final String email, final String phone, final String password, final String googleid) {
        final long[] res = {-1};

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                // Gets the data repository in write mode
                SQLiteDatabase db = getDb(context).getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(MedManagerContract.ProfileEntry.COLUMN_NAME, name);
                values.put(MedManagerContract.ProfileEntry.COLUMN_EMAIL, email);
                values.put(MedManagerContract.ProfileEntry.COLUMN_PHONE, phone);
                values.put(MedManagerContract.ProfileEntry.COLUMN_PASSWORD, password);
                values.put(MedManagerContract.ProfileEntry.COLUMN_GOOGLE_ID, googleid);
                values.put(MedManagerContract.ProfileEntry.COLUMN_LOGIN_STATUS, "1");
                try {
                    // Insert the new row, returning the primary key value of the new row
                    res[0] = db.insert(MedManagerContract.ProfileEntry.TABLE_NAME, null, values);
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ProfileInsert", e.getMessage());
                }
            }
        });
        return res[0];
    }

    /**
     * Calls database to read profile data on separate thread
     */
    public static String readProfileData(final Context context, final String column) {
        final String[] res = {null};
        new Thread(new Runnable() {
            public void run() {
                SQLiteDatabase db = getDb(context).getReadableDatabase();
                // Define a projection that specifies which columns from the database
                // you will actually use after this query.
                String[] projection = {
                        column
                };

                // Filter results WHERE "title" = 'My Title'
                String selection = BaseColumns._ID + " = ?";
                String[] selectionArgs = {"1"};

                // How you want the results sorted in the resulting Cursor
                String sortOrder = null;//ProfileEntry.COLUMN_NAME + " DESC";

                Cursor cursor = db.query(
                        MedManagerContract.ProfileEntry.TABLE_NAME,   // The table to query
                        projection,             // The array of columns to return (pass null to get all)
                        selection,              // The columns for the WHERE clause
                        selectionArgs,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        null               // The sort order
                );
                cursor.moveToNext();
                res[0] = cursor.getString(cursor.getColumnIndex(column));
                cursor.close();
            }
        });
        return res[0];
    }

    /**
     * Calls database to read profile data on separate thread
     */
    public static Profile readProfileData(final Context context) {
        final Profile[] profile = {null};
        new Thread(new Runnable() {
            public void run() {
                SQLiteDatabase db = getDb(context).getReadableDatabase();

                // Filter results WHERE "title" = 'My Title'
                String selection = BaseColumns._ID + " = ?";
                String[] selectionArgs = {"1"};

                // How you want the results sorted in the resulting Cursor
                String sortOrder = null;//ProfileEntry.COLUMN_NAME + " DESC";

                Cursor cursor = db.query(
                        MedManagerContract.ProfileEntry.TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        selection,              // The columns for the WHERE clause
                        selectionArgs,          // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        null               // The sort order
                );
                cursor.moveToNext();
                profile[0] = new Profile(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), "", "1");
                cursor.close();
            }
        });
        return profile[0];
    }

    /**
     * Calls database to update profile data on separate thread
     */
    public static long updateProfileData(final Context context, final String column, final String value) {
        final long[] res = {0};

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                // Gets the data repository in write mode
                SQLiteDatabase db = getDb(context).getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(column, value);

                // WHERE clause
                String whereClause = BaseColumns._ID + " = ?";
                String[] whereArgs = {"1"};

                // Update the new column, returning the primary key value of the new row
                res[0] = db.update(MedManagerContract.ProfileEntry.TABLE_NAME, values, whereClause, whereArgs);
                db.close();
            }
        });

        return res[0];
    }

    /**
     * Calls database and inserts medication data on separate thread
     */
    public static long saveMedData(final Context context, final String drugName, final String description, final String interval,
                                   final String hoursBtw, final String startDate, final String endDate) {
        final long[] res = {-1};

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                // Gets the data repository in write mode
                SQLiteDatabase db = getDb(context).getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(MedManagerContract.MedicationEntry.COLUMN_DRUG_NAME, drugName);
                values.put(MedManagerContract.MedicationEntry.COLUMN_DESCRIPTION, description);
                values.put(MedManagerContract.MedicationEntry.COLUMN_INTERVAL, interval);
                values.put(MedManagerContract.MedicationEntry.COLUMN_HOURS_BETWEEN, hoursBtw);
                values.put(MedManagerContract.MedicationEntry.COLUMN_START_DATE, startDate);
                values.put(MedManagerContract.MedicationEntry.COLUMN_END_DATE, endDate);

                // Insert the new row, returning the primary key value of the new row
                res[0] = db.insertOrThrow(MedManagerContract.MedicationEntry.TABLE_NAME, null, values);
                db.close();
            }
        });
        return res[0];
    }

    /**
     * Calls database to read medication data on separate thread
     */
    public static List<Medication> readAllMedData(final Context context) {
        final List<Medication> list = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
                SQLiteDatabase db = getDb(context).getReadableDatabase();

                // How you want the results sorted in the resulting Cursor
                String sortOrder = " DESC";

                Cursor cursor = db.query(
                        MedManagerContract.MedicationEntry.TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        null,              // The columns for the WHERE clause
                        null,              // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        sortOrder               // The sort order
                );

                //Read data rows into POJO objects
                while (cursor.moveToNext()) {
                    list.add(new Medication(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6)));
                }
                cursor.close();
            }
        });
        return list;
    }

    /**
     * Calls database to remove medication data on separate thread
     */
    public static int removeMedData(final Context context, final long position) {
        final int[] res = {0};

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                // Gets the data repository in write mode
                SQLiteDatabase db = getDb(context).getWritableDatabase();

                // WHERE clause
                String whereClause = BaseColumns._ID + " = ?";
                String[] whereArgs = {String.valueOf(position)};

                // Delete a row, returning the number of rows affected
                res[0] = db.delete(MedManagerContract.MedicationEntry.TABLE_NAME, whereClause, whereArgs);
                db.close();
            }
        });

        return res[0];
    }

    /**
     * Calls database to read medication data on separate thread
     */
    public static List<Medication> readMedData(final Context context, final String like) {
        final List<Medication> list = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
                SQLiteDatabase db = getDb(context).getReadableDatabase();

                // WHERE clause
                String whereClause = MedManagerContract.MedicationEntry.COLUMN_DRUG_NAME + " LIKE ?";
                String[] whereArgs = {like};

                // How you want the results sorted in the resulting Cursor
                String sortOrder = " DESC";

                Cursor cursor = db.query(
                        MedManagerContract.MedicationEntry.TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        whereClause,              // The columns for the WHERE clause
                        whereArgs,              // The values for the WHERE clause
                        null,                   // don't group the rows
                        null,                   // don't filter by row groups
                        sortOrder               // The sort order
                );

                //Read data rows into POJO objects
                while (cursor.moveToNext()) {
                    list.add(new Medication(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6)));
                }

                cursor.close();
            }
        });
        return list;
    }
}
