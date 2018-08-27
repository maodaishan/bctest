package com.maods.bctest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import retrofit2.http.HTTP;

/**
 * Created by MAODS on 2018/7/20.
 */

public class GlobalUtils {
    private static final String TAG="GlobalUtils";
    private static int CONNECTION_TIMEOUT=5*1000;
    private static final String ENCODE="UTF-8";

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

    /**
     * POST to server and get result
     */
    public static String postToServer(String urlInput,Map<String,String> inputs){
        byte[] data = null;
        try {
            data=getRequestData(inputs).getBytes(ENCODE);//get data to post
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder result=new StringBuilder();
        HttpURLConnection httpURLConnection=null;
        OutputStream outputStream=null;
        InputStreamReader inptStream=null;
        try {
            URL url = new URL(urlInput);

            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            //set request content type: text
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK
                    || response== HttpURLConnection.HTTP_ACCEPTED) {
                inptStream = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader reader=new BufferedReader(inptStream);
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line);
                }
            }else{
                StringBuilder sbErr=new StringBuilder();
                InputStream errIs=httpURLConnection.getErrorStream();
                Log.i(TAG,"errIs:"+errIs);
                if(errIs!=null){
                    int len;
                    byte[] buf=new byte[512];
                    while( (len=errIs.read(buf))>0){
                        sbErr.append(new String(buf));
                        for(int i=0;i<buf.length;i++){
                            buf[i]=0;
                        }
                    }
                    errIs.close();
                    Log.i(TAG,"error rsp:"+sbErr.toString());
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }finally {
            try {
                if (inptStream != null) {
                    inptStream.close();
                }
                if(outputStream!=null){
                    outputStream.close();
                }
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    private static String getRequestData(Map<String,String>params){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stringBuffer.append("{");
            for(Map.Entry<String, String> entry : params.entrySet()) {
                String key=entry.getKey();
                String value=entry.getValue();
                stringBuffer.append("\""+key+"\"")
                        .append(":");
                if(!value.startsWith("{") && !value.startsWith("[")
                        && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                    stringBuffer.append("\"");
                }
                stringBuffer.append(value);
                if(!value.startsWith("{") && !value.startsWith("[")
                        && !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                    stringBuffer.append("\"");
                }
                stringBuffer.append(",");
            }
            //delete last ","
            if(stringBuffer.length()>0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
            stringBuffer.append("}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result=stringBuffer.toString();
        Log.i(TAG,"params:"+result);
        return result;
        /*String result=null;
        try {
            result= URLEncoder.encode(stringBuffer.toString(),ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;*/
    }

    public static void showAlertMsg(Context context, int msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
