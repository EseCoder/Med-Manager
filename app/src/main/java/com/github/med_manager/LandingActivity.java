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
package com.github.med_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.med_manager.model.DataSource;

/**
 * Created by Ese Udom on 4/2/2018.
 */

/** Handles the app landing page */
public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private static LandingActivity sActivity;

    public static LandingActivity getInstance() {
        return sActivity;
    }

    public static boolean finishActivity() {
        getInstance().finish();
        return getInstance().isFinishing();//isDestroyed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        sActivity = this;

        //Checks to see if user is logged in
        checkLoginStatus();

        //Initializing buttons
        Button button = (Button)findViewById(R.id.button_signup);
        button.setOnClickListener(this);
        Button button1 = (Button)findViewById(R.id.button_login);
        button1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signup: startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.button_login: startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    /** Checks login status */
    private void checkLoginStatus() {
        //Retrieve login status from local database
        String s = DataSource.readProfileData(this, "loginstatus");
        int status = Integer.parseInt(s == null? "0" : s);

        if (status == 1) {
            startActivity(new Intent(LandingActivity.this, HomeActivity.class));
        }
    }
}
