package com.czsm.Demand_Driver.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.czsm.Demand_Driver.R;
import com.czsm.Demand_Driver.Service.CapPhoto;
import com.czsm.Demand_Driver.controller.AllinAllController;
import com.czsm.Demand_Driver.helper.RESTClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by macbook on 02/08/16.
 */
public class UserSupportActivity extends AppCompatActivity  implements RESTClient.ServiceResponseInterface {

    private Button signoutButton;
    private SharedPreferences allinallSharedPref;
    private AllinAllController allinAllController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_support);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Support");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        allinAllController = new AllinAllController(UserSupportActivity.this, this);
        allinallSharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        signoutButton      = (Button) findViewById(R.id.fragment_user_support_signout_button);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });


    }



    private void showConfirmDialog() {
        new android.support.v7.app.AlertDialog.Builder(UserSupportActivity.this)
                //set message, title, and icon
                .setTitle("Sign out")
                .setMessage("Do you want to sign out?")
                .setIcon(R.drawable.logo01)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        allinAllController.userSignOut(RESTClient.ID);
                        SharedPreferences.Editor editor = allinallSharedPref.edit();
                        editor.putBoolean("firstLaunch", true);
                        editor.remove("userId");
                        editor.apply();
                        Intent service = new Intent(getApplicationContext(), CapPhoto.class);
                        stopService(service);
                        FirebaseAuth.getInstance().signOut();
                        RESTClient.ID = null;
                        Intent loginIntent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void sendServiceResult(String serviceResult) {

    }

    @Override
    public void requestFailed() {

    }
}
