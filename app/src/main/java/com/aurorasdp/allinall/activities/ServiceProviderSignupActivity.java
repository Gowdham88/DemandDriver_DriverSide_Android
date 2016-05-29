package com.aurorasdp.allinall.activities;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.aurorasdp.allinall.R;
import com.aurorasdp.allinall.controller.AllinAllController;
import com.aurorasdp.allinall.helper.RESTClient;
import com.aurorasdp.allinall.helper.Util;
import com.aurorasdp.allinall.model.Service;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ServiceProviderSignupActivity extends AppCompatActivity implements Validator.ValidationListener, RESTClient.ServiceResponseInterface {
    @InjectView(R.id.provider_signup_name_edittext)
    @NotEmpty
    EditText nameEditText;

    @InjectView(R.id.provider_signup_age_spinner)
    Spinner ageSpinner;

    @InjectView(R.id.provider_signup_mobile_edittext)
    @NotEmpty
    EditText mobileEditText;

    @InjectView(R.id.provider_signup_service_spinner)
    Spinner serviceOfferedSpinner;

    @InjectView(R.id.provider_signup_terms_checkbox)
    @Checked(message = "Must accept terms and conditions")
    CheckBox termsCheckBox;

    @InjectView(R.id.provider_signup_signup_button)
    Button signUpButton;

    @InjectView(R.id.provider_signup_add_picture_linearlayout)
    LinearLayout addPicLayout;

    @InjectView(R.id.provider_signup_add_picture_imageview)
    ImageView profileImageView;

    private Validator providerSignupValidator;
    private AllinAllController allinAllController;

    public static final int GET_FROM_GALLERY = 0;
    private String encodedProfileImage, profileImageExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_signup);
        ButterKnife.inject(this);
        providerSignupValidator = new Validator(this);
        providerSignupValidator.setValidationListener(this);

        allinAllController = new AllinAllController(this, this);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                providerSignupValidator.validate();
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
        serviceOfferedSpinner.setAdapter(new ArrayAdapter<Service>(this, android.R.layout.simple_spinner_dropdown_item, RESTClient.SERVICES));
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
        String imageFilePath;
        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
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
                encodedProfileImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                profileImageView.setImageDrawable(Util.createRoundedBitmapDrawable(bitmap, getResources()));
            }
        }
    }

    @Override
    public void sendServiceResult(String serviceResult) {
        if (serviceResult.equalsIgnoreCase(getString(R.string.provider_signup_success))) {
            Intent providerIntent = new Intent(this, ServiceProviderActivity.class);
            startActivity(providerIntent);
        }
    }

    @Override
    public void requestFailed() {
        Util.requestFailed(this);
    }

    @Override
    public void onValidationSucceeded() {
        String error = validateSpinnerData();
        if (error != null)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        else {
            allinAllController.providerSignUp(nameEditText.getText().toString(), ageSpinner.getSelectedItem().toString(), mobileEditText.getText().toString(), RESTClient.SERVICES.get(serviceOfferedSpinner.getSelectedItemPosition()).getServiceId(), encodedProfileImage, profileImageExt);
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
}
