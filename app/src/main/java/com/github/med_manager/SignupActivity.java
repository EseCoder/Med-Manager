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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.med_manager.dialog.CommonDialog;
import com.github.med_manager.model.DataSource;
import com.github.med_manager.network.DataConnectionChecker;
import com.github.med_manager.receiver.MedStartAndEndCheckReceiver;
import com.github.med_manager.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Created by Ese Udom on 4/2/2018.
 */

/** Manages the app's sign up page */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final int RC_SIGN_IN = 9;
    private static final String TAG = SignupActivity.class.getSimpleName();
    private Button mButton;
    private SignInButton mButton1;
    private EditText mEditText, mEditText1, mEditText2, mEditText3;
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout mRelativeLayout;
    private PendingIntent mPendingIntent;
    private AlarmManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        textView.setText(getString(R.string.signup));

        //Initializing input fields
        mEditText = (EditText)findViewById(R.id.name);
        mEditText1 = (EditText)findViewById(R.id.email);
        mEditText2 = (EditText)findViewById(R.id.phone);
        mEditText3 = (EditText)findViewById(R.id.password);

        //Initializing action buttons
        mButton = (Button)findViewById(R.id.button_signup);
        mButton.setOnClickListener(this);

        mButton1 = (SignInButton)findViewById(R.id.google_signin);
        mButton1.setOnClickListener(this);

        initGoogleSignin();

        //Initialize progress bar
        mRelativeLayout = (RelativeLayout)findViewById(R.id.loading_screen);
    }

    /** Initializes google sign in client object */
    private void initGoogleSignin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signup: validateEntries();
                break;
            case R.id.google_signin: signupWithGoogle();
        }
    }

    /** Sends a google signin intent*/
    private void signupWithGoogle() {
        if (connectionAvailable()) {
            //Show progress bar
            mRelativeLayout.setVisibility(View.VISIBLE);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.connection_not_avalable), this);
        }
    }

    /** Validates entries and saves them in local database*/
    private void validateEntries() {
        //Retrieving entries
        String name = mEditText.getText().toString();
        String email = mEditText1.getText().toString();
        String phone = mEditText2.getText().toString();
        String password = mEditText3.getText().toString();

        if (!name.isEmpty() && !password.isEmpty()) {
            //Show progress bar
            mRelativeLayout.setVisibility(View.VISIBLE);

            if (!email.isEmpty() && Util.verifyEmail(email)) {
                //Check for already existing account
                String n = DataSource.readProfileData(this, "name");
                String e = DataSource.readProfileData(this, "email");
                if ((n != null && n.equals(name)) || (e != null && e.equals(email))) {
                    CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.account_already_exist), this);
                    //Remove progress bar
                    mRelativeLayout.setVisibility(View.GONE);
                } else {
                    save(name, email, phone, password);
                }
            } else {
                save(name, email, phone, password);
            }
        } else {
            //Showing an error dialog
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.name_password_cant_empty), this);
        }
    }

    /** Saves record in database */
    private void save(String name, String email, String phone, String password) {
        //Save to local database
        //Hash password to keep it safe
        DataSource.saveProfileData(this, name, email, phone, Util.computeSHAHash(password), "", "1");
        int c = 1;
        //Debug
        Log.d("SignupActivity", "Row ID = " + c);

        //Check if sign up was successful
        if (c != -1) {
            updateLoginStatus();

            cancelCheckingService();
            startCheckingService();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
            LandingActivity.getInstance().finish();
        } else {
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.signup_unsuccessful), this);
        }
        //Remove progress bar
        mRelativeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /** Handles sign in result */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            //Signed in successfully, show authenticated UI.
            //Save data retrieved to local database and launch home page
            //updateUI(account);
            DataSource.saveProfileData(this, account.getDisplayName(), account.getEmail(), "", Util.computeSHAHash(account.getDisplayName()),
                    account.getId(), "1");

            updateLoginStatus();

            cancelCheckingService();
            startCheckingService();

            //Remove progress bar
            mRelativeLayout.setVisibility(View.GONE);

            startActivity(new Intent(this, HomeActivity.class));
            finish();
            LandingActivity.getInstance().finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);

            //Showing error dialog
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.google_signin_failed), this);

            //Remove progressbar
            mRelativeLayout.setVisibility(View.GONE);
        }
    }

    /**Start alarm service to check medication start and end date */
    private void startCheckingService() {
        // Retrieve a PendingIntent that will perform a broadcast
        Intent checkIntent = new Intent(this, MedStartAndEndCheckReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, Util.MED_START_CHECK_CODE1, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 3600000;

        mManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, mPendingIntent);
        Log.d(TAG, "Med Start Check Alarm started");
    }

    /**Stops any previous alarm service*/
    public void cancelCheckingService() {
        try {
            Intent checkIntent = new Intent(this, MedStartAndEndCheckReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(this, Util.MED_START_CHECK_CODE1, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mManager.cancel(mPendingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Exception while cancelling Med Start Check Alarm");
        }

        Log.d(TAG, "Med Start Check Alarm cancelled");
    }

    /** Checks for connection status */
    private boolean connectionAvailable() {
        return new DataConnectionChecker(this).isConnected();
    }

    /** Updates login status */
    private void updateLoginStatus() {
        //Update login status to local database
        DataSource.updateProfileData(this, "loginstatus", "1");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
}
