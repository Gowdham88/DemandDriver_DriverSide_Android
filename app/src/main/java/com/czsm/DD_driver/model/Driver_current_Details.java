package com.czsm.DD_driver.model;

/**
 * Created by czsm4 on 13/04/18.
 */

public class Driver_current_Details {

    private String Start_Lat;
    private String Start_Long;
    private String User_Address;
    private String User_Phone_number;
    private String Date;
    private String Start_time;
    private String User_name;
    private String End_time;

    private String Cost;
    private String Driver_review;
    private String User_review;
    private String User_Booking_Time;

    private String User_ID;
    private String Car_type;
    private String User_Booking_ID;
    private String User_Book_Date_Time;



    public Driver_current_Details(String Start_Lat, String Start_Long, String User_Address, String User_Phone_number, String Date,
                                  String Start_time, String User_name, String End_time, String Cost, String Driver_review, String User_review,
                                  String User_Booking_Time, String User_ID, String Car_type, String User_Booking_ID,String User_Book_Date_Time) {

        this.Start_Lat=Start_Lat;
        this.Start_Long=Start_Long;
        this.User_Phone_number=User_Phone_number;
        this.User_Address=User_Address;
        this.Date=Date;
        this.User_name=User_name;
        this.Start_time=Start_time;
        this.End_time=End_time;
        this.Cost=Cost;
        this.Driver_review=Driver_review;
        this.User_review=User_review;
        this.User_Booking_Time=User_Booking_Time;
        this.User_ID=User_ID;
        this.Car_type=Car_type;
        this.User_Booking_ID=User_Booking_ID;
        this.User_Book_Date_Time=User_Book_Date_Time;
    }

    public Driver_current_Details() {


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

    public String getUser_Address() {
        return User_Address;
    }

    public void setUser_Address(String user_Address) {
        User_Address = user_Address;
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
    public String getUser_Booking_Time() {
        return User_Booking_Time;
    }

    public void setUser_Booking_Time(String user_Booking_Time) {
        User_Booking_Time = user_Booking_Time;
    }

    public String getCar_type() {
        return Car_type;
    }

    public void setCar_type(String car_type) {
        Car_type = car_type;
    }

    public String getUser_Booking_ID() {
        return User_Booking_ID;
    }

    public void setUser_Booking_ID(String user_Booking_ID) {
        User_Booking_ID = user_Booking_ID;
    }

    public String getUser_Book_Date_Time() {
        return User_Book_Date_Time;
    }

    public void setUser_Book_Date_Time(String user_Book_Date_Time) {
        User_Book_Date_Time = user_Book_Date_Time;
    }


}
