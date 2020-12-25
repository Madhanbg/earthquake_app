/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Dataearth>> {
    private static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    private static final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;
    /** Adapter for the list of earthquakes */
    private DataAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Now onCreate() is goingon");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       if( activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting()) {

           LoaderManager loaderManager = getLoaderManager();
           Log.i(LOG_TAG, "Now calling initLoader");
           loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
       }else{
           View loadingIndicator = findViewById(R.id.progressBar);
           loadingIndicator.setVisibility(View.GONE);
           mEmptyStateTextView = (TextView) findViewById(R.id.emptyState);
           mEmptyStateTextView.setText(R.string.no_internet_connection);
       }



        mAdapter = new DataAdapter(this, new ArrayList<Dataearth>());
        ListView earthquakeListView =(ListView)findViewById(R.id.list);
        earthquakeListView.setAdapter(mAdapter);
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyState);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Dataearth currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Dataearth>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "Now loader is created");
        return new EarthquakeLoader(EarthquakeActivity.this,USGS_URL);

    }

    @Override
    public void onLoadFinished(Loader<List<Dataearth>> loader, List<Dataearth> dataearths) {
        Log.i(LOG_TAG, "Now loader is finished");
        mEmptyStateTextView.setText(R.string.no_earthquakes);
         mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (dataearths != null && !dataearths.isEmpty()) {
            mAdapter.addAll(dataearths);

    }
    }

    @Override
    public void onLoaderReset(Loader<List<Dataearth>> loader) {
        mAdapter.clear();
        Log.i(LOG_TAG, "Now loader is resetting");
    }


}
