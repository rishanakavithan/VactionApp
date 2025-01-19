package com.example.id23047207;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class VacationDatabaseHelper extends SQLiteOpenHelper {

// This is the SQL database
    private static final String DB_NAME = "vacationData";
    private static final int DB_VERSION = 1;

    public VacationDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE VACATION("+"_id INTEGER PRIMARY KEY AUTOINCREMENT,"+" nameOfLocation TEXT,"+" country TEXT,"+ "latitudeGPS TEXT, "+"longitudeGPS TEXT,"+"date TEXT, "+"rating TEXT);");

    }

   // Not particularly sure if we need this since i am not sure if we will update the versions
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS VACATION");
        onCreate(db);

    }

    public static void insertVacation(SQLiteDatabase db, String nameOfLocation , String country, String latitudeGPS, String longitudeGPS, String date , String rating ){

        ContentValues inputValues = new ContentValues();
        inputValues.put("nameOfLocation",nameOfLocation);
        inputValues.put("country",country);
        inputValues.put("latitudeGPS",latitudeGPS);
        inputValues.put("longitudeGPS",longitudeGPS);
        inputValues.put("date",date);
        inputValues.put("rating",rating);

        db.insert("VACATION",null,inputValues);

    }

// THIS IS SUPPOSED TO STORE THE ITEM CLASS AND ALLOW THE USER TO SORT THE LIST OF VACATIONS BY DATE,RATING
public List<Item> getAllVacations(SQLiteDatabase db, String orderBy) {
    List<Item> items = new ArrayList<>();
    Cursor cursor = db.query("VACATION", null, null, null, null, null, orderBy);

    while (cursor.moveToNext()) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("nameOfLocation"));
        String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        String latitude = cursor.getString(cursor.getColumnIndexOrThrow("latitudeGPS"));
        String longitude = cursor.getString(cursor.getColumnIndexOrThrow("longitudeGPS"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"));

        items.add(new Item(id, name, country, latitude, longitude, date, rating));
        Log.d("VacationDatabaseHelper", "Querying vacations sorted by: " + orderBy);
    }
    cursor.close();
    return items;
}

    public List<Item> getItemSortedByName(SQLiteDatabase db) {
        return getAllVacations(db, "nameOfLocation COLLATE NOCASE ASC");
    }

    public List<Item> getItemSortedByCountry(SQLiteDatabase db) {
        return getAllVacations(db, "country COLLATE NOCASE ASC");
    }

    public List<Item> getItemSortedByRating(SQLiteDatabase db) {
        return getAllVacations(db, "rating DESC");
    }
    public List<Item> getItemsSortedByDefault(SQLiteDatabase db) {
        return getAllVacations(db, "_id DESC");
    }

    public void updateVacation(SQLiteDatabase db, long vacation_id,String nameOfLocation , String country, String latitudeGPS, String longitudeGPS, String date , String rating){


        ContentValues inputValues = new ContentValues();
        inputValues.put("nameOfLocation",nameOfLocation);
        inputValues.put("country",country);
        inputValues.put("latitudeGPS",latitudeGPS);
        inputValues.put("longitudeGPS",longitudeGPS);
        inputValues.put("date",date);
        inputValues.put("rating",rating);

        db.update("VACATION",inputValues,"_id=?",new String[]{String.valueOf(vacation_id)});


    }

    public void deleteVacation(SQLiteDatabase db, long id){
        db.delete("VACATION","_id=?",new String[]{String.valueOf(id)});
    }
}
