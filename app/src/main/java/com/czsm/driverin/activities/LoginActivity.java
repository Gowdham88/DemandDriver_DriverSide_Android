package com.czsm.driverin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.driverin.Firebasemodel.ServiceproviderList;
import com.czsm.driverin.Firebasemodel.UserList;
import com.czsm.driverin.R;
import com.czsm.driverin.controller.AllinAllController;
import com.czsm.driverin.helper.RESTClient;
import com.czsm.driverin.helper.Util;
import com.czsm.driverin.receiver.PushNotificationReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.pushbots.push.Pushbots;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {

    @InjectView(R.id.login_number_edittext)
    @NotEmpty
    EditText numberEditText;

    @InjectView(R.id.login_password_edittext)
    @NotEmpty
    EditText passwordEditText;

    @InjectView(R.id.login_isprovider_checkbox)
    CheckBox isProvider;

    @InjectView(R.id.login_signin_button)
    Button signinButton;

    @InjectView(R.id.login_new_account_textview)
    TextView newAccount;

    @InjectView(R.id.login_provider_signup_button)
    Button providerSignUpButton;

    @InjectView(R.id.login_forgot_password_textview)
    TextView forgotPassTextview;

    private Validator loginValidator;
    private AllinAllController allinAllController;
    private SharedPreferences allinallSharedPreferences;
    public static ProgressDialog loading;
    String phoneToResetPassword;
    public static String SIGNUP_TYPE;

    FirebaseAuth auth;
    DatabaseReference db;
    String fcm_id = "";

    List<UserList> userlist = new ArrayList<UserList>();

    int i=0,max = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.inject(this);

        auth  = FirebaseAuth.getInstance();
        db    = FirebaseDatabase.getInstance().getReference();

        allinallSharedPreferences = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        String regID = allinallSharedPreferences.getString("regID", "");
        if (regID.equalsIgnoreCase("")) {
            loading = ProgressDialog.show(this, "Registering ... ", "Please wait...");
            Pushbots.sharedInstance().init(getApplicationContext());
            if (Pushbots.sharedInstance() != null)
                Pushbots.sharedInstance().setCustomHandler(PushNotificationReceiver.class);
        }





        if (!allinallSharedPreferences.getBoolean("firstLaunch", true)) {
            if (!allinallSharedPreferences.getString("userId", "").equalsIgnoreCase("")) {

                Intent userIntent = new Intent(this, DashBoardActivity.class);
                userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(userIntent);

            } else if (!allinallSharedPreferences.getString("providerId", "").equalsIgnoreCase("")) {

                Intent providerIntent = new Intent(LoginActivity.this, ServiceProviderActivity.class);
                providerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(providerIntent);


                ValueEventListener listner = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            ServiceproviderList list = child.getValue(ServiceproviderList.class);

                            child.getRef().child("application_usage_count").setValue(list.getApplication_usage_count()+1);




                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e("loadPost:onCancelled", databaseError.toException().toString());
                    }
                };

                db.child("ServiceproviderList").orderByKey().equalTo(allinallSharedPreferences.getString("providerId", "")).addListenerForSingleValueEvent(listner);

            }
        }

        allinAllController = new AllinAllController(this, this);
        loginValidator     = new Validator(this);
        loginValidator.setValidationListener(this);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginValidator.validate();
            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SIGNUP_TYPE = "user";
                Intent otpIntent = new Intent(getApplicationContext(), SendOTPActivity.class);
                startActivity(otpIntent);

            }
        });
        providerSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SIGNUP_TYPE = "provider";
                Intent otpIntent = new Intent(getApplicationContext(), SendOTPActivity.class);
                startActivity(otpIntent);

            }
        });

        forgotPassTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneToResetPassword = numberEditText.getText().toString();
                if (phoneToResetPassword.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Number", Toast.LENGTH_LONG).show();
                } else
                    allinAllController.getUserEmail(phoneToResetPassword);


            }
        });
    }



    private void showResetDialog(final String phone) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Do you really want to reset the  password and send the new password to your Email:\n " + RESTClient.EMAIL + "?\nYou can not get new password if your email is not correct")
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String pass = Util.generateRandom();
                        String encrypted = Util.md5(pass);
                        allinAllController.resetPassword(phone, encrypted, pass);
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void onValidationSucceeded() {

        if (isProvider.isChecked()) {

            /*******************Service provider login**************************/
            auth.signInWithEmailAndPassword(numberEditText.getText().toString()+"@demanddriver.com",passwordEditText.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child: dataSnapshot.getChildren()) {

                                    if(child.getKey() != null){

                                        SharedPreferences.Editor editor = allinallSharedPreferences.edit();
                                        editor.putBoolean("firstLaunch", false);
                                        editor.putString("providerId", child.getKey());
                                        editor.apply();
                                        Intent providerIntent = new Intent(LoginActivity.this, ServiceProviderActivity.class);
                                        providerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(providerIntent);

                                    }

                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.w("loadPost:onCancelled", databaseError.toException());

                            }
                        };

                        db.child("ServiceproviderList").orderByChild("mobileno").equalTo(numberEditText.getText().toString()).addValueEventListener(postListener);



                    } else {

                        Toast.makeText(getApplicationContext(),"Login unsuccessfull",Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }

        else {

            /******************* User login **************************/

            auth.signInWithEmailAndPassword(numberEditText.getText().toString()+"@demanddriver.com",passwordEditText.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot child: dataSnapshot.getChildren()) {

                                    UserList user = child.getValue(UserList.class);
                                    Log.e("Dddddd",child.getKey());

                                    if(child.getKey() != null){

                                        SharedPreferences.Editor editor = allinallSharedPreferences.edit();
                                        editor.putBoolean("firstLaunch", false);
                                        editor.putString("userId", child.getKey());
                                        editor.putString("username", user.getName());
                                        editor.putString("usermobile", user.getMobileno());
                                        editor.putString("useraddress", user.getAddress());
                                        editor.putString("userimage", user.getProfilepic());
                                        editor.apply();
                                        Intent userIntent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                        userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(userIntent);


                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Log.w("loadPost:onCancelled", databaseError.toException());

                            }
                        };
                        db.child("UserList").orderByChild("mobileno").equalTo(numberEditText.getText().toString()).addValueEventListener(postListener);



                    } else {

                        Toast.makeText(getApplicationContext(),"Login unsuccessfull",Toast.LENGTH_SHORT).show();

                    }

                }
            });


        }

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.user_login_success))) {

        } else if (serviceResult.equalsIgnoreCase(getString(R.string.provider_login_success))) {

        } else if (serviceResult.equalsIgnoreCase(getString(R.string.user_get_mail_success))) {

            showResetDialog(phoneToResetPassword);
        } else {
            Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void requestFailed() {

        Util.requestFailed(this);

    }


}
