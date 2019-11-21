package com.example.myapplicationfirs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.*;


public class UtilityScreen extends AppCompatActivity implements View.OnClickListener {

    private Button btnAssociateUtility;
    JSONObject permitted_doctype_data ;



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
                builder.setMessage("What Document do you want to Associate the RFID Tag with?")
                        .setView(view)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selected_doctype = doc_name_spinner.getSelectedItem().toString();
                                startAssocistaionScanningActivity( selected_doctype );

                            }
                        }).setNegativeButton("Cancel",null)
                        .setCancelable(false);

                AlertDialog alert = builder.show();
        }
    }

    private void set_doctypes_on_spinner(final Spinner doc_name_spinner  ) {

        RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        //String url1 = "http://192.168.0.15/api/method/nhance.rfid_android_api.get_permitted_doctypes";
        String url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.GET_PERMITTED_DOCTYPE_DATA);
        System.out.println("***************Enters fetch_permitted_doctypes, url :::: "+ url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("***************From  get_rfid_details_ac_doc_number  response : "+response );
                        set_permitted_doctype_data(response);
                        try{

                            JSONArray jsonArray = response.getJSONArray("message");
                            String temp_pemitted_doctypes[]= new String[jsonArray.length()];


                            if (jsonArray.length() != 0 ){  //valid doc no
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject objects = jsonArray.getJSONObject(i);
                                    String doctype = objects.getString("permitted_doctype");
                                    temp_pemitted_doctypes[i] = doctype ;
                                }

                                ArrayAdapter<String> myadapter = new ArrayAdapter<>(UtilityScreen.this,android.R.layout.simple_list_item_1,temp_pemitted_doctypes);
                                myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                doc_name_spinner.setAdapter(myadapter);
                            }
                            else{ //pemitted_doctypes  has not been configured case
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("***************From  fetch_permitted_doctypes  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return UtilityScreen.this.getHeaders();
            }
        };
        requestQueue1.add(JsonRequest);

    }
    public Map<String, String> getHeaders () {
        Map<String, String> headers = new HashMap<>();
        SharedPreferences prefs = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(Constants.USER_ID, null);
        String sid = prefs.getString(Constants.SESSION_ID, null);
        headers.put("user_id", userId);
        headers.put("sid", sid);

        System.out.println("Suresh ************ From Home userId : "+ userId);
        System.out.println("Suresh ************ From Home sid : "+ sid);
        return headers;
    }

    private void set_permitted_doctype_data(JSONObject temp_permitted_doctype_data  ) {
        permitted_doctype_data =  temp_permitted_doctype_data;
    }

    public void startAssocistaionScanningActivity( String selected_doctype )
    {
        Intent startAssocistaionScanningActivity = new Intent(UtilityScreen.this,MeritUHF.class);
        startAssocistaionScanningActivity.putExtra("selected_doctype",selected_doctype) ;
        startAssocistaionScanningActivity.putExtra("permitted_doctype_data",permitted_doctype_data.toString()) ;

        startActivity(startAssocistaionScanningActivity);
    }

}

/*
public void transferIT(View view){
String value = ed1.getText().toString()
Intent intent = new Intent(this, Page.class);
intent.putExtra("key",value);
startActivity(intent);
}

Then in onCreate of second activity

String value = getIntent().getExtras().getString("key");
 */

