package com.czsm.Demand_Driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginScreenActivity extends AppCompatActivity {
EditText PhoneEdt;
    ImageView FrdRelLay;
ImageView RelImg;
    private FirebaseAuth mAuth;
    String phoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthCredential credential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlogin);
        PhoneEdt=(EditText)findViewById(R.id.phone_edt);
   FrdRelLay=(ImageView) findViewById(R.id.rel_lay);
        RelImg=(ImageView)findViewById(R.id.rel_img);
        mAuth = FirebaseAuth.getInstance();

        RelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginScreenActivity.this);
                if(PhoneEdt.getText().toString().isEmpty()||PhoneEdt.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
                }else{
                    phoneNumber=PhoneEdt.getText().toString();
                    startPhoneNumberVerification(phoneNumber);
                    Toast.makeText(LoginScreenActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
                }
            }
        });
        FrdRelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginScreenActivity.this);
                if(PhoneEdt.getText().toString().isEmpty()||PhoneEdt.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
                }else{

                    startPhoneNumberVerification(phoneNumber);
                    Toast.makeText(LoginScreenActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
//                    startActivity(intent);
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
//                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId= verificationId;
                mResendToken = token;
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(LoginScreenActivity.this, SplashActivity.class));
                            finish();
                        } else {
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }


    private void startPhoneNumberVerification( String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                this.phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


}
