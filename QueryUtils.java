package com.example.android.quakereport;

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

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {



    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Dataearth> extractearthquakesfromurl(String requesturl){
        URL url = createURL(requesturl);

        String jsonresponse = null;
        try {
            jsonresponse = makeHttprequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Dataearth> earthquake = extractEarthquakes(jsonresponse);

        // Return the {@link Event}
        return earthquake;
    }

    private static URL createURL(String stringurl){
        URL url = null;
        try {
            url = new URL(stringurl);
        } catch (MalformedURLException e) {
              Log.e(LOG_TAG,"The problem with URL",e);
        }
        return url;
    }

    private static String makeHttprequest(URL url) throws IOException {
        String jsonresponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonresponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection =(HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1500);
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonresponse = readfromstream(inputStream);
            }else {
                Log.e(LOG_TAG,"Error response code:"+ urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"Problem with HTTP request",e);
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonresponse;
    }

    private static String readfromstream(InputStream inputStream)throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream!= null){
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            try {
                String line = bufferedReader.readLine();
                while (line!=null ){
                    output.append(line);
                    line = bufferedReader.readLine();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem in readfromstream method",e);
            }
        } return output.toString();

    }

    /**
     * Return a list of {@link Dataearth} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Dataearth> extractEarthquakes(String earthquakeJSON) {


        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Dataearth> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
              JSONObject jsonRootObject = new JSONObject(earthquakeJSON);
              JSONArray jsonArray = jsonRootObject.optJSONArray("features");
              for (int i=0;i<jsonArray.length();i++){
                  JSONObject jsonObject = jsonArray.getJSONObject(i);
                  JSONObject jsonproperties = jsonObject.getJSONObject("properties");

                  double mag = jsonproperties.getDouble("mag");
                  String place = jsonproperties.optString("place").toString();
                  long time = jsonproperties.getLong("time");
                  String url = jsonproperties.getString("url");

                 earthquakes.add(new Dataearth(mag,place,time,url));
              }

            /*
             TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
             build up a list of Earthquake objects with the corresponding data.
            */

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}