package com.czsm.DD_driver.Firebasemodel;

/**
 * Created by macbook on 11/08/16.
 */
public class ServiceList {

    public String providerid;
    public String name;
    public String driverimage;
    public String address;
    public String latitude;
    public String longitude;

    public String getDriverimage() {
        return driverimage;
    }

    public void setDriverimage(String driverimage) {
        this.driverimage = driverimage;
    }

    public ServiceList() {


    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public String getLatitude() {

        return latitude;
    }

    public void setLatitude(String latitude) {

        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {

        this.longitude = longitude;
    }

    public String getProviderid() {

        return providerid;
    }

    public void setProviderid(String providerid) {

        this.providerid = providerid;
    }
}
