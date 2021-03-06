package com.example.aditya.surveymatrix;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.surveygini.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Registration extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();
    ConstraintSet constraintSet = null;
    ConstraintLayout constraintLayout = null;
    EditText editTextDate = null;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    HashMap<String,String> spinnerMap = new HashMap<String, String>();
    String []name, type, label, length, datatype, mandatory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Bundle bundle = getIntent().getExtras();
        String result = bundle.getString("resp");
        constraintSet = new ConstraintSet();
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        constraintSet.clone(constraintLayout);
        setLayout(result);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Registration.this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setLayout(String text){
        int padding = 300;
        try{
            JSONObject jsonObj = new JSONObject(text);
            JSONArray field = jsonObj.getJSONArray("field");
            int size = field.length();
            name=new String[size];
            type=new String[size];
            label=new String[size];
            length=new String[size];
            datatype=new String[size];
            mandatory=new String[size];
            int i;
            for (i = 0; i < field.length(); i++ , padding=padding+200) {
                JSONObject element = field.getJSONObject(i);
                name[i] = element.getString("name");
                label[i]= element.getString("label");
                type[i]= element.getString("type");
                length[i]= element.getString("length");
                datatype[i]= element.getString("datatype");
                mandatory[i]= element.getString("mandatory");
                if(type[i].equals("dropdown")){
                    JSONArray options = element.getJSONArray("options");
                    Spinner spinner = new Spinner(this);
                    spinner.setId(i+1);
                    List<String> list = new ArrayList<>();
                    list.add(label[i]);
                    for(int j=0;j<options.length();j++){
                        JSONObject optionNames = options.getJSONObject(j);
                        spinnerMap.put(optionNames.getString("id"),optionNames.getString("label"));
                        list.add(optionNames.getString("label"));
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    constraintLayout.addView(spinner);

                    constraintSet.connect(spinner.getId(),ConstraintSet.LEFT,R.id.constraintLayout,ConstraintSet.LEFT);
                    constraintSet.connect(spinner.getId(),ConstraintSet.RIGHT,R.id.constraintLayout,ConstraintSet.RIGHT);
                    constraintSet.connect(spinner.getId(),ConstraintSet.TOP,R.id.constraintLayout,ConstraintSet.TOP,padding);
                    constraintSet.constrainHeight(spinner.getId(),ConstraintSet.WRAP_CONTENT);

                    constraintSet.applyTo(constraintLayout);
                    padding=padding-100;
                }
                else if(name[i].equals("date_of_birth")){
                    editTextDate = new EditText(this);
                    editTextDate.setHint(label[i]);
                    editTextDate.setId(i+1);
                    editTextDate.setClickable(true);
                    editTextDate.setFocusable(false);

                    year  = myCalendar.get(Calendar.YEAR);
                    month = myCalendar.get(Calendar.MONTH);
                    day   = myCalendar.get(Calendar.DAY_OF_MONTH);

                    editTextDate.setHint(new StringBuilder().append("Date Of Birth: ").append(day).append("-").append(month + 1)
                            .append("-").append(year)
                            .append(" "));

                    editTextDate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // On button click show datepicker dialog
                            showDialog(DATE_PICKER_ID);

                        }

                    });

                    constraintLayout.addView(editTextDate);

                    constraintSet.connect(editTextDate.getId(),ConstraintSet.LEFT,R.id.constraintLayout,ConstraintSet.LEFT);
                    constraintSet.connect(editTextDate.getId(),ConstraintSet.RIGHT,R.id.constraintLayout,ConstraintSet.RIGHT);
                    constraintSet.connect(editTextDate.getId(),ConstraintSet.TOP,R.id.constraintLayout,ConstraintSet.TOP,padding);
                    constraintSet.constrainHeight(editTextDate.getId(),ConstraintSet.WRAP_CONTENT);

                    constraintSet.applyTo(constraintLayout);
                }
                else{
                    EditText editText = new EditText(this);
                    editText.setHint(label[i]);
                    editText.setId(i+1);
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    constraintLayout.addView(editText);

                    constraintSet.connect(editText.getId(),ConstraintSet.LEFT,R.id.constraintLayout,ConstraintSet.LEFT);
                    constraintSet.connect(editText.getId(),ConstraintSet.RIGHT,R.id.constraintLayout,ConstraintSet.RIGHT);
                    constraintSet.connect(editText.getId(),ConstraintSet.TOP,R.id.constraintLayout,ConstraintSet.TOP,padding);
                    constraintSet.constrainHeight(editText.getId(),ConstraintSet.WRAP_CONTENT);

                    constraintSet.applyTo(constraintLayout);
                }
            }
            Button button = new Button(this);
            button.setId(i+1);
            button.setText("Register");

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                   /* if(isValidateFieldData()){

                    }*/
                   boolean valid = isValidateFieldData();
                   if(valid)
                    Toast.makeText(Registration.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                }
            });
            constraintLayout.addView(button);
            constraintSet.connect(button.getId(),ConstraintSet.LEFT,R.id.constraintLayout,ConstraintSet.LEFT);
            constraintSet.connect(button.getId(),ConstraintSet.RIGHT,R.id.constraintLayout,ConstraintSet.RIGHT);
            constraintSet.connect(button.getId(),ConstraintSet.TOP,R.id.constraintLayout,ConstraintSet.TOP,padding);
            constraintSet.constrainHeight(button.getId(),ConstraintSet.WRAP_CONTENT);

            constraintSet.applyTo(constraintLayout);
        }catch(Exception e){

        }
    }

    public boolean isValidateFieldData(){
        boolean isValid=true;
        try{
            for(int i=0;i<name.length;i++){
                if(type[i].equals("dropdown") && mandatory[i].equals("Y")) {
                    Spinner text = findViewById(i+1);
                    if(!spinnerMap.containsValue(text.getSelectedItem().toString())) {
                        TextView errorText = (TextView)text.getSelectedView();
                        errorText.setError("Please select an option");//changes the selected item text to this
                        isValid=false;
                    }
                }
                else if(name[i].equals("date_of_birth")) {
                    EditText text = findViewById(i+1);
                    //Toast.makeText(Registration.this,text.getText().toString(),Toast.LENGTH_LONG).show();
                    if(text.getText().toString().length()==0) {
                        text.setError("Please select a date");
                        isValid=false;
                    }
                }
                else if(mandatory[i].equals("Y")){
                    EditText text = findViewById(i+1);
                    if(text.getText().toString().length()==0) {
                        text.setError("Please fill this field");
                        isValid=false;
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(Registration.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        return  isValid;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return null;
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            editTextDate.setText(new StringBuilder().append(day).append("-").append(month + 1)
                    .append("-").append(year)
                    .append(" "));
            editTextDate.setError(null);
        }
    };
}
