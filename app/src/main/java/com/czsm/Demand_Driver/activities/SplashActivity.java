package com.czsm.Demand_Driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.czsm.Demand_Driver.R;

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
//                Toast.makeText(SplashActivity.this, "haii", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this,LoginScreenActivity.class));
                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
