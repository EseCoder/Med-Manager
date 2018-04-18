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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Ese Udom on 4/17/2018.
 */
@Dao
public interface MedicationDao {

    @Query("SELECT * FROM meds")
    List<Medication> getAll();

    @Query("SELECT * FROM meds WHERE drugname LIKE :like")
    List<Medication> findByName(String like);

    @Insert
    void insertMedication(Medication medication);

    @Delete
    void delete(Medication medication);
}
