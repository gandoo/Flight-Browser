package com.vtkick.vtk.flight;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.os.Looper.getMainLooper;

/**
 * Created by Flames on 2/1/17.
 * This class handles the autocomplete for the fromOrigin and toDestination views.
 */
public class MyAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private final static String API_URL = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete";
    private static ArrayList<String> resultList;
    private Toast toast;

    public MyAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        toast = Toast.makeText(context, context.getResources().getString(R.string.connection_toast), Toast.LENGTH_SHORT);
    }

    @Override
    public int getCount() {
        if (resultList != null)
            return resultList.size();
        return 0;
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    /**
     * Handles the autocomplete filter.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    //retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    if (resultList != null) {
                        //assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    } else {
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!toast.getView().isShown())
                                    toast.show();
                            }
                        });
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Handles the call to the Amadeus Autocomplete API.
     *
     * @param input -> the current string that the user has typed.
     * @return -> the array that holds the autocomplete suggestions.
     */
    private static ArrayList<String> autocomplete(String input) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            input = input.replaceAll(" ", "%20"); //replace space character with '%20'
            //build the string that holds the link
            StringBuilder sb = new StringBuilder(API_URL);
            sb.append("?apikey=" + BuildConfig.MY_AMADEUS_API_KEY
                    + "&term=" + input
                    + "&all_airports=false");

            //connect to the link
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in;
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK)
                return null;
            else
                in = new InputStreamReader(conn.getInputStream());

            //load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1)
                jsonResults.append(buff, 0, read);
        } catch (MalformedURLException e) {
            Log.e("AUTOCOMP", "Error processing API URL", e);
            return null;
        } catch (IOException e) {
            Log.e("AUTOCOMP", "Error connecting to API", e);
            return null;
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return getAutocompleteFromJSON(jsonResults);
    }

    /**
     * Parses JSON data and puts them in an ArrayList.
     *
     * @param jsonResults -> the json results from the web service.
     * @return -> the ArrayList with the data.
     */
    private static ArrayList<String> getAutocompleteFromJSON(StringBuilder jsonResults) {
        try {
            //create a JSON Array hierarchy from the results
            JSONArray jsonArray = new JSONArray(jsonResults.toString());

            //extract the labels from the results
            resultList = new ArrayList<>(jsonArray.length());
            String label;
            JSONObject c;
            for (int i = 0; i < jsonArray.length(); i++) {
                c = jsonArray.getJSONObject(i);
                label = c.getString("label");
                resultList.add(label);
            }
        } catch (JSONException e) {
            Log.e("JSON AUTOCOMP", "Cannot process JSON results", e);
        }
        return resultList;
    }


}
