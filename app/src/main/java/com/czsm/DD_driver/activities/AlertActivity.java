package com.czsm.DD_driver.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.czsm.DD_driver.R;

public class AlertActivity extends AppCompatActivity {
TextView AcceptTxt,CancelTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userreqalert);
        AcceptTxt=(TextView)findViewById(R.id.accept_button);
        CancelTxt=(TextView)findViewById(R.id.cancel_button);
        AcceptTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent next=new Intent(AlertActivity.this,CurrentReqActivity.class);
               startActivity(next);
            }
        });
        CancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AlertActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

