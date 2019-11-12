package com.example.myapplicationfirs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;


//

public class MeritUHF extends AppCompatActivity implements  OnClickListener
{
    //buttons
    private  Button btnScan;
    private  Button btnScan1;
    private  Button btnScan2;
    private Button btnAssociate;
    private Button btnGetDetails;
    //buttons

    private EditText editRfid1 ;
    private EditText editRfid2 ;
    private EditText editDocNo ; //DocNo = name in ErpNext
    private EditText editDocType ;
    private TextView tvItemCode;


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

    //rfidValidation
    JSONObject de_associate_rfid_details = new JSONObject();  //de_associate_rfid_details = {"RFID1" : {"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}

     String as_ds_updated_details = "" ;

    private android.widget.Spinner myspinner ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_uhf);
        shared = getSharedPreferences("UhfRfPower", 0);
        editor = shared.edit();
        power = shared.getInt("power", 30);
        area = shared.getInt("area", 3);

        initView();
        set_doctypes_on_spinner();

        Thread thread = new InventoryThread();
        thread.start();

        Util.initSoundPool(this);

        //connect_erp_server(); //login erp
        getLoggedInUserData(); //session id checking from login screen

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
        //TextView textView_title_config;
        //textView_title_config = (TextView) findViewById(R.id.textview_title_config);
        //textView_title_config.setText("Port:com" + 13+";Power:" + powerString + " (EU)");
        manager = UhfReader.getInstance();  //gets power from here
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

        btnGetDetails = (Button)findViewById(R.id.btnGetDetails);
        btnGetDetails.setOnClickListener(this);

        editRfid1 = (EditText) findViewById(R.id.editRfid1);
        editRfid2 = (EditText) findViewById(R.id.editRfid2);
        editDocNo = (EditText) findViewById(R.id.editDocNo);
        editDocType = (EditText) findViewById(R.id.editDoctype);
        tvItemCode =  (TextView)  findViewById(R.id.tvItemCodeVal);

        tvEpcLabel = (TextView)findViewById(R.id.tvEpcLabel1);
        listEPC = new ArrayList<EPC>();
        textVersion = (TextView) findViewById(R.id.textView_version);

        myspinner = (android.widget.Spinner) findViewById(R.id.simple_spinner);




        try {
            de_associate_rfid_details.put("RFID_TAG1","empty");
            de_associate_rfid_details.put("RFID_TAG2","empty");


        } catch (Exception e) {
            Log.e("ERROR",e.toString());
        }
    } //initView ends

    private void set_doctypes_on_spinner(){
        String doctype_names[] = {"Serial No","Batch No","Employee"};
        ArrayAdapter<String> myadapter = new ArrayAdapter<>(MeritUHF.this,android.R.layout.simple_list_item_1,doctype_names);
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myspinner.setAdapter(myadapter);
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

                //suresh
                tvEpcLabel.setText(epc);
                Util.play(1, 0);
                //suresh

                if(rfid1Flag){
                    editRfid1.setText(epc);
                    rfid1Flag = false;

                    //RFID tag1 validattion against all tag1 of all serial numbers

                    String rfid_tag1 = editRfid1.getText().toString() ;

                    if (rfid_tag1 != null){
                        rfid_validation_against_serno("RFID_TAG1",rfid_tag1);


                    }
                    //RFID tag1 validattion against all tag2 of all serial numbers

                }
                if(rfid2Flag){
                    editRfid2.setText(epc);
                    rfid2Flag = false;

                    //RFID tag2 validattion against all tag1 of all serial numbers

                    String rfid_tag2 = editRfid2.getText().toString() ;
                    JSONObject rfiid_tag2_exist_details ;

                    if (rfid_tag2 != null){
                        rfid_validation_against_serno("RFID_TAG2",rfid_tag2);
                    }
                    //RFID tag2 validattion against all tag2 of all serial numbers
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

            //btnGetDetails

            case R.id.btnGetDetails:
                System.out.println("***************************GetDetails Button clicked**************************************");
                String doc_type = editDocType.getText().toString() ;
                String  doc_no = editDocNo.getText().toString();
                get_rfid_details_ac_doc_number( doc_type,doc_no);
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
                System.out.println("***************************Associate Button clicked**************************************");

                final String rf1 = editRfid1.getText().toString();
                final String rf2 = editRfid2.getText().toString();
                final String doc_no_fi = editDocNo.getText().toString();
                String temp_doc_type  = editDocType.getText().toString() ;
                temp_doc_type = temp_doc_type.trim();
                temp_doc_type = temp_doc_type.replaceAll(" +", "%20");
                final String doc_type_fi = temp_doc_type;


                System.out.println("*************************** from Associate Button clicked**************************************rf1,rf2"+rf1 +" " +rf2 );
                System.out.println ("***************************from Associate Button clicked de_associate_rfid_details**************************************de_associate_rfid_details "+de_associate_rfid_details );

                //dde_associate_rfid_details : {"RFID_TAG1":{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"},"RFID_TAG2":{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}}eAssociate

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (de_associate_rfid_details.getString("RFID_TAG1") != "empty" ){

                                System.out.println("***************************from Associate Button clicked RFID_TAG1 exist ************************************** " );


                                JSONObject RFID_TAG1_detail = de_associate_rfid_details.getJSONObject("RFID_TAG1");

                                String tag_to_be_removed   =  RFID_TAG1_detail.getString("matched_tag");
                                String sereno_with_dup_tag =  RFID_TAG1_detail.getString("duplicate_serial_no");

                                deAssociateRFID(tag_to_be_removed,sereno_with_dup_tag);

                                System.out.println("***************************from Associate Button clicked RFID_TAG1 and before remove data local de_associate_rfid_details:"+de_associate_rfid_details );
                                de_associate_rfid_details.put("RFID_TAG1","empty");
                                System.out.println("***************************from Associate Button clicked RFID_TAG1 and removed data local de_associate_rfid_details:"+de_associate_rfid_details );

                            }
                            else {  //remove these 2 else blogs after testing
                                System.out.println("***************************from Associate Button clicked RFID_TAG1 not exist ************************************** " );

                            }
                            System.out.println("***************************from Associate Button clicked passed RFID_TAG1************************************** ");

                            if (de_associate_rfid_details.getString("RFID_TAG2") != "empty") {
                                JSONObject RFID_TAG2_detail = de_associate_rfid_details.getJSONObject("RFID_TAG2");

                                String tag_to_be_removed   =  RFID_TAG2_detail.getString("matched_tag");
                                String sereno_with_dup_tag =  RFID_TAG2_detail.getString("duplicate_serial_no");

                                deAssociateRFID(tag_to_be_removed,sereno_with_dup_tag);

                                System.out.println("***************************from Associate Button clicked RFID_TAG2 and before remove data local de_associate_rfid_details:"+de_associate_rfid_details );
                                de_associate_rfid_details.put("RFID_TAG2","empty");
                                System.out.println("***************************from Associate Button clicked RFID_TAG2 and removed data local de_associate_rfid_details:"+de_associate_rfid_details );

                            }
                            else {  //remove these 2 else blogs after testing
                                System.out.println("***************************from Associate Button clicked RFID_TAG2 not exist ************************************** " );
                            }



                            //syncApiCallDummy1();
                            //System.out.println("***************************from Associate Button syncApiCallDummy1 called**************************************");
                            //syncApiCallDummy2() ;
                            //System.out.println("***************************from Associate Button syncApiCallDummy2 called**************************************");

                            if(rf1 != null || rf2 != null){
                                    associateRFIDTags(username,rf1,rf2,doc_type_fi,doc_no_fi);
                                    System.out.println("***************************from Associate Button clicked  associateRFIDTags  fun  called ");

                            }

                        } catch (JSONException e) {
                            System.out.println("***************************from Associate Button clicked Error in try ************************************** "+e );
                        }
                    }
                });
                thread.start();
                System.out.println("***************************from Associate Button clicked Sample  check for associateRFIDTags  fun ");
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

    private void getLoggedInUserData() {

        //using this function top lines to check urls


        System.out.println("Suresh ************ From getLoggedInUserData  Entered ");
        String myUrl2 = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.GET_LOGGED_USER);
        //String myUrl2 = "http://192.168.0.15/api/method/frappe.auth.get_logged_user";//localhost url
        System.out.println("Suresh ************ From getLoggedInUserData  customurl came "+myUrl2);

        //?fields=["pch_rfid_tag1","pch_rfid_tag2","item_code"]&filters=[["Serial%20No","name","=","dummy"]]

        String  fields_str = "[\"pch_rfid_tag1\",\"pch_rfid_tag2\",\"item_code\"]" ;
        String  filters = "[[\"Serial%20No\",\"name\",\"=\",\"dummy\"]]" ;




        //get_rfid_details_ac_doc_number
        //http:/api/resource/Serial%20No?fields=["pch_rfid_tag1","pch_rfid_tag2","item_code"]&filters=[["Serial%20No","name","=","dummy"]]


        String serial_no = "dummy";
        String doc_name = "Stock  Entry ";
        String doc_no = "10000";

        doc_name = doc_name.trim();
        doc_name = doc_name.replaceAll(" +", "%20");


        String url = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE,null,null);
        url += "/"+doc_name +"?fields=[\"pch_rfid_tag1\",\"pch_rfid_tag2\",\"item_code\"]&filters=[[\""+doc_name +"\",\"name\",\"=\",\""+ doc_no+"\"]]" ;
        System.out.println("Suresh ************ From getLoggedInUserData  get_rfid_details_ac_doc_number came "+url);




       /* Sample url checkings
       //actual --> String rfid_val_url = "http://192.168.0.15/api/resource/Serial%20No?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag1\",\"=\",\""+rfid_tag+ "\"]]";//localhost url
        String rfid_tag = "178999";
        String rfid_validation1 = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO);
        rfid_validation1 += "?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag1\",\"=\",\""+rfid_tag+ "\"]] " ;
        System.out.println("Suresh ************ From getLoggedInUserData  rfid_validation1 came "+rfid_validation1);
        //actual --> String rfid_val_url = "http://192.168.0.15/api/resource/Serial%20No?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag2\",\"=\",\""+rfid_tag+ "\"]]";//localhost url
        String rfid_validation2 = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO);
        rfid_validation2 += "?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag2\",\"=\",\""+rfid_tag+ "\"]] " ;
        System.out.println("Suresh ************ From getLoggedInUserData  rfid_validation2 came "+rfid_validation2);
        //deassociate
        String deassociate = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO,serialNum);
        System.out.println("Suresh ************ From getLoggedInUserData  deassociate came "+deassociate);
        */

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //String myUrl2 = "http://192.168.0.62:8000/api/method/frappe.auth.get_logged_user"; //dev lap url

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, myUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Suresh ************ From getLoggedInUserData  Response came  ");
                try {
                    JSONObject object = new JSONObject(response);
                    //String loggedInUser = object.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }//end of sucess responseP
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Suresh ************ From getLoggedInUserData  Error in request  came  ");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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

    private void associateRFIDTags(String username, String rf1, String rf2,final String doc_type, final String doc_no) throws JSONException {


        System.out.println("****************************Enters associateRFIDTags**************************************");
        System.out.println("**************************** from associateRFIDTags************************************** rf1data "+ rf1+ "rf2:" +rf2+"doc_type :"+doc_type +"doc_no :"+doc_no);

        String new_url = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, null);
        new_url += "/"+ doc_type  + "/" + doc_no ;

        //from custom url->http://192.168.0.15/api/resource/Serial%20No/1000
        //String new_url ="http://192.168.0.15/api/resource/Serial%20No/"+serialNum ;

        JSONObject rfid_data = new JSONObject();
        rfid_data.put("pch_rfid_tag1",rf1);
        rfid_data.put("pch_rfid_tag2",rf2);

        System.out.println("****************************Suresh from associateRFIDTags rfid_data**************************************"+ rfid_data);

        final RequestQueue requestQueue = Volley.newRequestQueue(MeritUHF.this);

        // prepare  Request
        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.PUT, new_url,rfid_data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        System.out.println("****************************JSON Object Response came  for associateRFIDTags**************************************"+response.toString());


                        try{
                            JSONObject dataObject = response.getJSONObject("data");

                            String updated_rfid_tag1 = dataObject.getString("pch_rfid_tag1");
                            String updated_rfid_tag2 = dataObject.getString("pch_rfid_tag1");

                            if (updated_rfid_tag1 != "null" && updated_rfid_tag1 != null ){
                                as_ds_updated_details += doc_type+" : "+ doc_no +" has been associated with given RFID Tags" ;
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String dialog_title = "Association Details"; //{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}


                        AlertDialog.Builder builder = new AlertDialog.Builder(MeritUHF.this);

                        builder.setMessage(as_ds_updated_details).setTitle(dialog_title)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        System.out.println("*************************** Dialog box  Ok clicked**************************************");

                                    }
                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                        as_ds_updated_details ="";

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("****************************JSON Object Erro responce came  for associateRFIDTags**************************************");
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

    //validation functions  // {"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}
    private void rfid_validation_against_serno(final String tagName, String rfid_tag) {

        System.out.println("*****************Enters rfid_validation_against_serno for  tagName :" +tagName+ " rfid_tag : " +rfid_tag);


        RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        String rfid_val_url = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO);
        rfid_val_url += "?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag1\",\"=\",\""+rfid_tag+ "\"]] " ;

        //String rfid_val_url = "http://192.168.0.15/api/resource/Serial%20No?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag1\",\"=\",\""+rfid_tag+ "\"]]";//localhost url

        System.out.println(" From rfid_validation_against_serno rfid_val_url" +rfid_val_url);

        //JSon Request


        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, rfid_val_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            System.out.println("From  response of "+tagName+"  validation response :   "+response );

                            JSONArray jsonArray = response.getJSONArray("data");

                            if (jsonArray.length() != 0 ){                                //{"data":[{"name":"MeritSystems"}]}

                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno jsonArray  "+jsonArray );

                                //for sure only one duplicate value will be there so i am itreating array
                                JSONObject objectInArray = jsonArray.getJSONObject(0);
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno objectInArray  "+objectInArray );

                                String duplicate_serial_no = objectInArray.getString("name");
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno duplicate_serial_no  "+duplicate_serial_no );

                                JSONObject dup_rfid_tag_details = new JSONObject() ;


                                dup_rfid_tag_details.put("duplicate_serial_no",duplicate_serial_no);
                                dup_rfid_tag_details.put("matched_tag","pch_rfid_tag1");   //{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}

                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno dup_rfid_tag_details  "+dup_rfid_tag_details );
                                show_alert_dialog(tagName,dup_rfid_tag_details);
                                System.out.println("From  response of "+tagName+" called alert box" );
                                //alert box rfid1

                            }
                            else{
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno no matching found for pch_rfid_tag1 " );
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Suresh ************ Error rfid_validation_against_serno response type " + error);
                        Toast.makeText(getApplicationContext(),"Error in getLoggedInUserData connection" , Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }

        };
        requestQueue1.add(JsonRequest);



        //req2
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);

        String rfid_val_url2 = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO);
        rfid_val_url2 += "?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag2\",\"=\",\""+rfid_tag+ "\"]] " ;

        //String rfid_val_url2 = "http://192.168.0.15/api/resource/Serial%20No?fields=[\"name\"]&filters=[[\"Serial%20No\",\"pch_rfid_tag2\",\"=\",\""+rfid_tag+ "\"]]";//localhost url

        System.out.println("Suresh ************ From rfid_validation_against_serno rfid_val_url2" +rfid_val_url2);

        //JSon Request
        JsonObjectRequest JsonRequest2 = new JsonObjectRequest(Request.Method.GET, rfid_val_url2,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try {
                            System.out.println("From  response of "+tagName+"  validation response :   "+response );
                            JSONArray jsonArray = response.getJSONArray("data");


                            if (jsonArray.length() != 0 ){
                                //{"data":[{"name":"MeritSystems"}]}

                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno jsonArray  "+jsonArray );

                                //for sure only one duplicate value will be there so i am itreating array
                                JSONObject objectInArray = jsonArray.getJSONObject(0);
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno objectInArray  "+objectInArray );

                                String duplicate_serial_no = objectInArray.getString("name");
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno duplicate_serial_no  "+duplicate_serial_no );

                                JSONObject dup_rfid_tag_details = new JSONObject() ;

                                dup_rfid_tag_details.put("duplicate_serial_no",duplicate_serial_no);
                                dup_rfid_tag_details.put("matched_tag","pch_rfid_tag2");

                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno dup_rfid_tag_details  "+dup_rfid_tag_details );

                                show_alert_dialog(tagName,dup_rfid_tag_details);
                                System.out.println("From  response of "+tagName+" called alert box" );


                            }
                            else{
                                System.out.println("From  response of "+tagName+" rfid_validation_against_serno no matching found for pch_rfid_tag2 " );
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Suresh ************ Error rfid_validation_against_serno response type " + error);

                        Toast.makeText(getApplicationContext(),"Error in getLoggedInUserData connection" , Toast.LENGTH_LONG).show();                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }

        };

        //Json Request
        requestQueue2.add(JsonRequest2);

    }

    public void  show_alert_dialog(final String tagName, final JSONObject rfid_tag_exist_details ) throws JSONException{

        System.out.print("*************************** Enters show_alert_dialog************************************** for tagName : " +tagName+" rfid_tag_exist_details : "+rfid_tag_exist_details);

        String dialog_message ;
        String dialog_title ;

        final String loc_tagName = tagName == "RFID_TAG1" ? "Rfid Tag1" : "Rfid Tag2" ; //tagName is final cant change as client wants

        dialog_title = "The Selected   "+loc_tagName +"Already Exist"; //{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}

        dialog_message = loc_tagName + "is already bound with "+ rfid_tag_exist_details.getString("matched_tag")+" of Serial Number "+ rfid_tag_exist_details.getString("duplicate_serial_no") + ".Do you want to reassociate this RFID Tag with this Item";

        AlertDialog.Builder builder = new AlertDialog.Builder(MeritUHF.this);

        builder.setMessage(dialog_message).setTitle(dialog_title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("*************************** Dialog box  yes clicked**************************************"+loc_tagName);

                        try {
                            if (tagName == "RFID_TAG1") {
                                de_associate_rfid_details.put("RFID_TAG1",rfid_tag_exist_details);
                                System.out.println("***************************  yes Pressed RFID_TAG1 inserted to de_associate_rfid_details************************************** de_associate_rfid_details : "+de_associate_rfid_details);

                            }
                            else if (tagName == "RFID_TAG2"){
                                de_associate_rfid_details.put("RFID_TAG2",rfid_tag_exist_details);
                                System.out.println("***************************  yes Pressed RFID_TAG2 inserted to de_associate_rfid_details************************************** de_associate_rfid_details : "+de_associate_rfid_details);

                            }
                            System.out.println("***************************  yes Pressed ************************************** de_associate_rfid_details : "+de_associate_rfid_details);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (tagName == "RFID_TAG1"){
                            editRfid1.setText("");
                        }
                        else if (tagName == "RFID_TAG2") {
                            editRfid2.setText("");
                        }
                        dialog.cancel();

                    }
                });

        AlertDialog alert = builder.create();

        //stop scanning
        startFlag = false;
        if (tagName == "RFID_TAG1"){
            btnScan1.setText("Scan-1");
        }
        else if (tagName == "RFID_TAG2") {
            btnScan2.setText("Scan-2");
        }

        alert.show();

    }

    //de_associate_rfid_details : {"RFID_TAG1":{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"},"RFID_TAG2":{"duplicate_serial_no":"MeritSystems","matched_tag":"pch_rfid_tag2"}}eAssociate

    private void deAssociateRFID(String tag_to_be_removed, String sereno_with_dup_tag) throws JSONException {

        System.out.println("****************************Enters deAssociateRFID**************************************");
        System.out.println("**************************** from deAssociateRFID parameter details************************************** tag_to_be_removed "+ tag_to_be_removed+ "sereno_with_dup_tag:" +sereno_with_dup_tag);

        String new_url = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE, null, CustomUrl.SERIAL_NO,sereno_with_dup_tag);
        //String new_url ="http://192.168.0.15/api/resource/Serial%20No/"+sereno_with_dup_tag ;

        JSONObject rfid_data = new JSONObject();
        rfid_data.put(tag_to_be_removed,"");

        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, new_url,rfid_data, future, future)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders_one();
            }
        };
        volleyRequestQueue.add(request);

        System.out.println("****************************from deAssociateRFID  request added to the queue**************************************");


        try {
            System.out.println("****************************from deAssociateRFID  inside try before future.get()");

            //JSONObject response = future.get();
            JSONObject response = future.get(60,TimeUnit.SECONDS);
            System.out.println("****************************from deAssociateRFID Came inside try after    response:"+response);

            JSONObject dataObject = response.getJSONObject("data");

            String deleted_rfidTag = dataObject.getString(tag_to_be_removed);

            if(deleted_rfidTag =="null"){
                as_ds_updated_details +=  tag_to_be_removed + " has been disassociated from Serial no : "+ sereno_with_dup_tag + ". \n";
            }

        } catch(InterruptedException | ExecutionException ex)
        {
            //check to see if the throwable in an instance of the volley error
            System.out.println("****************************from deAssociateRFID  Exception enters 1 exc**************************************");

            if(ex.getCause() instanceof VolleyError)
            {
                //grab the volley error from the throwable and cast it back
                VolleyError volleyError = (VolleyError)ex.getCause();
                //now just grab the network response like normal
                NetworkResponse networkResponse = volleyError.networkResponse;
                System.out.println("****************************from deAssociateRFID  Exception networkResponse:"+networkResponse);

            }
        }
        catch(TimeoutException te)
        {
            System.out.println("****************************from deAssociateRFID  Exception TimeoutException:"+te);
        }


        /*
        System.out.println("****************************Enters deAssociateRFID**************************************");
        System.out.println("**************************** from deAssociateRFID************************************** tag_to_be_removed "+ tag_to_be_removed+ "sereno_with_dup_tag:" +sereno_with_dup_tag);

        String new_url ="http://192.168.0.15/api/resource/Serial%20No/"+sereno_with_dup_tag ;
        //String new_url ="http://192.168.0.62/api/resource/Serial%20No/"+serialNum ; //lap


        JSONObject rfid_data = new JSONObject();
        rfid_data.put(tag_to_be_removed,"");

        System.out.println("****************************from deAssociateRFID rfid_data**************************************"+ rfid_data);

        final RequestQueue requestQueue = Volley.newRequestQueue(MeritUHF.this);

        // prepare  Request
        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.PUT, new_url,rfid_data,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        System.out.println("****************************JSON Object Response came for deAssociateRFID**************************************"+response.toString());

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("****************************JSON Object Erro responce came  for deAssociateRFID**************************************");
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
        */

    }

    private void syncApiCallDummy1() throws JSONException {

        System.out.println("****************************Enters syncApiCallDummy1 after headers**************************************");


        RequestQueue volleyRequestQueue = Volley.newRequestQueue(this);

        //http://localhost/api/resource/Serial%20No

        String new_url ="http://192.168.0.15/api/resource/Serial%20No" ;

        String rfid_val_url = "http://192.168.0.15/api/resource/Serial%20No";//localhost url




        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, new_url, null, future, future)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }
        };
        volleyRequestQueue.add(request);

        System.out.println("****************************from syncApiCallDummy1  request added to the queue**************************************");


        try {
            System.out.println("****************************from syncApiCallDummy1  inside try before future.get()");

            //JSONObject response = future.get();
            JSONObject response = future.get(60,TimeUnit.SECONDS);
            System.out.println("****************************from syncApiCallDummy1 Came inside try after    response:"+response);

        } catch(InterruptedException | ExecutionException ex)
        {
            //check to see if the throwable in an instance of the volley error
            System.out.println("****************************from syncApiCallDummy1  Exception enters 1 exc**************************************");

            if(ex.getCause() instanceof VolleyError)
            {
                //grab the volley error from the throwable and cast it back
                VolleyError volleyError = (VolleyError)ex.getCause();
                //now just grab the network response like normal
                NetworkResponse networkResponse = volleyError.networkResponse;
                System.out.println("****************************from syncApiCallDummy1  Exception networkResponse:"+networkResponse);

            }
        }
        catch(TimeoutException te)
        {
            System.out.println("****************************from syncApiCallDummy1  Exception TimeoutException:"+te);
        }
    }

    //get doc no details start
    //String url = "http://192.168.0.15/api/resource/Serial%20No?fields=[\"pch_rfid_tag1\",\"pch_rfid_tag2\",\"item_code\"]&filters=[[\"Serial%20No\",\"name\",\"=\",\""+ serial_no+"\"]]";//localhost url // \""+rfid_tag+ "\"

    private void get_rfid_details_ac_doc_number(String doc_type,String doc_no) {

        doc_type = doc_type.trim();
        doc_type = doc_type.replaceAll(" +", "%20");

        RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        String url = Utility.getInstance().buildUrl(CustomUrl.API_RESOURCE,null, null);

        url += "/"+ doc_type +"?fields=[\"pch_rfid_tag1\",\"pch_rfid_tag2\"]&filters=[[\""+doc_type +"\",\"name\",\"=\",\""+ doc_no+"\"]]" ;
        System.out.println("***************Enters get_rfid_details_ac_doc_number, url :::: "+url );

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("***************From  get_rfid_details_ac_doc_number  response : "+response );
                        // {"data":[{"pch_rfid_tag2":null,"pch_rfid_tag1":null}]} ,, Invalid ser no = {"data":[]}
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() != 0 ){  //valid doc no

                                JSONObject objectInArray = jsonArray.getJSONObject(0);
                                String rfid_tag1 = objectInArray.getString("pch_rfid_tag1");
                                String rfid_tag2 = objectInArray.getString("pch_rfid_tag2");

                                System.out.println("***************From  get_rfid_details_ac_doc_number  rfid_tag1 : "+rfid_tag1 + "rfid_tag2 : "+ rfid_tag2 );

                                if (rfid_tag1 != "null" ){
                                    editRfid1.setText(rfid_tag1);
                                }
                                else{
                                    System.out.println("***************From  get_rfid_details_ac_doc_number   RFID tag1 null ");
                                    editRfid1.setText(rfid_tag1); //for null now entering null only .
                                }

                                if (rfid_tag2 != "null"  ){
                                    editRfid2.setText(rfid_tag2);
                                }
                                else{
                                    System.out.println("***************From  get_rfid_details_ac_doc_number   RFID tag2 null ");
                                    editRfid2.setText(rfid_tag2); //for null now entering null only .
                                }

                                if ( rfid_tag2 == "null" &&  rfid_tag1 == "null" )
                                {
                                    System.out.println("***************From  get_rfid_details_ac_doc_number  No RFID tags have Associated Given Serial Number has : ");
                                    Toast.makeText(MeritUHF.this, "No RFID tags have Associated Given Serial Number  " ,Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{ //invalid ser no
                                Toast.makeText(MeritUHF.this, "Please enter the valid Serial No " ,Toast.LENGTH_SHORT).show();
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
                        System.out.println("***************From  get_rfid_details_ac_doc_number  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return MeritUHF.this.getHeaders();
            }
        };
        requestQueue1.add(JsonRequest);
    }
} //whole class ends
//end


