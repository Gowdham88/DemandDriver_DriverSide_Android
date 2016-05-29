package com.aurorasdp.allinall.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VerifyOTPActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {

    @InjectView(R.id.verify_otp_edittext)
    @NotEmpty
    EditText otpEditText;

    @InjectView(R.id.verify_otp_verify_button)
    Button verifyButton;

    @InjectView(R.id.verify_otp_resend_textview)
    TextView resendTextview;

    private String mobile;
    private Validator otpValidator;
    private AllinAllController allinAllController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        ButterKnife.inject(this);

        allinAllController = new AllinAllController(this, this);
        otpValidator = new Validator(this);
        otpValidator.setValidationListener(this);

        mobile = getIntent().getStringExtra("mobile");

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpValidator.validate();
            }
        });
        resendTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allinAllController.sendSms(mobile);
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        if (otpEditText.getText().toString().equalsIgnoreCase(RESTClient.OTP)) {
            if (LoginActivity.SIGNUP_TYPE.equalsIgnoreCase("user")) {
                Intent userSignUpIntent = new Intent(this, UserSignupActivity.class);
                startActivity(userSignUpIntent);
                finish();
            } else {
                Intent providerSignUpIntent = new Intent(this, ServiceProviderSignupActivity.class);
                startActivity(providerSignUpIntent);
                finish();
            }
        } else
            Toast.makeText(this, "This OTP is not right", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}
