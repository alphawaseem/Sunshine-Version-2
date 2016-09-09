package com.example.android.sunshine.app;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {


    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private int cityId = 573201;
    private int noOfDays = 7 ;
    private String appid = AppId.getApiKey();
    private  String units = "metrics";
    private String mode = "json";

    private final String QUERY_PARAM  = "q" ;
    private final String MODE_PARAM = "mode";
    private final String UNITS_PARAM = "units" ;
    private final String DAYS_PARAM = "cnt" ;
    private final String API_KEY_PARAM = "appid" ;

    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?" ;

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // dummy data copied from udacity git repo
        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        ArrayList<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, weekForecast);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);



        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<URL,Void,String[]> {

        @Override
        protected String[] doInBackground(URL... urls) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            URL url = urls[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1500);
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG,forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            Uri builder = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_PARAM,Integer.toString(cityId))
                .appendQueryParameter(DAYS_PARAM,Integer.toString(noOfDays))
                .appendQueryParameter(UNITS_PARAM,units)
                .appendQueryParameter(MODE_PARAM,mode)
                .appendQueryParameter(API_KEY_PARAM,appid).build();

            URL url = new URL(builder.toString());
            switch (item.getItemId()) {
                case R.id.refresh : new FetchWeatherTask().execute(url);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return true;
    }

}
