package com.example.myapplicationfirs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.example.myapplicationfirs.common.BasicActivity;
import com.example.myapplicationfirs.utils.CustomUrl;


public class Login extends BasicActivity {

    boolean serveraddressfound=false;
    String my_PREFS_NAME;
    EditText et_serverAddress ;
    Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_serverAddress = (EditText)findViewById(R.id.et_serverAddress);
        setButton = (Button)findViewById(R.id.btn_set);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverAddress =  et_serverAddress.getText().toString();

                if(serverAddress != null || serverAddress != ""){
                    serveraddressfound = true;

                    SharedPreferences.Editor editor = getSharedPreferences(my_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("ServeraddressFound_MPRP",serveraddressfound);
                    editor.putString("serverAddress_MPRP",serverAddress);
                    editor.apply();

                    System.out.println(" ************From Login  serverAddress :"+serverAddress);

                    CustomUrl.setServerAddress(serverAddress);

                    System.out.println(" ************From Login  CustomUrl.getServerAddress()) :"+CustomUrl.getServerAddress());

                    callloginFragment();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Enter the Server Address to continue" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void autoLogout() {
        //do nothng
    }

    private void callloginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        addFragment(loginFragment);
    }

    public void addFragment(Fragment fragment1) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.login_content, fragment1);
        fragmentTransaction.commitAllowingStateLoss();
    }



}


