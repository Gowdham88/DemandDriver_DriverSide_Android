package com.aurorasdp.allinall.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserSignupActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {
    @InjectView(R.id.user_signup_name_edittext)
    @NotEmpty
    EditText nameEditText;

    @InjectView(R.id.user_signup_age_spinner)
    Spinner ageSpinner;

    @InjectView(R.id.user_signup_mobile_edittext)
    @NotEmpty
    EditText mobileEditText;

    @InjectView(R.id.user_signup_email_edittext)
    @NotEmpty
    @Email
    EditText emailEditText;

    @InjectView(R.id.user_signup_password_edittext)
    @NotEmpty
    @Password
    EditText passwordEditText;

    @InjectView(R.id.user_signup_country_code_spinner)
    Spinner countryCodeSpinner;

    @InjectView(R.id.user_signup_terms_checkbox)
    @Checked(message = "Must accept terms and conditions")
    CheckBox termsCheckBox;

    @InjectView(R.id.user_signup_signup_button)
    Button signUpButton;

    @InjectView(R.id.user_signup_provider_textview)
    TextView isProviderTextView;

    @InjectView(R.id.user_signup_add_picture_linearlayout)
    LinearLayout addPicLayout;

    @InjectView(R.id.user_signup_add_picture_imageview)
    ImageView profileImageView;

    private Validator userSignupValidator;
    private AllinAllController allinAllController;

    public static final int GET_FROM_GALLERY = 0;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        ButterKnife.inject(this);
        allinAllController = new AllinAllController(this, this);
        userSignupValidator = new Validator(this);
        userSignupValidator.setValidationListener(this);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignupValidator.validate();
            }
        });
        isProviderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allinAllController.getServices();
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

        // country code
        String codes[] = {"0091"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, codes);
        countryCodeSpinner.setAdapter(adapter);

        List age = new ArrayList<Integer>();
        age.add("Age");
        for (int i = 16; i <= 100; i++) {
            age.add(Integer.toString(i));
        }
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_dropdown_item, age);
        ageSpinner.setAdapter(spinnerArrayAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            profileImageUri = data.getData();
            bitmap = Bitmap.createScaledBitmap(Util.getCorrectlyOrientedImage(this, profileImageUri), 500, 500, false);
        }
        if (bitmap != null) {
            profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            profileImageView.setImageDrawable(Util.createRoundedBitmapDrawable(bitmap, getResources()));
        }
    }

    @Override
    public void onValidationSucceeded() {
        String error = validateSpinnerData();
        if (error != null)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        else {
            allinAllController.sendSms(mobileEditText.getText().toString());
        }
    }

    private String validateSpinnerData() {
        if (ageSpinner.getSelectedItemPosition() <= 0)
            return "Must Select Age";
        return null;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Util.onValidationFailed(this, errors);
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.otp_sendsms_success))) {
            Intent otpIntent = new Intent(this, SendOTPActivity.class);
            Bundle otpBundle = new Bundle();
            otpBundle.putString("name", nameEditText.getText().toString());
            otpBundle.putString("age", ageSpinner.getSelectedItem().toString());
            otpBundle.putString("mobile", mobileEditText.getText().toString());
            otpBundle.putString("email", emailEditText.getText().toString());
            otpBundle.putString("password", passwordEditText.getText().toString());
            otpBundle.putString("countryCode", countryCodeSpinner.getSelectedItem().toString());
            otpBundle.putParcelable("profileImageUri", profileImageUri);
            otpIntent.putExtras(otpBundle);
            startActivity(otpIntent);
        } else if (serviceResult.equalsIgnoreCase(getString(R.string.service_get_services_success))) {

        } else Toast.makeText(this, serviceResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }
}
/**
 email = extras.getString("email");
 password = extras.getString("password");
 countryCode = extras.getString("countryCode");
 Uri imageUri = extras.getParcelable("profilePic");
 if (imageUri != null) {
 Bitmap bitmap = Bitmap.createScaledBitmap(Util.getCorrectlyOrientedImage(this, imageUri), 500, 500, false);
 ByteArrayOutputStream baos = new ByteArrayOutputStream();
 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
 byte[] imageBytes = baos.toByteArray();
 encodedProfileImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
 // User had pick an image.
 Cursor cursor = getContentResolver().query(imageUri, new String[]
 {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
 cursor.moveToFirst();
 // Link to the image
 String imageFilePath = cursor.getString(0);
 profileImageExt = imageFilePath.substring(imageFilePath.lastIndexOf(".") + 1);
 }
 */