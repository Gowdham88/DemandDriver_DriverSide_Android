package com.aurorasdp.allinall.model;

import com.aurorasdp.allinall.helper.Util;

/**
 * Created by AAshour on 5/16/2016.
 */
public class UserBooking {
    private String bookingId;
    private String providerName;
    private String bookingCode;
    private String dateTime;
    private String address;
    private String service;
    private byte[] decodedPic;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String date, String time) {
        this.dateTime = Util.getDateTime(date, time);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public byte[] getDecodedPic() {
        return decodedPic;
    }

    public void setDecodedPic(byte[] decodedPic) {
        this.decodedPic = decodedPic;
    }
}
