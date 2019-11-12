package com.example.myapplicationfirs.utils;



import android.net.Uri;

import java.util.Map;



public class Utility {
    public static Utility utility;
    public static Utility getInstance(){
        if(utility==null){
            utility = new Utility();
        }
        return utility;
    }
    public  String buildParams(String firstparam,String secondParam){

        return firstparam+Constants.SINGLE_SPACE+secondParam;
    }
    public String buildUrl(String methodORresource, Map<String,String> queryparameters, String... dottedpath){
        //building my URi,
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(CustomUrl.URL_SCHEME);

        System.out.println(" ************From Utility class CustomUrl.getServerAddress()************************ : "+CustomUrl.getServerAddress());

        builder.encodedAuthority(CustomUrl.getServerAddress());
        builder.appendPath(CustomUrl.API);
        builder.appendPath(methodORresource);

        if (dottedpath != null){
            for(String path:dottedpath)
            {
                System.out.println(" ************From Utility class inside for loop path "+path);

                builder.appendPath(path);

            }
        }



        //add the parameters to the urlstring
        if(queryparameters!=null)

        {
            for (Map.Entry<String, String> entry : queryparameters.entrySet()){
                builder.appendQueryParameter(entry.getKey(),entry.getValue());
            }

        }
        System.out.println(" ************From Utility class builder.build().toString()*********************** : "+builder.build().toString());

        return  builder.build().toString();
    }

}
