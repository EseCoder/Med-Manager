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

import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by Ese Udom on 4/6/2018.
 */
public final class MedManagerContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MedManagerContract() {}

    /* Inner class that defines the table contents */
    public static class ProfileEntry implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_GOOGLE_ID = "googleid";
        public static final String COLUMN_LOGIN_STATUS = "loginstatus";
    }

    /* Inner class that defines the table contents */
    public static class MedicationEntry implements BaseColumns {
        public static final String TABLE_NAME = "meds";
        public static final String COLUMN_DRUG_NAME = "drugname";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_HOURS_BETWEEN = "hoursbetween";
        public static final String COLUMN_START_DATE = "startdate";
        public static final String COLUMN_END_DATE = "enddate";
    }
}
