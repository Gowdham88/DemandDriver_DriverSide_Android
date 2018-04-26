package com.czsm.DD_driver.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.Firebasemodel.ServiceproviderList;
import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Service.CapPhoto;
import com.czsm.DD_driver.controller.AllinAllController;
import com.czsm.DD_driver.helper.RESTClient;
import com.czsm.DD_driver.helper.Util;
import com.czsm.DD_driver.model.Data;
import com.czsm.DD_driver.model.Driver_complete_details;
import com.czsm.DD_driver.model.ProviderBooking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProviderBookingActivity extends AppCompatActivity {

    @BindView(R.id.provider_book_name_textview)
    TextView nameTextView;

    @BindView(R.id.provider_book_datetime_textview)
    TextView dateTimeTextView;

    @BindView(R.id.provider_book_contact_textview)
    TextView contactTextView;

    @BindView(R.id.provider_book_address_textview)
    TextView addressTextView;

    @BindView(R.id.provider_book_complete_buttonstart)
    Button StartButton;

    @BindView(R.id.provider_book_complete_buttoncomplete)
    Button completeButton;

    @BindView(R.id.provider_book_toolbar)
    Toolbar toolbar;

    private AllinAllController allinAllController;
    int bookingIndex;
    String username, usermobile, useraddress, date, userid, id = "", UserLat, UserLong;
    SharedPreferences sharedPreferences;
//    DatabaseReference db;
    ImageView forwardImg;
    String name,datetime,contact,add,uidvalue,driverhours,drivermin,driversecs,UserDate,UserTime,UserUid;
    FirebaseFirestore db;
    String formattendDatedriver,formattedstrDatedriver;
    String dateuser,Useruid,amt,Driverno,CarType;
           long currentamt,amount;
    DocumentReference documentReference;
    String Rndmuid,UserBooking_ID;
    List<Driver_complete_details> datalist = new ArrayList<Driver_complete_details>();
    List<Driver_complete_details> datalist1 = new ArrayList<Driver_complete_details>();
    String Usrphonenumber,Cartype,latval,longittudeval,address,cityval,usrbkid,Date,UsrUid,usrname,status,UsrReview,UsrBkTime,UserName;
    String Statusval="Completed",UsrReviews;
    TextView toolbartext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_book);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Driverno = PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_DRIVERPHONENUMBER);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

//                Intent mapintent= new Intent(ProviderBookingActivity.this,ServiceProviderActivity.class);
//                startActivity(mapintent);
            }
        });
        toolbartext=(TextView)findViewById(R.id.toolbar_one);
//        toolbartext.setText("Appointment");
        setTitle(R.string.appointment);
          db= FirebaseFirestore.getInstance();
        uidvalue = PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        forwardImg = (ImageView) findViewById(R.id.add_arrow);
//        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
//        id = sharedPreferences.getString("providerId", "");
        amt=PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PRICE);
        String uuid = UUID.randomUUID().toString();
        Rndmuid= uuid;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            username = extras.getString("name", "");
            usermobile = extras.getString("phonenumber", "");
            useraddress = extras.getString("address", "");
            date = extras.getString("datatime", "");
            UserLat = extras.getString("userlats", "");
            UserLong = extras.getString("userlongs", "");
            UserUid  = extras.getString("useruid","");
            UserDate=extras.getString("userdate", "");
            UserTime=extras.getString("usertime", "");
            CarType=extras.getString("Car_type", "");
            UserBooking_ID=extras.getString("Booking_ID", "");
Log.e("username",username);


        }


        try {

            nameTextView.setText(username);
            dateTimeTextView.setText(date);
            contactTextView.setText(usermobile);
            addressTextView.setText(useraddress);


        } catch (NullPointerException e) {

            e.printStackTrace();
        }


        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_USERUID,UserUid);
        Useruid=  PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_USERUID);
        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_CARTYPE,CarType);
//        Toast.makeText(ProviderBookingActivity.this, Useruid, Toast.LENGTH_SHORT).show();
        Map<String, Object> datadriver = new HashMap<>();
        datadriver.put("Car_type",CarType);
