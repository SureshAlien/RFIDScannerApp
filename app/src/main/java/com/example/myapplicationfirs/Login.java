package com.example.myapplicationfirs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class Login extends AppCompatActivity {

    Button loginButton;

    JSONObject jsonObject;
    boolean isaNewLogin;
    String IS_NEW_LOGIN;

    EditText username_entry;
    EditText password_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_entry = (EditText) findViewById(R.id.username_entry);
        password_entry = (EditText)findViewById(R.id.password_entry);
        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {

                showProgress();
                requestLogin();

            }
        });

        CookieManager cookieManager = new CookieManager(new PersistentCookieStoreManager(context), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
    }


    }
