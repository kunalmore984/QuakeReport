package com.example.quakereport;

import static com.example.quakereport.MainActivity.LOG_TAG;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    /** Sample JSON response for a USGS query */
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractFeatureFromJSON(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        // new arraylist....
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        // Parsing the JSON....
        try {
            JSONObject base = new JSONObject(earthquakeJSON);
            JSONArray feature = base.getJSONArray("features");
            for (int i=0; i<feature.length();i++){
                JSONObject fobj = feature.getJSONObject(i);
                JSONObject pobj = fobj.getJSONObject("properties");
                double magnitude = pobj.getInt("mag");
                String location = pobj.getString("place");
                long time = pobj.getInt("time");
                String Jurl = pobj.getString("url");
                Earthquake earthquake = new Earthquake(magnitude,location,time,Jurl);
                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthquakes;
    }

    private static String makehttprequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if (url == null){
            return jsonResponse;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error Response code : "+urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // TODO: Handle the exception
            Log.e(LOG_TAG,"IOException error : ",e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Earthquake> fetchEarthquakedata(String reqURL){
        URL url = createUrl(reqURL);
        String jsonResponse = "";
        try {
            jsonResponse = makehttprequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Earthquake> eqs = extractFeatureFromJSON(jsonResponse);
        Log.v(LOG_TAG,"In fetchEarthquakedata : setting connection request and fetching the list : 7");
        return eqs;
    }

    private static URL createUrl(String url){
        URL newurl =null;
        try {
            newurl = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
            return null;
        }
        return newurl;
    }

}
