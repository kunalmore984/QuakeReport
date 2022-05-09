package com.example.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/*import androidx.loader.content.AsyncTaskLoader;*/

import java.util.List;

import android.content.Context;
import android.util.Log;

import java.util.List;
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    String murls;
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    public EarthquakeLoader(@NonNull Context context, String url) {
        super(context);
        murls = url;

    }

    @Nullable
    @Override
    public List<Earthquake> loadInBackground() {
        if (murls.length() < 1 || murls == null) {
            return null;
        }
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakedata(murls);
        Log.v(LOG_TAG,"In loadInBackground : Loading earthquake data from query utlis : 6");
        return earthquakes;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(LOG_TAG,"In onStartLoading: executing forceload method : 5");
    }
}
