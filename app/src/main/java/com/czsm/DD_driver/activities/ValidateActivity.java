package com.czsm.DD_driver.activities;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ValidateActivity extends AppCompatActivity {
    EditText mOtpEdt;
    TextView mResendotpTxt,mPhonenumbetEdt,mResendtxt;
    Bundle bundle;
    String phonrnum;
    ImageView FrdRelLay;
    ImageView RelImg;
    String otpNumber;
    String value;
    RelativeLayout parentLayout;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthCredential credential;
    boolean string;
    String uid,uidvalue;
    private android.support.v7.app.AlertDialog dialog;
    String referdr;
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
        parentLayout=(RelativeLayout)findViewById(R.id.parent_lay);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ValidateActivity.this);
            }
        });
        Intent intent = this.getIntent();
        bundle = intent.getExtras();
        mAuth = FirebaseAuth.getInstance();
//        value = bundle.getString("value", "empty");
//     if(value.equals("dashboard")){
        if (bundle != null) {
            phonrnum = bundle.getString("phonenumber");
            mVerificationId=bundle.getString("vericode");
//            mResendToken= (PhoneAuthProvider.ForceResendingToken) bundle.get("mtoken");
//            Toast.makeText(this, mVerificationId, Toast.LENGTH_SHORT).show();
//         }
        }
//     else {
//
//             phonrnum = bundle.getString("phonenumber1");
//             mVerificationId=bundle.getString("vericode1");
////            mResendToken= (PhoneAuthProvider.ForceResendingToken) bundle.get("mtoken");
////            Toast.makeText(this, mVerificationId, Toast.LENGTH_SHORT).show();
//
//     }
//     else{
//
//     }

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

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e("okiok", "onVerificationCompleted:" + credential);
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
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
//                Toast.makeText(LoginScreenActivity.this,"Verification code sent to mobile",Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;

                mResendToken = token;
                final FirebaseUser user = mAuth.getCurrentUser();


            }
        };
        mResendtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhoneNumberVerification(phonrnum);
                resendVerificationCode(phonrnum, mResendToken);
            }
        });
        mResendtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhoneNumberVerification(phonrnum);
                resendVerificationCode(phonrnum, mResendToken);
            }
        });


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
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
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            String uid = user.getUid();
                            String Phno=user.getPhoneNumber();
                            PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID,uid);
                            PreferencesHelper.setPreferenceBoolean(getApplicationContext(), PreferencesHelper.PREFERENCE_LOGGED_IN,true);
                            uidvalue = PreferencesHelper.getPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID);
//                            DatabaseReference refe= FirebaseDatabase.getInstance().getReference("driverNotifications");
//                            refe.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
//                            referdr=FirebaseInstanceId.getInstance().getToken();
////                            Toast.makeText(ValidateActivity.this, referdr, Toast.LENGTH_SHORT).show();
//                            Log.e("Tokdr",referdr);
//                            Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();

                            AddDatabase(phonrnum,uid);
                            hideProgressDialog();
////
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(ValidateActivity.this,"Verification failed code invalid",Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                            }
                        }
                    }
                });
    }

    private void AddDatabase(String phoneNumber, final String uid){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        referdr=FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> data = new HashMap<>();
        data.put("driverphoneNumber",phoneNumber);
        data.put("driverUID", uid);
        data.put("driverToken", referdr);
//
//        Toast.makeText(ValidateActivity.this, uid, Toast.LENGTH_SHORT).show();
//        Users users1 = new Users(phoneNumber,uid);


        db.collection("DriverDetails").document(uid).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("uid",uid);
                Intent intent=new Intent(ValidateActivity.this,ServiceProviderActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Error", "Error adding document", e);
                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

            }

        });
        db.collection("DriverInActive").document(uid).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("uid",uid);
                Intent intent=new Intent(ValidateActivity.this,ServiceProviderActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Error", "Error adding document", e);
                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

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
