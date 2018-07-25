package com.maods.bctest.EOS;

import android.app.Notification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.maods.bctest.ChainCommonOperations;
import com.maods.bctest.GlobalUtils;

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

    private static final String PARAM_ACCOUNT_NAME="account_name";
    private static final String PARAM_BLOCK_NUMBER_OR_ID="block_num_or_id";
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
}
