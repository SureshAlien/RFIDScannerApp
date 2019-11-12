package com.example.myapplicationfirs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FetchDocument extends AppCompatActivity {

    private android.widget.Spinner myspinner ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_document);
        System.out.println("********Came inside onCreate FetchDocument ");


        myspinner = (android.widget.Spinner) findViewById(R.id.spinner_fetch_doc);
        set_doctypes_on_spinner();


    }

    private void set_doctypes_on_spinner(){
        System.out.println("********Came inside  FetchDocument  set_doctypes_on_spinner");
        String doctype_names[] = {"Serial No","Batch No","Employee"};
        ArrayAdapter<String> myadapter = new ArrayAdapter<>(FetchDocument.this,android.R.layout.simple_list_item_1,doctype_names);
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(myadapter);
    }
}
