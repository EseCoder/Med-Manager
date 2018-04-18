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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Ese Udom on 4/8/2018.
 */
@Entity(tableName = "meds")
public class Medication {

    // POJO class fields
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid", typeAffinity = ColumnInfo.INTEGER)
    private int mid;

    @ColumnInfo(name = "drugname", typeAffinity = ColumnInfo.TEXT)
    private String drugName;

    @ColumnInfo(name = "description", typeAffinity = ColumnInfo.TEXT)
    private String desc;

    @ColumnInfo(name = "interval", typeAffinity = ColumnInfo.TEXT)
    private String interval;

    @ColumnInfo(name = "hoursbetween", typeAffinity = ColumnInfo.TEXT)
    private String hoursBtw;

    @ColumnInfo(name = "startdate", typeAffinity = ColumnInfo.TEXT)
    private String startDate;

    @ColumnInfo(name = "enddate", typeAffinity = ColumnInfo.TEXT)
    private String endDate;

    public Medication(String drugName, String desc, String interval, String hoursBtw, String startDate, String endDate) {
        this.drugName = drugName;
        this.desc = desc;
        this.interval = interval;
        this.hoursBtw = hoursBtw;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getHoursBtw() {
        return hoursBtw;
    }

    public void setHoursBtw(String hoursBtw) {
        this.hoursBtw = hoursBtw;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
