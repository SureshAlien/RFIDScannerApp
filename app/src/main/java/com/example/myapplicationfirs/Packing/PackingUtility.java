package com.example.myapplicationfirs.Packing;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.myapplicationfirs.R;
import com.example.myapplicationfirs.SiPbDetails;
import com.example.myapplicationfirs.UtilityScreen;
import com.example.myapplicationfirs.utils.Constants;

public class PackingUtility extends AppCompatActivity implements View.OnClickListener {

    private Button btnReceiveStartNewUtility;
    private Button btnMoveUtility;
    private Button btnPackUtility;
    private Button btnGrabUtility;
    private Button btnScanUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_utility);

        btnReceiveStartNewUtility = (Button)findViewById(R.id.btnReceiveStartNewUtility);
        btnReceiveStartNewUtility.setOnClickListener(this);

        btnMoveUtility = (Button)findViewById(R.id.btnMoveUtility);
        btnMoveUtility.setOnClickListener(this);

        btnPackUtility = (Button)findViewById(R.id.btnPackUtility);
        btnPackUtility.setOnClickListener(this);

        btnGrabUtility = (Button)findViewById(R.id.btnGrabUtility);
        btnGrabUtility.setOnClickListener(this);

        btnScanUtility = (Button)findViewById(R.id.btnScanUtility);
        btnScanUtility.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReceiveStartNewUtility :
                System.out.println("su_debug Packing details button pressed");
                ReceiveStartNewUtility_btn_action();

            case R.id.btnMoveUtility :
                System.out.println("su_debug btn_si_pipb_details button pressed");

            case R.id.btnPackUtility :
                System.out.println("su_debug Packing details button pressed");

            case R.id.btnGrabUtility :
                System.out.println("su_debug btn_si_pipb_details button pressed");

            case R.id.btnScanUtility :
                System.out.println("su_debug Packing details button pressed");
                
        }

    }

    private void ReceiveStartNewUtility_btn_action() {
        System.out.println("su_debug btnAssociateUtility button pressed");
        View view = LayoutInflater.from(PackingUtility.this).inflate(R.layout.activity_fetch_document,null);

        final Spinner doc_name_spinner = (Spinner) view.findViewById(R.id.spinner_fetch_doc);
        String[] packing_doctypes = {"Packing Item", "Packing Box"};

        ArrayAdapter<String> myadapter = new ArrayAdapter<>(PackingUtility.this,android.R.layout.simple_list_item_1,packing_doctypes);

        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doc_name_spinner.setAdapter(myadapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(PackingUtility.this);
        builder.setMessage(Constants.ASSOCIATE_UTILTY_DIALOG_MESSAGE)
                .setView(view)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected_doctype = doc_name_spinner.getSelectedItem().toString();
                        startSelectedPAckingActivity( selected_doctype );
                    }

                }).setNegativeButton("Cancel",null)
                .setCancelable(false);

        AlertDialog alert = builder.show();
    }

    private void startSelectedPAckingActivity(String selected_doctype) {
        if(selected_doctype.equals( "Packing Item" )){
            Intent SiDetailsActivity = new Intent(PackingUtility.this, SiPbDetails.class);
            startActivity(SiDetailsActivity);
        }
        else  if(selected_doctype.equals( "Packing Box" )){
            Intent SiDetailsActivity = new Intent(PackingUtility.this, SiPbDetails.class);
            startActivity(SiDetailsActivity);
        }

    }
}