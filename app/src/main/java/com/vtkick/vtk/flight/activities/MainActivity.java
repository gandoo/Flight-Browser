package com.vtkick.vtk.flight.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vtkick.vtk.flight.DateFragment;
import com.vtkick.vtk.flight.MyAutocompleteAdapter;
import com.vtkick.vtk.flight.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This is the main Activity class. Through this class, the user
 * input is handled. When the user initializes the search, the next
 * activity starts. (ResultsActivity)
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AutoCompleteTextView fromBtn;
    private AutoCompleteTextView toBtn;
    private Toast latinToast;
    private Toast connectionToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the toasts that are likely to be used
        latinToast = Toast.makeText(this, getResources().getString(R.string.latin), Toast.LENGTH_SHORT);
        connectionToast = Toast.makeText(this, getResources().getString(R.string.connection_toast), Toast.LENGTH_SHORT);

        checkConnection();
        setCurrentDate();

        initializeSpinner();

        //handle the AutoCompleteTextViews
        fromBtn = (AutoCompleteTextView) findViewById(R.id.fromBtn);
        toBtn = (AutoCompleteTextView) findViewById(R.id.toBtn);
        initializeAutoComplete(fromBtn);
        initializeAutoComplete(toBtn);

        addListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    /**
     * Sets the fromDateBtn text to current date.
     */
    private void setCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar cal = Calendar.getInstance();
        Button fromDateBtn = (Button) findViewById(R.id.fromDateBtn);
        fromDateBtn.setText(dateFormat.format(cal.getTime()));
        fromDateBtn.setActivated(false);
    }

    /**
     * Initializes the class spinner.
     */
    private void initializeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.class_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Initializes a given AutoCompleteTextView.
     */
    private void initializeAutoComplete(AutoCompleteTextView btn) {
        btn.setAdapter(new MyAutocompleteAdapter(this, R.layout.list_item));
        btn.setDropDownHorizontalOffset(-20);
        btn.setOnItemClickListener(this);
        addTextChangedListener(btn);
    }

    /**
     * This method is responsible for hiding the keyboard
     * when an item is selected.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Checks whether the user has an active connection.
     * If not, toast appropriate message.
     */
    private void checkConnection() {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork == null || activeNetwork.getState() != NetworkInfo.State.CONNECTED) {
            if (!connectionToast.getView().isShown())
                connectionToast.show();
        }
    }

    /**
     * This method adds all the on click listeners of the layout's views.
     */
    private void addListeners() {
        //deleteBtn1 listener
        final ImageButton deleteBtn1 = (ImageButton) findViewById(R.id.deleteBtn1);
        deleteBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromBtn.setText("");
            }
        });

        //deleteBtn2 listener
        final ImageButton deleteBtn2 = (ImageButton) findViewById(R.id.deleteBtn2);
        deleteBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toBtn.setText("");
            }
        });

        //fromDateBtn listener
        final Button fromDateBtn = (Button) findViewById(R.id.fromDateBtn);
        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDateBtn.setActivated(true);
                DialogFragment picker = new DateFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        //toDateBtn listener
        final Button toDateBtn = (Button) findViewById(R.id.toDateBtn);
        toDateBtn.setClickable(false);
        toDateBtn.setBackgroundResource(R.drawable.my_button_disabled);
        toDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDateBtn.setActivated(true);
                DialogFragment picker = new DateFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        //returnCheckBox listener
        final CheckBox returnChkBox = (CheckBox) findViewById(R.id.returnChkBox);
        returnChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (returnChkBox.isChecked()) {
                    toDateBtn.setText(fromDateBtn.getText().toString());
                    toDateBtn.setClickable(true);
                    toDateBtn.setBackgroundResource(R.drawable.my_button_idle);
                } else {
                    toDateBtn.setText("");
                    toDateBtn.setClickable(false);
                    toDateBtn.setBackgroundResource(R.drawable.my_button_disabled);
                }
            }
        });

        //directFlightsOnlyCheckBox listener
        final CheckBox directChkBox = (CheckBox) findViewById(R.id.directChkBox);
        directChkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //searchBtn listener
        final Button searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromBtn.getText().toString().isEmpty() || toBtn.getText().toString().isEmpty() || fromDateBtn.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.required_fields), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    AutoCompleteTextView fromBtn = (AutoCompleteTextView) findViewById(R.id.fromBtn);
                    AutoCompleteTextView toBtn = (AutoCompleteTextView) findViewById(R.id.toBtn);
                    TextView adultsTView = (TextView) findViewById((R.id.adultTView));
                    TextView childrenTView = (TextView) findViewById((R.id.childrenTView));
                    TextView infantsTView = (TextView) findViewById((R.id.infantTView));
                    Spinner classSpinner = (Spinner) findViewById((R.id.class_spinner));
                    intent.putExtra("from", fromBtn.getText().toString());
                    intent.putExtra("to", toBtn.getText().toString());
                    intent.putExtra("fromDate", fromDateBtn.getText().toString());
                    intent.putExtra("toDate", toDateBtn.getText().toString());
                    intent.putExtra("adults", adultsTView.getText().toString());
                    intent.putExtra("children", childrenTView.getText().toString());
                    intent.putExtra("infants", infantsTView.getText().toString());
                    intent.putExtra("class", classSpinner.getSelectedItem().toString());
                    intent.putExtra("directOnly", directChkBox.isChecked() + "");
                    startActivity(intent);
                }
            }
        });

        addModPeopleButtonsListeners();
    }

    /**
     * Adds the subtract and add button listeners of the views
     * adultTView, childrenTView, infantTView calling the method
     * addModPeopleListener.
     */
    private void addModPeopleButtonsListeners() {
        Button sub1Btn = (Button) findViewById(R.id.sub1);
        TextView adultsTView = (TextView) findViewById(R.id.adultTView);
        Button add1Btn = (Button) findViewById(R.id.add1);
        addModPeopleListener(sub1Btn, add1Btn, adultsTView);

        Button sub2Btn = (Button) findViewById(R.id.sub2);
        TextView childrenTView = (TextView) findViewById(R.id.childrenTView);
        Button add2Btn = (Button) findViewById(R.id.add2);
        addModPeopleListener(sub2Btn, add2Btn, childrenTView);

        Button sub3Btn = (Button) findViewById(R.id.sub3);
        TextView infantsTView = (TextView) findViewById(R.id.infantTView);
        Button add3Btn = (Button) findViewById(R.id.add3);
        addModPeopleListener(sub3Btn, add3Btn, infantsTView);
    }

    /**
     * This method actually adds the listeners of the subtract and add
     * buttons. It is called from the addModPeopleButtonsListeners method.
     */
    private void addModPeopleListener(Button subBtn, Button addBtn, final TextView textView) {
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(textView.getText().toString()) > 0)
                    textView.setText(Integer.parseInt(textView.getText().toString()) - 1 +"");
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(Integer.parseInt(textView.getText().toString()) + 1+"");
            }
        });
    }

    /**
     * Handles the text changed listener of the AutoCompleteTextViews.
     * It is used to check if a user types in greek and warns them.
     */
    private void addTextChangedListener(AutoCompleteTextView button) {
        button.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!latinToast.getView().isShown()) {
                    if (charSequence.toString().matches("^[α-ωΑ-Ω ]")) {
                        latinToast.show();
                    }
                }
            }
        });
    }
}
