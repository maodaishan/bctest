package com.maods.bctest.EOS;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSUtils {
    private static final String TAG="EOSUtils";
    private static final String[] CANDIDATE_NODES=new String[]{
            "http://mainnet.eoscanada.com",
            "http://mainnet.eoscalgary.io",
            "http://mainnet.genereos.io",
    };
    public static final String VERSION = "v1";
    public static final String API_CHAIN = "chain";
    public static final String API_WALLET="wallet";
    public static final String API_HISTORY="history";
    public static final String API_NET="net";
    public static final String API_PRODUCER="producer";

    public static final String ACTION_GET_INFO="get_info";

    private static Object mServerTestSync=new Object();
    private static boolean sServerTested=false;
    private static List<String> sServerNodes=new ArrayList<String>();


    //Can't run in UI thread
    public static List<String> getAvailableSeeds(){
        synchronized (mServerTestSync){
            if(sServerTested){
                return sServerNodes;
            }
            String target;
            for(int i=0;i<CANDIDATE_NODES.length;i++){
                target=CANDIDATE_NODES[i];
                URL url=null;
                try {
                    StringBuilder sb=new StringBuilder(target);
                    sb.append("/"+VERSION+"/"+API_CHAIN+"/"+ACTION_GET_INFO);
                    url=new URL(sb.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        httpURLConnection.disconnect();
                        sServerNodes.add(target);
                    } else {
                        Log.d("TAG httpUrlConnection : ",httpURLConnection.getResponseCode() +"");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sServerTested=true;
        }
        return sServerNodes;
    }
}
