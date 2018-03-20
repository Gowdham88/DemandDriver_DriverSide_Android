package com.czsm.Demand_Driver.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.czsm.Demand_Driver.PreferencesHelper;
import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Utils;
import com.czsm.Demand_Driver.model.Users;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginScreenActivity extends AppCompatActivity {
EditText PhoneEdt;
    ImageView FrdRelLay;
ImageView RelImg;
RelativeLayout parentLayout;
    CheckBox isProvider;
    private FirebaseAuth mAuth;
    String phoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    RadioButton mRadioBtn;
    PhoneAuthCredential credential;
    TextView countrycode;
    private android.support.v7.app.AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlogin);
        PhoneEdt=(EditText)findViewById(R.id.phone_edt);
   FrdRelLay=(ImageView) findViewById(R.id.rel_lay);
        parentLayout=(RelativeLayout)findViewById(R.id.parentlay);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(LoginScreenActivity.this);
            }
        });
        countrycode=(TextView)findViewById(R.id.code_txt);
        countrycode.setText("India(+91)");
        String getcountrycode=countrycode.getText().toString();
        RelImg=(ImageView)findViewById(R.id.rel_img);
        mAuth = FirebaseAuth.getInstance();
        isProvider=(CheckBox) findViewById(R.id.login_isprovider_checkbox);
        RelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginScreenActivity.this);
                if(PhoneEdt.getText().toString().isEmpty()||PhoneEdt.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
                }
                else{
//                    if(mRadioBtn.isChecked()){
                    phoneNumber=PhoneEdt.getText().toString();
                    startPhoneNumberVerification(phoneNumber);
////                    Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
////                    intent.putExtra("phonenumber",PhoneEdt.getText().toString());
////                    startActivity(intent);
////                    Toast.makeText(LoginScreenActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Intent intent=new Intent(LoginScreenActivity.this,DashBoardActivity.class);
////                    intent.putExtra("phonenumber",PhoneEdt.getText().toString());
////                    intent.putExtra("vericode",mVerificationId.toString());
//                    startActivity(intent);
//                    PhoneEdt.setText("");
//                    finish();
                }
            }
        });
        FrdRelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginScreenActivity.this);
                if(PhoneEdt.getText().toString().isEmpty()||PhoneEdt.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
                }
                else
//                    if(mRadioBtn.isChecked()){
                    phoneNumber=PhoneEdt.getText().toString();
                    startPhoneNumberVerification(phoneNumber);
////                    Toast.makeText(LoginScreenActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
////                    Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
////                    intent.putExtra("phonenumber",PhoneEdt.getText().toString());
////                    startActivity(intent);
//                }
//                else
                    {
//                    Intent intent=new Intent(LoginScreenActivity.this,DashBoardActivity.class);
////                    intent.putExtra("phonenumber",PhoneEdt.getText().toString());
////                    intent.putExtra("vericode",mVerificationId.toString());
//                    startActivity(intent);
//                    PhoneEdt.setText("");
//                    finish();
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
                AddDatabase(phoneNumber);
//                if (isProvider.isChecked()){
//                    Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
//                    intent.putExtra("value","dashboard");
//                    intent.putExtra("string",true);
//                    intent.putExtra("phonenumber",PhoneEdt.getText().toString());
//                    intent.putExtra("vericode",mVerificationId.toString());
//
////                    intent.putExtra("mtoken",mResendToken);
//                    startActivity(intent);
//                isProvider.setChecked(false);
//                PhoneEdt.setText("");
//                }
//                else{
//                    Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
////                    intent.putExtra("value","service");
//                    intent.putExtra("phonenumber1",PhoneEdt.getText().toString());
//                    intent.putExtra("vericode1",mVerificationId.toString());
//                    startActivity(intent);
//                    PhoneEdt.setText("");
//                    isProvider.setChecked(false);
////                    finish();
//                }

            }
        };


    }



    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
//                            PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID, user.getUid());

//                            hideProgressDialog();
//                            Toast.makeText(LoginScreenActivity.this, (CharSequence) user, Toast.LENGTH_SHORT).show();
//                            Intent intent=new Intent(LoginScreenActivity.this,ValidateActivity.class);
//                                 startActivity(intent);
//                            Toast.makeText(LoginScreenActivity.this, "success", Toast.LENGTH_SHORT).show();
//                            finish();
                        } else {
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }


    private void AddDatabase(String phoneNumber){

        String uid = PreferencesHelper.getPreference(LoginScreenActivity.this, PreferencesHelper.PREFERENCE_FIREBASE_UUID);
//        Toast.makeText(LoginScreenActivity.this, uid, Toast.LENGTH_SHORT).show();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        final Users users = new Users(PhoneEdt.getText().toString());
//        showProgressDialog();
        Map<String, Boolean> comData = new HashMap<>();
        comData.put(uid, true);

        Users users1 = new Users(phoneNumber,uid);

        db.collection("Users").add(users1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                if (isProvider.isChecked()) {
                    Intent intent = new Intent(LoginScreenActivity.this, ValidateActivity.class);
                    intent.putExtra("value", "dashboard");
                    intent.putExtra("string", true);
                    intent.putExtra("phonenumber", PhoneEdt.getText().toString());
                    intent.putExtra("vericode", mVerificationId.toString());

//                    intent.putExtra("mtoken",mResendToken);
                    startActivity(intent);
                    isProvider.setChecked(false);
                    PhoneEdt.setText("");
                } else {
                    Intent intent = new Intent(LoginScreenActivity.this, ValidateActivity.class);
//                    intent.putExtra("value","service");
                    intent.putExtra("phonenumber1", PhoneEdt.getText().toString());
                    intent.putExtra("vericode1", mVerificationId.toString());
                    startActivity(intent);
                    PhoneEdt.setText("");
                    isProvider.setChecked(false);
//                    finish();
                }


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


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginScreenActivity.this);
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
