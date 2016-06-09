package com.aurorasdp.allinall.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AAshour on 5/15/2016.
 */
public class AllinAllController {

    Context context;
    RESTClient.ServiceResponseInterface restClientInterface;
    final String DEV_URL = "http://54.200.102.214/All_In_All_Backend/index.php";
    final String DEPLOY_URL = "";
    String mainServiceURL;

    public AllinAllController(Context context, RESTClient.ServiceResponseInterface restClientInterface) {
        this.context = context;
        this.restClientInterface = restClientInterface;
        mainServiceURL = DEV_URL;
    }

    public void sendSms(String mobile) {
        Log.e(context.getString(R.string.tag), "Controller: Send SMS " + mobile);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        RESTClient.OTP = "1234";
        restClient.sendServiceResult(context.getString(R.string.otp_sendsms_success));
//        restClient.callRESTService(Request.Method.POST, DEV_URL + "/OTP/sendsms",
//                new ArrayList<String>(Arrays.asList("phone")),
//                new ArrayList<String>(Arrays.asList(mobile)), "Sending SMS .....");
    }

    public void userSignUp(String name, String countryCode, String mobile, String email, String address, String password, String profileImage, String profileImageExt, String regId) {
        Log.e(context.getString(R.string.tag), "Controller: User Sign Up " + name + " " + mobile + " " + email + " " + address + " " + regId + " " + profileImage + " " + profileImageExt);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/user/signup",
                new ArrayList<String>(Arrays.asList("name", "country_code", "phone", "email", "address", "password", "profile_pic", "image_extension", "reg_id")),
                new ArrayList<String>(Arrays.asList(name, countryCode, mobile, email, address, Util.md5(password), profileImage, profileImageExt, regId)), "Signing up .....");
    }

    public void userLogin(String phone, String password, String regId) {
        Log.e(context.getString(R.string.tag), "Controller: User Login " + phone + " " + password + " " + regId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/user/login",
                new ArrayList<String>(Arrays.asList("phone", "password", "reg_id")),
                new ArrayList<String>(Arrays.asList(phone, Util.md5(password), regId)), "Loging in .....");
    }


    public void providerSignUp(String name, String email, String countryCode, String mobile, String address, String serviceOffered, String password, String profileImage, String profileImageExt, String regId) {
        Log.e(context.getString(R.string.tag), "Controller: Service Provider Sign Up " + name + " " + email + " " + countryCode + " " + mobile + " " + serviceOffered + " " + regId + " " + profileImage + " " + profileImageExt + " " + password);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service_provider/signup",
                new ArrayList<String>(Arrays.asList("name", "email", "phone", "country_code", "address", "profile_pic", "image_extension", "service_id", "password", "reg_id")),
                new ArrayList<String>(Arrays.asList(name, email, mobile, countryCode, address, profileImage, profileImageExt, serviceOffered, password, regId)), "Signing up .....");
    }

    public void providerSignOut(String providerId) {
        Log.e(context.getString(R.string.tag), "Controller: Service Provider Sign Out " + providerId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service_provider/signout",
                new ArrayList<String>(Arrays.asList("service_provider_id")),
                new ArrayList<String>(Arrays.asList(providerId)), null);
    }

    public void userSignOut(String userId) {
        Log.e(context.getString(R.string.tag), "Controller: User Sign Out " + userId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service_provider/signout",
                new ArrayList<String>(Arrays.asList("user_id")),
                new ArrayList<String>(Arrays.asList(userId)), null);
    }

    public void providerLogin(String phone, String password, String regId) {
        Log.e(context.getString(R.string.tag), "Controller: Service Provider Login " + phone + " " + password + " " + regId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service_provider/login",
                new ArrayList<String>(Arrays.asList("phone", "password", "reg_id")),
                new ArrayList<String>(Arrays.asList(phone, password, regId)), "Loging in .....");
    }

    public void listProviderBookings(String providerId, String newLogin, String message) {
        Log.e(context.getString(R.string.tag), "Controller: List Provider Bookings " + providerId + " " + newLogin);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/appointment/getServiceProviderBookings",
                new ArrayList<String>(Arrays.asList("service_provider_id", "new_login")),
                new ArrayList<String>(Arrays.asList(providerId, newLogin)), message);
    }

    public void getWalletData(String providerId) {
        Log.e(context.getString(R.string.tag), "Controller: Get Provider Wallet data " + providerId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service_provider/getWalletData",
                new ArrayList<String>(Arrays.asList("service_provider_id")),
                new ArrayList<String>(Arrays.asList(providerId)), "Loading Wallet Data ..... ");
    }

    public void listUserHistory(String userId, String message) {
        Log.e(context.getString(R.string.tag), "Controller: List User History " + userId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/appointment/getHistoryForUser",
                new ArrayList<String>(Arrays.asList("user_id")),
                new ArrayList<String>(Arrays.asList(userId)), message);
    }

    public void resetPassword(String userPhone, String encryptedPass, String decryptedPass) {
        Log.e(context.getString(R.string.tag), "Controller: Send Mail By new Password " + userPhone + " - " + encryptedPass + " - " + decryptedPass);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, mainServiceURL + "/user/sendMailByNewPassword",
                new ArrayList<String>(Arrays.asList("phone", "encrypted_password", "unencrypted_password")),
                new ArrayList<String>(Arrays.asList(userPhone, encryptedPass, decryptedPass)), "Sending Email .....");
    }

    public void getUserEmail(String userPhone) {
        Log.e(context.getString(R.string.tag), "Controller: Get User Email " + userPhone);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, mainServiceURL + "/user/getUserEmail",
                new ArrayList<String>(Arrays.asList("phone")),
                new ArrayList<String>(Arrays.asList(userPhone)), "Wait .....");
    }

    public void getServices() {
        Log.e(context.getString(R.string.tag), "Controller: Get services ");
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service/getServices",
                new ArrayList<String>(),
                new ArrayList<String>(), "Loading Services ....");
    }

    public void getServicesOffered() {
        Log.e(context.getString(R.string.tag), "Controller: Get services Offered");
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service/getServiceLabels",
                new ArrayList<String>(),
                new ArrayList<String>(), null);
    }


    public void getOngoingAppointments(String userId, String message) {
        Log.e(context.getString(R.string.tag), "Controller: List User Ongoing appointments " + userId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/appointment/getUserBookings",
                new ArrayList<String>(Arrays.asList("user_id")),
                new ArrayList<String>(Arrays.asList(userId)), message);
    }

    public void completeBooking(String userId, String message) {
        Log.e(context.getString(R.string.tag), "Controller: complete booking " + userId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/appointment/getUserBookings",
                new ArrayList<String>(Arrays.asList("user_id")),
                new ArrayList<String>(Arrays.asList(userId)), message);
    }

    public void getServiceProviderList(String serviceId) {
        Log.e(context.getString(R.string.tag), "Controller: get service provider list " + serviceId);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/service/getAvailableServiceProvidersList",
                new ArrayList<String>(Arrays.asList("service_id")),
                new ArrayList<String>(Arrays.asList(serviceId)), "Loading Providers ...");
    }

    public void bookAppointment(String userId, String date, String time, String serviceId,
                                String longitude, String latitude, String carType, String hourly, String bookNow) {
        Log.e(context.getString(R.string.tag), "Controller: Book Appointment " + userId + " " + date + " " + time + " " +
                serviceId + " " + longitude + " " + latitude + " " + carType + " " + hourly + " " + bookNow);
        RESTClient restClient = new RESTClient(context);
        restClient.setServiceResponseInterface(restClientInterface);
        restClient.callRESTService(Request.Method.POST, DEV_URL + "/appointment/bookAppointment",
                new ArrayList<String>(Arrays.asList("user_id", "date", "time", "service_id", "user_longitude", "user_latitude", "car_type", "hourly_or_outstation", "book_now")),
                new ArrayList<String>(Arrays.asList(userId, date, time, serviceId, longitude, latitude, carType, hourly, bookNow)), "Booking .... ");
    }

}
