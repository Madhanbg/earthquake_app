package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;


public class EarthquakeLoader extends AsyncTaskLoader<List<Dataearth>> {
    private String mUrl;
    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();
    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Now onStartLoading method");
        forceLoad();
    }

    @Override
    public List<Dataearth> loadInBackground() {
        Log.i(LOG_TAG, "Now loadInBackground method goin on");
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Dataearth> earthquakes = QueryUtils.extractearthquakesfromurl(mUrl);
        return earthquakes;
    }
}
