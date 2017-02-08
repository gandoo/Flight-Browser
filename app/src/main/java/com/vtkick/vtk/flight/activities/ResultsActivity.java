package com.vtkick.vtk.flight.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vtkick.vtk.flight.MyExpandableListAdapter;
import com.vtkick.vtk.flight.R;
import com.vtkick.vtk.flight.models.UserInputModel;
import com.vtkick.vtk.flight.RetrieveResultListTask;
import com.vtkick.vtk.flight.models.ReturnModel;

import java.util.ArrayList;

/**
 * This is the class that handles the Activity of the search results.
 */
public class ResultsActivity extends AppCompatActivity {

    private Intent intent;
    private UserInputModel im;
    private ProgressBar progressBar;
    private Toast connectionToast;
    private MyExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        intent = getIntent();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connectionToast = Toast.makeText(this, getResources().getString(R.string.connection_toast), Toast.LENGTH_SHORT);

        checkConnection();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //prevents a warning

        getDataFromIntent();

        //initialize fromBtn and toBtn
        Button fromBtn = (Button) findViewById(R.id.fromBtn);
        Button toBtn = (Button) findViewById(R.id.toBtn);
        fromBtn.setText(im.getFromMsg());
        toBtn.setText(im.getToMsg());

        //handle date views
        handleDateViewsAppearance();

        //convert data
        convertAirportInput();
        convertDates();

        //initialize the search (use of api)
        RetrieveResultListTask task = new RetrieveResultListTask(this);
        task.execute();
    }

    /**
     * Gets the data from the intent of the previous activity
     * and stores it on the input model variable.
     */
    private void getDataFromIntent() {
        im = new UserInputModel();
        Intent intent = getIntent();
        im.setFromMsg(intent.getStringExtra("from"));
        im.setToMsg(intent.getStringExtra("to"));
        im.setFromDateMsg(intent.getStringExtra("fromDate"));
        im.setToDateMsg(intent.getStringExtra("toDate"));
        im.setAdultsMsg(intent.getStringExtra("adults"));
        im.setChildrenMsg(intent.getStringExtra("children"));
        im.setInfantsMsg(intent.getStringExtra("infants"));
        im.setClassMsg(intent.getStringExtra("class"));
        im.setDirectOnlyMsg(intent.getStringExtra("directOnly"));
    }

    /**
     * Handles the fromDate and toDate views.
     */
    private void handleDateViewsAppearance() {
        TextView fromDateTView = (TextView) findViewById(R.id.dateTxt);
        TextView hyphenTView = (TextView) findViewById(R.id.dateTxt2);
        TextView toDateTView = (TextView) findViewById(R.id.dateTxt3);
        fromDateTView.setText(im.getFromDateMsg());
        toDateTView.setText(im.getToDateMsg());
        if (im.getToDateMsg().equals(""))
            hyphenTView.setText("");
        if (im.getToDateMsg().length() < 1)
            fromDateTView.setGravity(Gravity.START); //when no return date is set, change fromDate gravity
    }

    /**
     * Inflate the menu. The menu consists of a refresh button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    /**
     * Discards the origin and destination texts and keeps only airport code
     */
    private void convertAirportInput() {
        String[] temp;
        if (im.getFromMsg().contains("[")) {
            temp = im.getFromMsg().split("\\[");
            im.setFromMsg(temp[1]);
            im.setFromMsg(im.getFromMsg().substring(0, im.getFromMsg().length() - 1));
        }

        if (im.getToMsg().contains("[")) {
            temp = im.getToMsg().split("\\[");
            im.setToMsg(temp[1]);
            im.setToMsg(im.getToMsg().substring(0, im.getToMsg().length() - 1));
        }
    }

    /**
     * Converts the dates from dd/MM/yyyy to yyyy/MM/dd preparing
     * them for the api.
     */
    private void convertDates() {
        String[] temp;
        if (im.getFromDateMsg().length() > 2) {
            temp = im.getFromDateMsg().split("/");
            im.setFromDateMsg(temp[0]); //fromDateMsg used as temporary variable
            temp[0] = temp[2];
            temp[2] = im.getFromDateMsg();
            im.setFromDateMsg(temp[0] + "-" + temp[1] + "-" + temp[2]);
        }

        if (im.getToDateMsg().length() > 2) {
            temp = im.getToDateMsg().split("/");
            im.setToDateMsg(temp[0]); //toDateMsg used as temporary variable
            temp[0] = temp[2];
            temp[2] = im.getToDateMsg();
            im.setToDateMsg(temp[0] + "-" + temp[1] + "-" + temp[2]);
        }
    }

    /**
     * Sets the adapter of the results list.
     *
     * @param dataList -> the list that holds the results.
     */
    public void setAdapter(ArrayList<ReturnModel> dataList) {
        adapter = new MyExpandableListAdapter(this, dataList);
        ExpandableListView list = (ExpandableListView) findViewById(R.id.expListView);
        list.setAdapter(adapter);
    }

    /**
     * Sets the appropriate message when no flight is found.
     */
    public void setNoResultsAdapter() {
        TextView noResultTxt = (TextView) findViewById(R.id.noResultTxt);
        noResultTxt.setText(getResources().getString(R.string.no_result));
    }

    public MyExpandableListAdapter getAdapter() {
        return adapter;
    }

    /**
     * Handles the menu items' listeners.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.refreshButton:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Not the most elegant way to refresh the search.
     * Simple and effective for now.
     */
    private void refresh() {
        startActivity(intent);
        finish();
    }

    /**
     * Checks whether the user has an active connection.
     * If not, toast appropriate message.
     */
    public void checkConnection() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork == null || activeNetwork.getState() != NetworkInfo.State.CONNECTED) {
            if (!connectionToast.getView().isShown())
                Toast.makeText(this, getResources().getString(R.string.connection_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public UserInputModel getIm() {
        return im;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
