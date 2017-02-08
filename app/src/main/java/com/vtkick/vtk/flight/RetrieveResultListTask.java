package com.vtkick.vtk.flight;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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

/**
 * Created by Flames on 12/1/17.
 * This class executes the call to the Amadeus Flights API on a secondary thread.
 */
public class RetrieveResultListTask extends AsyncTask<Void, Integer, ArrayList<ReturnModel>> {

    private static final String API_URL = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search";
    private static final String CURRENCY = "EUR";
    private static final int RESULTS_LIMIT = 10;
    private ResultsActivity act;

    public RetrieveResultListTask(ResultsActivity act) {
        this.act = act;
        act.checkConnection();
    }

    /**
     * This method makes the connection with the Amadeus Flight API
     * and extracts the data.
     *
     * @return -> the ArrayList with the flights' data.
     */
    @Override
    protected ArrayList<ReturnModel> doInBackground(Void... voids) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            //build the string that holds the link
            StringBuilder sb = new StringBuilder(API_URL);
            sb.append("?apikey=" + BuildConfig.MY_AMADEUS_API_KEY
                    + "&origin=" + act.getIm().getFromMsg()
                    + "&destination=" + act.getIm().getToMsg()
                    + "&departure_date=" + act.getIm().getFromDateMsg());
            if (!act.getIm().getToDateMsg().equals("")) //if toDate field was filled
                sb.append("&return_date=" + act.getIm().getToDateMsg());
            if (act.getIm().getAdultsMsg().isEmpty()) //if adults field is null make it 0
                act.getIm().setAdultsMsg("0");
            sb.append("&adults=" + Integer.parseInt(act.getIm().getAdultsMsg()));
            if (Integer.parseInt(act.getIm().getChildrenMsg()) > 0)
                sb.append("&children=" + Integer.parseInt(act.getIm().getChildrenMsg()));
            if (Integer.parseInt(act.getIm().getInfantsMsg()) > 0)
                sb.append("&infants=" + Integer.parseInt(act.getIm().getInfantsMsg()));
            sb.append("&nonstop=" + act.getIm().getDirectOnlyMsg());
            if (!act.getIm().getClassMsg().equals(""))
                sb.append("&travel_class=" + act.getIm().getClassMsg());
            sb.append("&currency=" + CURRENCY
                    + "&number_of_results=" + RESULTS_LIMIT);

            //connect to the link
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in;
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
            Log.e("RESULTLIST", "Error processing API URL", e);
            return null;
        } catch (IOException e) {
            Log.e("RESULTLIST", "Error connecting to API", e);
            return null;
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return getResultsFromJSON(jsonResults);
    }

    /**
     * Parses JSON data and puts them in an ArrayList.
     *
     * @param jsonResults -> the json results from the web service.
     * @return -> the ArrayList with the data.
     */
    private ArrayList<ReturnModel> getResultsFromJSON(StringBuilder jsonResults) {
        ArrayList<ReturnModel> returnList = null;
        //convert the JSON data to an ArrayList data
        try {
            if (jsonResults.length() > 0) {
                // create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray resultsArray = jsonObj.getJSONArray("results");
                returnList = new ArrayList<>(resultsArray.length());

                String[] temp;
                ReturnModel ret;
                JSONArray itinerariesArray, flightsArray;
                JSONObject fareObject, outboundObject, inboundObject, arptObject;
                for (int i = 0; i < resultsArray.length(); i++) {
                    ret = new ReturnModel();
                    itinerariesArray = resultsArray.getJSONObject(i).getJSONArray("itineraries");
                    outboundObject = itinerariesArray.getJSONObject(0).getJSONObject("outbound");

                    fareObject = resultsArray.getJSONObject(i).getJSONObject("fare");
                    flightsArray = outboundObject.getJSONArray("flights");
                    arptObject = flightsArray.getJSONObject(0).getJSONObject("origin");

                    ret.setFromOrigin(act.getIm().getFromMsg());
                    ret.setToDestination(act.getIm().getToMsg());

                    ret.setFromArpt(arptObject.getString("airport"));
                    ret.setToArpt("");
                    for (int j = 0; j < flightsArray.length(); j++) {
                        arptObject = flightsArray.getJSONObject(j).getJSONObject("destination");
                        ret.setToArpt(ret.getToArpt() + " - " + arptObject.getString("airport"));
                    }
                    ret.setDepartureDate(flightsArray.getJSONObject(0).getString("departs_at"));
                    temp = ret.getDepartureDate().split("T"); // split time
                    ret.setDepartFromTime(temp[1]);           //  split time
                    ret.setArrivalDate(flightsArray.getJSONObject(flightsArray.length() - 1).getString("arrives_at"));
                    temp = ret.getArrivalDate().split("T"); // split time
                    ret.setDepartToTime(temp[1]);           //  split time
                    ret.setAirline(flightsArray.getJSONObject(0).getString("operating_airline"));
                    ret.setPrice(fareObject.getString("total_price"));

                    if (!itinerariesArray.getJSONObject(0).isNull("inbound")) {
                        inboundObject = itinerariesArray.getJSONObject(0).getJSONObject("inbound");
                        flightsArray = inboundObject.getJSONArray("flights");
                        arptObject = flightsArray.getJSONObject(0).getJSONObject("origin");
                        ret.setReturnFromArpt(arptObject.getString("airport"));
                        ret.setReturnToArpt("");
                        for (int j = 0; j < flightsArray.length(); j++) {
                            arptObject = flightsArray.getJSONObject(j).getJSONObject("destination");
                            ret.setReturnToArpt(ret.getReturnToArpt() + " - " + arptObject.getString("airport"));
                        }
                        ret.setReturnDate(flightsArray.getJSONObject(0).getString("departs_at"));
                        temp = ret.getReturnDate().split("T"); //split time
                        ret.setReturnFromTime(temp[1]); //split time
                        ret.setReturnArrival(flightsArray.getJSONObject(flightsArray.length() - 1).getString("arrives_at"));
                        temp = ret.getReturnArrival().split("T"); //split time
                        ret.setReturnToTime(temp[1]); //split time
                    }
                    returnList.add(ret);
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e("JSON RESULTLIST", "Cannot process JSON results", e);
        }
        return returnList;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    /**
     * Passes the ArrayList to the RetrieveAirlineTask for further processing.
     * This method is called when the doInBackground method is finished.
     *
     * @param result -> the data that the doInBackground method returns.
     */
    protected void onPostExecute(ArrayList<ReturnModel> result) {
        if (result != null) {
            RetrieveAirlineTask task = new RetrieveAirlineTask(act);
            ReturnModel[] returnModel = result.toArray(new ReturnModel[result.size()]);
            task.execute(returnModel);
        } else { //no results
            act.getProgressBar().setVisibility(View.GONE);
            act.setNoResultsAdapter();
        }
    }
}
