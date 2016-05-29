package com.aurorasdp.allinall.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.model.ProviderBooking;
import com.aurorasdp.allinall.model.Service;
import com.aurorasdp.allinall.model.UserBooking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.PortUnreachableException;
import java.util.ArrayList;

/**
 * Created by AAshour on 5/15/2016.
 */
public class RESTClient {
    private Context context;
    ServiceResponseInterface serviceResponseInterface;
    private ProgressDialog loading;

    // static fields:
    public static String ID; // the is of the registered object (User or Provider)
    public static String OTP;
    public static ArrayList<Service> SERVICES;
    public static ArrayList<ProviderBooking> PROVIDER_BOOKINGS;
    public static ArrayList<UserBooking> USER_BOOKINGS;
    public static String BALANCE;
    public static String SCHEME;
    public static String EMAIL;
    // end static fields

    public RESTClient(Context context) {
        this.context = context;
    }

    public interface ServiceResponseInterface {
        public void sendServiceResult(String serviceResult);

        public void requestFailed();
    }

    public void setServiceResponseInterface(ServiceResponseInterface serviceResponseInterface) {
        this.serviceResponseInterface = serviceResponseInterface;
    }


    public void callRESTService(final int method, final String serviceURL, ArrayList<String> paramsNames, ArrayList<String> paramsValues, String loadingDialogMessage) {
        if (Util.isNetworkAvailable(context)) {
            if (loadingDialogMessage != null)
                loading = ProgressDialog.show(context, loadingDialogMessage, "Please wait...", false, false);
            JSONObject jsonParams = new JSONObject();
            try {
                for (int i = 0; i < paramsNames.size(); i++) {
                    jsonParams.put(paramsNames.get(i), paramsValues.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e(context.getString(R.string.tag), "RESTClient: put input parameters " + serviceURL + " " + paramsValues.toString());
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (method, serviceURL, jsonParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Log.e(context.getString(R.string.tag), "RESTClient: message " + message);
                                // /OTP/sendsms
                                if (message.equalsIgnoreCase(context.getString(R.string.otp_sendsms_success))) {
                                    if (!response.isNull("OTP")) {
                                        Log.e(context.getString(R.string.tag), "OTP " + response.getString("OTP"));
                                        OTP = response.getString("OTP");
                                    }
                                }// /user/signup
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_signup_success))) {
                                    if (!response.isNull("user_id")) {
                                        ID = response.getString("user_id");
                                    }
                                }// /user/login
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_login_success))) {
                                    if (!response.isNull("user_id")) {
                                        ID = response.getString("user_id");
                                    }
                                }// /service_provider/signup
                                else if (message.equalsIgnoreCase(context.getString(R.string.provider_signup_success))) {
                                    if (!response.isNull("service_provider_id")) {
                                        ID = response.getString("service_provider_id");
                                    }
                                }// /service_provider/login
                                else if (message.equalsIgnoreCase(context.getString(R.string.provider_login_success))) {
                                    if (!response.isNull("service_provider_id")) {
                                        ID = response.getString("service_provider_id");
                                    }
                                }// /service/getServices
                                else if (message.equalsIgnoreCase(context.getString(R.string.service_get_services_success))) {
                                    if (!response.isNull("services")) {
                                        final JSONArray serviceJSON = response.getJSONArray("services");
                                        SERVICES = jsonArrayToServicesArray(serviceJSON);
                                    }
                                }// /appointment/getServiceProviderBookings
                                else if (message.equalsIgnoreCase(context.getString(R.string.provider_list_bookings_success))) {
                                    if (!response.isNull("bookings")) {
                                        final JSONArray bookingJSON = response.getJSONArray("bookings");
                                        PROVIDER_BOOKINGS = jsonArrayToBookingsArray(bookingJSON);
                                    }
                                }// /service_provider/getWalletData
                                else if (message.equalsIgnoreCase(context.getString(R.string.provider_get_wallet_success))) {
                                    if (!response.isNull("wallet_data")) {
                                        JSONObject walletObject = response.getJSONObject("wallet_data");
                                        SCHEME = walletObject.getString("scheme_type");
                                        BALANCE = walletObject.getString("balance");
                                    }
                                }// /appointment/getHistoryForUser
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_list_history_success))) {
                                    if (!response.isNull("history")) {
                                        final JSONArray historyJSON = response.getJSONArray("history");
                                        USER_BOOKINGS = jsonArrayToHistoryArray(historyJSON);
                                    }
                                } else if (message.equalsIgnoreCase(context.getString(R.string.user_list_history_fail))) {
                                    USER_BOOKINGS = new ArrayList<UserBooking>();
                                }// /user/getUserEmail
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_get_mail_success))) {
                                    if (!response.isNull("email")) {
                                        EMAIL = response.getString("email");
                                    }
                                }
                                sendServiceResult(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (loading != null)
                                    loading.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            requestFailed();
                            error.printStackTrace();

                        }
                    });


            jsonRequest.setShouldCache(false);
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(context).add(jsonRequest);
            /*
            if (serviceURL.equalsIgnoreCase("/provider/login"))
                sendServiceResult(context.getString(R.string.provider_login_success));
            else if (serviceURL.equalsIgnoreCase("/user/login"))
                sendServiceResult(context.getString(R.string.user_login_success));
            else if (serviceURL.equalsIgnoreCase("/OTP/sendsms")) {
                RESTClient.OTP = "1234";
                sendServiceResult(context.getString(R.string.otp_sendsms_success));
            } else if (serviceURL.equalsIgnoreCase("/user/signup"))
                sendServiceResult(context.getString(R.string.user_signup_success));
            else if (serviceURL.equalsIgnoreCase("/provider/signup"))
                sendServiceResult(context.getString(R.string.provider_signup_success));
            else if (serviceURL.equalsIgnoreCase("/provider/listBookings")) {
                RESTClient.PROVIDER_BOOKINGS = new ArrayList<ProviderBooking>();
                sendServiceResult(context.getString(R.string.provider_list_bookings_success));
            }
            else if (serviceURL.equalsIgnoreCase("/user/listHistory")) {
                RESTClient.USER_BOOKINGS = new ArrayList<UserBooking>();
                sendServiceResult(context.getString(R.string.user_list_bookings_success));
            }*/
        }
    }

    private ArrayList<UserBooking> jsonArrayToHistoryArray(JSONArray historyJSON) {
        ArrayList<UserBooking> bookings = new ArrayList<UserBooking>();
        for (int i = 0; i < historyJSON.length(); i++) {
            UserBooking booking = new UserBooking();
            try {
                booking.setBookingId(((JSONObject) historyJSON.get(i)).getString("appointment_id"));
                booking.setProviderName(((JSONObject) historyJSON.get(i)).getString("service_provider_name"));
                booking.setDateTime(((JSONObject) historyJSON.get(i)).getString("appointment_date"), ((JSONObject) historyJSON.get(i)).getString("appointment_time"));
                booking.setService(((JSONObject) historyJSON.get(i)).getString("service_name"));
                booking.setAddress(((JSONObject) historyJSON.get(i)).getString("address"));
                booking.setDecodedPic(Base64.decode(((JSONObject) historyJSON.get(i)).getString("service_provider_pic"), Base64.DEFAULT));
                bookings.add(booking);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    private ArrayList<ProviderBooking> jsonArrayToBookingsArray(JSONArray bookingJSON) {
        ArrayList<ProviderBooking> bookings = new ArrayList<ProviderBooking>();
        for (int i = 0; i < bookingJSON.length(); i++) {
            ProviderBooking booking = new ProviderBooking();
            try {
                booking.setBookingId(((JSONObject) bookingJSON.get(i)).getString("booking_id"));
                booking.setUserId(((JSONObject) bookingJSON.get(i)).getString("user_id"));
                booking.setUserName(((JSONObject) bookingJSON.get(i)).getString("user_name"));
                booking.setDateTime(((JSONObject) bookingJSON.get(i)).getString("appointment_date"), ((JSONObject) bookingJSON.get(i)).getString("appointment_time"));
                booking.setService(((JSONObject) bookingJSON.get(i)).getString("service_name"));
                booking.setAge(((JSONObject) bookingJSON.get(i)).getString("user_age"));
                booking.setDecodedPic(Base64.decode(((JSONObject) bookingJSON.get(i)).getString("user_pic"), Base64.DEFAULT));
                bookings.add(booking);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }

    private ArrayList<Service> jsonArrayToServicesArray(JSONArray serviceJSON) {
        ArrayList<Service> services = new ArrayList<Service>();
        for (int i = 0; i < serviceJSON.length(); i++) {
            Service service = new Service();
            try {
                service.setServiceId(((JSONObject) serviceJSON.get(i)).getString("service_id"));
                service.setServiceName(((JSONObject) serviceJSON.get(i)).getString("service_name"));
                services.add(service);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return services;
    }

    public void sendServiceResult(String serviceResult) {
        if (loading != null)
            loading.dismiss();
        serviceResponseInterface.sendServiceResult(serviceResult);
    }


    public void requestFailed() {
        if (loading != null)
            loading.dismiss();
        serviceResponseInterface.requestFailed();
    }
}
