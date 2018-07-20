package com.maods.bctest;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MAODS on 2018/7/20.
 */

public class GlobalUtils {
    private static final String TAG="GlobalUtils";
    private static int CONNECTION_TIMEOUT=5*1000;

    /**
     * Can't be called in UI thread
     * May return empty String if execute failed.
     * @param input, the target url
     * @return
     */
    public static String getContentFromUrl(String input){
        URL url=null;
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader is=null;
        HttpURLConnection httpURLConnection=null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                is = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader reader=new BufferedReader(is);
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                is.close();
                httpURLConnection.disconnect();
            } else {
                Log.d("TAG httpUrlConnection : ",httpURLConnection.getResponseCode() +"");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(is!=null){
                    is.close();
                }
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