//        datadriver.put("Driver_Lat","12.9010");
//        datadriver.put("Driver_Long","80.2279");

//        data.put("usertimedate",formattedstrDatedriver);
//
//        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
//        Users users1 = new Users(phoneNumber,uid);


        db.collection("Driver_details").document(uidvalue).update(datadriver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Error", "Error adding document", e);
//                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

            }

        });


//        Map<String, Object> data = new HashMap<>();
//        data.put("Driver_ID",uidvalue);
//        data.put("Driver_Phone_number",Driverno);
//        data.put("Driver_Booking_ID",Rndmuid);
////        data.put("usertimedate",formattedstrDatedriver);
////
////        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
////        Users users1 = new Users(phoneNumber,uid);
//
//
//        db.collection("Current_booking").document(UserBooking_ID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
////                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("Error", "Error adding document", e);
////                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();
//
//            }
//
//        });




//        Map<String, Object> dataval = new HashMap<>();
//        dataval.put("Driver_ID",uidvalue);
//        dataval.put("Driver_Phone_number",Driverno);
//        dataval.put("Driver_Booking_ID",Rndmuid);
//
//        db.collection("Completed_booking").document(UserBooking_ID).update(dataval).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
////                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("Error", "Error adding document", e);
////                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();
//
//            }
//
//        });

        contact=contactTextView.getText().toString();
        forwardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addintent = new Intent(ProviderBookingActivity.this, MapScreenActivity.class);
                addintent.putExtra("userlat", UserLat);
                addintent.putExtra("userlong", UserLong);
                startActivity(addintent);
            }
        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                 showConfirmDialog("Cancelled","Are you sure you want to cancel this appointment.");
////
//
//            }
//        });

        contactTextView.setOnClickListener(new View.OnClickListener() {
            Intent call = new Intent(Intent.ACTION_DIAL);
            @Override
            public void onClick(View v){
                call.setData(Uri.parse("tel:"+ contact));
                startActivity(call);
            }
        });


        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
//                Toast.makeText(ProviderBookingActivity.this, "click", Toast.LENGTH_SHORT).show();

//                StartButton.setVisibility(View.GONE);

//                showConfirmDialog("Completed","Are you sure you want to cancel this appointment.");

            }
        });


    }

    private void validation() {
        name=nameTextView.toString();
        datetime=dateTimeTextView.toString();
        add=addressTextView.toString();

       if(name.isEmpty() || name.equals(null)){
           Toast.makeText(ProviderBookingActivity.this, "name is empty", Toast.LENGTH_SHORT).show();
       }
       else if(datetime.isEmpty()|| datetime.equals(null)){
           Toast.makeText(ProviderBookingActivity.this, "Date and Time is empty", Toast.LENGTH_SHORT).show();
       }
       else if(contact.isEmpty()|| contact.equals(null)){
           Toast.makeText(ProviderBookingActivity.this, "Phonenumber is empty", Toast.LENGTH_SHORT).show();
       }
       else if(add.isEmpty()|| add.equals(null)){
           Toast.makeText(ProviderBookingActivity.this, "address is empty", Toast.LENGTH_SHORT).show();
       }
        else
        {

            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat dfs = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
            formattedstrDatedriver = dfs.format(currentTime);
            completeButton.setVisibility(View.VISIBLE);
            documentReference=db.collection("Driver_current_booking").document(uidvalue);
            HashMap<String,Object> usercurrentbookingid=new HashMap<>();
//
            usercurrentbookingid.put("Driver_Booking_ID",Rndmuid);
//
            documentReference.set(usercurrentbookingid)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });
            Map<String, Object> data = new HashMap<>();
//            data.put("End_time",formattendDatedriver);
            data.put("Start_time",formattedstrDatedriver);
