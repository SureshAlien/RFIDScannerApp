package com.example.myapplicationfirs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.hdhe.uhf.reader.UhfReader;
import com.android.hdhe.uhf.readerInterface.TagModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import cn.pda.serialport.Tools;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.RequestFuture;
import com.example.myapplicationfirs.R;

//erpnext connection volley
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;


public class PackingDetails extends AppCompatActivity implements  OnClickListener {

    private UhfReader manager;
    private ListView listViewData;
    private ArrayList<EPC> listEPC;
    private ArrayList<String> listepc = new ArrayList<String>();

    //flags
    private boolean startFlag = false;
    private boolean runFlag = true;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private int power = 0 ;//rate of work
    private int area = 0;

    private KeyReceiver keyReceiver;
    private  Toast toast;
    private ProgressDialog progressDialog;

    private  Button btnScanRfid;

    private TextView textVersion;
    private TextView tvEpcLabel;
    private TextView tvPointer;
    private TextView tvTagReference;
    private TextView tv_tag_refrence_id;
    private TextView tvbox_status;
    private TextView tv_box_name;
    private TextView tv_box_id;
    private TextView tvPointer_wh;
    private TextView tvPointer_rarb;
    private TextView tv_box_rarb_id;
    private TextView tv_pitem_rarb_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packing_details);


        shared = getSharedPreferences("UhfRfPower", 0);
        editor = shared.edit();
        power = shared.getInt("power", 30);
        area = shared.getInt("area", 3);

        initView();

        Thread thread = new InventoryThread();
        thread.start();
        Util.initSoundPool(this);
        System.out.println("***** From  pd passed after thread " );
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String powerString = "";
//		switch (UhfManager.Power) {
//			case SerialPort.Power_3v3:
//				powerString = "power_3V3";
//				break;
//			case SerialPort.Power_5v:
//				powerString = "power_5V";
//				break;
//			case SerialPort.Power_Scaner:
//				powerString = "scan_power";
//				break;
//			case SerialPort.Power_Psam:
//				powerString = "psam_power";
//				break;
//			case SerialPort.Power_Rfid:
        powerString = "rfid_power";
//				break;
//			default:
//				break;
//		}
        //TextView textView_title_config;
        //textView_title_config = (TextView) findViewById(R.id.textview_title_config);
        //textView_title_config.setText("Port:com" + 13+";Power:" + powerString + " (EU)");
        manager = UhfReader.getInstance();  //gets power from here        System.out.println("*****From  pd on_resume " );


        if (manager == null) {

            textVersion.setText("initFails");
            return;
        }
        //debug
        /*if (manager != null) {
            textVersion.setText("getInstance method sucessfuly returned UhfReader's Object");
            return;
        } */

        //debug
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        registerReceiver();

//		Log.e("", "value" + power);
        manager.setOutputPower(power);
        manager.setWorkArea(area);
