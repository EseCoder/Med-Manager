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

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.github.med_manager.dialog.CommonDialog;
import com.github.med_manager.dialog.DatePickerDialogFixedNougatSpinner;
import com.github.med_manager.model.DataSource;
import com.github.med_manager.util.Util;

import java.util.Calendar;

/**
 * Created by Ese Udom on 4/9/2018.
 */

/** Manages the app's new medication page*/
public class NewMedicationActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        DialogInterface.OnClickListener {

    private EditText mEditText, mEditText1, mEditText2, mEditText3, mEditText4, mEditText5;
    private Button mButton;
    private DatePickerDialog mDatePickerDialog;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_med);

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
        textView.setText(getString(R.string.new_medication));

        //Initializing input fields
        mEditText = (EditText) findViewById(R.id.drug_name);
        mEditText1 = (EditText) findViewById(R.id.description);
        mEditText2 = (EditText) findViewById(R.id.interval);
        mEditText3 = (EditText) findViewById(R.id.hours);
        mEditText4 = (EditText) findViewById(R.id.start_date);
        mEditText5 = (EditText) findViewById(R.id.end_date);

        //Initializing action button
        mButton = (Button) findViewById(R.id.create);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_date:
                showDatePickerDialog(v);
                break;
            case R.id.end_date:
                showDatePickerDialog(v);
                break;
            case R.id.create:
                createNewMed();
        }
    }

    /**
     * Creates new medication reminder
     */
    private void createNewMed() {
        String drugName = mEditText.getText().toString();
        String description = mEditText1.getText().toString();
        String interval = mEditText2.getText().toString();
        String hours = mEditText3.getText().toString();
        String startDate = mEditText4.getText().toString();
        String endDate = mEditText5.getText().toString();

        if (!drugName.isEmpty() && !description.isEmpty() && !interval.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
            //Save med data
            DataSource.saveMedData(this, drugName, description, interval, hours,
                    String.valueOf(Util.dateToTimeInMillis(startDate)), String.valueOf(Util.dateToTimeInMillis(endDate)));
            //Set alarm
            addMedDates(startDate, endDate, interval, drugName, hours);

            finish();
        } else
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.fill_all), this);
    }

    /**
     * Medication start and end dates are added to a shared preference for easy access when waiting for reminder. So this method adds
     * the newly created dates in a certain order
     * @param startDate
     * @param endDate
     */
    private void addMedDates(String startDate, String endDate, String interval, String drugName, String hours) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String dates = sharedPreferences.getString(getString(R.string.prefs_file_med_dates_key), "");

        if (!dates.isEmpty()) {
            dates = dates + "," + startDate + "|" + endDate + "|" + interval + "|" + drugName + "|" + hours;
        } else {
            dates = startDate + "|" + endDate + "|" + interval + "|" + drugName + "|" + hours;
        }
        sharedPreferences.edit()
                .putString(getString(R.string.prefs_file_med_dates_key), dates)
                .apply();
    }

    /**
     * Launches a date picker dialog with current date set
     * @param v
     */
    private void showDatePickerDialog(final View v) {
        // Using DatePickerDialog
        // Calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR); // current year
        int month = c.get(Calendar.MONTH); // current month
        int day = c.get(Calendar.DAY_OF_MONTH); // current day

        //Initialize a global v
        mView = v;

        // date picker dialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //A bug fix for Android versions higher than 20
            final Context themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog);

            mDatePickerDialog = new DatePickerDialogFixedNougatSpinner(themedContext, this, year, month, day);
            mDatePickerDialog.show();
        } else {
            mDatePickerDialog = new DatePickerDialog(this, this, year, month, day);
            mDatePickerDialog.show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Set day of month, month and year values in the edit text
        EditText editText = (EditText) mView;
        editText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