//            data.put("Cost",amt);
//
//        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
//        Users users1 = new Users(phoneNumber,uid);


            db.collection("Current_booking").document(UserBooking_ID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                        Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Error", "Error adding document", e);
//                        Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

                }

            });

        }

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ProviderBookingActivity.this, "ok", Toast.LENGTH_SHORT).show();
                showConfirmDialog();

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                 formattendDatedriver = df.format(currentTime);
//

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                try {
                    Date date1 = simpleDateFormat.parse(formattedstrDatedriver);
                    Date date2 = simpleDateFormat.parse(formattendDatedriver);

                    printDifference(date1, date2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Map<String, Object> datasatus = new HashMap<>();
                datasatus.put("Status","Completed");
                db.collection("Current_booking").document(UserBooking_ID).update(datasatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
//                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

                    }

                });


//                Map<String, Object> datasatuscm = new HashMap<>();
//                datasatuscm.put("Status","Completed");
//                db.collection("Completed_booking").document(UserBooking_ID).update(datasatuscm).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Error", "Error adding document", e);
////                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();
//
//                    }
//
//                });
//                String formattedDateuser = df.format(datetime);
                Map<String, Object> data = new HashMap<>();
                data.put("End_time",formattendDatedriver);
                data.put("Start_time",formattedstrDatedriver);
                data.put("Cost",amt);
//
//        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
//        Users users1 = new Users(phoneNumber,uid);


                db.collection("Current_booking").document(UserBooking_ID).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
//                        Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

                    }

                });

//
//                Map<String, Object> datausercomplete = new HashMap<>();
//                datausercomplete.put("End_time",formattendDatedriver);
//                datausercomplete.put("Start_time",formattedstrDatedriver);
//                datausercomplete.put("Cost",amt);
////
////        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
////        Users users1 = new Users(phoneNumber,uid);
//
//
//                db.collection("Completed_booking").document(UserBooking_ID).update(datausercomplete).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                        Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Error", "Error adding document", e);
////                        Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();
//
//                    }
//
//                });


                documentReference=db.collection("User_completed_booking").document(uidvalue);
                HashMap<String,Object> usercurrentbookingid=new HashMap<>();
//
                usercurrentbookingid.put("User_Booking_ID",UserBooking_ID);
//
                documentReference.set(usercurrentbookingid)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });

                documentReference=db.collection("Driver_completed_booking").document(uidvalue);
                HashMap<String,Object> usercurrentbookingcmp=new HashMap<>();
//
                usercurrentbookingcmp.put("Driver_Booking_ID",Rndmuid);
//
                documentReference.set(usercurrentbookingcmp)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });

                com.google.firebase.firestore.Query driverfirst = db.collection("Current_booking");

        driverfirst.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                          @Override
                                          public void onSuccess(QuerySnapshot documentSnapshots) {

                                              if (documentSnapshots.getDocuments().size() < 1) {

                                                  return;

                                              }

                                              for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                                                  Driver_complete_details data = document.toObject(Driver_complete_details.class);
                                                  datalist.add(data);
                                                   Usrphonenumber= String.valueOf(datalist.get(0).getUser_Phone_number());
                                                 Cartype= String.valueOf(datalist.get(0).getCar_type());
                                              latval= String.valueOf(datalist.get(0).getStart_Lat());
                                                 longittudeval= String.valueOf(datalist.get(0).getStart_Long());
                                                 address= String.valueOf(datalist.get(0).getUser_Address());
                                                  cityval= String.valueOf(datalist.get(0).getCity());
                                                  usrbkid= String.valueOf(datalist.get(0).getUser_Booking_ID());
                                                 Date= String.valueOf(datalist.get(0).getDate());
                                                 UsrUid= String.valueOf(datalist.get(0).getUser_ID());
                                                  UserName= String.valueOf(datalist.get(0).getUser_name());
                                                  status= String.valueOf(datalist.get(0).getStatus());
                                                  UsrReview= String.valueOf(datalist.get(0).getUser_review());
                                                  UsrBkTime= String.valueOf(datalist.get(0).getUser_Booking_Time());
//                                                  Toast.makeText(ProviderBookingActivity.this, UserName, Toast.LENGTH_SHORT).show();





//                                                  String latt= String.valueOf(datalist.get(0).getLat());
//                                                  Toast.makeText(MapActivity.this, latt, Toast.LENGTH_SHORT).show();

//                                                  Log.e("datalist",datalist.get(0).getLat());
//                                hideProgressDialog();

                                              }
