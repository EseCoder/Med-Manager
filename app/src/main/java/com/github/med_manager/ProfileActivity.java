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

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.med_manager.model.DataSource;
import com.github.med_manager.model.Profile;
import com.github.med_manager.util.Util;

/**
 * Created by Ese Udom on 4/9/2018.
 */

/** Manages the app's profile page */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText mEditText, mEditText1, mEditText2, mEditText3;
    private Button mButton;
    private TextView mTextView, mTextView1, mTextView2, mTextView3;
    private MenuItem mEditMenuItem;
    private boolean mPasswordChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initializing toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Using an alternative custom drop shadow for toolbar on older devices
        View shadowView = findViewById(R.id.view_shadow);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            shadowView.setVisibility(View.VISIBLE);
        }

        //Setting custom toolbar title
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(getString(R.string.profile));

        Profile profile = DataSource.readProfileData(this);

        //Initialing text views
        mTextView = (TextView) findViewById(R.id.name_text);
        mTextView.setText(profile.getName());
        mTextView1 = (TextView) findViewById(R.id.email_text);
        if (profile.getEmail().isEmpty())
            mTextView1.setVisibility(View.GONE);
        else mTextView1.setText(profile.getEmail());
        mTextView2 = (TextView) findViewById(R.id.phone_text);
        if (profile.getPhone().isEmpty())
            mTextView2.setVisibility(View.GONE);
        else mTextView2.setText(profile.getPhone());
        mTextView3 = (TextView) findViewById(R.id.password_text);
        mTextView3.setText("123456");

        //Initializing input fields
        mEditText = (EditText) findViewById(R.id.name);
        mEditText1 = (EditText)findViewById(R.id.email);
        mEditText2 = (EditText)findViewById(R.id.phone);
        mEditText3 = (EditText)findViewById(R.id.password);
        mEditText3.addTextChangedListener(this);

        //Initializing action buttons
        mButton = (Button)findViewById(R.id.save);
        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                verifyAndSave();
        }
    }

    private void verifyAndSave() {
        //Retrieving entries
        String name = mEditText.getText().toString();
        String email = mEditText1.getText().toString();
        String phone = mEditText2.getText().toString();
        String password = mEditText3.getText().toString();

        if (!name.isEmpty() && !password.isEmpty()) {

            if (!email.isEmpty() && Util.verifyEmail(email)) {
                //Save to local database
                //Hash password to keep it safe
                DataSource.updateProfileData(this, "name", name);
                DataSource.updateProfileData(this, "email", email);
                DataSource.updateProfileData(this, "phone", phone);
                if (mPasswordChanged)
                DataSource.updateProfileData(this, "password", Util.computeSHAHash(password));

                reset(name, email, phone, password);
            }
        }
    }

    private void reset(String name, String email, String phone, String password) {
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(name);

        mTextView1.setVisibility(View.VISIBLE);
        if (email.isEmpty())
            mTextView1.setVisibility(View.GONE);
        else mTextView1.setText(email);

        mTextView2.setVisibility(View.VISIBLE);
        if (phone.isEmpty())
            mTextView2.setVisibility(View.GONE);
        else mTextView2.setText(phone);

        mTextView3.setVisibility(View.VISIBLE);
        mTextView3.setText("123456");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        mEditMenuItem = menu.findItem(R.id.edit);
        mEditMenuItem.setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit && item.isEnabled()) {
            switchToEdit();
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchToEdit() {
        mTextView.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setText(mTextView.getText());

        mTextView1.setVisibility(View.GONE);
        mEditText1.setVisibility(View.VISIBLE);
        mEditText1.setText(mTextView1.getText());

        mTextView2.setVisibility(View.GONE);
        mEditText2.setVisibility(View.VISIBLE);
        mEditText2.setText(mTextView2.getText());

        mTextView3.setVisibility(View.GONE);
        mEditText3.setVisibility(View.VISIBLE);
        mEditText3.setText(mTextView3.getText());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mPasswordChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
