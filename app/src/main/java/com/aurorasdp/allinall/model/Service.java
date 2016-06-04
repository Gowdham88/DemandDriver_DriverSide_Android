package com.aurorasdp.allinall.model;

/**
 * Created by AAshour on 5/19/2016.
 */
public class Service {
    private String serviceId;
    private String serviceName;
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

    @Override
    public String toString() {
        return serviceName;
    }
}
