package com.vtkick.vtk.flight;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.vtkick.vtk.flight.activities.ResultsActivity;
import com.vtkick.vtk.flight.models.ReturnModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Flames on 13/1/17.
 * This class executes the call to the IATA Airline API on a secondary thread.
 */
public class RetrieveAirlineTask extends AsyncTask<ReturnModel, Void, ArrayList<ReturnModel>> {

    private static final String API_URL = "https://iatacodes.org/api/v6/airlines";
    private ResultsActivity act;

    public RetrieveAirlineTask(ResultsActivity act) {
        this.act = act;
        act.checkConnection();
    }

    /**
     * This method makes the connection with the IATA API and extracts the data.
     *
     * @param strings -> the ArrayList received from the RetrieveResultTask.
     * @return -> the ArrayList with the altered airline.
     */
    @Override
    protected ArrayList<ReturnModel> doInBackground(ReturnModel... strings) {
        ArrayList<ReturnModel> result = new ArrayList<>(Arrays.asList(strings));

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        URL url;
        InputStreamReader in;
        StringBuilder sb;
        for (ReturnModel res : result) {
            try {
                jsonResults = new StringBuilder();
                //build the string that holds the link
                sb = new StringBuilder(API_URL);
                sb.append("?api_key=" + BuildConfig.MY_IATA_API_KEY
                        + "&code=" + res.getAirline());

                //connect to the link
                url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    return null;
                } else
                    in = new InputStreamReader(conn.getInputStream());

                //load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1)
                    jsonResults.append(buff, 0, read);
            } catch (MalformedURLException e) {
                Log.e("AIRLINE", "Error processing API URL", e);
                return null;
            } catch (IOException e) {
                Log.e("AIRLINE", "Error connecting to API", e);
                return null;
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            res.setAirline(getAirlineFromJSON(jsonResults));
        }
        return result;
    }

    /**
     * Parses the JSON data and returns the name of the airline.
     * @param jsonResults -> the json results from the web service.
     * @return -> the name of the airline.
     */
    private String getAirlineFromJSON(StringBuilder jsonResults) {
        try {
            //extract the airline name from the results and return it
            return (new JSONObject(jsonResults.toString())).
                    getJSONArray("response").
                    getJSONObject(0).getString("name");
        } catch (JSONException e) {
            Log.e("JSON AIRLINE", "Cannot process JSON results", e);
        }
        return "ERROR";
    }

    /**
     * Sets the result list adapter and hides the progress bar.
     * Also expands all groups from the list.
     * This method is called when the doInBackground method is finished.
     *
     * @param result -> the data that the doInBackground method returns.
     */
    protected void onPostExecute(ArrayList<ReturnModel> result) {
        act.setAdapter(result);
        act.getProgressBar().setVisibility(View.GONE);
        //expand all groups
        ExpandableListView mExpandableListView = (ExpandableListView) act.findViewById(R.id.expListView);
        for(int i=0; i<act.getAdapter().getGroupCount(); i++)
            mExpandableListView.expandGroup(i);
    }
}
