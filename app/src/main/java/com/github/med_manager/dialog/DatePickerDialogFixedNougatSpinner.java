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
package com.github.med_manager.dialog;

/**
 * Created by Ese Udom on 10/17/2017.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DatePicker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Workaround for this bug: https://code.google.com/p/android/issues/detail?id=222208
 * In Android 7.0 Nougat, spinner mode for the DatePicker in DatePickerDialog is
 * incorrectly displayed as calendar, even when the theme specifies otherwise.
 *
 * Modified slightly from the equivalent fix for TimePicker from @jeffdgr8:
 *     https://gist.github.com/jeffdgr8/6bc5f990bf0c13a7334ce385d482af9f
 */
public class DatePickerDialogFixedNougatSpinner extends DatePickerDialog {

    private final static String TAG = DatePickerDialogFixedNougatSpinner.class.getSimpleName();

    /**
     * Creates a new date picker dialog.
     *
     * @param context the parent context
     * @param listener the listener to call when the time is set
     * @param year the initial year
     * @param month the initial month
     * @param dayOfMonth the initial day of month
     */
    public DatePickerDialogFixedNougatSpinner(Context context, OnDateSetListener listener, int year, int month,
                                              int dayOfMonth) {

        super(context, listener, year, month, dayOfMonth);
        fixSpinner(context, year, month, dayOfMonth);
    }

    /**
     * Creates a new time picker dialog with the specified theme.
     *
     * @param context the parent context
     * @param themeResId the resource ID of the theme to apply to this dialog
     * @param listener the listener to call when the time is set
     * @param year the initial year
     * @param month the initial month
     * @param dayOfMonth the initial day of month
     */
    public DatePickerDialogFixedNougatSpinner(Context context, int themeResId, OnDateSetListener listener, int year, int month,
                                              int dayOfMonth) {

        super(context, themeResId, listener, year, month, dayOfMonth);
        fixSpinner(context, year, month, dayOfMonth);
    }

    private void fixSpinner(Context context, int year, int month, int dayOfMonth) {
        // The spinner vs not distinction probably started in lollipop but applying this
        // for versions < nougat leads to a crash trying to get DatePickerSpinnerDelegate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // Get the theme's android:datePickerMode
                final int MODE_SPINNER = 1;
                Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
                Field datePickerStyleableField = styleableClass.getField("DatePicker");
                int[] datePickerStyleable = (int[]) datePickerStyleableField.get(null);
                final TypedArray a = context.obtainStyledAttributes(null, datePickerStyleable,
                        android.R.attr.datePickerStyle, 0);
                Field datePickerModeStyleableField = styleableClass.getField("DatePicker_datePickerMode");
                int datePickerModeStyleable = datePickerModeStyleableField.getInt(null);
                final int mode = a.getInt(datePickerModeStyleable, MODE_SPINNER);
                a.recycle();

                if (mode == MODE_SPINNER) {
                    DatePicker datePicker = (DatePicker) findField(DatePickerDialog.class,
                            DatePicker.class, "mDatePicker").get(this);
                    Class<?> delegateClass = Class.forName("android.widget.DatePicker$DatePickerDelegate");
                    Field delegateField = findField(DatePicker.class, delegateClass, "mDelegate");
                    Object delegate = delegateField.get(datePicker);

                    Class<?> spinnerDelegateClass = Class.forName("android.widget.DatePickerSpinnerDelegate");

                    // In 7.0 Nougat for some reason the datePickerMode is ignored and the
                    // delegate is DatePickerCalendarDelegate
                    if (delegate.getClass() != spinnerDelegateClass) {
                        delegateField.set(datePicker, null); // throw out the DatePickerCalendarDelegate!
                        datePicker.removeAllViews(); // remove the DatePickerCalendarDelegate views

                        Constructor spinnerDelegateConstructor = spinnerDelegateClass
                                .getDeclaredConstructor(DatePicker.class, Context.class,
                                        AttributeSet.class, int.class, int.class);
                        spinnerDelegateConstructor.setAccessible(true);

                        // Instantiate a DatePickerSpinnerDelegate
                        delegate = spinnerDelegateConstructor.newInstance(datePicker, context,
                                null, android.R.attr.datePickerStyle, 0);

                        // set the DatePicker.mDelegate to the spinner delegate
                        delegateField.set(datePicker, delegate);

                        // Set up the DatePicker again, with the DatePickerSpinnerDelegate
                        datePicker.updateDate(year, month, dayOfMonth);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Field findField(Class objectClass, Class fieldClass, String expectedName) {
        try {
            Field field = objectClass.getDeclaredField(expectedName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            Log.i(TAG, e.getMessage()); // ignore
        }

        // search for it if it wasn't found under the expected ivar name
        for (Field searchField : objectClass.getDeclaredFields()) {
            if (searchField.getType() == fieldClass) {
                searchField.setAccessible(true);
                return searchField;
            }
        }
        return null;
    }

}
