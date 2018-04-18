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
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Ese Udom on 4/17/2018.
 */
@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile WHERE pid = '1'")
    Profile getProfile();

    @Query("SELECT :col FROM profile WHERE pid = '1'")
    String getProfileData(String col);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProfile(Profile profile);

    @Query("UPDATE profile SET :col = :val WHERE pid = '1")
    void updateProfile(String col, String val);

    @Delete
    void delete(Profile profile);
}
