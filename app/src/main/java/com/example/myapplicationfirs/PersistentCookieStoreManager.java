package com.example.myapplicationfirs;



import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplicationfirs.utils.Constants;
import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;


import java.util.Map;



public class PersistentCookieStoreManager implements CookieStore {

    /**
     * The default preferences string.
     */
    private static final  String PREF_DEFAULT_STRING = "";

    /**
     * The preferences name.
     */
    private static final  String PREFS_NAME = PersistentCookieStoreManager.class.getName();

    /**
     * The preferences session cookie key.
     */
    private static  final String PREF_SESSION_COOKIE = "sid";
    private static final  String PREF_USER_ID_COOKIE = "user_id";

    private CookieStore mStore;
    private Context mContext;
    private Map<String,String> cookieValues = new HashMap<>();

    /**
     * @param context The application context
     */
    public PersistentCookieStoreManager(Context context) {
        // prevent context leaking by getting the application context
        mContext = context.getApplicationContext();

        // get the default in memory store and if there is a cookie stored in shared preferences,
        // we added it to the cookie store
        mStore = new CookieManager().getCookieStore();
        String jsonSessionCookie = getJsonSessionCookieString();

        if (!jsonSessionCookie.equals(PREF_DEFAULT_STRING)) {
            Gson gson = new Gson();
            HttpCookie cookie = gson.fromJson(jsonSessionCookie, HttpCookie.class);
            mStore.add(URI.create(cookie.getDomain()), cookie);
        }
    }


    @Override
    public void add(URI uri, HttpCookie cookie) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME,Context.MODE_PRIVATE).edit();
        if (cookie.getName().equals("sid")) {
            // if the cookie that the cookie store attempt to add is a session cookie,
            // we remove the older cookie and save the new one in shared preferences

            remove(URI.create(cookie.getDomain()), cookie);



            editor.putString(Constants.SESSION_ID, cookie.getValue());
            editor.apply();
            editor.commit();

        }else if(cookie.getName().equals(PREF_USER_ID_COOKIE)){
            remove(URI.create(cookie.getDomain()), cookie);



            editor.putString(Constants.USER_ID, cookie.getValue());
            editor.apply();
            editor.commit();
        }
        else{
            //not needed
        }

        mStore.add(URI.create(cookie.getDomain()), cookie);


    }

    public Map<String,String> getCookieHeaders(){
        return cookieValues;

    }

    private Map<String,String> parseJsonData(HttpCookie cookie) {
        Map<String,String> map = new HashMap<>();
        CommonResponseHeaders commonHeaders = new CommonResponseHeaders();
        //Iterate HttpCookie object
        for(HttpCookie httpCookies: getCookies()){
            if(httpCookies.getName().equals(PREF_USER_ID_COOKIE)){

                map.put(Constants.USER_ID,httpCookies.getValue());
                commonHeaders.setUserId(httpCookies.getValue());
            }else if(httpCookies.getName().equalsIgnoreCase("sid")){

                map.put(Constants.SESSION_ID,httpCookies.getValue());
                commonHeaders.setSessionId(httpCookies.getValue());
            }else{
                //not needed
            }
        }

        return map;
    }


    @Override
    public List<HttpCookie> get(URI uri) {
        return mStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return mStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return mStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return mStore.remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return mStore.removeAll();
    }

    private String getJsonSessionCookieString() {
        return getPrefs().getString(PREF_SESSION_COOKIE, PREF_DEFAULT_STRING);
    }

    /**
     * Saves the HttpCookie to SharedPreferences as a json string.
     *
     * //@param cookie The cookie to save in SharedPreferences.
     */
    private void saveSessionCookie(HttpCookie cookie) {
        Gson gson = new Gson();
        String jsonSessionCookieString = gson.toJson(cookie);
        //Extra Added

        SharedPreferences.Editor editor = getPrefs().edit();


        editor.putString(Constants.SESSION_ID, jsonSessionCookieString);
        editor.apply();
    }
    private void saveUserCookie(HttpCookie cookie) {


        //Extra Added
        String userId = cookie.getValue();
        SharedPreferences.Editor editor = getPrefs().edit();


        editor.putString(Constants.USER_ID, userId);
        editor.apply();
    }


    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

