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

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.med_manager.R;

/**
 * Created by Ese Udom on 4/2/2018.
 */
public class CommonDialog {

    /** Cancellable dialog with negative and positive action buttons*/
    public static void showMessageOKCancel(Context context, String title, String message, DialogInterface.OnClickListener okListener,
                                           DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getResources().getString(R.string.ok_caps), okListener)
                .setNegativeButton(context.getResources().getString(R.string.cancel), cancelListener)
                .create()
                .show();
    }

    /** Cancellable dialog with only positive action button */
    public static void showMessageOKCancel1(Context context, String title, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getResources().getString(R.string.ok_caps), okListener)
                .create()
                .show();
    }
}
