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

import android.content.Context;

import java.util.List;

/**
 * Created by Ese Udom on 4/17/2018.
 */

/** Utililty class for database transactions */
public class DataSource {

    /**
     * Calls database and inserts profile data on separate thread
     */
    public static void saveProfileData(final Context context, final String name, final String email, final String phone, final String password,
                                       final String googleID, final String loginStatus) {

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                MedManagerDatabase.getInstance(context)
                        .profileDao()
                        .insertProfile(new Profile(name, email, phone, password, googleID, loginStatus));
            }
        });
    }

    /**
     * Calls database to read profile data on separate thread
     */
    public static String readProfileData(final Context context, final String column) {
        final String[] res = {null};
        new Thread(new Runnable() {
            public void run() {
                res[0] = MedManagerDatabase.getInstance(context)
                        .profileDao()
                        .getProfileData(column);
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
                profile[0] = MedManagerDatabase.getInstance(context)
                        .profileDao()
                        .getProfile();
            }
        });

        return profile[0];
    }

    /**
     * Calls database to update profile data on separate thread
     */
    public static void updateProfileData(final Context context, final String column, final String value) {

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                MedManagerDatabase.getInstance(context)
                        .profileDao()
                        .updateProfile(column, value);
            }
        });
    }

    /**
     * Calls database and inserts medication data on separate thread
     */
    public static void saveMedData(final Context context, final String drugName, final String description, final String interval,
                                   final String hoursBtw, final String startDate, final String endDate) {

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                MedManagerDatabase.getInstance(context)
                        .medicationDao()
                        .insertMedication(new Medication(drugName, description, interval, hoursBtw, startDate, endDate));
            }
        });
    }

    /**
     * Calls database to read medication data on separate thread
     */
    public static List<Medication> readAllMedData(final Context context) {
        final List<Medication>[] list = new List[]{null};
        new Thread(new Runnable() {
            public void run() {
                list[0] = MedManagerDatabase.getInstance(context)
                        .medicationDao()
                        .getAll();
            }
        });

        return list[0];
    }

    /**
     * Calls database to remove medication data on separate thread
     */
    public static void removeMedData(final Context context, final Medication medication) {

        //Running potential long running process in another thread
        new Thread(new Runnable() {
            public void run() {
                MedManagerDatabase.getInstance(context)
                        .medicationDao()
                        .delete(medication);
            }
        });
    }

    /**
     * Calls database to read medication data on separate thread
     */
    public static List<Medication> readMedData(final Context context, final String like) {
        final List<Medication>[] list = new List[]{null};
        new Thread(new Runnable() {
            public void run() {
                list[0] = MedManagerDatabase.getInstance(context)
                        .medicationDao()
                        .findByName(like);
            }
        });

        return list[0];
    }
}
