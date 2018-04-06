package com.czsm.DD_driver.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.czsm.DD_driver.Firebasemodel.ServiceproviderList;
import com.czsm.DD_driver.R;
import com.czsm.DD_driver.controller.AllinAllController;
import com.czsm.DD_driver.helper.RESTClient;
import com.czsm.DD_driver.helper.Util;
import com.czsm.DD_driver.model.Service;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServiceProviderSignupActivity extends AppCompatActivity {
    @BindView(R.id.provider_signup_name_edittext)
    @NotEmpty
    EditText nameEditText;

    @BindView(R.id.provider_signup_phone_edittext)
    EditText phoneEditText;

    @BindView(R.id.provider_signup_address_edittext)
    @NotEmpty
    EditText addressEditText;

    @BindView(R.id.provider_signup_email_edittext)
    @NotEmpty
    EditText emailEditText;

    @BindView(R.id.provider_signup_password_edittext)
    @NotEmpty
    @Password(min = 6, message = "Password must be 6 digits at least")
    EditText passwordEditText;

    @BindView(R.id.provider_signup_confirm_password_edittext)
    @ConfirmPassword
    EditText confirmPasswordEditText;

    @BindView(R.id.provider_signup_service_spinner)
    Spinner serviceOfferedSpinner;

    @BindView(R.id.provider_signup_terms_checkbox)
    @Checked(message = "Must accept terms and conditions")
    CheckBox termsCheckBox;

    @BindView(R.id.provider_signup_button)
    Button signUpButton;

    @BindView(R.id.provider_signup_add_picture_imageview)
    ImageView profileImageView;

    private Validator providerSignupValidator;
    private AllinAllController allinAllController;
    private SharedPreferences allinAllSharedPreferences;

    public static final int GET_FROM_GALLERY = 0;
    private String encodedProfileImage, profileImageExt;
    private String code, mobile;


    /*********************Firebase content****************************/
    FirebaseAuth auth;
    DatabaseReference db;

    StorageReference mountainsRef;
    StorageReference mountainImagesRef;
    FirebaseStorage storage;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_signup2);
        ButterKnife.bind(this);
        providerSignupValidator = new Validator(this);
//        providerSignupValidator.setValidationListener(this);

        auth    = FirebaseAuth.getInstance();
        db      = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

//        allinAllController = new AllinAllController(this, this);
        allinAllSharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile");
            code   = extras.getString("code");
        }

        phoneEditText.setText(code + mobile);
        serviceOfferedSpinner.setAdapter(new ArrayAdapter<Service>(this,R.layout.spinner_item, RESTClient.SERVICES_OFFERED));
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                providerSignupValidator.validate();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                startActivityForResult(getIntent, GET_FROM_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String imageFilePath;
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-demanddriver.appspot.com");
            mountainsRef = storageRef.child("ProfileImages");
            mountainImagesRef = storageRef.child("profile.jpg");


            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, new String[]
                        {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                imageFilePath = cursor.getString(0);

                if (imageFilePath != null) {
                    profileImageExt = imageFilePath.substring(imageFilePath.lastIndexOf(".") + 1);
                    bitmap = Bitmap.createScaledBitmap(Util.getCorrectlyOrientedImage(this, selectedImage), 500, 500, false);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    UploadTask uploadTask = mountainImagesRef.putBytes(imageBytes);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            Toast.makeText(getApplicationContext(),"Upload failed,Please try again",Toast.LENGTH_SHORT).show();
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            encodedProfileImage = taskSnapshot.getDownloadUrl().toString();
                            profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            profileImageView.setImageDrawable(Util.createRoundedBitmapDrawable(bitmap, getResources()));

                        }
                    });

                }
            }
        }
    }

//    @Override
//    public void sendServiceResult(String serviceResult) {
//
//        if (serviceResult.equalsIgnoreCase(getString(R.string.provider_signup_success))) {
//
//            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
//            editor.putBoolean("firstLaunch", false);
//            editor.putString("providerId", RESTClient.ID);
//            editor.apply();
//            Intent providerIntent = new Intent(this, ServiceProviderActivity.class);
//            providerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(providerIntent);
//
//        } else
//            Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void requestFailed() {
//        Util.requestFailed(this);
//    }
//
//    @Override
//    public void onValidationSucceeded() {
//
//        String error = validateSpinnerData();
//        if (error != null)
//            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//        else {
//
//
//            auth.createUserWithEmailAndPassword(mobile+"@demanddriver.com",passwordEditText.getText().toString()).addOnCompleteListener(ServiceProviderSignupActivity.this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                    if(task.isSuccessful()){
//
//                        ServiceproviderList service = new ServiceproviderList();
//                        service.setName(nameEditText.getText().toString());
//                        service.setEmail(emailEditText.getText().toString());
//                        service.setAddress(addressEditText.getText().toString());
//                        service.setCountrycode(code);
//                        service.setMobileno(mobile);
//                        service.setProfilepic(encodedProfileImage);
//                        service.setWalletbalance("0");
//                        service.setApplication_usage_count(0);
//                        service.setBookings_count(0);
//                        service.setLatitude("12.9010");
//                        service.setLongitude("80.2279");
//                        service.setLongitude("free");
//
//                        db.child("ServiceproviderList").push().setValue(service);
//
//                        ValueEventListener postListener = new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                                    ServiceproviderList user = child.getValue(ServiceproviderList.class);
//                                    Log.e("Dddddd",child.getKey());
//
//                                    if(child.getKey() != null){
//
//                                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
//                                        editor.putBoolean("firstLaunch", false);
//                                        editor.putString("providerId", child.getKey());
//                                        editor.apply();
//                                        Intent providerIntent = new Intent(ServiceProviderSignupActivity.this, ServiceProviderActivity.class);
//                                        providerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(providerIntent);
//
//                                    }
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                                Log.w("loadPost:onCancelled", databaseError.toException());
//
//                            }
//                        };
//
//                        db.child("ServiceproviderList").orderByChild("mobileno").equalTo(mobile).addValueEventListener(postListener);
//
//
//
//                    } else {
//
//                        Toast.makeText(getApplicationContext(),"Login failed please try again",Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }
//            });
//
//        }
//
//    }

    private String validateSpinnerData() {

        if (serviceOfferedSpinner.getSelectedItemPosition() <= 0)
            return "Must Select Service Offered";
        return null;

    }

//    @Override
//    public void onValidationFailed(List<ValidationError> errors) {
//        Util.onValidationFailed(this, errors);
//    }


}
