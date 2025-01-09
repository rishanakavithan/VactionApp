package com.example.id23047207;

public class Item {
    private long id;

    private String nameOfLocation;
    private String country;
    private String latitudeGPS;
    private String longitudeGPS;
    private String date;
    private String rating;


    public Item(long id, String nameOfLocation,String country, String latitudeGPS, String  longitudeGPS, String date, String rating){

        this.id = id;
        this.nameOfLocation = nameOfLocation;
        this.country = country;
        this.latitudeGPS = latitudeGPS;
        this.longitudeGPS = longitudeGPS;
        this.date = date;
        this.rating = rating;
    }

//SETTING SETTERS && GETTERS
    public long getId (){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }

    //NAME OF LOCATION
    public String getNameOfLocation(){
        return nameOfLocation;
    }
    public void setNameOfLocation(String nameOfLocation){
        this.nameOfLocation = nameOfLocation;
    }

    //COUNTRY
    public String getCountry(){
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }

    //LATITUDEGPS
    public String getLatitudeGPS(){
        return latitudeGPS;
    }
    public void setLatitudeGPS(String latitudeGPS){
        this.latitudeGPS=latitudeGPS;
    }

    //LONGITUDEGPS
    public String getLongitudeGPS(){
        return longitudeGPS;
    }
    public void setLongitudeGPS(String longitudeGPS){
        this.longitudeGPS=longitudeGPS;
    }

    //DATE
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date=date;
    }

    //RATING
    public String getRating(){
        return rating;
    }
    public void setRating(String rating){
        this.rating = rating;
    }
    public String toString(){
        return nameOfLocation + "  |  " + country + "  |  " + latitudeGPS + " : " + longitudeGPS + "\n " + date + "  |  Rate: " + rating+"/10";
    }

}
