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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSUtils {
    private static final String TAG="EOSUtils";
    public static int MAINNET=1;
    public static int TESTNET=2;

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

    private static HashMap<String,EosNetParams> sEosNetsMap=new HashMap<String,EosNetParams>();
    static{
        EosNetParams eosMainNet=new EOSMainNet();
        sEosNetsMap.put(eosMainNet.getNetName(),eosMainNet);
        EOSKylinTestNet kylinTestNet=new EOSKylinTestNet();
        sEosNetsMap.put(kylinTestNet.getNetName(),kylinTestNet);
        BOSMainNet bosMainNet=new BOSMainNet();
        sEosNetsMap.put(bosMainNet.getNetName(),bosMainNet);
        BOSTestNet bosTestNet=new BOSTestNet();
        sEosNetsMap.put(bosTestNet.getNetName(),bosTestNet);
        MEETONEMainNet meetone=new MEETONEMainNet();
        sEosNetsMap.put(meetone.getNetName(),meetone);
    }
    public interface EosNetParams{
        public String getNetName();
        public String[] getCandidateNodes();
        public String getSysTokenName();
        public double getDoubleFromAsset(String asset);
        public int getNetType();
    }

    public static String[] getAvailableNets(){
        Iterator<Map.Entry<String,EosNetParams>> ite=sEosNetsMap.entrySet().iterator();
        ArrayList<String> nets=new ArrayList<String>();
        while(ite.hasNext()){
            Map.Entry<String,EosNetParams> entry=ite.next();
            nets.add(entry.getKey());
        }
        String[] result=new String[nets.size()];
        for(int i=0;i<nets.size();i++){
            String net=nets.get(i);
            result[i]=net;
        }
        return result;
    }
    public static String getCurrentNetName(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(BCTestApp.getContext());
        String net=pref.getString(PREF_CURRENT_NET,"eos_main_net");
        Log.i(TAG,"get current net:"+net);
        return net;
    }

    public static void setCurrentNet(Context context,String newNet){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_CURRENT_NET,newNet).commit();
        sServerTested=false;
        Log.i(TAG,"set current net:"+newNet);
    }

    public static EosNetParams getCurrentNetParams(){
        String name=getCurrentNetName();
        return sEosNetsMap.get(name);
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
        String[] availableNets=new String[0];
        final int testServerCount=1;
        EosNetParams netParams=getCurrentNetParams();
        if(netParams!=null){
            availableNets=netParams.getCandidateNodes();
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
        EosNetParams params=getCurrentNetParams();
        return params.getSysTokenName();
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
        EosNetParams params=getCurrentNetParams();
        return params.getDoubleFromAsset(asset);
    }

    public static int getNetType(){
        EosNetParams params=getCurrentNetParams();
        return params.getNetType();
    }
}
