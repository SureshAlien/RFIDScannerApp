package com.example.myapplicationfirs.Packing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationfirs.R;
import com.example.myapplicationfirs.SiDetails;
import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PICreateActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private Button btn_submit;

    private EditText item_code_entry ;
    private EditText qty_entry ;
    private EditText uom_entry ;
    private EditText parent_item_code_entry ;
    private EditText wh_entry ;
    private EditText rarb_entry ;

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


        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {

                showProgress();
                create_pi_doc(
                        item_code_entry.toString(),
                        qty_entry.toString(),
                        uom_entry.toString(),
                        parent_item_code_entry.toString(),
                        wh_entry.toString(),
                        rarb_entry.toString()
                        );

            }
        });
    }

    private void create_pi_doc(String item_code_entry, String qty_entry, String uom_entry, String parent_item_code_entry, String wh_entry,String rarb_entry) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> create_pi_doc_map = new HashMap<>();
        create_pi_doc_map.put("item_code_entry",item_code_entry);
        create_pi_doc_map.put("qty_entry",qty_entry);
        create_pi_doc_map.put("uom_entry",uom_entry);
        create_pi_doc_map.put("parent_item_code_entry",parent_item_code_entry);
        create_pi_doc_map.put("wh_entry",wh_entry);
        create_pi_doc_map.put("rarb_entry",rarb_entry);

        String create_pi_doc_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, create_pi_doc_map, CustomUrl.CREATE_PI_DOC);
        System.out.println("***** From set_pi_pb_details create_pi_doc_url"+create_pi_doc_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, create_pi_doc_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println("*****from create_pi_doc   : response :"+response.toString());

                            JSONObject stat = response.getJSONObject("message");
                            System.out.println("***** From create_pi_doc  stat : "+stat );


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("***************From  create_pi_doc  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PICreateActivity.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
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