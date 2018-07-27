package com.maods.bctest.EOS;

import android.app.Notification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.maods.bctest.ChainCommonOperations;
import com.maods.bctest.GlobalUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSOperations implements ChainCommonOperations {
    private static final String TAG="EOSOperations";

    public static final String ACTION_GET_INFO="get_info";
    public static final String ACTION_GET_ACCOUNT="get_account";
    public static final String ACTION_GET_BLOCK="get_block";
    public static final String ACTION_GET_ABI="get_abi";
    public static final String ACTION_GET_CODE="get_code";
    public static final String ACTION_GET_TABLE_ROWS="get_table_rows";
    public static final String ACTION_GET_RAM_PRICE="get_ram_price";        //actually this's not HTTP API, just for easy use
    public static final String ACTION_GET_PRODUCERS="get_producers";

    private static final String PARAM_ACCOUNT_NAME="account_name";
    private static final String PARAM_BLOCK_NUMBER_OR_ID="block_num_or_id";
    private static final String PARAM_CODE_AS_WASM="code_as_wasm";
    private static final String CODE_AS_WASM="false";
    private static final String PARAM_SCOPE="scope";
    private static final String PARAM_TABLE="table";
    private static final String PARAM_CODE="code";
    private static final String PARAM_JSON="json";
    private static final String RESULT_AS_JSON="true";
    private static final String ACCOUNT_EOSIO="eosio";
    private static final String TABLE_RAMMARKET="rammarket";
    @Override
    public List<String> getServerNode(){
        return EOSUtils.getAvailableServers();
    }

    /**
     * Can't be called in UI thread
     * may return null or empty String if failed
     * will call "http://127.0.0.1:8888/v1/chain/get_info"
     */
    public static String getInfo(){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_INFO);
            String url=sb.toString();
            String content=GlobalUtils.getContentFromUrl(url);
            Log.i(TAG,"geting info from:"+url+",content:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread
     * may return null or empty String if failed
     * curl http://mainnet.eoscanada.com/v1/chain/get_producers -X POST -d {\"json\":\"true\"}
     */
    public static String getProducers(){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_PRODUCERS);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_JSON,RESULT_AS_JSON);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting getProducers from:"+url+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }
    /**
     * Can't be called in UI thread.
     * Get Account info.
     * ex. curl http://jungle.cryptolions.io/v1/chain/get_account -X POST -d {"account_name":"xxxxx"}
     */
    public static String getAccount(String accountName){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_ACCOUNT);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_ACCOUNT_NAME,accountName);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting account from:"+url+" for account:"+accountName+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread.
     * Get Block Num.
     * ex. curl https://mainnet.eoscannon.io/v1/chain/get_block -X POST -d {"block_num_or_id":"5"}
     */
    public static String getBlock(String blockNum){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_BLOCK);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_BLOCK_NUMBER_OR_ID,blockNum);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting block from:"+url+"for block "+blockNum+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread.
     * Get ABI
     * ex. curl https://mainnet.eoscannon.io/v1/chain/get_abi -X POST -d {"account_name":"eosio"}
     */
    public static String getABI(String accountName){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_ABI);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_ACCOUNT_NAME,accountName);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting ABI from:"+url+"for account: "+accountName+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread.
     * Get Code
     * ex. curl https://mainnet.eoscannon.io/v1/chain/get_abi -X POST -d {"account_name":"eosio"}
     */
    public static String getCode(String accountName){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_CODE);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_ACCOUNT_NAME,accountName);
            params.put(PARAM_CODE_AS_WASM,CODE_AS_WASM);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting Code from:"+url+"for account: "+accountName+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread.
     * Get Table rows
     * ex. curl http://mainnet.eoscanada.com/v1/chain/get_table_rows -X POST -d {\"code\":\"eosio\",\"scope\":\"eosio\",\"table\":\"rammarket\",\"json\":\"true\"}
     */
    public static String getTableRows(String accountName,String contractName,String tableName){
        List<String>servers=EOSUtils.getAvailableServers();
        if(servers.size()==0){
            return null;
        }
        for(int i=0;i<servers.size();i++){
            String server=servers.get(i);
            StringBuilder sb=new StringBuilder(server);
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+ACTION_GET_TABLE_ROWS);
            String url=sb.toString();
            HashMap<String,String> params=new HashMap<String,String>();
            params.put(PARAM_SCOPE,accountName);
            params.put(PARAM_CODE,contractName);
            params.put(PARAM_TABLE,tableName);
            params.put(PARAM_JSON,RESULT_AS_JSON);
            String content=GlobalUtils.postToServer(url,params);
            Log.i(TAG,"geting Table rows from:"+url+"for account: "+accountName+",contract:"+contractName+",table:"+tableName+",result:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }

    /**
     * Can't be called in UI thread.
     * Get RamPrice
     * the result is for every kB
     * Actually use get_table_rows get needed info, then calculate it according bancor
     * use: curl http://mainnet.eoscanada.com/v1/chain/get_table_rows -X POST -d {\"code\":\"eosio\",\"scope\":\"eosio\",\"table\":\"rammarket\",\"json\":\"true\"}
     *                           EOS balance
     * then: RAM price (/k)= -----------------------
     *                           RAM left  * 1024
     */
    public static String getRamPrice(){
        StringBuilder result=new StringBuilder();
        String jsonStr=getTableRows(ACCOUNT_EOSIO,ACCOUNT_EOSIO,TABLE_RAMMARKET);
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        try {
            JSONObject json=new JSONObject(jsonStr);
            JSONArray rows=json.getJSONArray("rows");
            JSONObject data=rows.getJSONObject(0);
            JSONObject base=data.getJSONObject("base");
            String ramBalance=base.getString("balance");
            JSONObject quote = data.getJSONObject("quote");
            String eosBalance=quote.getString("balance");
            float ram=0;
            float eos=0;
            try {
                //remove " RAM" and " EOS" from the string.
                ramBalance=ramBalance.substring(0,ramBalance.length()-4);
                eosBalance=eosBalance.substring(0,eosBalance.length()-4);
                ram = Float.parseFloat(ramBalance);
                eos = Float.parseFloat(eosBalance);
                float ramPrice=eos/ram;//price for every Byte.
                ramPrice=ramPrice*1024;//return value is for every KB.
                result.append("RAM price:"+ramPrice);
                result.append("\nOriginal data:"+jsonStr);
            }catch(NumberFormatException e){
                Log.e(TAG,"number format error，ram:"+ram+",eos:"+eos);
                return jsonStr;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
