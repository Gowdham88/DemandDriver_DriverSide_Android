package com.aurorasdp.allinall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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


    @InjectView(R.id.user_signup_add_picture_linearlayout)
    LinearLayout addPicLayout;

    @InjectView(R.id.user_signup_add_picture_imageview)
    ImageView profileImageView;

    private Validator userSignupValidator;
    private AllinAllController allinAllController;
    private SharedPreferences allinAllSharedPreferences;

    public static final int GET_FROM_GALLERY = 0;
    private String profileImageExt, encodedProfileImage;
    private String code, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        ButterKnife.inject(this);
        allinAllController = new AllinAllController(this, this);
        allinAllSharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        userSignupValidator = new Validator(this);
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

        addPicLayout.setOnClickListener(new View.OnClickListener() {
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
        Bitmap bitmap = null;
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
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
                    encodedProfileImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    profileImageView.setImageDrawable(Util.createRoundedBitmapDrawable(bitmap, getResources()));
                }
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
//        String error = validateSpinnerData();
//        if (error != null)
//            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//        else {
        allinAllController.userSignUp(nameEditText.getText().toString(), code, mobile, emailEditText.getText().toString(),
                addressEditText.getText().toString(), passwordEditText.getText().toString(), encodedProfileImage, profileImageExt, allinAllSharedPreferences.getString("regID", ""));
//        }
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
            Intent userIntent = new Intent(this, UserActivity.class);
            userIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(userIntent);
        } else Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}