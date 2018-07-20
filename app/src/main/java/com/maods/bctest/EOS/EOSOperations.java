package com.maods.bctest.EOS;

import android.app.Notification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.maods.bctest.ChainCommonOperations;
import com.maods.bctest.GlobalUtils;

import java.util.List;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSOperations implements ChainCommonOperations {
    private static final String TAG="EOSOperations";

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
            sb.append("/"+EOSUtils.VERSION+"/"+EOSUtils.API_CHAIN+"/"+EOSUtils.ACTION_GET_INFO);
            String url=sb.toString();
            String content=GlobalUtils.getContentFromUrl(url);
            Log.i(TAG,"geting info from:"+url+",content:"+content);
            if(!TextUtils.isEmpty(content)){
                return content;
            }
        }
        return null;
    }
}