//                            hideProgressDialog();


                                          }
                                      });
//                com.google.firebase.firestore.Query driverfirst1 = db.collection("User_details");
//
//                driverfirst1.get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot documentSnapshots) {
//
//                                if (documentSnapshots.getDocuments().size() < 1) {
//
//                                    return;
//
//                                }
//
//                                for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
//
//                                    Driver_complete_details data1 = document.toObject(Driver_complete_details.class);
//                                    datalist1.add(data1);
//                                    UsrReviews= String.valueOf(datalist1.get(0).getUser_review());
//                                    Toast.makeText(ProviderBookingActivity.this, UsrReviews, Toast.LENGTH_SHORT).show();
//
//
//
//
//
//
//
//
////                                                  String latt= String.valueOf(datalist.get(0).getLat());
////                                                  Toast.makeText(MapActivity.this, latt, Toast.LENGTH_SHORT).show();
//
////                                                  Log.e("datalist",datalist.get(0).getLat());
////                                hideProgressDialog();
//
//                                }
////                            hideProgressDialog();
//
//
//                            }
//                        });

        if(Statusval.equals(status)){



        }



            }
        });

    }
    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(ProviderBookingActivity.this)
                //set message, title, and icon
                .setMessage("Do you want to Complete?")
                .setIcon(R.drawable.logo01)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        popup();

                        documentReference=db.collection("Completed_booking").document(UserBooking_ID);
                        HashMap<String,Object> updatesvaluescomplete=new HashMap<>();
//                        updatesvalues.put("Driver_name",);
//                        updatesvalues.put("Driver_ID",);
                        updatesvaluescomplete.put("User_name",username);
                        updatesvaluescomplete.put("User_ID",UsrUid);
//                        updatesvalues.put("Driver_Phone_number",);
                        updatesvaluescomplete.put("User_Phone_number",Usrphonenumber);
                        updatesvaluescomplete.put("Car_type",Cartype);
                        updatesvaluescomplete.put("Start_Lat",latval);
                        updatesvaluescomplete.put("Start_Long",longittudeval);
                        updatesvaluescomplete.put("User_Address",address);
                        updatesvaluescomplete.put("City",cityval);
                        updatesvaluescomplete.put("User_Booking_ID",usrbkid);
                        updatesvaluescomplete.put("Status","Completed");
                        updatesvaluescomplete.put("Date",Date);
                        updatesvaluescomplete.put("End_time",formattendDatedriver);
                        updatesvaluescomplete.put("Start_time",formattedstrDatedriver);
                        updatesvaluescomplete.put("Cost",amt);
                        updatesvaluescomplete.put("User_review",UsrReview);
                        updatesvaluescomplete.put("Driver_ID",uidvalue);
                        updatesvaluescomplete.put("Driver_Phone_number",Driverno);
                        updatesvaluescomplete.put("Driver_Booking_ID",Rndmuid);
                        updatesvaluescomplete.put("User_Booking_Time",UsrBkTime);
//                        updatesvaluescomplete.put("User_Token",TokenVal);

                        documentReference.set(updatesvaluescomplete)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        db.collection("Current_booking").document(UserBooking_ID).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(ProviderBookingActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });

                                        Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }




    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long diff=(different/1000)/60;

//        long minsdif=different / 60;

       Log.e("different " , String.valueOf(different));
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long hrmins=elapsedHours*60;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long TtlMins= hrmins+elapsedMinutes;

        long elapsedSeconds = different / secondsInMilli;
        String dateval= String.valueOf(elapsedDays+","+elapsedHours+","+elapsedMinutes+","+elapsedSeconds);
        Log.e("dateval", String.valueOf(dateval));
        Log.e("elapsedDays", String.valueOf(elapsedDays));
        Log.e("elapsedHours", String.valueOf(elapsedHours));
        Log.e("elapsedMinutes", String.valueOf(TtlMins ));
        Log.e("elapsedSeconds", String.valueOf(elapsedSeconds));
