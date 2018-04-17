package com.czsm.DD_driver.model;

/**
 * Created by czsm4 on 13/04/18.
 */

public class Driver_complete_details {

    private String Start_Lat;
    private String Start_Long;
    private String Address;
    private String User_Phone_number;
    private String Date;
    private String Start_time;
    private String User_name;
    private String End_time;
    private String Cost;

    private String Driver_review;
    private String User_review;
    private String Time;
    private String User_ID;
    private String User_Booking_ID;


    public Driver_complete_details(String Start_Lat, String Start_Long, String Address,String User_Phone_number,String Date,String Start_time,String User_name,String End_time,String Cost,String Driver_review,String User_review,String Time,String User_ID,String User_Booking_ID) {

        this.Start_Lat=Start_Lat;
        this.Start_Long=Start_Long;
        this.User_Phone_number=User_Phone_number;
        this.Address=Address;
        this.Date=Date;
        this.User_name=User_name;
        this.Start_time=Start_time;
        this.End_time=End_time;
        this.Cost=Cost;
        this.Driver_review=Driver_review;
        this.User_review=User_review;
        this.Time=Time;
        this.User_ID=User_ID;
        this.User_Booking_ID=User_Booking_ID;
    }

    public Driver_complete_details() {


    }

    public String getStart_Lat() {
        return Start_Lat;
    }

    public void setStart_Lat(String start_Lat) {
        Start_Lat = start_Lat;
    }

    public String getStart_Long() {
        return Start_Long;
    }

    public void setStart_Long(String start_Long) {
        Start_Long = start_Long;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStart_time() {
        return Start_time;
    }

    public void setStart_time(String start_time) {
        Start_time = start_time;
    }

    public String getEnd_time() {
        return End_time;
    }

    public void setEnd_time(String end_time) {
        End_time = end_time;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getDriver_review() {
        return Driver_review;
    }

    public void setDriver_review(String driver_review) {
        Driver_review = driver_review;
    }

    public String getUser_review() {
        return User_review;
    }

    public void setUser_review(String user_review) {
        User_review = user_review;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUser_Phone_number() {
        return User_Phone_number;
    }

    public void setUser_Phone_number(String user_Phone_number) {
        User_Phone_number = user_Phone_number;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }


    public String getUser_Booking_ID() {
        return User_Booking_ID;
    }

    public void setUser_Booking_ID(String user_Booking_ID) {
        User_Booking_ID = user_Booking_ID;
    }



}
