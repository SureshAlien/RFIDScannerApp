package com.example.myapplicationfirs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SiDetails extends AppCompatActivity implements View.OnClickListener {

    private Button btn_get_p_details;
    private Button btn_make_dn;

    private Button btn_pb_needed;
    private Button btn_pb_completed;
    private Button btn_pb_par_completed;
    private Button btn_pb_pending;

    private Button btn_pi_completed;
    private Button btn_pi_par_completed;
    private Button btn_pi_pending;
    private Button btn_pi_needed;


    private EditText editDocNo;
    private TextView  tv_si_delievery_status ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si_details);

        initview();
    }

    private void initview() {

        editDocNo = (EditText) findViewById(R.id.editDocNo);
        tv_si_delievery_status = (TextView)   findViewById(R.id.tv_si_delievery_status);

        btn_get_p_details = (Button)findViewById(R.id.btn_get_p_details);
        btn_get_p_details.setOnClickListener(this);

        btn_make_dn = (Button)findViewById(R.id.btn_make_dn);
        btn_make_dn.setOnClickListener(this);

        btn_pb_needed = (Button)findViewById(R.id.btn_pb_needed);
        btn_pb_pending = (Button)findViewById(R.id.btn_pb_pending);
        btn_pb_completed = (Button)findViewById(R.id.btn_pb_completed);
        btn_pb_par_completed = (Button)findViewById(R.id.btn_pb_par_completed);

        btn_pi_needed = (Button)findViewById(R.id.btn_pi_needed);
        btn_pi_completed = (Button)findViewById(R.id.btn_pi_completed);
        btn_pi_par_completed = (Button)findViewById(R.id.btn_pi_par_completed);
        btn_pi_pending = (Button)findViewById(R.id.btn_pi_pending);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_get_p_details:
                String doc_id = editDocNo.getText().toString() ;
                set_pi_pb_details( doc_id );
                break;
            case R.id.btn_make_dn:
                String doc_id_temp = editDocNo.getText().toString() ;
                craete_delivery_note(doc_id_temp) ;
                break;
        }
    }//Onclick ends

    private void set_pi_pb_details(String doc_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> fetch_si_pipb_details_map = new HashMap<>();
        fetch_si_pipb_details_map.put("doc_id",doc_id);

        String fetch_si_pipb_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, fetch_si_pipb_details_map, CustomUrl.FETCH_SI_PIPB_DETAILS);
        System.out.println("***** From set_pi_pb_details fetch_si_pipb_details_url"+fetch_si_pipb_details_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, fetch_si_pipb_details_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println("*****from set_pi_pb_details   : response :"+response.toString());

                            JSONObject stat = response.getJSONObject("message");
                            System.out.println("***** From set_scanned_tag_details  stat : "+stat );

                            btn_pb_needed.setText(stat.getString("pb_needed"));
                            btn_pb_completed.setText(stat.getString("pb_completed"));
                            btn_pb_par_completed.setText(stat.getString("pb_par_completed"));
                            btn_pi_pending.setText(stat.getString("pb_pending"));

                            btn_pi_needed.setText(stat.getString("pi_needed"));
                            btn_pi_completed.setText(stat.getString("pi_completed"));
                            btn_pi_par_completed.setText(stat.getString("pi_par_completed"));
                            btn_pb_pending.setText(stat.getString("pi_pending"));

                            tv_si_delievery_status.setText(stat.getString("isDeliverable"));

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

                return SiDetails.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
    }

    private void craete_delivery_note(String doc_id_temp) {
        String isDeliverable  = tv_si_delievery_status.getText().toString() ;
        if(isDeliverable.equals("Yes")){
            String dummy = "dummy " ;
            make_delivery_note(doc_id_temp);
        }
        else{ //show alert dialog
            String title= "Delievery Note";
            String message = "Packing not yet completed" ;
            Toast.makeText(SiDetails.this, message ,Toast.LENGTH_SHORT).show();
        }
    }

    private void make_delivery_note(String doc_id_temp) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> make_delivery_note_map = new HashMap<>();
        make_delivery_note_map.put("doc_id",doc_id_temp);

        String make_delivery_note_map_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, make_delivery_note_map, CustomUrl.MAKE_DELIVERY_NOTE);
        System.out.println("***** From make_delivery_note make_delivery_note_map_url"+make_delivery_note_map_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.PUT, make_delivery_note_map_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("***** From make_delivery_note response dn successfuly created"+response.toString());
                        Toast.makeText(SiDetails.this, "Delivery Note has been created sucessfully" ,Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("***** From make_delivery_note error ");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return SiDetails.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
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