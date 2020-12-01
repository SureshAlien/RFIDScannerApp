package com.example.myapplicationfirs.Packing.RecStartNew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationfirs.R;
import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PICreateActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private Button btn_submit;

    private EditText item_code_entry;
    private EditText qty_entry;
    private EditText uom_entry;
    private EditText parent_item_code_entry;
    private EditText wh_entry;
    private EditText rarb_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_i_create);

        item_code_entry = (EditText) findViewById(R.id.item_code_entry);
        qty_entry = (EditText) findViewById(R.id.qty_entry);
        uom_entry = (EditText) findViewById(R.id.uom_entry);
        parent_item_code_entry = (EditText) findViewById(R.id.parent_item_code_entry);
        wh_entry = (EditText) findViewById(R.id.wh_entry);
        rarb_entry = (EditText) findViewById(R.id.rarb_entry);

        btn_submit = (Button) findViewById(R.id.btn_submit);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                showProgress();
                create_pi_doc(
                        item_code_entry.getText().toString(),
                        qty_entry.getText().toString(),
                        uom_entry.getText().toString(),
                        parent_item_code_entry.getText().toString(),
                        wh_entry.getText().toString(),
                        rarb_entry.getText().toString()
                );

            }
        });
    }

    private void create_pi_doc(String item_code_entry, String qty_entry, String uom_entry, String parent_item_code_entry, String wh_entry, String rarb_entry) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> create_pi_doc_map = new HashMap<>();
        create_pi_doc_map.put("item_code_entry", item_code_entry);
        create_pi_doc_map.put("qty_entry", qty_entry);
        create_pi_doc_map.put("uom_entry", uom_entry);
        create_pi_doc_map.put("parent_item_code_entry", parent_item_code_entry);
        create_pi_doc_map.put("wh_entry", wh_entry);
        create_pi_doc_map.put("rarb_entry", rarb_entry);

        String create_pi_doc_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, create_pi_doc_map, CustomUrl.CREATE_PI_DOC);
        System.out.println("***** From set_pi_pb_details create_pi_doc_url" + create_pi_doc_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, create_pi_doc_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            hideProgress();
                            System.out.println("*****from create_pi_doc   : response :" + response.toString());

                            JSONArray ser_no_array = response.getJSONArray("message");

                            String dialog_message = "";
                            String dialog_title = " ";

                            if( ser_no_array.length() > 0 ){
                                 dialog_title = "Successfully created  following Packing Items";
                                for (int i = 0; i < ser_no_array.length(); i++) {
                                    dialog_message += ser_no_array.getString(i) + ",";
                                }
                            }
                            else{
                                dialog_title = "Please Enter Valid Details";
                            }

                            dialog_message_box_display(dialog_message, dialog_title);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgress();
                        System.out.println("***************From  create_pi_doc  error : " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PICreateActivity.this.getHeaders_one();
            }
        };

        JsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(JsonRequest);
    }


    public Map<String, String> getHeaders_one() {
        Map<String, String> headers = new HashMap<>();
        SharedPreferences prefs = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(Constants.USER_ID, null);
        String sid = prefs.getString(Constants.SESSION_ID, null);
        headers.put("user_id", userId);
        headers.put("sid", sid);
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.progress_dialog_message));
        // progressDialog.setProgressStyle(R.style.ProgressBar);

        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void dialog_message_box_display(String dialog_message, String dialog_title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PICreateActivity.this);

        builder.setMessage(dialog_message).setTitle(dialog_title)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("*************************** Dialog box  Ok clicked**************************************");
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}

