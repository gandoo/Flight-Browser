package com.vtkick.vtk.flight;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Flames on 29/12/16.
 * This class is responsible for the calendar that pops
 * when the user intents to give the date input.
 */
public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        //create a new instance of DatePickerDialog
        DatePickerDialog dp = new DatePickerDialog(getActivity(), this, year, month, day);

        Button fromDateBtn = (Button) getActivity().findViewById(R.id.fromDateBtn);
        Button toDateBtn = (Button) getActivity().findViewById(R.id.toDateBtn);
        setMinMaxDates(fromDateBtn, toDateBtn, dp);

        return dp;
    }

    /**
     * Sets the minimum and maximum dates on the calendar depending
     * on what the user has already chosen. Sets by default the minimum
     * date of the calendar to be the current date.
     */
    private void setMinMaxDates(Button fromDateBtn, Button toDateBtn, DatePickerDialog dp) {
        if (fromDateBtn.isActivated()) { //if fromDateBtn is pressed
            if (!toDateBtn.getText().toString().isEmpty()) {
                String[] temp = toDateBtn.getText().toString().split("/");
                Calendar c2 = Calendar.getInstance();
                c2.set(Integer.parseInt(temp[2]), Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[0]));
                dp.getDatePicker().setMaxDate(c2.getTimeInMillis());
            }
            dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else { //if toDateBtn is pressed
            if (!fromDateBtn.getText().toString().isEmpty()) {
                String[] temp = fromDateBtn.getText().toString().split("/");
                Calendar c2 = Calendar.getInstance();
                c2.set(Integer.parseInt(temp[2]), Integer.parseInt(temp[1]) - 1, Integer.parseInt(temp[0]));
                dp.getDatePicker().setMinDate(c2.getTimeInMillis());
            } else
                dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
    }

    /**
     * This method is called when the user picks a date.
     * Sets the chosen date to be written in the appropriate date view.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (view.isShown()) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            String formattedDate = sdf.format(c.getTime());

            Button dateBtn;
            if (getActivity().findViewById(R.id.fromDateBtn).isActivated())
                dateBtn = (Button) getActivity().findViewById(R.id.fromDateBtn);
            else
                dateBtn = (Button) getActivity().findViewById(R.id.toDateBtn);

            dateBtn.setText(formattedDate);
            dateBtn.setActivated(false);
        }
    }

}

