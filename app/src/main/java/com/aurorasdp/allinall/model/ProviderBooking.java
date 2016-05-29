package com.aurorasdp.allinall.model;

import com.aurorasdp.allinall.helper.Util;

/**
 * Created by AAshour on 5/16/2016.
 */
public class ProviderBooking {
    private String bookingId;
    private String userId;
    private String userName;
    private String service;
    private String dateTime;
    private byte[] decodedPic;
    private String age;

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

    public byte[] getDecodedPic() {
        return decodedPic;
    }

    public void setDecodedPic(byte[] decodedPic) {
        this.decodedPic = decodedPic;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
