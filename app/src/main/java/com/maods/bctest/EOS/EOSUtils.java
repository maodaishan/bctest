package com.maods.bctest.EOS;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.maods.bctest.BCTestApp;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.GlobalUtils;

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
    public static final String NET_EOS_MAIN_NET="eos_main_net";
    public static final String NET_EOS_KYLIN_TEST_NET="kylin_test_net";
    public static final String NET_EOS_BOS_MAIN_NET="bos_main_net";
    public static final String NET_EOS_BOS_TEST_NET="bos_test_net";
    public static final String NET_EOS_MEETONE_MAIN_NET="meetone_main_net";
    public static final String TOKEN_EOS="EOS";
    public static final String TOKEN_BOS="BOS";
    public static final String TOKEN_MEETONE="MEETONE";
    public static int MAINNET=1;
    public static int TESTNET=2;
    private static final String[] MAIN_NET_CANDIDATE_NODES=new String[]{
            "http://api.eosbeijing.one",
            "http://api.jeda.one",
            "http://eosapi.nodepacific.com:8888",
            "http://api-mainnet.starteos.io",
            "http://eos.unlimitedeos.com:7777",
            "http://publicapi-mainnet.eosauthority.com",
            "http://api.cypherglass.com",
            "http://eu.eosdac.io",
            "http://api.eostitan.com",
            "http://api.oraclechain.io",
            "http://eos.eoscafeblock.com",
            "http://eos.greymass.com",
            "http://api-mainnet.eospacex.com",
            "http://peer1.eoshuobipool.com:8181",
            "http://api.bitmars.one",
            "http://mainnet.libertyblock.io:8888",
            "http://mainnet.eosio.sg",
            "http://bp.cryptolions.io",
            "http://mars.fn.eosbixin.com",
            "http://node2.liquideos.com",
            "http://mainnet.eoscanada.com"
    };
    private static final String[] KYLIN_TEST_NET_CANDIDATE_NOTES=new String[]{
        "http://39.108.231.157:30065",
            "http://api.jeda.one",
            "http://eosapi.nodepacific.com:8888",
            "http://api.eostribe.io",
            "http://fn001.eossv.org:80",
            "http://api-mainnet.starteos.io",
            "http://api.eosn.io",
            "http://api.tokenika.io",
            "http://mainnet.eoscanada.com"
    };
    private static final String[] BOS_MAIN_NET_CANDIDATE_NOTES=new String[]{
            "https://api.bos.eosrio.io",
            "https://api.bossweden.org",
            "http://bosapi-one.eosstore.co:8888",
            "http://bosapi-two.eosstore.co:8888",
            "https://api.hellobos.one",
            "http://api.eoshexagon.com:20888",
            "https://bosmatrix.blockmatrix.network",
            "https://api.bos42.io",
            "https://api-bos.oraclechain.io",
            "http://bosafrique.eosnairobi.io:9588",
            "https://bos-api.eoseoul.io"
    };
    private static final String[] BOS_TEST_NET_CANDIDATE_NOTES=new String[]{
            "http://47.254.82.241:80",
            "http://47.254.134.167:80",
            "http://49.129.133.66:80",
            "http://8.208.9.182:80",
            "http://47.91.244.124:80",
            "http://120.197.130.117:8020",
            "http://bos-testnet.meet.one:8888",
            "http://bos-testnet.mytokenpocket.vip:8890",
            "https://bos-testnet.eosphere.io",
            "https://boscore.eosrio.io",
            "https://api.bostest.alohaeos.com"
    };
    private static final String[] MEETONE_MAIN_NET_CANDIDATE_NOTES=new String[]{
            "http://mainnet.eosio.sg",
            "http://mars.fn.eosbixin.com",
            "http://api1.acroeos.one",
            "https://telosseed.ikuwara.com:8889",
            "http://bp.cryptolions.io",
            "http://api.eosn.io",
            "http://api-mainnet.starteos.io",
            "http://mainnet.eosnairobi.io",
            "http://api-meetone.eossv.org",
            "http://api.nytelos.com",
            "http://api-eos.blckchnd.com",
            "http://meetone.eossweden.eu",
            "http://api.eostribe.io",
            "http://api-meetone.eosbeijing.one",
            "http://node1.eosphere.io",
            "http://mainnet.genereos.io",
            "http://api.eosvenezuela.io:8888"
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

    private static final String PREF_CURRENT_NET="current_net";
    public static final String[] AVAILABLE_EOS_NETS=new String[]{
            NET_EOS_MAIN_NET,
            NET_EOS_KYLIN_TEST_NET,
            NET_EOS_BOS_MAIN_NET,
            NET_EOS_BOS_TEST_NET,
            NET_EOS_MEETONE_MAIN_NET
    };

    public static String getCurrentNet(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(BCTestApp.getContext());
        String net=pref.getString(PREF_CURRENT_NET,NET_EOS_MAIN_NET);
        Log.i(TAG,"get current net:"+net);
        return net;
    }

    public static void setCurrentNet(Context context,String newNet){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_CURRENT_NET,newNet).commit();
        sServerTested=false;
        Log.i(TAG,"set current net:"+newNet);
    }
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
        String[] availableNets;
        final int testServerCount;
        switch(getCurrentNet()){
            case NET_EOS_MAIN_NET:
                availableNets=MAIN_NET_CANDIDATE_NODES;
                testServerCount=5;
                break;
            case NET_EOS_KYLIN_TEST_NET:
                availableNets=KYLIN_TEST_NET_CANDIDATE_NOTES;
                testServerCount=1;
                break;
            case NET_EOS_BOS_MAIN_NET:
                availableNets=BOS_MAIN_NET_CANDIDATE_NOTES;
                testServerCount=1;
                break;
            case NET_EOS_BOS_TEST_NET:
                availableNets=BOS_TEST_NET_CANDIDATE_NOTES;
                testServerCount=1;
                break;
            case NET_EOS_MEETONE_MAIN_NET:
                availableNets=MEETONE_MAIN_NET_CANDIDATE_NOTES;
                testServerCount=1;
                break;
            default:
                availableNets=new String[]{};
                testServerCount=0;
                break;
        }
        for(int i=0;i<availableNets.length;i++){
            final String target=availableNets[i];
            URL url=null;
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    //String result=EOSOperations.getActions(target,"eosio",-1,-1);
                    String result= EOSOperations.getInfo(target);
                    if(!TextUtils.isEmpty(result)){
                        synchronized (mServerTestSync) {
                            sServerNodes.add(target);
                            sTestedNode++;
                            if (sTestedNode >= testServerCount) {
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

    public static String getSysTokenName(){
        String token;
        switch(getCurrentNet()){
            case NET_EOS_MAIN_NET:
            case NET_EOS_KYLIN_TEST_NET:
                token=TOKEN_EOS;
                break;
            case NET_EOS_BOS_MAIN_NET:
            case NET_EOS_BOS_TEST_NET:
                token=TOKEN_BOS;
                break;
            case NET_EOS_MEETONE_MAIN_NET:
                token=TOKEN_MEETONE;
                break;
            default:
                token=TOKEN_EOS;
                break;
        }
        return token;
    }

    /*public static List<String> getTestNetServers(){
        ArrayList<String>result=new ArrayList<String>();
        result.add("https://api-kylin.eosasia.one");
        return result;
    }*/

    public static boolean isAccountNameLeagle(String input){
        if(input.startsWith("eos") || input.contains(".")){//for eos system accounts.
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

    public static double getDoubleFromAsset(String asset){
        switch(getCurrentNet()){
            case NET_EOS_MEETONE_MAIN_NET:
                if(asset.endsWith("RAM")){
                    return Double.parseDouble(asset.substring(0,asset.length()-4));
                }else {
                    return Double.parseDouble(asset.substring(0, asset.length() - 8));
                }
            default:
                return Double.parseDouble(asset.substring(0,asset.length()-4));
        }
    }

    public static int getNetType(){
        switch(getCurrentNet()){
            case NET_EOS_MAIN_NET:
            case NET_EOS_BOS_MAIN_NET:
            case NET_EOS_MEETONE_MAIN_NET:
                return MAINNET;
            case NET_EOS_KYLIN_TEST_NET:
            case NET_EOS_BOS_TEST_NET:
                return TESTNET;
            default:
                return MAINNET;
        }
    }
}
