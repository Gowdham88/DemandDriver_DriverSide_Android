package com.czsm.Demand_Driver.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class ValidateActivity extends AppCompatActivity {
EditText mOtpEdt;
TextView mResendotpTxt,mPhonenumbetEdt,mResendtxt;
    Bundle bundle;
    String phonrnum;
    ImageView FrdRelLay;
    ImageView RelImg;
    String otpNumber;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthCredential credential;
    private android.support.v7.app.AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        mOtpEdt = (EditText) findViewById(R.id.otp_edt);
        mPhonenumbetEdt = (TextView) findViewById(R.id.phone_edt);
        mResendotpTxt = (TextView) findViewById(R.id.resend_txt);
        FrdRelLay = (ImageView) findViewById(R.id.rel_lay);
        RelImg = (ImageView) findViewById(R.id.rel_img);
        mResendtxt=(TextView)findViewById(R.id.resend_txt);
        Intent intent = this.getIntent();
        bundle = intent.getExtras();
        mAuth = FirebaseAuth.getInstance();
        if (bundle != null) {
            phonrnum = bundle.getString("phonenumber");
            mVerificationId=bundle.getString("vericode");
//            mResendToken= (PhoneAuthProvider.ForceResendingToken) bundle.get("mtoken");
//            Toast.makeText(this, mVerificationId, Toast.LENGTH_SHORT).show();
        }
        mPhonenumbetEdt.setText(phonrnum);
        mOtpEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOtpEdt.getText().toString().trim().length() == 6){
                    Utils.hideKeyboard(ValidateActivity.this);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mOtpEdt.getText().toString());
                    signInWithPhoneAuthCredential(credential);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mResendtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(phonrnum, mResendToken);
            }
        });


    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                ValidateActivity.this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
//                            Toast.makeText(ValidateActivity.this,"Verification done",Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent=new Intent(ValidateActivity.this,ServiceProviderActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(ValidateActivity.this,"Verification failed code invalid",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(ValidateActivity.this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        alertDialog.setView(R.layout.progress);
        dialog = alertDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void hideProgressDialog(){
        if(dialog!=null)
            dialog.dismiss();
    }
}
