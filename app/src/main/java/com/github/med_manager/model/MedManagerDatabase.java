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
package com.github.med_manager.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Ese Udom on 4/17/2018.
 */
@Database(entities = {Profile.class, Medication.class}, version = 1)
public abstract class MedManagerDatabase extends RoomDatabase {

    private static final String DB_NAME = "medmanager.db";
    private static volatile MedManagerDatabase instance;

    public abstract ProfileDao profileDao();

    public abstract MedicationDao medicationDao();

    static synchronized MedManagerDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MedManagerDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                MedManagerDatabase.class,
                DB_NAME).build();
    }


}
