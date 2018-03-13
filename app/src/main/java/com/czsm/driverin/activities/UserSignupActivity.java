package com.czsm.driverin.activities;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.czsm.driverin.Firebasemodel.UserList;
import com.czsm.driverin.R;
import com.czsm.driverin.controller.AllinAllController;
import com.czsm.driverin.helper.RESTClient;
import com.czsm.driverin.helper.Util;
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
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserSignupActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {


    @InjectView(R.id.user_signup_name_edittext)
    @NotEmpty
    EditText nameEditText;

    @InjectView(R.id.user_signup_email_edittext)
    @NotEmpty
    @Email
    EditText emailEditText;

    @InjectView(R.id.user_signup_phone_edittext)
    EditText phoneEditText;

    @InjectView(R.id.user_signup_address_edittext)
    @NotEmpty
    EditText addressEditText;

    @InjectView(R.id.user_signup_password_edittext)
    @NotEmpty
    @Password(min = 6, message = "Password must be 6 digits at least")
    EditText passwordEditText;

    @InjectView(R.id.user_signup_confirm_password_edittext)
    @ConfirmPassword
    EditText confirmPasswordEditText;

    @InjectView(R.id.user_signup_terms_checkbox)
    @Checked(message = "Must accept terms and conditions")
    CheckBox termsCheckBox;

    @InjectView(R.id.user_signup_button)
    Button signUpButton;

    @InjectView(R.id.user_signup_add_picture_imageview)
    ImageView profileImageView;

    private Validator userSignupValidator;
    private AllinAllController allinAllController;
    private SharedPreferences allinAllSharedPreferences;

    public static final int GET_FROM_GALLERY = 0;
    private String profileImageExt, encodedProfileImage;
    private String code, mobile;
    Bitmap bitmap = null;

    FirebaseAuth auth;
    DatabaseReference db;

    StorageReference mountainsRef;
    StorageReference mountainImagesRef;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        ButterKnife.inject(this);

        auth    = FirebaseAuth.getInstance();
        db      = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        allinAllController        = new AllinAllController(this, this);
        allinAllSharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        userSignupValidator       = new Validator(this);
        userSignupValidator.setValidationListener(this);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mobile = extras.getString("mobile");
            code = extras.getString("code");
        }
        phoneEditText.setText(code + mobile);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignupValidator.validate();
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

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-demanddriver.appspot.com");
            mountainsRef = storageRef.child("ProfileImages");
            mountainImagesRef = storageRef.child("profile.jpg");


            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, new String[]
                        {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                String imageFilePath = cursor.getString(0);

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

    @Override
    public void onValidationSucceeded() {

        auth.createUserWithEmailAndPassword(mobile+"@demanddriver.com",passwordEditText.getText().toString()).addOnCompleteListener(UserSignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    UserList user = new UserList();
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());
                    user.setAddress(addressEditText.getText().toString());
                    user.setCountrycode(code);
                    user.setMobileno(mobile);
                    user.setProfilepic(encodedProfileImage);

                    db.child("UserList").push().setValue(user);

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot child: dataSnapshot.getChildren()) {

                                UserList user = child.getValue(UserList.class);
                                Log.e("Dddddd",child.getKey());

                                if(child.getKey() != null){

                                    SharedPreferences.Editor editor = allinAllSharedPreferences.edit();
                                    editor.putBoolean("firstLaunch", false);
                                    editor.putString("userId", child.getKey());
                                    editor.putString("username", user.getName());
                                    editor.putString("usermobile", user.getMobileno());
                                    editor.putString("useraddress", user.getAddress());
                                    editor.putString("userimage", user.getProfilepic());
                                    editor.apply();
                                    Intent userIntent = new Intent(UserSignupActivity.this, DashBoardActivity.class);
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
                    db.child("UserList").orderByChild("mobileno").equalTo(mobile).addValueEventListener(postListener);



                } else {

                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();

                }

            }
        });



    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.user_signup_success))) {
            SharedPreferences.Editor editor = allinAllSharedPreferences.edit();
            editor.putBoolean("firstLaunch", false);
            editor.putString("userId", RESTClient.ID);
            editor.apply();
            Intent userIntent = new Intent(this, DashBoardActivity.class);
            userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(userIntent);
        } else Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}