package com.aurorasdp.allinall.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.helper.acra.GMailSender;
import com.aurorasdp.allinall.model.ProviderBooking;
import com.aurorasdp.allinall.model.Service;
import com.aurorasdp.allinall.model.ServiceProvider;
import com.aurorasdp.allinall.model.UserBooking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static ArrayList<Service> SERVICES_OFFERED;
    public static ArrayList<ProviderBooking> PROVIDER_BOOKINGS = new ArrayList<ProviderBooking>();
    public static ArrayList<UserBooking> USER_BOOKINGS_HISTORY = new ArrayList<UserBooking>();
    ;
    public static ArrayList<UserBooking> ONGOING_BOOKINGS = new ArrayList<UserBooking>();
    ;
    public static String BALANCE;
    public static String SCHEME;
    public static String EMAIL;
    public static ArrayList<ServiceProvider> PROVIDERS;
    public static String PENDING_MESSAGES;
    public static String PENDING_IDS;
    public static UserBooking BOOKED_APPOINTMENT;
    String parameters;
    // end static fields

    public RESTClient(Context context) {
        this.context = context;
        parameters = "";
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
                    parameters += paramsNames.get(i) + " = " + paramsValues.get(i) + "\n";
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
                                }// /service/getServiceLabels
                                else if (message.equalsIgnoreCase(context.getString(R.string.service_offered_success))) {
                                    if (!response.isNull("services")) {
                                        final JSONArray serviceJSON = response.getJSONArray("services");
                                        SERVICES_OFFERED = jsonArrayToServicesOfferedArray(serviceJSON);
                                    }
                                }// /appointment/getServiceProviderBookings
                                else if (message.equalsIgnoreCase(context.getString(R.string.provider_list_bookings_success))) {
                                    if (!response.isNull("bookings")) {
                                        final JSONArray bookingJSON = response.getJSONArray("bookings");
                                        PROVIDER_BOOKINGS.clear();
                                        PROVIDER_BOOKINGS.addAll(jsonArrayToBookingsArray(bookingJSON));
                                    }
                                } else if (message.equalsIgnoreCase(context.getString(R.string.provider_list_bookings_fail))) {
                                    PROVIDER_BOOKINGS.clear();
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
                                        USER_BOOKINGS_HISTORY.clear();
                                        USER_BOOKINGS_HISTORY.addAll(jsonArrayToHistoryArray(historyJSON));

                                    }
                                } else if (message.equalsIgnoreCase(context.getString(R.string.user_list_history_fail))) {
                                    USER_BOOKINGS_HISTORY.clear();
                                }// /user/getUserEmail
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_get_mail_success))) {
                                    if (!response.isNull("email")) {
                                        EMAIL = response.getString("email");
                                    }
                                }// /appointment/getUserBookings
                                else if (message.equalsIgnoreCase(context.getString(R.string.user_ongoing_appo_success))) {
                                    if (!response.isNull("bookings")) {
                                        final JSONArray bookingsJSON = response.getJSONArray("bookings");
                                        ONGOING_BOOKINGS.clear();
                                        ONGOING_BOOKINGS.addAll(jsonArrayToOngoingArray(bookingsJSON));
                                    }
                                } else if (message.equalsIgnoreCase(context.getString(R.string.user_ongoing_appo_fail))) {
                                    ONGOING_BOOKINGS.clear();
                                }// /service/getAvailableServiceProvidersList
                                else if (message.equalsIgnoreCase(context.getString(R.string.service_get_providers_success))) {
                                    if (!response.isNull("service_providers")) {
                                        final JSONArray providersJSON = response.getJSONArray("service_providers");
                                        PROVIDERS = jsonArrayToProvidersArray(providersJSON);
                                    }
                                }// /appointment/getPendingAppointments
                                else if (message.equalsIgnoreCase(context.getString(R.string.appointment_pending_success))) {
                                    if (!response.isNull("appointment_ids"))
                                        PENDING_IDS = response.getString("appointment_ids");

                                    if (!response.isNull("notification_messages"))
                                        PENDING_MESSAGES = response.getString("notification_messages");

                                }// /appointment/bookAppointment
                                else if (message.equalsIgnoreCase(context.getString(R.string.appointment_book_success))) {
                                    if (!response.isNull("appointment_data")) {
                                        JSONObject appointmentJsonObject = response.getJSONObject("appointment_data");
                                        BOOKED_APPOINTMENT = jsonObjectToUserBooking(appointmentJsonObject);
                                        RESTClient.ONGOING_BOOKINGS.add(BOOKED_APPOINTMENT);
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
                            requestFailed(error, serviceURL, parameters);
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
                RESTClient.USER_BOOKINGS_HISTORY = new ArrayList<UserBooking>();
                sendServiceResult(context.getString(R.string.user_list_bookings_success));
            }*/
        }
    }

    private UserBooking jsonObjectToUserBooking(JSONObject appointmentJsonObject) {
        UserBooking appointment = new UserBooking();
        try {
            appointment.setBookingId(appointmentJsonObject.getString("appointment_id"));
            appointment.setBookingCode(appointmentJsonObject.getString("appointment_code"));
            appointment.setProviderName(appointmentJsonObject.getString("service_provider_name"));
            appointment.setService(appointmentJsonObject.getString("service_label"));
            appointment.setDateTime(appointmentJsonObject.getString("appointment_date"), appointmentJsonObject.getString("appointment_time"));
            appointment.setStatus(appointmentJsonObject.getString("status"));
            appointment.setAddress(appointmentJsonObject.getString("address"));
            appointment.setDecodedPic(Base64.decode(appointmentJsonObject.getString("profile_pic_url"), Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    private ArrayList<ServiceProvider> jsonArrayToProvidersArray(JSONArray providersJSON) {
        ArrayList<ServiceProvider> providers = new ArrayList<ServiceProvider>();
        for (int i = 0; i < providersJSON.length(); i++) {
            ServiceProvider provider = new ServiceProvider();
            try {
                provider.setServiceProviderId(((JSONObject) providersJSON.get(i)).getString("service_provider_id"));
                provider.setServiceProviderName(((JSONObject) providersJSON.get(i)).getString("service_provider_name"));
                provider.setLongitude(((JSONObject) providersJSON.get(i)).getString("longitude"));
                provider.setLatitude(((JSONObject) providersJSON.get(i)).getString("latitude"));
                provider.setAddress(((JSONObject) providersJSON.get(i)).getString("address"));
                provider.setBookingsCount(((JSONObject) providersJSON.get(i)).getString("bookings_count"));
                provider.setAppUsageCount(((JSONObject) providersJSON.get(i)).getString("application_usage_count"));
                providers.add(provider);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return providers;
    }

    private ArrayList<UserBooking> jsonArrayToHistoryArray(JSONArray historyJSON) {
        ArrayList<UserBooking> bookings = new ArrayList<UserBooking>();
        for (int i = 0; i < historyJSON.length(); i++) {
            UserBooking booking = new UserBooking();
            try {
                booking.setBookingId(((JSONObject) historyJSON.get(i)).getString("appointment_id"));
                booking.setBookingCode(((JSONObject) historyJSON.get(i)).getString("appointment_code"));
                booking.setProviderName(((JSONObject) historyJSON.get(i)).getString("service_provider_name"));
                booking.setDateTime(((JSONObject) historyJSON.get(i)).getString("appointment_date"), ((JSONObject) historyJSON.get(i)).getString("appointment_time"));
                booking.setService(((JSONObject) historyJSON.get(i)).getString("service_label"));
                booking.setAddress(((JSONObject) historyJSON.get(i)).getString("address"));
                booking.setReview(((JSONObject) historyJSON.get(i)).getString("review"));
                booking.setDecodedPic(Base64.decode(((JSONObject) historyJSON.get(i)).getString("service_provider_pic"), Base64.DEFAULT));
                bookings.add(booking);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bookings;
    }


    private ArrayList<UserBooking> jsonArrayToOngoingArray(JSONArray historyJSON) {
        ArrayList<UserBooking> bookings = new ArrayList<UserBooking>();
        for (int i = 0; i < historyJSON.length(); i++) {
            UserBooking booking = new UserBooking();
            try {
                booking.setBookingId(((JSONObject) historyJSON.get(i)).getString("appointment_id"));
                booking.setProviderName(((JSONObject) historyJSON.get(i)).getString("service_provider_name"));
                booking.setDateTime(((JSONObject) historyJSON.get(i)).getString("appointment_date"), ((JSONObject) historyJSON.get(i)).getString("appointment_time"));
                booking.setService(((JSONObject) historyJSON.get(i)).getString("service_label"));
                booking.setStatus(((JSONObject) historyJSON.get(i)).getString("status"));
                booking.setAddress(((JSONObject) historyJSON.get(i)).getString("address"));
                booking.setBookingCode(((JSONObject) historyJSON.get(i)).getString("appointment_code"));
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
                booking.setUserAddress(((JSONObject) bookingJSON.get(i)).getString("user_address"));
                booking.setUserPhone(((JSONObject) bookingJSON.get(i)).getString("user_country_code"), ((JSONObject) bookingJSON.get(i)).getString("user_phone"));
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
                String serviceId = ((JSONObject) serviceJSON.get(i)).getString("service_id");
                service.setServiceId(serviceId);
                service.setServiceName(((JSONObject) serviceJSON.get(i)).getString("service_name"));
                service.setServiceLabel(((JSONObject) serviceJSON.get(i)).getString("service_label"));
                switch (serviceId) {
                    case "1":
                        service.setImageResource(R.drawable.services_call_driver);
                        break;
                    case "2":
                        service.setImageResource(R.drawable.services_mechanic);
                        break;
                    case "3":
                        service.setImageResource(R.drawable.services_plumber);
                        break;
                    case "4":
                        service.setImageResource(R.drawable.services_electrician);
                        break;
                    case "5":
                        service.setImageResource(R.drawable.services_carpenter);
                        break;
                }
                services.add(service);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return services;
    }

    private ArrayList<Service> jsonArrayToServicesOfferedArray(JSONArray serviceJSON) {
        ArrayList<Service> services = new ArrayList<Service>();
        Service dummyService = new Service();
        dummyService.setServiceId("0");
        dummyService.setServiceName("Service Offered");
        services.add(dummyService);
        for (int i = 0; i < serviceJSON.length(); i++) {
            Service service = new Service();
            try {
                service.setServiceId(((JSONObject) serviceJSON.get(i)).getString("service_id"));
                service.setServiceName(((JSONObject) serviceJSON.get(i)).getString("service_label"));
                services.add(service);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return services;
    }

    public void sendServiceResult(String serviceResult) {
        try {
            if (loading != null)
                loading.dismiss();
            serviceResponseInterface.sendServiceResult(serviceResult);
        } catch (IllegalArgumentException e) {
            e.getStackTrace();
        }
    }


    public void requestFailed(VolleyError error, String serviceUrl, String parameters) {
        if (loading != null)
            loading.dismiss();
        StringBuilder body = new StringBuilder();

        body.append("Service : \n " + serviceUrl)
                .append("\n")
                .append("Parameters : \n" + parameters)
                .append("\n");
        try {

            body.append("STACK TRACE : \n" + error.getMessage())
                    .append("\n")
                    .append("Error Cause : \n" + error.getCause())
                    .append("\n");
            //.append("Parsing Error : \n" + parseVolleyError(error))
            //.append("\n")
            //.append("Printing Stack Array : \n" + printStackTrack(error.getStackTrace()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyAsyncTask async = new MyAsyncTask(body.toString());
        async.execute();

        serviceResponseInterface.requestFailed();
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        String messageBody;

        public MyAsyncTask(String body) {
            this.messageBody = body;
        }

        @Override
        protected Void doInBackground(Void... params) {
            GMailSender gMailSender = new GMailSender("aurora.crash.report@gmail.com", "tgbyhn@23");
            try {
                // specify your recipients and send the email
                gMailSender.sendMail("AllinAll - Server Error REPORT", messageBody, "aurora.crash.report@gmail.com", "aurora.sdp@gmail.com, aurora.crash.report@gmail.com");
            } catch (Exception e) {
                Log.d("Error Sending email", e.toString());
            }
            return null;
        }
    }
}
