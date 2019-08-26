package com.example.myapplicationfirs.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.location.Address;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.myapplicationfirs.R;
import com.example.myapplicationfirs.Login;
import com.example.myapplicationfirs.utils.AlertDialogHandler;
import com.example.myapplicationfirs.utils.Constants;
import com.example.myapplicationfirs.utils.CustomUrl;
import com.example.myapplicationfirs.utils.Utility;



public abstract class BasicActivity extends AppCompatActivity {


    private ProgressDialog progressDialog;
    protected AlertDialog dialogbox;

    private static long lastInteractedTime;
    final Handler handler = new Handler();

    protected static List<Address> addresses = null;

    //added 25th Oc 2017 to save user and session id for one entire session
    private static String currentLoggedUserId;
    private static String currentLoggedSessionId;



    public static String getCurrentLoggedSessionId() {
        return currentLoggedSessionId;
    }

    public static void setCurrentLoggedSessionId(String currentLoggedSessionId) {
        BasicActivity.currentLoggedSessionId = currentLoggedSessionId;
    }

    public static String getCurrentLoggedUserId() {
        return currentLoggedUserId;
    }

    public static void setCurrentLoggedUserId(String currentLoggedUserId) {
        BasicActivity.currentLoggedUserId = currentLoggedUserId;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetTimer();
        lastInteractedTime = System.currentTimeMillis();
        //added on 25th Oct 2017, setting the session and user id only once for each session

        setCurrentLoggedSessionId(this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).getString(Constants.SESSION_ID, null));
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSessionInactivity();
        resetTimer();
    }

    // 1 min = 1 * 60 * 1000 ms
    protected abstract void autoLogout();

    // On Every User Interaction update the last interacted time and reset the SESSIONSTATE
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        lastInteractedTime = System.currentTimeMillis();
    }

    //Check whether the user is active or not
    public void resetTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSessionInactivity();
                handler.postDelayed(this, Constants.TEST_USER_ACTIVITY);
            }
        }, Constants.TEST_USER_ACTIVITY);
    }

    /**
     * check whether is Active or not
     */
    private void checkSessionInactivity() {
        long timeDifference;
        timeDifference = System.currentTimeMillis() - lastInteractedTime;
        if (timeDifference >= Constants.DISCONNECT_TIMEOUT) {
            autoLogout();
        }
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

    public void showAlertDialog(String title, String message, boolean cancelable, String positiveButton,
                                String negativeButton, String cancelButton, final View view, final AlertDialogHandler alertDialogHandler) {
        if (dialogbox != null) {
            dialogbox.dismiss();
        }


        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        // myAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AlertDialogTheme);


        if (title != null) {
            myAlertDialog.setTitle(title);
        }

        if (message != null) {
            myAlertDialog.setMessage(message);
        }
        if (view != null) {
            myAlertDialog.setView(view);
        }

        if (positiveButton != null) {
            myAlertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialogHandler != null) {
                        alertDialogHandler.onPositiveButtonClicked();
                    }
                }
            });
        }

        if (negativeButton != null) {
            myAlertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    if (alertDialogHandler != null) {
                        alertDialogHandler.onNegativeButtonClicked();
                    }
                }
            });
            /*myAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialogHandler.onNegativeButtonClicked();
                }
            });*/
        }
        if(cancelButton!=null){
            myAlertDialog.setNeutralButton(cancelButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if(alertDialogHandler!=null&& dialogbox!=null){
                        dialogbox.dismiss();
                    }

                }
            });


        }
        else{
            myAlertDialog.setCancelable(cancelable);
        }
        dialogbox = myAlertDialog.show();
        dialogbox.show();
        //Start: Added on 19th Feb 2018 to implement new interface
        Button nbutton = dialogbox.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (nbutton != null) {
            nbutton.setBackgroundColor(getResources().getColor(R.color.colorError));
            nbutton.setTextColor(Color.WHITE);
        }
        Button cbutton = dialogbox.getButton(DialogInterface.BUTTON_NEUTRAL);
        if(cbutton!=null)
        {
            cbutton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            cbutton.setTextColor(Color.WHITE);
        }
        Button pbutton = dialogbox.getButton(DialogInterface.BUTTON_POSITIVE);
        if (pbutton != null) {
            pbutton.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
            pbutton.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pbutton.getLayoutParams();
            layoutParams.weight = 10;
            pbutton.setLayoutParams(layoutParams);
            if (nbutton != null) {
                nbutton.setLayoutParams(layoutParams);
            }
            if(cbutton!=null){
                cbutton.setLayoutParams(layoutParams);
            }
        }

        //End: Added on 19th Feb 2018


    }




    public void destroyTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    protected void logout() {
        //logout in the back end
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(this);

        String myUrl = Utility.getInstance().buildUrl(CustomUrl.API_METHOD, null, CustomUrl.LOGOUT_URL);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(BasicActivity.this, "Logging out", Toast.LENGTH_LONG).show();
                        callLoginIntent();


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BasicActivity.this, "Something went wrong, the error is: " + error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                String user_id = prefs.getString(Constants.USER_ID, null);
                String sid = prefs.getString(Constants.SESSION_ID, null);
                headers.put("user_id", user_id);
                headers.put("sid", sid);
                return headers;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    private void callLoginIntent() {

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        // finish();

    }





    public void toastMaker(String returnmsg) {

        Toast toast = Toast.makeText(getApplicationContext(), returnmsg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView text = (TextView) view.findViewById(android.R.id.message);

        text.setTextColor(Color.WHITE);
        if(returnmsg.contains(getResources().getString(R.string.success_string))) {


            //To change the Background of Toast
            view.setBackgroundColor(getResources().getColor(R.color.colorSuccess));

        }
        else
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorError));
        }
        toast.show();

    }
}
