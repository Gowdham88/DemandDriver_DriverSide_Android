package com.czsm.driverin.Firebasemodel;

/**
 * Created by macbook on 12/08/16.
 */
public class AppointmentList {


    public  String userid;
    public  String providerid;
    public  String username;
    public  String useraddress;
    public  String usermobilenumber;
    public  String userreview;
    public  String userimage;
    public  String driverimage;
    public  String drivername;
    public  String driveraddress;
    public  String drivermobilenumber;
    public  String date;
    public  String time;
    public  String status;
    public  String cartype;
    public  String hourly_or_workstation;
    public  String user_latitude;
    public  String user_longitude;
    public  String book_now;
    public  String cancelid;
    public  long   timems;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress;
    }

    public String getUsermobilenumber() {
        return usermobilenumber;
    }

    public void setUsermobilenumber(String usermobilenumber) {
        this.usermobilenumber = usermobilenumber;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriveraddress() {
        return driveraddress;
    }

    public void setDriveraddress(String driveraddress) {
        this.driveraddress = driveraddress;
    }

    public String getDrivermobilenumber() {
        return drivermobilenumber;
    }

    public void setDrivermobilenumber(String drivermobilenumber) {
        this.drivermobilenumber = drivermobilenumber;
    }



    public long getTimems() {
        return timems;
    }

    public void setTimems(long timems) {
        this.timems = timems;
    }

    public AppointmentList(){


    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getHourly_or_workstation() {
        return hourly_or_workstation;
    }

    public void setHourly_or_workstation(String hourly_or_workstation) {
        this.hourly_or_workstation = hourly_or_workstation;
    }

    public String getUser_latitude() {
        return user_latitude;
    }

    public void setUser_latitude(String user_latitude) {
        this.user_latitude = user_latitude;
    }

    public String getUser_longitude() {
        return user_longitude;
    }

    public void setUser_longitude(String user_longitude) {
        this.user_longitude = user_longitude;
    }

    public String getBook_now() {
        return book_now;
    }

    public void setBook_now(String book_now) {
        this.book_now = book_now;
    }

    public String getCancelid() {
        return cancelid;
    }

    public void setCancelid(String cancelid) {
        this.cancelid = cancelid;
    }

    public String getUserreview() {
        return userreview;
    }

    public void setUserreview(String userreview) {
        this.userreview = userreview;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getDriverimage() {
        return driverimage;
    }

    public void setDriverimage(String driverimage) {
        this.driverimage = driverimage;
    }

}
