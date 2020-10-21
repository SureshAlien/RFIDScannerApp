package com.example.myapplicationfirs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class SiPbDetails extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearlayout_box1 ;
    private  LinearLayout linearlayout_box2 ;
    private  LinearLayout linearlayout_box3 ;
    private  LinearLayout linearlayout_box4 ;
    private  LinearLayout linearlayout_box5 ;

    private TextView box_id1;
    private TextView box_id2;
    private TextView box_id3;
    private TextView box_id4;
    private TextView box_id5;

    private TextView status1;
    private TextView status2;
    private TextView status3;
    private TextView status4;
    private TextView status5;

    private  Button btn_box1;
    private  Button btn_box2;
    private  Button btn_box3;
    private  Button btn_box4;
    private  Button btn_box5;
    private  Button btn_get_p_details;
    private  Button btn_scan_qr_bar;

    private EditText editDocNo;

    JSONArray si_box_list_array ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si_pb_details);
        initview();
    }

    private void initview() {
        linearlayout_box1 = (LinearLayout) findViewById(R.id.linearlayout_box1);
        linearlayout_box2 = (LinearLayout) findViewById(R.id.linearlayout_box2);
        linearlayout_box3 = (LinearLayout) findViewById(R.id.linearlayout_box3);
        linearlayout_box4 = (LinearLayout) findViewById(R.id.linearlayout_box4);
        linearlayout_box5 = (LinearLayout) findViewById(R.id.linearlayout_box5);
        linearlayout_box1.setVisibility(View.INVISIBLE);
        linearlayout_box2.setVisibility(View.INVISIBLE);
        linearlayout_box3.setVisibility(View.INVISIBLE);
        linearlayout_box4.setVisibility(View.INVISIBLE);
        linearlayout_box5.setVisibility(View.INVISIBLE);

        editDocNo = (EditText) findViewById(R.id.editDocNo);

        box_id1 = (TextView)   findViewById(R.id.box_id1);
        box_id2 = (TextView)   findViewById(R.id.box_id2);
        box_id3 = (TextView)   findViewById(R.id.box_id3);
        box_id4 = (TextView)   findViewById(R.id.box_id4);
        box_id5 = (TextView)   findViewById(R.id.box_id5);

        status1 = (TextView)   findViewById(R.id.status1);
        status2 = (TextView)   findViewById(R.id.status2);
        status3 = (TextView)   findViewById(R.id.status3);
        status4 = (TextView)   findViewById(R.id.status4);
        status5 = (TextView)   findViewById(R.id.status5);

        btn_box1 = (Button)findViewById(R.id.btn_box1);
        btn_box1.setOnClickListener(this);

        btn_box2 = (Button)findViewById(R.id.btn_box2);
        btn_box2.setOnClickListener(this);

        btn_box3 = (Button)findViewById(R.id.btn_box3);
        btn_box3.setOnClickListener(this);

        btn_box4 = (Button)findViewById(R.id.btn_box4);
        btn_box4.setOnClickListener(this);

        btn_box5 = (Button)findViewById(R.id.btn_box5);
        btn_box5.setOnClickListener(this);

        btn_get_p_details = (Button)findViewById(R.id.btn_get_p_details);
        btn_get_p_details.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_get_p_details:
                String doc_id = editDocNo.getText().toString() ;
                set_pb_details( doc_id );
                break;

            case R.id.btn_make_dn:
                String doc_id_temp = editDocNo.getText().toString() ;
                //craete_delivery_note(doc_id_temp) ;
                break;

            case R.id.btn_box1:
                String box_name = box_id1.getText().toString();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SiPbDetails.this);
                builderSingle.setTitle(box_name);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SiPbDetails.this, android.R.layout.select_dialog_singlechoice);

                try {
                   JSONObject box1_json_obj = si_box_list_array.getJSONObject(0);
                    for(int i = 0; i < si_box_list_array.length(); i++) {
                        JSONObject box_pi_obj = si_box_list_array.getJSONObject(i);
                        if(box_name.equals(box_pi_obj.getString("box_name_to_display"))){

                            JSONArray box_pi_name_array  = box_pi_obj.getJSONArray("box_pi_details_list");
                            for(int j = 0; j < si_box_list_array.length(); j++) {
                                String pi_name = box_pi_name_array.getJSONObject(j).getString("pi_name_to_display");
                                String display_qty = box_pi_name_array.getJSONObject(j).getString("display_qty");
                                int pi_actual_qty;
                                String pi_display;
                                if(display_qty.equals("Yes")){
                                    pi_actual_qty = box_pi_name_array.getJSONObject(j).getInt("pi_actual_qty");
                                    pi_display = pi_name +"-"+ String.valueOf(pi_actual_qty) ;
                                }else{
                                    String pi_wh_name = box_pi_name_array.getJSONObject(j).getString("pi_warehouse");
                                    String pi_rarb = box_pi_name_array.getJSONObject(j).getString("pi_rarb");
                                    pi_display = pi_name +"-"+ pi_wh_name +"("+pi_rarb+ ")" ;

                                }
                                arrayAdapter.add(pi_display);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(SiPbDetails.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
                break;

            case R.id.btn_box2:
                String box_name2 = box_id2.getText().toString();
                AlertDialog.Builder builderSingle2 = new AlertDialog.Builder(SiPbDetails.this);
                builderSingle2.setTitle(box_name2);
                final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(SiPbDetails.this, android.R.layout.select_dialog_singlechoice);

                try {
                    JSONObject box1_json_obj = si_box_list_array.getJSONObject(0);
                    for(int i = 0; i < si_box_list_array.length(); i++) {
                        JSONObject box_pi_obj = si_box_list_array.getJSONObject(i);
                        if(box_name2.equals(box_pi_obj.getString("box_name_to_display"))){

                            JSONArray box_pi_name_array  = box_pi_obj.getJSONArray("box_pi_details_list");
                            for(int j = 0; j < si_box_list_array.length(); j++) {
                                String pi_name = box_pi_name_array.getJSONObject(j).getString("pi_name_to_display");
                                String display_qty = box_pi_name_array.getJSONObject(j).getString("display_qty");
                                int pi_actual_qty;
                                String pi_display;
                                if(display_qty.equals("Yes")){
                                    pi_actual_qty = box_pi_name_array.getJSONObject(j).getInt("pi_actual_qty");
                                    pi_display = pi_name +"-"+ String.valueOf(pi_actual_qty) ;
                                }else{
                                    String pi_wh_name = box_pi_name_array.getJSONObject(j).getString("pi_warehouse");
                                    String pi_rarb = box_pi_name_array.getJSONObject(j).getString("pi_rarb");
                                    pi_display = pi_name +"-"+ pi_wh_name +"("+pi_rarb+ ")" ;

                                }
                                arrayAdapter2.add(pi_display);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                builderSingle2.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle2.setAdapter(arrayAdapter2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter2.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(SiPbDetails.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle2.show();
                break;
            case R.id.btn_box3:
                break;
            case R.id.btn_box4:
                break;
            case R.id.btn_box5:
                break;

        }

    }

    private void set_pb_details(String doc_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> fetch_si_pipb_details_map = new HashMap<>();
        fetch_si_pipb_details_map.put("doc_id",doc_id);

        String fetch_si_pipb_details_siscreen_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, fetch_si_pipb_details_map, CustomUrl.FETCH_SI_PIPB_DETAILS_SISCREEN);
        System.out.println("***** From set_pi_pb_details fetch_si_pipb_details_siscreen_url"+fetch_si_pipb_details_siscreen_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, fetch_si_pipb_details_siscreen_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println("*****from set_pb_details   : response :"+response.toString());

                            JSONObject stat = response.getJSONObject("message");

                            System.out.println("***** From set_pb_details  stat : "+stat );
                            boolean isValid = stat.getBoolean("isValid");


                            if(isValid){ //valid si
                                int box_count = stat.getInt("stat");
                                JSONArray box_list_array = stat.getJSONArray("box_details_list");
                                set_box_list_array(box_list_array);

                                if (box_count == 1){
                                    linearlayout_box1.setVisibility(View.VISIBLE);


                                    box_id1.setText(box_list_array.getJSONObject(0).getString("box_name_to_display"));
                                    status1.setText(box_list_array.getJSONObject(0).getString("box_status"));

                                }
                                if (box_count == 2){
                                    linearlayout_box1.setVisibility(View.VISIBLE);
                                    linearlayout_box2.setVisibility(View.VISIBLE);


                                    box_id1.setText(box_list_array.getJSONObject(0).getString("box_name_to_display"));
                                    status1.setText(box_list_array.getJSONObject(0).getString("box_status"));

                                    box_id2.setText(box_list_array.getJSONObject(1).getString("box_name_to_display"));
                                    status2.setText(box_list_array.getJSONObject(1).getString("box_status"));
                                }
                                if (box_count == 3){
                                    linearlayout_box1.setVisibility(View.VISIBLE);
                                    linearlayout_box2.setVisibility(View.VISIBLE);
                                    linearlayout_box3.setVisibility(View.VISIBLE);


                                    box_id1.setText(box_list_array.getJSONObject(0).getString("box_name_to_display"));
                                    status1.setText(box_list_array.getJSONObject(0).getString("box_status"));

                                    box_id2.setText(box_list_array.getJSONObject(1).getString("box_name_to_display"));
                                    status2.setText(box_list_array.getJSONObject(1).getString("box_status"));

                                    box_id3.setText(box_list_array.getJSONObject(2).getString("box_name_to_display"));
                                    status3.setText(box_list_array.getJSONObject(2).getString("box_status"));
                                }
                                if (box_count == 4){
                                    linearlayout_box1.setVisibility(View.VISIBLE);
                                    linearlayout_box2.setVisibility(View.VISIBLE);
                                    linearlayout_box3.setVisibility(View.VISIBLE);
                                    linearlayout_box4.setVisibility(View.INVISIBLE);

                                    box_id1.setText(box_list_array.getJSONObject(0).getString("box_name_to_display"));
                                    status1.setText(box_list_array.getJSONObject(0).getString("box_status"));

                                    box_id2.setText(box_list_array.getJSONObject(1).getString("box_name_to_display"));
                                    status2.setText(box_list_array.getJSONObject(1).getString("box_status"));

                                    box_id3.setText(box_list_array.getJSONObject(2).getString("box_name_to_display"));
                                    status3.setText(box_list_array.getJSONObject(2).getString("box_status"));

                                    box_id4.setText(box_list_array.getJSONObject(3).getString("box_name_to_display"));
                                    status4.setText(box_list_array.getJSONObject(3).getString("box_status"));
                                }

                                if (box_count == 5){
                                    linearlayout_box1.setVisibility(View.VISIBLE);
                                    linearlayout_box2.setVisibility(View.VISIBLE);
                                    linearlayout_box3.setVisibility(View.VISIBLE);
                                    linearlayout_box4.setVisibility(View.INVISIBLE);
                                    linearlayout_box5.setVisibility(View.INVISIBLE);

                                    box_id1.setText(box_list_array.getJSONObject(0).getString("box_name_to_display"));
                                    status1.setText(box_list_array.getJSONObject(0).getString("box_status"));

                                    box_id2.setText(box_list_array.getJSONObject(1).getString("box_name_to_display"));
                                    status2.setText(box_list_array.getJSONObject(1).getString("box_status"));

                                    box_id3.setText(box_list_array.getJSONObject(2).getString("box_name_to_display"));
                                    status3.setText(box_list_array.getJSONObject(2).getString("box_status"));

                                    box_id4.setText(box_list_array.getJSONObject(3).getString("box_name_to_display"));
                                    status4.setText(box_list_array.getJSONObject(3).getString("box_status"));

                                    box_id5.setText(box_list_array.getJSONObject(4).getString("box_name_to_display"));
                                    status5.setText(box_list_array.getJSONObject(4).getString("box_status"));
                                }

                            }else{
                                System.out.println("*****please enter a valid sales invoice number" );
                                Toast.makeText(SiPbDetails.this, "InValid Sales Invoice ID" ,Toast.LENGTH_SHORT).show();
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
                        System.out.println("***************From  set_scanned_tag_details  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return SiPbDetails.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
    }

    private void set_box_list_array(JSONArray box_list_array) {
        si_box_list_array =  box_list_array ;
    }

    public Map<String, String> getHeaders_one () {
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
}