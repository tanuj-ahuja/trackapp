package com.example.android.trackme.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by tanujanuj on 20/10/17.
 */

public class NetworkUtils {
    final static String MEETING_COUNT_URL="https://trackme-22c21.firebaseio.com/users";

    public static URL buildUrl(){
        Uri builtUri=Uri.parse(MEETING_COUNT_URL);
        URL url=null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlForDetails(String detailId){
        Uri builtUri=Uri.parse(MEETING_COUNT_URL).buildUpon()
                .appendEncodedPath("/"+detailId)
                .appendEncodedPath("/meetingcount.json")
                .build();

        URL url=null;
        try{
            url=new URL(builtUri.toString());

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildUrlForMeetingDetails(String detailId){
        Uri builtUri=Uri.parse(MEETING_COUNT_URL).buildUpon()
                .appendEncodedPath("/"+detailId)
                .appendEncodedPath("/meetings.json")
                .build();

        URL url=null;
        try{
            url=new URL(builtUri.toString());

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
