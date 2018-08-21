package com.maods.bctest.EOS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by MAODS on 2018/8/14.
 */

public class Action {
    private static final String ACCOUNT="account";
    private static final String NAME="name";
    private static final String AUTHORIZATION="authorization";
    private static final String DATA="data";
    //private static final String HEX_DATA="hex_data";
    private static final String ACTOR="actor";
    private static final String PERMISSION="permission";

    public String mAccount;
    public String mName;
    public List<Authorization> mAuth;
    public String mData;
    //public String mHexData;

    public class Authorization{
        public String mActor;
        public String mPermission;
    }

    public Action(String account,String name,String data,/*String hexData,*/List<Map<String,String>> authorizations){
        mAccount=account;
        mName=name;
        mData=data;
        //mHexData=data;
        mAuth=new ArrayList<Authorization>();
        for(int i=0;i<authorizations.size();i++) {
            Map<String, String> oneAuth=authorizations.get(i);
            Authorization auth=new Authorization();
            auth.mActor=oneAuth.get(ACTOR);
            auth.mPermission=oneAuth.get(PERMISSION);
            mAuth.add(auth);
        }
    }

    @Override
    public String toString(){
        JSONObject json=toJson();
        return json.toString();
    }

    public JSONObject toJson(){
        JSONObject json=new JSONObject();
        try {
            json.put(ACCOUNT,mAccount);
            json.put(NAME,mName);
            json.put(DATA,mData);
            //json.put(HEX_DATA,mHexData);
            JSONArray auth=new JSONArray();
            for(int i=0;i<mAuth.size();i++){
                Authorization a=mAuth.get(i);
                JSONObject aJson=new JSONObject();
                aJson.put(ACTOR,a.mActor);
                aJson.put(PERMISSION,a.mPermission);
                auth.put(aJson);
            }
            json.put(AUTHORIZATION,auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