//        Log.e("elapsedMinutes", String.valueOf(elapsedMinutes));



//        Map<String, Object> data = new HashMap<>();
//        data.put("USERUID",Useruid);
//        data.put("TimeDuration",TtlMins);
//
//        db.collection("DriverBookingsCompleted").document(uidvalue).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
////                Toast.makeText(ProviderBookingActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("Error", "Error adding document", e);
//                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();
//
//            }
//
//        });
        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

         if(TtlMins>60){
             long extramins=TtlMins-60;
            currentamt=99;
            amount= (int) (currentamt+(extramins*1));
        }
        else if(TtlMins<=60){
            amount=99;
        }
        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PRICE, String.valueOf(amount));
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//
//            finish();
//            return true;
//
//        } else
//            return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void sendServiceResult(String serviceResult) {
//
//    }
//
//    @Override
//    public void requestFailed() {
//        Util.requestFailed(this);
//    }

    private void popup() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.userreqalert, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(deleteDialogView);
        TextView Acce = (TextView)deleteDialogView.findViewById(R.id.accept_button);
        TextView amountTxt = (TextView)deleteDialogView.findViewById(R.id.amount_Txt);
        String amtval=String.valueOf(amount);
        amountTxt.setText(amtval);
//        DriverNumber.setText(driverphonenumber);
        final AlertDialog alertDialog1 = alertDialog.create();
        Acce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRatingDialog();


                alertDialog1.dismiss();
            }
        });

        alertDialog1.setCanceledOnTouchOutside(false);
        try {
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog1.show();
//        alertDialog1.getWindow().setLayout((int) Utils.convertDpToPixel(228,getActivity()),(int)Utils.convertDpToPixel(220,getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog1.getWindow().getAttributes());
//        lp.height=200dp;
//        lp.width=228;
        lp.gravity = Gravity.CENTER;
//        lp.windowAnimations = R.style.DialogAnimation;
        alertDialog1.getWindow().setAttributes(lp);
    }
    public void showRatingDialog() {
        final View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_review_booking, null);

        new android.app.AlertDialog.Builder(this).setIcon(android.R.drawable.btn_star_big_on).setTitle("Review")
                .setView(dialogView)
                .setMessage("Appreciate giving feedback about this Appointment")
                .setCancelable(false)
                .setPositiveButton("Rate",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.dialog_review_booking_ratingbar);
                                String review = ratingBar.getProgress() + "";
//                                Toast.makeText(ProviderBookingActivity.this, review, Toast.LENGTH_SHORT).show();
//                                PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_USERRATING,review);
//                                reviewTextView.setText(review);

                                DocumentReference documentReference = db.collection("Current_booking").document(UserBooking_ID);
                                HashMap<String,Object> updatesvalues=new HashMap<>();
                                updatesvalues.put("Driver_review",review);

                                documentReference.update(updatesvalues)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {


                                    }
                                });

                                documentReference = db.collection("Driver_details").document(uidvalue);
                                HashMap<String,Object> updatesdriver=new HashMap<>();
                                updatesdriver.put("Driver_review",review);


                                documentReference.update(updatesvalues)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {


                                    }
                                });

//                                if(status.equals("Completed")){
                                    documentReference = db.collection("Completed_booking").document(UserBooking_ID);
                                    HashMap<String,Object> updatesvaluescomplete=new HashMap<>();
                                    updatesvaluescomplete.put("Driver_review",review);

                                    documentReference.update(updatesvaluescomplete)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                                Toast.makeText(ProviderBookingActivity.this, "successfull", Toast.LENGTH_SHORT).show();
//
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {


                                        }
                                    });
//                                }


                                completeButton.setVisibility(View.GONE);
                                StartButton.setVisibility(View.GONE);
//                                child.getRef().child("userreview").setValue(review);
                                dialog.dismiss();
//                                finish();
                            }
                        })

                // Button Cancel
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

//                                child.getRef().child("userreview").setValue("0");
//                                finish();
                                dialog.cancel();
                            }
                        }).setCancelable(false).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
