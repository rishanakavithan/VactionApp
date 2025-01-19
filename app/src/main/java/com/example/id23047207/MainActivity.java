package com.example.id23047207;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.getSystemService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;


import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.widget.Toolbar;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VacationDatabaseHelper dbHelper;

    private Spinner sortSpinner;

    private ShareActionProvider shareActionProvider;

//CURRENT LOCATION

    private Button CurrentAddressButton;
    private TextView CurrentAddressView;
    private LocationRequest locationRequest;


//LISTVIEW
    public static LinkedList<Item> vacationList = new LinkedList<>();
    ListView listView;
    ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_id);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (toolbar != null){
            Log.d("MainActivity","Tool bar is working");
        }else {
            Log.d("MainActivity", "Tool bar is not working");
        }
        setSupportActionBar(toolbar);
        //end of toolbar code

//LISTVIEW
        listView = findViewById(R.id.listView_id);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,vacationList);
        listView.setAdapter(adapter);

//SORTING SPINNER

        //initialise the database
        dbHelper = new VacationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();



        //initialise the sort spinner
        sortSpinner = (Spinner) findViewById(R.id.sortSpinner_id);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.sortOptions,android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = parent.getItemAtPosition(position).toString();
                sortVacations(selectedSort);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



//CURRENT LOCATION

        CurrentAddressButton = (Button) findViewById(R.id.CurrentAddress_button);
        CurrentAddressView = (TextView) findViewById(R.id.currentAddress_id);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        CurrentAddressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getCurrentLocation();
         }
        });




        //Create and assign a listener to the ListView
        AdapterView.OnItemClickListener itemClickListener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(MainActivity.this, MyVacation.class);
                long vacation_id = vacationList.get(position).getId();
                intent.putExtra("vacation_id",vacation_id);
                startActivity(intent);
            }
        };

        listView.setOnItemClickListener(itemClickListener);

//LONG PRESS DELETE LISTVIEW

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?>parent,View view,int position,long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are sure you want to delete? ");

                //This is the Positive button AKA OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//DELETE FROM DATABASE

                        VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(MainActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        long vacation_id = vacationList.get(position).getId();
                        dbHelper.deleteVacation(db,vacation_id);
                        vacationList.remove(position);
                        adapter.notifyDataSetChanged();

                        updateEmptyView();
                        Toast.makeText(getApplicationContext(), "Vacation Deleted", Toast.LENGTH_SHORT).show();
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
                return true;
            }
        });
    }



// BACKGROUND LISTVIEW VISIBLE & INVISIBLE
    public void onStart(){
        super.onStart();

        VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        vacationList.clear();
        vacationList.addAll(dbHelper.getAllVacations(db,null));

        updateEmptyView();
        adapter.notifyDataSetChanged();
    }

 // BACKGROUND LISTVIEW VISIBLE & INVISIBLE

    public void updateEmptyView(){

        TextView emptyText=(TextView) findViewById(R.id.noItemView_id);

        if(vacationList.isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else{
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }


// MENU BAR CODE

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){

    //ADD VACATION MENU BAR CODE

            if(item.getItemId() == R.id.addVacation) {
                startActivity(new Intent(MainActivity.this, MyVacation.class));
                return true;
            }


    // DELETE VACATION MENU BAR CODE

            else if (item.getItemId() == R.id.deleteAll) {

                if(vacationList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Vacation List is already empty", Toast.LENGTH_SHORT).show();
                    return true;
                }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("This will clear the Vacation list!");

            //This is the Positive button AKA OK button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

 //DELETE FROM DATABASE

                    VacationDatabaseHelper dbHelper = new VacationDatabaseHelper(MainActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();


                    db.delete("VACATION", null, null);
                    vacationList.clear();
                    adapter.notifyDataSetChanged();

                    updateEmptyView();
                    Toast.makeText(getApplicationContext(), "Vacation List Deleted", Toast.LENGTH_SHORT).show();
                }

            });
            //This is for the Negative button AKA CANCEL button
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;

        }


    // HELP MAIN MENU BAR CODE


            else if(item.getItemId() ==R.id.helpMain){
            Dialog help_Dialog = new Dialog(this);
            help_Dialog.setContentView(R.layout.help_main);
            help_Dialog.show();
            return true;

    }
            return super.onOptionsItemSelected(item);


    }// End of Menu Bar Code - Add , Delete , Help


//SORTING SPINNER

    private void sortVacations(String sortOption) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Item> sortedVacations;

        // Get sorted list based on selected option
        switch (sortOption) {
            case "Name A-Z":
                sortedVacations = dbHelper.getItemSortedByName(db);
                break;
            case "Country A-Z":
                sortedVacations = dbHelper.getItemSortedByCountry(db);
                break;
            case "Rating":
                sortedVacations = dbHelper.getItemSortedByRating(db);
                break;
            default:
                sortedVacations = dbHelper.getItemsSortedByDefault(db);
                break;
        }

        updateListView(sortedVacations);
        Log.d("MainActivity", "Selected sort option: " + sortOption);
    }

    private void updateListView(List<Item> sortedVacations) {

        vacationList.clear();
        vacationList.addAll(sortedVacations);
        adapter.notifyDataSetChanged();
    }


//CURRENT LOCATION
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == 1){
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (isGPSEnabled()) {

                getCurrentLocation();

            }else {

                turnOnGPS();
            }
        }
    }


}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        CurrentAddressView.setText("Latitude: "+ latitude + "   |   " + "Longitude: "+ longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }                } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;                               resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }


}