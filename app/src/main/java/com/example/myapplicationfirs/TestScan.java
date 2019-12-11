package com.example.myapplicationfirs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestScan extends AppCompatActivity {

    private Button btnScanQr;
    public static EditText editRfid1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        editRfid1 = (EditText)findViewById(R.id.editScannedData) ;
        btnScanQr = (Button)findViewById(R.id.btnScanQr);
    }

    public void sendMessage(View view) {

        Intent startUtilitiesActivity = new Intent(TestScan.this,BarCodeScanning.class);
        startActivity(startUtilitiesActivity);


    }

    }
