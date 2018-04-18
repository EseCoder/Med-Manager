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

/** Manages the app's login page */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final int RC_SIGN_IN = 9;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mEditText, mEditText1;
    private Button mButton;
    private SignInButton mButton1;
    private RelativeLayout mRelativeLayout;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        textView.setText(getString(R.string.login));

        //Initializing input fields
        mEditText = (EditText)findViewById(R.id.email);
        mEditText1 = (EditText)findViewById(R.id.password);

        //Initializing action buttons
        mButton = (Button)findViewById(R.id.button_login);
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
            case R.id.button_login: validateEntries();
                break;
            case R.id.google_signin: loginWithGoogle();
                break;
        }
    }

    /** Sends a google signin intent*/
    private void loginWithGoogle() {
        if (connectionAvailable()) {
            //Show progress bar
            mRelativeLayout.setVisibility(View.VISIBLE);

            //Query database
            String id = DataSource.readProfileData(this, "googleid");
            String name = DataSource.readProfileData(this, "name");
            if ((name != null && !name.isEmpty()) || (id != null && !id.isEmpty())) {
                //Show progress bar
                mRelativeLayout.setVisibility(View.GONE);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                LandingActivity.getInstance().finish();
            } else {
                mRelativeLayout.setVisibility(View.GONE);
                CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.no_account), this);
            }

            //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            //startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.connection_not_avalable), this);
        }
    }

    /** Validates entries and saves them in local database*/
    private void validateEntries() {
        //Retrieving entries
        String nameEmail = mEditText.getText().toString();
        String password = mEditText1.getText().toString();

        if (!nameEmail.isEmpty() && !password.isEmpty()) {
            //Show progress bar
            mRelativeLayout.setVisibility(View.VISIBLE);
            //Query from database and verify
            String name = DataSource.readProfileData(this, "name");
            String email = DataSource.readProfileData(this, "email");
            String pd = DataSource.readProfileData(this, "password");

            if ((nameEmail.equals(name) || nameEmail.equals(email)) && password.equals(pd)) {
                updateLoginStatus();

                startActivity(new Intent(this, HomeActivity.class));
                finish();
                LandingActivity.getInstance().finish();
            } else {
                mRelativeLayout.setVisibility(View.GONE);
                CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.password_email_not_matching), this);
            }
        }
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

            // Signed in successfully, show authenticated UI.
            //Query local database to see if openid exists, if not show error dialog
            //updateUI(account);

            //Remove progress bar
            mRelativeLayout.setVisibility(View.GONE);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);

            //Showing error dialog
            CommonDialog.showMessageOKCancel1(this, getString(R.string.oops), getString(R.string.google_signin_failed), this);

            //Remove progress bar
            mRelativeLayout.setVisibility(View.GONE);
        }
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
