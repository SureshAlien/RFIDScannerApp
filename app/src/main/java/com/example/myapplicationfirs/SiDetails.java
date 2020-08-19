package com.example.myapplicationfirs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button btn_pi_needed;
    private Button btn_pi_completed;
    private Button btn_pi_par_completed;

    private EditText editDocNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si_details);

        initview();
    }

    private void initview() {
        editDocNo = (EditText) findViewById(R.id.editDocNo);

        btn_get_p_details = (Button)findViewById(R.id.btn_get_p_details);
        btn_get_p_details.setOnClickListener(this);

        btn_make_dn = (Button)findViewById(R.id.btn_make_dn);
        btn_make_dn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_get_p_details:
                String doc_id = editDocNo.getText().toString() ;
                set_pi_pb_details( doc_id );
                break;
            case R.id.btn_make_dn:
                craete_delivery_note() ;
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

                            /*
                            tvPointer.setText(stat.getString("pointer"));
                            tvTagReference.setText(stat.getString("tag_attached"));
                            tv_tag_refrence_id.setText(stat.getString("tag_doc_id"));
                            tvbox_status.setText(stat.getString("box_status"));
                            tv_box_name.setText(stat.getString("box_name"));
                            tv_box_id.setText(stat.getString("box_doc_id"));

                             */

                            /*

                            if (jsonArray.length() != 0 ){  //RFID tag already exist
                                JSONObject objects = jsonArray.getJSONObject(0);
                                String matched_rfid_tag_details_name = objects.getString("name");
                                //System.out.println("***** From response  rfid_validation_against_doc  matched_rfid_tag_details_name : "+matched_rfid_tag_details_name );
                                fetch_rfidTagDetailsDoc_data(tagName, matched_rfid_tag_details_name) ;

                            }
                            else{ //New  RFID tag,NO duplication
                                System.out.println("***** From response  rfid_validation_against_doc  No duplicate found for tag :"+rfid_tag);
                            }
                             */
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

    private void craete_delivery_note() {

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