package com.example.myapplicationfirs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class UtilityScreen extends AppCompatActivity implements View.OnClickListener {

    private Button btnAssociateUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_screen);
        System.out.println("********Came inside onCreate ");

        btnAssociateUtility = (Button)findViewById(R.id.btnAssociateUtility);
        btnAssociateUtility.setOnClickListener(this);
        System.out.println("********Came inside onClick ");
    }

    public void onClick(View v) {
        System.out.println("********Came inside onClick ");

        switch (v.getId()) {
            case R.id.btnAssociateUtility :
                Toast.makeText(UtilityScreen.this, "You have clicked Scan Button" ,Toast.LENGTH_SHORT).show();

                System.out.println("********Suresh clicked button");

                View view = LayoutInflater.from(UtilityScreen.this).inflate(R.layout.activity_fetch_document,null);

                final Spinner doc_name_spinner = (Spinner) view.findViewById(R.id.spinner_fetch_doc);
                set_doctypes_on_spinner( doc_name_spinner );


                AlertDialog.Builder builder = new AlertDialog.Builder(UtilityScreen.this);
                builder.setMessage("Please Select the Doctype")
                        .setView(view)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("Donne with custom dialog box");

                            }
                        }).setNegativeButton("Cancel",null)
                        .setCancelable(false);

                AlertDialog alert = builder.show();
        }
    }

    private void set_doctypes_on_spinner( Spinner doc_name_spinner ){
        System.out.println("********Came inside  FetchDocument  set_doctypes_on_spinner");
        String doctype_names[] = {"Serial No","Batch No","Employee"};
        ArrayAdapter<String> myadapter = new ArrayAdapter<>(UtilityScreen.this,android.R.layout.simple_list_item_1,doctype_names);
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doc_name_spinner.setAdapter(myadapter);
    }

}