//		byte[] version_bs = manager.getFirmware();
//		if (version_bs!=null){
//			textView_title_config.append("("+new String(version_bs)+")");
//		}

    }

    @Override
    protected void onPause() {
        startFlag = false;
        btnScanRfid.setText("Start");
        manager.close();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        startFlag = false;
        runFlag = false;
        if (manager != null) {
            manager.close();
        }
        super.onDestroy();
    }

    private void initView() {
        textVersion = (TextView) findViewById(R.id.textView_version);
        tvEpcLabel = (TextView)findViewById(R.id.tvEpcLabel1);
        tvPointer = (TextView)findViewById(R.id.tvPointer);
        tvTagReference = (TextView)findViewById(R.id.tvTagReference);
        tv_tag_refrence_id = (TextView)findViewById(R.id.tv_tag_refrence_id);
        tvbox_status = (TextView)findViewById(R.id.tvbox_status);
        tv_box_name = (TextView)findViewById(R.id.tv_box_name);
        tv_box_id = (TextView)findViewById(R.id.tv_box_id);

        tvPointer_wh = (TextView)findViewById(R.id.tvPointer_wh);
        tvPointer_rarb = (TextView)findViewById(R.id.tvPointer_rarb);
        tv_box_rarb_id = (TextView)findViewById(R.id.tv_box_rarb_id);
        tv_pitem_rarb_id = (TextView)findViewById(R.id.tv_pitem_rarb_id);

        btnScanRfid = (Button)findViewById(R.id.btnScanRfid);
        btnScanRfid.setOnClickListener(this);
    }

    class InventoryThread extends Thread {
        private List<TagModel> tagList;
        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
                    tagList = manager.inventoryRealTime(); //实时盘存
                    if(tagList != null && !tagList.isEmpty()){
                        //播放提示音
                        Util.play(1, 0);
                        for(TagModel tag:tagList){
                            if(tag == null){
                                String epcStr = "";
//								String epcStr = new String(epc);
                                addToList(listEPC, epcStr, (byte)-1);
                            }else{
                                String epcStr = Tools.Bytes2HexString(tag.getmEpcBytes(), tag.getmEpcBytes().length);
//								String epcStr = new String(epc);
                                addToList(listEPC, epcStr, tag.getmRssi());
                            }

                        }
                    }
                    tagList = null ;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addToList(final List<EPC> list, final String epc, final byte rssi)  {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tvEpcLabel.setText(epc);
                Util.play(1, 0);
                String rfid_tag = tvEpcLabel.getText().toString() ;

                if (rfid_tag != null){
                    set_scanned_tag_details(rfid_tag);
                }
                /*
                if(rfid1Flag){
                    editRfid1.setText(epc);
                    rfid1Flag = false;

                    String rfid_tag1 = editRfid1.getText().toString() ;

                    if (rfid_tag1 != null){
                        rfid_validation_against_doc("RFID_TAG1",rfid_tag1);
                    }
                }
                */
            }
        });
    } //addlist ends

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnScanRfid:

                if (!startFlag) {
                    startFlag = true;
                    btnScanRfid.setText("Stop");
                } else {
                    startFlag = false;
                    btnScanRfid.setText("Scan RFID");
                }
                break;
        }
    }//Onclick ends
    private void registerReceiver() {
        keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        registerReceiver(keyReceiver , filter);
    }

    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown) {
                if (toast == null) {
                    toast = Toast.makeText(PackingDetails.this, "KeyReceiver:keyCode = down" + keyCode, Toast.LENGTH_SHORT);
                } else {
                    toast.setText("KeyReceiver:keyCode = down" + keyCode);
                }
                toast.show();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:
                    case KeyEvent.KEYCODE_F2:
                    case KeyEvent.KEYCODE_F3:
                    case KeyEvent.KEYCODE_F4:
                    case KeyEvent.KEYCODE_F5:
                        onClick(btnScanRfid);
                        break;
                }
            }
        }
    }

    //api_here

    public  void set_scanned_tag_details(String rfid_tag){

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> fetch_tag_packing_details_map = new HashMap<>();
        fetch_tag_packing_details_map.put("rfid_tag",rfid_tag);
        //fetch_tag_packing_details

        String fetch_tag_packing_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, fetch_tag_packing_details_map, CustomUrl.FETCH_TAG_PACKING_DETAILS);
        //String fetch_tag_packing_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.TEST_PD_FROM_ANDROID);
        System.out.println("***** From response  rfid_validation_against_doc  rfid_validation_against_doc_url :"+fetch_tag_packing_details_url);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, fetch_tag_packing_details_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            System.out.println("*****from set_scanned_tag_details   : response"+response.toString());

                            JSONObject stat = response.getJSONObject("message");
                            System.out.println("***** From set_scanned_tag_details  stat : "+stat );

                            //String docstatus = stat.getString("docstatus") ;
                            tvPointer.setText(stat.getString("pointer"));
                            tvTagReference.setText(stat.getString("tag_attached"));
                            tv_tag_refrence_id.setText(stat.getString("tag_doc_id"));
                            tvbox_status.setText(stat.getString("box_status"));
                            tv_box_name.setText(stat.getString("box_name"));
                            tv_box_id.setText(stat.getString("box_doc_id"));

                            tvPointer_wh.setText(stat.getString("pointer_warehouse"));
                            tvPointer_rarb.setText(stat.getString("pointer_rarb_id"));
                            tv_box_rarb_id.setText(stat.getString("pb_rarb_id"));
                            tv_pitem_rarb_id.setText(stat.getString("pi_rarb_id"));
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

                return PackingDetails.this.getHeaders_one();
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