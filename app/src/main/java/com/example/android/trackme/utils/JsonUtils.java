package com.example.android.trackme.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tanujanuj on 20/10/17.
 */

public class JsonUtils {
    public static String[] getSimpleDetailStringsFromJson(Context context, String JsonStr,String count)
            throws JSONException {
        final String OWM_ACTIVITY="activity";
        final String OWN_NAME="clientn";
        String[] parsedData=null;
        JSONArray Json=new JSONArray(JsonStr);

        Integer integer=Integer.valueOf(count)-1;

        JSONObject jsonObject=Json.getJSONObject(integer);

        parsedData =new String[2];
        parsedData[0]=jsonObject.getString(OWM_ACTIVITY);
        parsedData[1]=jsonObject.getString(OWN_NAME);


        return parsedData;
    }

}
