package com.czsm.Demand_Driver.model;

/**
 * Created by AAshour on 5/19/2016.
 */
public class Service {
    private String serviceId;
    private String serviceName;
    private String serviceLabel;
    private int imageResource;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }

    @Override
    public String toString() {
        return serviceName;
    }
}
