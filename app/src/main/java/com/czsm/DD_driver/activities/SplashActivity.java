package com.czsm.DD_driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.czsm.DD_driver.PreferencesHelper;
import com.czsm.DD_driver.R;

public class SplashActivity extends AppCompatActivity {
    Window mWindow;
    ImageView topSlice,BtmSlice,logoImg,TextImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        topSlice=(ImageView)findViewById(R.id.top_img) ;
        BtmSlice=(ImageView)findViewById(R.id.btm_img) ;
        logoImg=(ImageView)findViewById(R.id.logo_img) ;
        TextImg=(ImageView)findViewById(R.id.txt_img) ;
        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //starts main activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isLoggedIn = PreferencesHelper.getPreferenceBoolean(SplashActivity.this, PreferencesHelper.PREFERENCE_LOGGED_IN);
                if(isLoggedIn )
                {
//                    boolean isDashboard = PreferencesHelper.getPreferenceBoolean(SplashActivity.this, PreferencesHelper.PREFERENCE_DASHBOARD);
//                    if(isDashboard){
                        Intent indashboard=new Intent(SplashActivity.this,DashBoardActivity.class);
                        indashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(indashboard);
                        finish();

//                    }
//                    else{
//                        Intent inservice=new Intent(SplashActivity.this,ServiceProviderActivity.class);
//                        inservice.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(inservice);
//                        finish();
//                    }


                }
                else {
                    Intent in=new Intent(SplashActivity.this,LoginScreenActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    finish();
                }
//                Toast.makeText(SplashActivity.this, "haii", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }
}
