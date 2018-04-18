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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.med_manager.database.MedManagerContract.MedicationEntry;
import com.github.med_manager.database.MedManagerContract.ProfileEntry;

/**
 * Created by Ese Udom on 4/6/2018.
 */
public class MedManagerDbHelper extends SQLiteOpenHelper {

    //Initializing contant fields
    private static final String DATABASE_NAME = "medmanager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_PROFILE =
            "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                    ProfileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProfileEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ProfileEntry.COLUMN_EMAIL + " TEXT," +
                    ProfileEntry.COLUMN_PHONE + " TEXT," +
                    ProfileEntry.COLUMN_PASSWORD + " TEXT NOT NULL," +
                    ProfileEntry.COLUMN_GOOGLE_ID + " TEXT," +
                    ProfileEntry.COLUMN_LOGIN_STATUS + " TEXT NOT NULL);";

    private static final String SQL_CREATE_MEDS =
            "CREATE TABLE " + MedicationEntry.TABLE_NAME + " (" +
                    MedicationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MedicationEntry.COLUMN_DRUG_NAME + " TEXT NOT NULL," +
                    MedicationEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                    MedicationEntry.COLUMN_INTERVAL + " TEXT NOT NULL," +
                    MedicationEntry.COLUMN_HOURS_BETWEEN + " TEXT," +
                    MedicationEntry.COLUMN_START_DATE + " TEXT NOT NULL," +
                    MedicationEntry.COLUMN_END_DATE + " TEXT NOT NULL);";

    private static final String SQL_DELETE_PROFILE =
            "DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME;

    private static final String SQL_DELETE_MEDS =
            "DROP TABLE IF EXISTS " + MedicationEntry.TABLE_NAME;

    public MedManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROFILE);
        db.execSQL(SQL_CREATE_MEDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // simply to discard the data and start over
        db.execSQL(SQL_DELETE_PROFILE);
        db.execSQL(SQL_DELETE_MEDS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public String getDbName() {
        return DATABASE_NAME;
    }
}
