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
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.med_manager.adapter.MedListAdapter;
import com.github.med_manager.model.DataSource;
import com.github.med_manager.model.Medication;

import java.util.List;

/**
 * Created by Ese Udom on 4/3/2018.
 */

/** Manages the app's home page */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout, mLinearLayout1;
    private TextView mTextView;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayout;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponents();
        initList();
    }

    private void initComponents() {
        //Initialize toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Create a custom toolbar drop shadow for older versions
        View shadowView = findViewById(R.id.view_shadow);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            shadowView.setVisibility(View.VISIBLE);
        }

        //Initialize navigation view and drawer layout
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        //Initialize profile pane on the navigation drawer
        mLinearLayout = (LinearLayout) findViewById(R.id.profile_pane);
        mLinearLayout.setOnClickListener(this);

        //Initialize logout text view on the navigation drawer
        mTextView = (TextView) findViewById(R.id.logout);
        mTextView.setOnClickListener(this);

        //Initialize the fab
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

    }

    private void initList() {
        mRecyclerView = (RecyclerView) findViewById(R.id.med_list);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.loading_screen);
        mLinearLayout1 = (LinearLayout) findViewById(R.id.empty_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadDataSet();
    }

    /** Loads data set from database and displays them in recycler view */
    private void loadDataSet() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        List<Medication> list = DataSource.readAllMedData(this);
        if (list != null && !list.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new MedListAdapter(this, R.layout.fragment_med_list_item, list, mRelativeLayout);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //Minimize app
        this.moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_pane:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.logout:
                logUserOut();
                break;
            case R.id.fab:
                startActivity(new Intent(this, NewMedicationActivity.class));
                break;
        }
    }

    /** Updates user login status in database and resets to Landing page */
    private void logUserOut() {
        DataSource.updateProfileData(this, "loginstatus", "0");
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        mSearchMenuItem = menu.findItem(R.id.search);
        mSearchMenuItem.setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search && item.isEnabled()) {
            startActivity(new Intent(this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
