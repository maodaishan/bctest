package com.maods.bctest.EOS;

import android.text.TextUtils;
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
            "http://api.eosbeijing.one",
            "http://api.oraclechain.io",
            "http://eosapi.nodepacific.com:8888",
            "http://api.eosstore.co:6789",
            "http://node2.liquideos.com",
            "http://api.eoseoul.io",
            "http://api.eostribe.io",
            "http://mainnet.eoscalgary.io",
            "http://api-mainnet.starteos.io",
            "http://eos.genesis-mining.com",
            "http://eu.eosdac.io",
            "http://api-mainnet.eosgravity.com",
            "http://eos.greymass.com",
            "http://publicapi-mainnet.eosauthority.com",
            "http://api.bp.fish"
    };
    public static final String VERSION = "v1";
    public static final String API_CHAIN = "chain";
    public static final String API_WALLET="wallet";
    public static final String API_HISTORY="history";
    public static final String API_NET="net";
    public static final String API_PRODUCER="producer";
    public static final int ACCOUNT_LENGTH=12;
    public static final String REMEMBER_WALLET_PSWD="eos_remember_wallet_pswd";

    private static Object mServerTestSync=new Object();
    private static boolean sServerTested=false;
    private static List<String> sServerNodes=new ArrayList<String>();
    private static int sTestedNode=0;


    //Can't run in UI thread
    public static List<String> getAvailableServers(){
        synchronized (mServerTestSync) {
            if (sServerTested) {
                return sServerNodes;
            }else{
                sTestedNode=0;
                sServerNodes.clear();
            }
        }
        for(int i=0;i<CANDIDATE_NODES.length;i++){
            final String target=CANDIDATE_NODES[i];
            URL url=null;
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    String result=EOSOperations.getActions(target,"eosio",-1,-1);
                    if(!TextUtils.isEmpty(result)){
                        synchronized (mServerTestSync) {
                            sServerNodes.add(target);
                            sTestedNode++;
                            if (sTestedNode >= 5) {
                                mServerTestSync.notify();
                            }
                        }
                    }
                }
            });
            t.start();
        }
        synchronized (mServerTestSync){
            try {
                mServerTestSync.wait();
            }catch(InterruptedException e){

            }
            sServerTested=true;
        }
        return sServerNodes;
    }

    public static List<String> getTestNetServers(){
        ArrayList<String>result=new ArrayList<String>();
        result.add("https://api-kylin.eosasia.one");
        return result;
    }

    public static boolean isAccountNameLeagle(String input){
        if(input.startsWith("eos")){//for eos system accounts.
            return true;
        }
        if(input.length()!=EOSUtils.ACCOUNT_LENGTH){
            return false;
        }
        for(int i=0;i<input.length();i++){
            char c=input.charAt(i);
            if(!((c>='a' && c<='z') || (c>='1' && c<='5'))){
                return false;
            }
        }
        return true;
    }
}
