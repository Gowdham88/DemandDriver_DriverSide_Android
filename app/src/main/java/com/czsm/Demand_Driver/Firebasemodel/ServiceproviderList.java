package com.czsm.Demand_Driver.Firebasemodel;

/**
 * Created by macbook on 10/08/16.
 */
public class ServiceproviderList {

    public String name;
    public String email;
    public String mobileno;
    public String address;
    public String password;
    public String countrycode;
    public String profilepic;
    public String walletbalance;

    public String scheme_type;
    public int bookings_count;
    public int application_usage_count;
    public String latitude;
    public String longitude;
    public String status;


    public ServiceproviderList(){


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getWalletbalance() {
        return walletbalance;
    }

    public void setWalletbalance(String walletbalance) {
        this.walletbalance = walletbalance;
    }

    public String getScheme_type() {
        return scheme_type;
    }

    public void setScheme_type(String scheme_type) {
        this.scheme_type = scheme_type;
    }

    public int getBookings_count() {
        return bookings_count;
    }

    public void setBookings_count(int bookings_count) {
        this.bookings_count = bookings_count;
    }

    public int getApplication_usage_count() {

        return application_usage_count;

    }

    public void setApplication_usage_count(int application_usage_count) {

        this.application_usage_count = application_usage_count;
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


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
