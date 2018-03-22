package com.czsm.DD_driver.model;

/**
 * Created by czsm4 on 19/03/18.
 */

public class Users {
    private String phonenumber;
    private String uid;
    private String lat;
    private String longi;



    public Users() {

    }
    public Users(String phonenumber,String uid,String lat,String longi) {
        this.phonenumber = phonenumber;
        this.uid=uid;
        this.lat=lat;
        this.longi=longi;



//        this.profileImageURL = profileImageURL;
//        this.username        = username;

    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



//    private String profileImageURL;
//    private String username;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }


}
