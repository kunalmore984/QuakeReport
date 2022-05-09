package com.example.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    private EarthquakeAdapter mearthquakeAdapter;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView emptyview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes the list of earthquakes as input
        mearthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        emptyview = (TextView) findViewById(R.id.empty_view);


        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isConnectedOrConnecting()){
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.v(LOG_TAG,"initloader message : 1");
        }else {
            ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);
            mProgressBar.setVisibility(View.GONE);
            emptyview.setText(R.string.No_Internet);
        }
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mearthquakeAdapter);
        earthquakeListView.setEmptyView(emptyview);
            /*emptyview.setVisibility(View.VISIBLE);*/

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mearthquakeAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the LoaderManager, in order to interact with loaders.

    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        EarthquakeLoader earthquakeLoader = new EarthquakeLoader(MainActivity.this,REQUEST_URL);
        Log.v(LOG_TAG,"OnCreateLoader message : 2");
        return earthquakeLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        emptyview.setText(R.string.No_Earthquake);
       ProgressBar ProgressBar = (android.widget.ProgressBar) findViewById(R.id.loading_indicator);
        ProgressBar.setVisibility(View.GONE);
       /* if (isConnected == false){
            emptyview.setText("NO Internet Connection !");
        }*/
        // Clear the adapter of previous earthquake data
        mearthquakeAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mearthquakeAdapter.addAll(data);

        }
        Log.v(LOG_TAG,"In onLoadFinished Clearing the custom adapter : 3");

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mearthquakeAdapter.clear();
        Log.v(LOG_TAG,"IN onLoaderReset : 4");
    }
}