package com.maods.bctest.EOS;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MAODS on 2018/8/23.
 */

public class EOSAbiHelper {
    private JSONObject mMain;
    private String mAccount;
    private String mVersion;
    private JSONArray mTypes;
    private JSONArray mStructs;
    private JSONArray mActions;
    private JSONArray mTables;
    private JSONArray mRicardianClauses;
    private JSONArray mErrMsgs;
    private JSONArray mAbiExtensions;
    public EOSAbiHelper(String abi) throws JSONException{
        if(TextUtils.isEmpty(abi)){
            return;
        }
        JSONObject json=new JSONObject(abi);
        mMain=json.getJSONObject("abi");
        mAccount=json.getString("account_name");
        if(mMain!=null){
            mVersion=mMain.getString("version");
            mTypes=mMain.getJSONArray("types");
            mStructs=mMain.getJSONArray("structs");
            mActions=mMain.getJSONArray("actions");
            mTables=mMain.getJSONArray("tables");
            mRicardianClauses=mMain.getJSONArray("ricardian_clauses");
            mErrMsgs=mMain.getJSONArray("error_messages");
            mAbiExtensions=mMain.getJSONArray("abi_extensions");
        }
    }

    public JSONArray getActions(){
        return mActions;
    }

    public JSONObject getStructByName(String name){
        if(TextUtils.isEmpty(name) || mStructs==null){
            return null;
        }
        JSONObject struct=null;
        for(int i=0;i<mStructs.length();i++){
            try {
                struct=mStructs.getJSONObject(i);
                if(struct.getString("name").equalsIgnoreCase(name)){
                    return struct;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
