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
 * Created by Ese Udom on 4/9/2018.
 */
@Entity(tableName = "profile")
public class Profile {

    // POJO class fields
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pid", typeAffinity = ColumnInfo.INTEGER)
    private int pid;

    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private String name;

    @ColumnInfo(name = "email", typeAffinity = ColumnInfo.TEXT)
    private String email;

    @ColumnInfo(name = "phone", typeAffinity = ColumnInfo.TEXT)
    private String phone;

    @ColumnInfo(name = "password", typeAffinity = ColumnInfo.TEXT)
    private String password;

    @ColumnInfo(name = "googleid", typeAffinity = ColumnInfo.TEXT)
    private String googleID;

    @ColumnInfo(name = "loginstatus", typeAffinity = ColumnInfo.TEXT)
    private String loginStatus;

    public Profile(String name, String email, String phone, String password, String googleID, String loginStatus) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.googleID = googleID;
        this.loginStatus = loginStatus;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }
}
