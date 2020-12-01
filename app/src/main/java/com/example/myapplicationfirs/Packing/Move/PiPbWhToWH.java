package com.example.myapplicationfirs.Packing.Move;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;
import com.android.hdhe.uhf.readerInterface.TagModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplicationfirs.EPC;
import com.example.myapplicationfirs.PackingDetails;
import com.example.myapplicationfirs.R;
import com.example.myapplicationfirs.SiPbDetails;
import com.example.myapplicationfirs.Util;
import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pda.serialport.Tools;

public class PiPbWhToWH extends AppCompatActivity implements View.OnClickListener {
    //scan variables start
    private UhfReader manager;
    private ListView listViewData;
    private ArrayList<EPC> listEPC;
    private ArrayList<String> listepc = new ArrayList<String>();
    //flags
    private boolean startFlag = false;
    private boolean runFlag = true;
    private int power = 0 ;//rate of work
    private int area = 0;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private PiPbWhToWH.KeyReceiver keyReceiver;
    private  Toast toast;

    private TextView tvEpcLabel;
    //scan variables ends


    private EditText packing_item;
    private EditText parent_item;
    private EditText serial_no;
    private EditText source_wh;
    private EditText source_rarb;
    private EditText target_wh;
    private EditText target_rarb;
    private ProgressDialog progressDialog;


    private Button btnScanRfid;
    private Button btn_make_transfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_pb_wh_to_w_h);

        shared = getSharedPreferences("UhfRfPower", 0);
        editor = shared.edit();
        power = shared.getInt("power", 30);
        area = shared.getInt("area", 3);

        initview();

        Thread thread = new InventoryThread();
        thread.start();
        Util.initSoundPool(this);

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
            System.out.println("***** initFails **************");
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
        btnScanRfid.setText("Scan Rfid");
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

    private void initview() {
        packing_item = (EditText) findViewById(R.id.packing_item);
        parent_item = (EditText) findViewById(R.id.parent_item);
        serial_no = (EditText) findViewById(R.id.serial_no);
        source_wh = (EditText) findViewById(R.id.source_wh);
        source_rarb = (EditText) findViewById(R.id.source_rarb);
        target_wh = (EditText) findViewById(R.id.target_wh);
        target_rarb = (EditText) findViewById(R.id.target_rarb);

        //scan
        tvEpcLabel =(TextView) findViewById(R.id.tvEpcLabel);

        btn_make_transfer = (Button)findViewById(R.id.btn_make_transfer);
        btn_make_transfer.setOnClickListener(this);

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
                    get_pi_details(rfid_tag);
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

    private void get_pi_details(String rfid_tag) {
        showProgress();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, String> rfid_map = new HashMap<>();
        rfid_map.put("rfid_tag",rfid_tag);
        //fetch_tag_packing_detailsget_packing_item_details_ac_to_rfid

        String fetch_tag_packing_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, rfid_map, CustomUrl.GET_PACKING_ITEM_DETAILS_AC_TO_RFID);
        //String fetch_tag_packing_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.TEST_PD_FROM_ANDROID);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, fetch_tag_packing_details_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        try{
                            System.out.println("*****from get_pi_details   : response"+response.toString());

                            JSONObject stat = response.getJSONObject("message");
                            System.out.println("***** From get_pi_details  stat : "+stat );

                            if(stat.getString("isPackingItem").equals("invalid_tag")){
                                Toast.makeText(PiPbWhToWH.this, "Scanned Tag has not associated with any Packing Item" ,Toast.LENGTH_SHORT).show();
                            }
                            else if (stat.getString("isPackingItem").equals("no")){
                                Toast.makeText(PiPbWhToWH.this, "Scanned Tag is not associated with packing item" ,Toast.LENGTH_SHORT).show();
                            }
                            else if (stat.getString("isPackingItem").equals("yes")){
                                packing_item.setText(stat.getString("packing_item"));
                                parent_item.setText(stat.getString("parent_item"));
                                source_wh.setText(stat.getString("source_wh"));
                                source_rarb.setText(stat.getString("source_rarb"));
                                serial_no.setText(stat.getString("serial_no"));
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
                        hideProgress();
                        System.out.println("***************From  get_pi_details  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PiPbWhToWH.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
    }

    private void create_stock_entry_transfer() {
        showProgress();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject stock_entry_data =  new JSONObject();
        try {
            stock_entry_data.put("packing_item",packing_item.getText().toString());
            stock_entry_data.put("parent_item",parent_item.getText().toString());
            stock_entry_data.put("serial_no",serial_no.getText().toString());
            stock_entry_data.put("source_wh",source_wh.getText().toString());
            stock_entry_data.put("source_rarb",source_rarb.getText().toString());
            stock_entry_data.put("target_wh",target_wh.getText().toString());
            stock_entry_data.put("target_rarb",target_rarb.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> stock_entry_map = new HashMap<>();
        stock_entry_map.put("req_data",stock_entry_data.toString());
        //fetch_tag_packing_detailsget_packing_item_details_ac_to_rfid

        String create_stock_entry_transfer_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, stock_entry_map, CustomUrl.CREATE_STOCK_ENTRY_TRANSFER);
        //String fetch_tag_packing_details_url = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.TEST_PD_FROM_ANDROID);

        JsonObjectRequest JsonRequest = new JsonObjectRequest(Request.Method.GET, create_stock_entry_transfer_url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgress();
                        try{
                            System.out.println("*****from create_stock_entry_transfer   : response"+response.toString());

                            String se_name = response.getString("message");
                            if(se_name.length() > 0){
                                Toast.makeText(PiPbWhToWH.this, "Packing Item has Successfully moved" ,Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(PiPbWhToWH.this, "Transfer Failed" ,Toast.LENGTH_SHORT).show();

                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PiPbWhToWH.this, "Transfer Failed" ,Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgress();
                        System.out.println("***************From  create_stock_entry_transfer  error : "+error );
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return PiPbWhToWH.this.getHeaders_one();
            }
        };
        requestQueue.add(JsonRequest);
    }

    @Override
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

            case R.id.btn_make_transfer:
                create_stock_entry_transfer();
                break;
        }
    }

    private void registerReceiver() {
        keyReceiver = new PiPbWhToWH.KeyReceiver();
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
                    toast = Toast.makeText(PiPbWhToWH.this, "KeyReceiver:keyCode = down" + keyCode, Toast.LENGTH_SHORT);
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
}