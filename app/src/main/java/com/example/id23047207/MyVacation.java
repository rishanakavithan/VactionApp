package com.example.id23047207;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MyVacation extends AppCompatActivity {
    boolean newItem;
    long vacation_id =-1;
    private EditText locationName_Edit, longitudeEdit, latitudeEdit, date_edit , ratingEdit;
    private Spinner countrySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vacation);

//TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_idTwo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }



//THE FORM


        locationName_Edit = findViewById(R.id.locationName_Edit);
        countrySpinner=findViewById(R.id.country_spinner);
        date_edit = findViewById(R.id.date_edit);
        latitudeEdit = findViewById(R.id.latitudeEdit);
        longitudeEdit = findViewById(R.id.longitudeEdit);
        ratingEdit = findViewById(R.id.rating_Bar);



        Intent intent = getIntent();
        vacation_id = intent.getLongExtra("vacation_id",-1);
        newItem = vacation_id == -1;


        if (!newItem){

//SAVE TO DATABASE
            VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for(Item item : MainActivity.vacationList) {
                if (item.getId() == vacation_id) {
                    locationName_Edit.setText(item.getNameOfLocation());
                    date_edit.setText(item.getDate());
                    latitudeEdit.setText(item.getLatitudeGPS());
                    longitudeEdit.setText(item.getLongitudeGPS());
                    ratingEdit.setText(item.getRating());

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    countrySpinner.setAdapter(adapter);
                    countrySpinner.setSelection(adapter.getPosition(item.getCountry()));
                    break;
                }
            }
        }
    }
//MENU BAR CODE
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //SAVE TO DATABASE
        VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

    //SAVE ITEM MENU BAR CODE

        if (item.getItemId() == R.id.save_item) {
        //VALIDATE THE INPUT
            //LOCATION NAME

            String name = locationName_Edit.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Location name cannot be empty.", Toast.LENGTH_SHORT).show();
                return false;
            }

            //COUNTRY

            String country = countrySpinner.getSelectedItem().toString();
            if (country.equals("Select a Country")) {
                Toast.makeText(this, "Please select a country.", Toast.LENGTH_SHORT).show();
                return false;
            }

            //DATE
            
            String date = date_edit.getText().toString().trim();
            if (date.isEmpty()) {
                Toast.makeText(this, "Date cannot be empty.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(date.length()>2){
                date=date.substring(0,2)+"/"+date.substring(2);
            }
            if(date.length()>5){
                date=date.substring(0,5)+"/"+date.substring(5);
            }

            //LATITUDE GPS

            String latitudeGPS = latitudeEdit.getText().toString().trim();
            if (latitudeGPS.isEmpty()) {
                Toast.makeText(this, "Latitude cannot be empty.", Toast.LENGTH_SHORT).show();
                return false;
            }

            //LONGITUDE GPS

            String longitudeGPS = longitudeEdit.getText().toString().trim();
            if (longitudeGPS.isEmpty()) {
                Toast.makeText(this, "Longitude cannot be empty.", Toast.LENGTH_SHORT).show();
                return false;
            }

            //RATING

            String rating = ratingEdit.getText().toString().trim();
            if (rating.isEmpty()) {
                Toast.makeText(this, "Rating cannot be empty.", Toast.LENGTH_SHORT).show();
                return false;
            }
            try{
            int rateValue = Integer.parseInt(rating);
            if(rateValue<1 || rateValue>10){
                Toast.makeText(this, "Rating must be between 1 and 10", Toast.LENGTH_SHORT).show();
                return false;
            }}catch (NumberFormatException e){
                Toast.makeText(this, "Rating must be a number", Toast.LENGTH_SHORT).show();
                return false;
            }



            if (newItem) {
                VacationDatabaseHelper.insertVacation(db, name, country, latitudeGPS, longitudeGPS, date, rating);
                Toast.makeText(this, "Vacation added!", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateVacation(db, vacation_id,name, country, latitudeGPS, longitudeGPS, date, rating);
                Toast.makeText(this, "Vacation Updated", Toast.LENGTH_SHORT).show();
            }
            finish();
            return true;
        }

    //DELETE ITEM MENU BAR CODE

        else if (item.getItemId() == R.id.delete_item) {
            if (!this.newItem) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete?");

                //This is the Positive button AKA OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(MyVacation.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        dbHelper.deleteVacation(db, vacation_id);

                        Toast.makeText(getApplicationContext(), "Vacation Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                //This is for the Negative button AKA CANCEL button
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();

            } else {
                Toast.makeText(getApplicationContext(), "Can't Delete Unsaved Vacations...", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

    //HELP MENU BAR CODE

        else if (item.getItemId() == R.id.help_item){
        Dialog help_Dialog = new Dialog(this);
        help_Dialog.setContentView(R.layout.help);
        help_Dialog.show();
        return true;
    }
                return onOptionsItemSelected(item);
        }//end of toolbar code


    }


