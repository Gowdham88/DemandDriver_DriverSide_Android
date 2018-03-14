package com.czsm.Demand_Driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.controller.AllinAllController;
import com.czsm.Demand_Driver.helper.RESTClient;
import com.czsm.Demand_Driver.helper.Util;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SendOTPActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {

    @BindView(R.id.send_otp_mobile_edittext)
    @NotEmpty
    EditText phoneEditText;

    @BindView(R.id.send_otp_send_button)
    Button sendButton;

    @BindView(R.id.send_otp_country_code_spinner)
    Spinner countryCodeSpinner;

    private Validator otpValidator;
    private AllinAllController allinAllController;
    String code, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        ButterKnife.bind(this);

        allinAllController = new AllinAllController(this, this);
        otpValidator = new Validator(this);
        otpValidator.setValidationListener(this);

        // country code
        String codes[] = {"0091", "0020", "00966"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, codes);
        countryCodeSpinner.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpValidator.validate();
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        mobile = phoneEditText.getText().toString();
        code = countryCodeSpinner.getSelectedItem().toString();
        allinAllController.sendSms(code + mobile);

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.otp_sendsms_success))) {
            Intent verifyOTPIntent = new Intent(this, VerifyOTPActivity.class);
            Bundle verifyOTPBundle = new Bundle();
            verifyOTPBundle.putString("code", code);
            verifyOTPBundle.putString("mobile", mobile);
            verifyOTPIntent.putExtras(verifyOTPBundle);
            startActivity(verifyOTPIntent);
            finish();
        } else {
            Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}
