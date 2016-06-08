package com.aurorasdp.allinall.model;

/**
 * Created by AAshour on 6/7/2016.
 */
public class ServiceProvider {
    private String serviceProviderId;
    private String serviceProviderName;
    private String longitude;
    private String latitude;
    private String address;
    private String bookingsCount;
    private String appUsageCount;

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(String bookingsCount) {
        this.bookingsCount = bookingsCount;
    }

    public String getAppUsageCount() {
        return appUsageCount;
    }

    public void setAppUsageCount(String appUsageCount) {
        this.appUsageCount = appUsageCount;
    }
}
