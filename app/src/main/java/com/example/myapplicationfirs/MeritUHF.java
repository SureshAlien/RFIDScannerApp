package com.example.myapplicationfirs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.android.hdhe.uhf.reader.UhfReader;
import com.android.hdhe.uhf.readerInterface.TagModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.pda.serialport.Tools;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;


//

public class MeritUHF extends AppCompatActivity implements  OnClickListener
{
    //buttons
    private  Button btnScan;
    private  Button btnScan1;
    private  Button btnScan2;
    private Button btnAssociate;
    //buttons

    private EditText editRfid1 ;
    private EditText editRfid2 ;
    private EditText editSerNo ;


    private TextView tvEpcLabel;
    private UhfReader manager;
    private ListView listViewData;
    private ArrayList<EPC> listEPC;
    private ArrayList<String> listepc = new ArrayList<String>();

    //flags
    private boolean startFlag = false;
    private boolean runFlag = true;
    private boolean rfid1Flag = false;
    private boolean rfid2Flag = false;
    //flags



    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private TextView textVersion;


    private int power = 0 ;//rate of work
    private int area = 0;
    private int thread_count = 0 ;
    private int start_flag_count = 0 ;

    private KeyReceiver keyReceiver;
    private  Toast toast;

    //erpconnectiom
    String username = "administrator";
    JSONObject jsonObject;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_uhf);
        shared = getSharedPreferences("UhfRfPower", 0);
        editor = shared.edit();
        power = shared.getInt("power", 30);
        area = shared.getInt("area", 3);


        initView();

        Thread thread = new InventoryThread();
        thread.start();

        Util.initSoundPool(this);


    } //onCreate ends

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
        TextView textView_title_config;
        textView_title_config = (TextView) findViewById(R.id.textview_title_config);
        textView_title_config.setText("Port:com" + 13
                +";Power:" + powerString + " (EU)");
        manager = UhfReader.getInstance();
        if (manager == null) {
            textVersion.setText("initFails");
            return;
        }
        //debug
        if (manager != null) {
            textVersion.setText("getInstance method sucessfuly returned UhfReader's Object");
            return;
        }

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
        btnScan.setText("Start");
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

        btnScan = (Button)findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);

        btnScan1 = (Button)findViewById(R.id.btnScan1);
        btnScan1.setOnClickListener(this);

        btnScan2 = (Button)findViewById(R.id.btnScan2);
        btnScan2.setOnClickListener(this);


        btnAssociate = (Button)findViewById(R.id.btnAssociate);
        btnAssociate.setOnClickListener(this);




        editRfid1 = (EditText) findViewById(R.id.editRfid1);
        editRfid2 = (EditText) findViewById(R.id.editRfid2);
        editSerNo = (EditText) findViewById(R.id.editSerNo);


        tvEpcLabel = (TextView)findViewById(R.id.tvEpcLabel1);
        listEPC = new ArrayList<EPC>();
        textVersion = (TextView) findViewById(R.id.textView_version);





    } //initView ends

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



    private void addToList(final List<EPC> list, final String epc, final byte rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //suresh
                tvEpcLabel.setText(epc);
                Util.play(1, 0);
                //suresh

                if(rfid1Flag){
                    editRfid1.setText(epc);
                    rfid1Flag = false;
                }
                if(rfid2Flag){
                    editRfid2.setText(epc);
                    rfid2Flag = false;
                }
            }
        });
    } //addlist ends


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnScan:
                Toast.makeText(MeritUHF.this, "You have clicked Scan Button" ,Toast.LENGTH_SHORT).show();
                System.out.println("***************************scan Button clicked**************************************");

                if (!startFlag) {
                    startFlag = true;
                    btnScan.setText("Stop");


                } else {
                    startFlag = false;
                    btnScan.setText("Start");

                }
                break;

            case R.id.btnScan1:
                Toast.makeText(MeritUHF.this, "You have clicked RFID Scan Button" ,Toast.LENGTH_SHORT).show();
                if (!startFlag) {
                    startFlag = true;
                    btnScan1.setText("Stop");
                    rfid1Flag = true ;

                } else {
                    startFlag = false;
                    btnScan1.setText("Scan-1");

                }
                break;

            case R.id.btnScan2:
                Toast.makeText(MeritUHF.this, "You have clicked RFID Scan Button" ,Toast.LENGTH_SHORT).show();
                if (!startFlag) {
                    startFlag = true;
                    btnScan2.setText("Stop");
                    rfid2Flag = true;

                } else {
                    startFlag = false;
                    btnScan2.setText("Scan-2");

                }
                break;
            case R.id.btnAssociate :
                Toast.makeText(MeritUHF.this, "You have clicked Associate Button" ,Toast.LENGTH_SHORT).show();
                System.out.println("***************************Associate Button clicked**************************************");

                String rf1 = editRfid1.getText().toString();
                String rf2 = editRfid2.getText().toString();
                String serialNum = editSerNo.getText().toString();
                System.out.println("***************************Associate Button clicked**************************************");
                System.out.println("***************************Associate Button clicked**************************************"+rf1 +" " +rf2 );

                if(rf1 != null || rf2 != null){

                    connect_erp_server(rf1,rf2,serialNum);
                }

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
                    toast = Toast.makeText(MeritUHF.this, "KeyReceiver:keyCode = down" + keyCode, Toast.LENGTH_SHORT);
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
                        onClick(btnScan);
                        break;
                }
            }
        }
    }

    //start of erp connection codes
    public void connect_erp_server(String rf1,String rf2,String serialNum){
        System.out.println("***************************Associate Button clicked**************************************");
        requestLogin(rf1,rf2,serialNum);

        CookieManager cookieManager = new CookieManager(new com.example.myapplicationfirs.PersistentCookieStoreManager(MeritUHF.this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
    } //end connect_erp_server

    private void requestLogin(final String rf1, final String rf2, final String serialNum) {
        final RequestQueue requestQueue1 = Volley.newRequestQueue(MeritUHF.this);

        //String  myUrl = "http://192.168.0.62:8000/api/method/login"; //developer lap url
        String  myUrl = "http://192.168.0.15/api/method/login";  //localhost url

        System.out.println("Suresh ************ From requestLogin "+myUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Suresh ***********Came inside login respomse");


                String loginResponse = null;


                try {
                    if(response!=null){
                        jsonObject = new JSONObject(response);
                    }
                    loginResponse = jsonObject.get("message").toString();
                    if (loginResponse!=null && loginResponse.equalsIgnoreCase(com.example.myapplicationfirs.Constants.LOGIN_RESPONSE) ) {
                        String loggedUser = jsonObject.get("full" +
                                "_name").toString();
                        System.out.println("Suresh ************From requestLogin Respons ************************ : "+loggedUser);

                        //added on 25th Oct 2017 to tie user down to a warehouse
                        getLoggedInUserData(rf1,rf2,serialNum);

                    } else {
                        Toast.makeText(getApplicationContext(),"loginResponse is null" , Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("ERROR",e.toString());
                }
            }


        }//end of sucess response
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Suresh ************ From requestLogin Error in login connection ");

                Toast.makeText(getApplicationContext(),"Error in login connection" , Toast.LENGTH_LONG).show();


            }
        })
                //end of error response and stringRequest params
        {
            //This is for providing body for Post request@Override
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> mapObject = new HashMap<>();
                mapObject.put(com.example.myapplicationfirs.Constants.KEY_NAME, "administrator");
                mapObject.put(com.example.myapplicationfirs.Constants.KEY_PASS, "password");
                return mapObject;

            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue1.add(stringRequest);

    } //end of request login

    private void getLoggedInUserData(final String rf1, final String rf2, final String serialNum) {
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);

        //StringRequest stringRequest1 = new StringRequest()

        //String myUrl2 = "http://192.168.0.62:8000/api/method/frappe.auth.get_logged_user"; //dev lap url
        String myUrl2 = "http://192.168.0.15/api/method/frappe.auth.get_logged_user";//localhost url
        // String myUrl2 = "http://192.168.0.109:8000/api/method/login?usr=Administrator&pwd=password";

        System.out.println("Suresh ************ From Home getLoggedInUserData() : " );


        StringRequest stringRequest = new StringRequest(Request.Method.PUT, myUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    //String loggedInUser = object.getString("message");
                    System.out.println("Suresh ************ From getLoggedInUserData Response came for this sec login JSONObject object "+object );
                    System.out.println("Suresh ************ From getLoggedInUserData Response came for this sec login JSONObject object "+object.getString("message") );

                    System.out.println("Suresh ************ From getLoggedInUserData Response came for this sec login  ");

                    System.out.println("Suresh ************ From getLoggedInUserData Response came for this sec login  ");
                    populatetheDataModel(username,rf1,rf2,serialNum);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }//end of sucess responseP
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("Suresh ************ From getLoggedInUserData fragments Errorrrr in login connection ");

                Toast.makeText(getApplicationContext(),"Error in getLoggedInUserData connection" , Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue2.add(stringRequest);


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
    } //end of getuser data

    private void populatetheDataModel(String username,String rf1,String rf2,String serialNum) throws JSONException {

        System.out.println("****************************Suresh from populatetheDataModel**************************************");
        System.out.println("**************************** from populatetheDataModel************************************** rf1data "+ rf1+ "rf2:" +rf2+"serialNum :"+serialNum);

        String new_url ="http://192.168.0.15/api/resource/Serial%20No/"+serialNum ;
        //String new_url ="http://192.168.0.62/api/resource/Serial%20No/"+serialNum ; //lap


        JSONObject rfid_data = new JSONObject();

        // putting data to JSONObject
        rfid_data.put("pch_rfid_tag1",rf1);

        rfid_data.put("pch_rfid_tag2",rf2);

        System.out.println("****************************Suresh from populatetheDataModel rfid_data**************************************"+ rfid_data);




        String url = "http://192.168.0.15/api/method/nhance.api.android_api_test"; //lap


        final RequestQueue requestQueue = Volley.newRequestQueue(MeritUHF.this);

        //Json object request

        // prepare the Request
        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.PUT, new_url,rfid_data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        System.out.println("****************************JSON Object Response came **************************************"+response.toString());

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("****************************JSON Object Erro responce came **************************************");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders_one();
            }

        };

// add it to the RequestQueue
        requestQueue.add(JsonRequest);

        //Json object request

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

    //End of erp connection codes


} //whole class ends
