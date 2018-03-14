package com.czsm.Demand_Driver.model;

import com.czsm.Demand_Driver.helper.Util;

/**
 * Created by AAshour on 5/16/2016.
 */
public class ProviderBooking {
    private String bookingId;
    private String userId;
    private String userName;
    private String service;
    private String dateTime;
    private String userPhone;
    private String userAddress;
    private byte[] decodedPic;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String date, String time) {
        this.dateTime = Util.getDateTime(date, time);
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String code, String phone) {
        if (code.length() > 2)
            this.userPhone = "+" + code.substring(2) + "-" + phone;
        else
            this.userPhone = code + "-" + phone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public byte[] getDecodedPic() {
        return decodedPic;
    }

    public void setDecodedPic(byte[] decodedPic) {
        this.decodedPic = decodedPic;
    }

}
