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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.github.med_manager.adapter.MedListAdapter;
import com.github.med_manager.model.DataSource;
import com.github.med_manager.model.Medication;

import java.util.List;

/**
 * Created by Ese Udom on 4/9/2018.
 */

/** Manages the app's search activity page */
public class SearchActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;
    private RelativeLayout mRelativeLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initComponents();
    }

    private void initComponents() {
        //Initialize toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View shadowView = findViewById(R.id.view_shadow);

        //Using an alternative custom drop shadow for toolbar on older devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            shadowView.setVisibility(View.VISIBLE);
        }

        //Initialize some more
        mRecyclerView = (RecyclerView) findViewById(R.id.search_result_list);
        mLinearLayout = (LinearLayout) findViewById(R.id.empty_list);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.loading_screen);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        //searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null)
            handleIntent(intent);
    }

    /** Handles search query */
    private void handleQuery() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            loadResult(query);
        }
    }

    /** Handles search intent*/
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null && !query.isEmpty()) {
                loadResult(query);
            }
        }
    }

    /** Handles search query*/
    private void handleQuery(String query) {
        loadResult(query);
    }

    private void displayResult(List<Medication> medications) {
        // specify an adapter
        if (medications != null) {
            if (medications.isEmpty()) {
                mRelativeLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mRelativeLayout.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new MedListAdapter(this, R.layout.fragment_med_list_item, medications, mRelativeLayout);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private void loadResult(String query) {
        displayResult(DataSource.readMedData(this, query));
    }
}
