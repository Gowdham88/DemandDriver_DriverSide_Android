package com.czsm.DD_driver.model;

/**
 * Created by czsm4 on 06/04/18.
 */

public class Data {

    private String Currentlat;
    private String Currentlong;
    private String address;
    private String phoneNumber;
    private String date;
    private String time;
    private String UsersUID;
    private String Name;


    public Data() {


    }

    public Data(String Currentlat, String Currentlong, String phoneNumber,String address,String date,String time,String Name,String UsersUID) {

        this.Currentlat=Currentlat;
        this.Currentlong=Currentlong;
        this.phoneNumber=phoneNumber;
        this.address=address;
        this.date=date;
        this.time=time;
        this.Name=Name;
        this.UsersUID=UsersUID;
    }


    public String getCurrentlat() {
        return Currentlat;
    }

    public void setCurrentlat(String currentlat) {
        Currentlat = currentlat;
    }

    public String getCurrentlong() {
        return Currentlong;
    }

    public void setCurrentlong(String currentlong) {
        Currentlong = currentlong;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getUsersUID() {
        return UsersUID;
    }

    public void setUsersUID(String usersUID) {
        UsersUID = usersUID;
    }


}

