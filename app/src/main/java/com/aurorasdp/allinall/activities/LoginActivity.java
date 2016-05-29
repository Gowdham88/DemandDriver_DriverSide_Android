package com.aurorasdp.allinall.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

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
    String phoneToResetPassword;
    public static String SIGNUP_TYPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        allinAllController = new AllinAllController(this, this);
        loginValidator = new Validator(this);
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
                //set message, title, and icon
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
        if (isProvider.isChecked())
            allinAllController.providerLogin(numberEditText.getText().toString(), passwordEditText.getText().toString());
        else
            allinAllController.userLogin(numberEditText.getText().toString(), passwordEditText.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.user_login_success))) {
            allinAllController.listUserHistory(RESTClient.ID, "Loading History .....");
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.provider_login_success))) {
            allinAllController.listProviderBookings(RESTClient.ID, "Loading Bookings .....");
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.provider_list_bookings_success))) {
            allinAllController.getWalletData(RESTClient.ID);
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.provider_get_wallet_success))) {
            Intent providerIntent = new Intent(this, ServiceProviderActivity.class);
            startActivity(providerIntent);
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.user_list_history_success))) {
            Intent userIntent = new Intent(this, UserActivity.class);
            startActivity(userIntent);
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
