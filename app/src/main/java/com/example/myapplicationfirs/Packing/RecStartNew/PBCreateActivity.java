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
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class PBCreateActivity extends AppCompatActivity implements View.OnClickListener {

    int pitems_count;
    private ProgressDialog progressDialog;


    private LinearLayout linearlayout_pi1 ;
    private  LinearLayout linearlayout_pi2 ;
    private  LinearLayout linearlayout_pi3 ;
    private  LinearLayout linearlayout_pi4 ;
    private  LinearLayout linearlayout_pi5 ;

    private TextView pi_item1;
    private TextView pi_item2;
    private TextView pi_item3;
    private TextView pi_item4;
    private TextView pi_item5;

    private TextView pi_item1_actual_qty;
    private TextView pi_item2_actual_qty;
    private TextView pi_item3_actual_qty;
    private TextView pi_item4_actual_qty;
    private TextView pi_item5_actual_qty;

    private EditText pi_item1_accept_qty;
    private EditText pi_item2_accept_qty;
    private EditText pi_item3_accept_qty;
    private EditText pi_item4_accept_qty;
    private EditText pi_item5_accept_qty;

    private EditText pb_item_code_entry;
    private EditText parent_item_code_entry;
    private EditText wh_entry;
    private EditText rarb_entry;


    private Button btn_get_packing_items;
    private  Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_b_create);

        initview();

        btn_get_packing_items.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                showProgress();
                btn_get_packing_items_action();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
               showProgress();
               btn_submit_action();
            }
        });
    }

    private void initview() {
        linearlayout_pi1 = (LinearLayout) findViewById(R.id.linearlayout_pi1);
        linearlayout_pi2 = (LinearLayout) findViewById(R.id.linearlayout_pi2);
        linearlayout_pi3 = (LinearLayout) findViewById(R.id.linearlayout_pi3);
        linearlayout_pi4 = (LinearLayout) findViewById(R.id.linearlayout_pi4);
        linearlayout_pi5 = (LinearLayout) findViewById(R.id.linearlayout_pi5);
        linearlayout_pi1.setVisibility(View.INVISIBLE);
        linearlayout_pi2.setVisibility(View.INVISIBLE);
        linearlayout_pi3.setVisibility(View.INVISIBLE);
        linearlayout_pi4.setVisibility(View.INVISIBLE);
        linearlayout_pi5.setVisibility(View.INVISIBLE);

        pi_item1_accept_qty = (EditText) findViewById(R.id.pi_item1_accept_qty);
        pi_item2_accept_qty = (EditText) findViewById(R.id.pi_item2_accept_qty);
        pi_item3_accept_qty = (EditText) findViewById(R.id.pi_item3_accept_qty);
        pi_item4_accept_qty = (EditText) findViewById(R.id.pi_item4_accept_qty);
        pi_item5_accept_qty = (EditText) findViewById(R.id.pi_item5_accept_qty);

        pb_item_code_entry = (EditText) findViewById(R.id.pb_item_code_entry);
        parent_item_code_entry = (EditText) findViewById(R.id.parent_item_code_entry_pb);
        wh_entry = (EditText) findViewById(R.id.wh_entry_pb);
        rarb_entry = (EditText) findViewById(R.id.rarb_entry_pb);

        pi_item1 = (TextView)findViewById(R.id.pi_item1);
        pi_item2 = (TextView)findViewById(R.id.pi_item2);
        pi_item3 = (TextView)findViewById(R.id.pi_item3);
        pi_item4 = (TextView)findViewById(R.id.pi_item4);
        pi_item5 = (TextView)findViewById(R.id.pi_item5);

        pi_item1_actual_qty = (TextView)findViewById(R.id.pi_item1_actual_qty);
        pi_item2_actual_qty = (TextView)findViewById(R.id.pi_item2_actual_qty);
        pi_item3_actual_qty = (TextView)findViewById(R.id.pi_item3_actual_qty);
        pi_item4_actual_qty = (TextView)findViewById(R.id.pi_item4_actual_qty);
        pi_item5_actual_qty = (TextView)findViewById(R.id.pi_item5_actual_qty);

        btn_get_packing_items = (Button)findViewById(R.id.btn_get_packing_items);
        btn_get_packing_items.setOnClickListener(this);

        btn_submit = (Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
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
    private void btn_submit_action() {
        String parent_item = parent_item_code_entry.getText().toString();
        String wh_entry_str = wh_entry.getText().toString();
        String rarb_entry_str = rarb_entry.getText().toString();
        String box_name = pb_item_code_entry.getText().toString();

        try {
            JSONObject total_items_json = new JSONObject();
            //items_json_array.add("one");


            if (pitems_count == 1){
                JSONObject item_json1 = new JSONObject();
                item_json1.put("packing_item",pi_item1.getText().toString());
                item_json1.put("actual_qty",pi_item1_actual_qty.getText().toString());
                item_json1.put("received_qty",pi_item1_accept_qty.getText().toString());

                total_items_json.put("item1",item_json1);
            }
            if (pitems_count == 2){
                JSONObject item_json1 = new JSONObject();
                item_json1.put("packing_item",pi_item1.getText().toString());
                item_json1.put("actual_qty",pi_item1_actual_qty.getText().toString());
                item_json1.put("received_qty",pi_item1_accept_qty.getText().toString());

                JSONObject item_json2 = new JSONObject();
                item_json2.put("packing_item",pi_item2.getText().toString());
                item_json2.put("actual_qty",pi_item2_actual_qty.getText().toString());
                item_json2.put("received_qty",pi_item2_accept_qty.getText().toString());

                total_items_json.put("item1",item_json1);
                total_items_json.put("item2",item_json2);

            }

            if (pitems_count == 3){
                JSONObject item_json1 = new JSONObject();
                item_json1.put("packing_item",pi_item1.getText().toString());
                item_json1.put("actual_qty",pi_item1_actual_qty.getText().toString());
                item_json1.put("received_qty",pi_item1_accept_qty.getText().toString());

                JSONObject item_json2 = new JSONObject();
                item_json2.put("packing_item",pi_item2.getText().toString());
                item_json2.put("actual_qty",pi_item2_actual_qty.getText().toString());
                item_json2.put("received_qty",pi_item2_accept_qty.getText().toString());

                JSONObject item_json3 = new JSONObject();
                item_json3.put("packing_item",pi_item3.getText().toString());
                item_json3.put("actual_qty",pi_item3_actual_qty.getText().toString());
                item_json3.put("received_qty",pi_item3_accept_qty.getText().toString());

                total_items_json.put("item1",item_json1);
                total_items_json.put("item2",item_json2);
                total_items_json.put("item3",item_json3);
            }


            if (pitems_count == 4){
                JSONObject item_json1 = new JSONObject();
                item_json1.put("packing_item",pi_item1.getText().toString());
                item_json1.put("actual_qty",pi_item1_actual_qty.getText().toString());
                item_json1.put("received_qty",pi_item1_accept_qty.getText().toString());

                JSONObject item_json2 = new JSONObject();
                item_json2.put("packing_item",pi_item2.getText().toString());
                item_json2.put("actual_qty",pi_item2_actual_qty.getText().toString());
                item_json2.put("received_qty",pi_item2_accept_qty.getText().toString());

                JSONObject item_json3 = new JSONObject();
                item_json3.put("packing_item",pi_item3.getText().toString());
                item_json3.put("actual_qty",pi_item3_actual_qty.getText().toString());
                item_json3.put("received_qty",pi_item3_accept_qty.getText().toString());

                JSONObject item_json4 = new JSONObject();
                item_json4.put("packing_item",pi_item4.getText().toString());
                item_json4.put("actual_qty",pi_item4_actual_qty.getText().toString());
                item_json4.put("received_qty",pi_item4_accept_qty.getText().toString());

                total_items_json.put("item1",item_json1);
                total_items_json.put("item2",item_json2);
                total_items_json.put("item3",item_json3);
                total_items_json.put("item4",item_json4);
            }


            if (pitems_count == 5){
                JSONObject item_json1 = new JSONObject();
                item_json1.put("packing_item",pi_item1.getText().toString());
                item_json1.put("actual_qty",pi_item1_actual_qty.getText().toString());
                item_json1.put("received_qty",pi_item1_accept_qty.getText().toString());

                JSONObject item_json2 = new JSONObject();
                item_json2.put("packing_item",pi_item2.getText().toString());
                item_json2.put("actual_qty",pi_item2_actual_qty.getText().toString());
                item_json2.put("received_qty",pi_item2_accept_qty.getText().toString());

                JSONObject item_json3 = new JSONObject();
                item_json3.put("packing_item",pi_item3.getText().toString());
                item_json3.put("actual_qty",pi_item3_actual_qty.getText().toString());
                item_json3.put("received_qty",pi_item3_accept_qty.getText().toString());

                JSONObject item_json4 = new JSONObject();
                item_json4.put("packing_item",pi_item4.getText().toString());
                item_json4.put("actual_qty",pi_item4_actual_qty.getText().toString());
                item_json4.put("received_qty",pi_item4_accept_qty.getText().toString());

                JSONObject item_json5 = new JSONObject();
                item_json5.put("packing_item",pi_item5.getText().toString());
                item_json5.put("actual_qty",pi_item5_actual_qty.getText().toString());
                item_json5.put("received_qty",pi_item5_accept_qty.getText().toString());

                total_items_json.put("item1",item_json1);
                total_items_json.put("item2",item_json2);
                total_items_json.put("item3",item_json3);
                total_items_json.put("item4",item_json4);
                total_items_json.put("item5",item_json5);
            }

            JSONObject entity = new JSONObject();
            entity.put("parent_item",parent_item);
            entity.put("current_warehouse",wh_entry_str);
            entity.put("current_rarb_id",rarb_entry_str);
            entity.put("packing_box",box_name);
            entity.put("items_tab1", total_items_json);

            //Request Started
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            Map<String, String> create_pb = new HashMap<>();
            create_pb.put("entity", String.valueOf(entity));

            String create_pb_doc_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, create_pb, CustomUrl.CREATE_PB_DOC);
            //System.out.println("***** From btn_submit_action get_packing_items_url" + create_pb_doc_url );

            System.out.println("***** su_debug from btn_submit_action before request");

            JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, create_pb_doc_url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                hideProgress();
                                System.out.println("*****from btn_submit_action   : response :" + response.toString());
                                JSONArray pbox__array = response.getJSONArray("message");

                                String dialog_message = "";
                                String dialog_title = " ";

                                if( pbox__array.length() > 0 ){
                                    dialog_title = "Successfully created  following Packing Box";
                                    for (int i = 0; i < pbox__array.length(); i++) {
                                        dialog_message += pbox__array.getString(i) + ",";
                                    }
                                }
                                else{
                                    dialog_message += "Failed to create Packing Box" ;
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
                            System.out.println("***************From  btn_submit_action  error : " + error);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    return PBCreateActivity.this.getHeaders_one();
                }
            };

            JsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(JsonRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void btn_get_packing_items_action() {
        System.out.println("***** came inside" );

        String parent_item = parent_item_code_entry.getText().toString();
        String wh_entry_str = wh_entry.getText().toString();
        String rarb_entry_str = rarb_entry.getText().toString();
        String box_name = pb_item_code_entry.getText().toString();

        //Request Started
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> get_packing_items_map = new HashMap<>();
        get_packing_items_map.put("box_name", box_name);
        get_packing_items_map.put("parent_item", parent_item);

        String get_packing_items_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, get_packing_items_map, CustomUrl.GET_PITEMS_AC_TO_BOX_NAME);
        System.out.println("***** From btn_get_packing_items_action get_packing_items_url" + get_packing_items_url );

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, get_packing_items_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            hideProgress();
                            System.out.println("*****from btn_get_packing_items_action   : response :" + response.toString());
                            JSONArray pitems__array = response.getJSONArray("message");
                            if( pitems__array.length() > 0 ){
                                set_pi_items_view(pitems__array);
                            }
                            else{
                                String dialog_message = "";
                                String dialog_title = "Please Enter Valid Details";

                                dialog_message_box_display(dialog_message, dialog_title);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgress();
                        System.out.println("***************From  btn_get_packing_items_action  error : " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PBCreateActivity.this.getHeaders_one();
            }
        };



        requestQueue.add(JsonRequest);
    }

    private void set_pi_items_view(JSONArray pitems__array) throws JSONException {
        pitems_count = pitems__array.length();
        pitems_count = pitems__array.length();
        if (pitems_count == 1){
            linearlayout_pi1.setVisibility(View.VISIBLE);
            pi_item1.setText(pitems__array.getJSONObject(0).getString("pitem"));
            pi_item1_actual_qty.setText(pitems__array.getJSONObject(0).getString("pitem_qty"));
        }
        if (pitems_count == 2){
            linearlayout_pi1.setVisibility(View.VISIBLE);
            pi_item1.setText(pitems__array.getJSONObject(0).getString("pitem"));
            pi_item1_actual_qty.setText(pitems__array.getJSONObject(0).getString("pitem_qty"));

            linearlayout_pi2.setVisibility(View.VISIBLE);
            pi_item2.setText(pitems__array.getJSONObject(1).getString("pitem"));
            pi_item2_actual_qty.setText(pitems__array.getJSONObject(1).getString("pitem_qty"));
        }

        if (pitems_count == 3){
            linearlayout_pi1.setVisibility(View.VISIBLE);
            pi_item1.setText(pitems__array.getJSONObject(0).getString("pitem"));
            pi_item1_actual_qty.setText(pitems__array.getJSONObject(0).getString("pitem_qty"));

            linearlayout_pi2.setVisibility(View.VISIBLE);
            pi_item2.setText(pitems__array.getJSONObject(1).getString("pitem"));
            pi_item2_actual_qty.setText(pitems__array.getJSONObject(1).getString("pitem_qty"));

            linearlayout_pi3.setVisibility(View.VISIBLE);
            pi_item3.setText(pitems__array.getJSONObject(2).getString("pitem"));
            pi_item3_actual_qty.setText(pitems__array.getJSONObject(2).getString("pitem_qty"));
        }

        if (pitems_count == 4){
            linearlayout_pi1.setVisibility(View.VISIBLE);
            pi_item1.setText(pitems__array.getJSONObject(0).getString("pitem"));
            pi_item1_actual_qty.setText(pitems__array.getJSONObject(0).getString("pitem_qty"));

            linearlayout_pi2.setVisibility(View.VISIBLE);
            pi_item2.setText(pitems__array.getJSONObject(1).getString("pitem"));
            pi_item2_actual_qty.setText(pitems__array.getJSONObject(1).getString("pitem_qty"));

            linearlayout_pi3.setVisibility(View.VISIBLE);
            pi_item3.setText(pitems__array.getJSONObject(2).getString("pitem"));
            pi_item3_actual_qty.setText(pitems__array.getJSONObject(2).getString("pitem_qty"));

            linearlayout_pi4.setVisibility(View.VISIBLE);
            pi_item4.setText(pitems__array.getJSONObject(3).getString("pitem"));
            pi_item4_actual_qty.setText(pitems__array.getJSONObject(3).getString("pitem_qty"));
        }
        if (pitems_count == 5){
            linearlayout_pi1.setVisibility(View.VISIBLE);
            pi_item1.setText(pitems__array.getJSONObject(0).getString("pitem"));
            pi_item1_actual_qty.setText(pitems__array.getJSONObject(0).getString("pitem_qty"));

            linearlayout_pi2.setVisibility(View.VISIBLE);
            pi_item2.setText(pitems__array.getJSONObject(1).getString("pitem"));
            pi_item2_actual_qty.setText(pitems__array.getJSONObject(1).getString("pitem_qty"));

            linearlayout_pi3.setVisibility(View.VISIBLE);
            pi_item3.setText(pitems__array.getJSONObject(2).getString("pitem"));
            pi_item3_actual_qty.setText(pitems__array.getJSONObject(2).getString("pitem_qty"));

            linearlayout_pi4.setVisibility(View.VISIBLE);
            pi_item4.setText(pitems__array.getJSONObject(3).getString("pitem"));
            pi_item4_actual_qty.setText(pitems__array.getJSONObject(3).getString("pitem_qty"));

            linearlayout_pi5.setVisibility(View.VISIBLE);
            pi_item5.setText(pitems__array.getJSONObject(4).getString("pitem"));
            pi_item5_actual_qty.setText(pitems__array.getJSONObject(4).getString("pitem_qty"));
        }
    }



    @Override
    public void onClick(View view) {

    }
    public void dialog_message_box_display(String dialog_message, String dialog_title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PBCreateActivity.this);

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
