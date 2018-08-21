package com.maods.bctest.EOS;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAODS on 2018/8/14.
 */

public class Transaction {
    private static final String TAG="Transaction";

    private static final String REF_BLOCK_NUM="ref_block_num";
    private static final String REF_BLOCK_PREFIX="ref_block_prefix";
    private static final String EXPIRATION="expiration";
    private static final String SCOPE="scope";
    private static final String ACTIONS="actions";
    private static final String SIGNATURES="signatures";
    private static final String AUTHORIZATIONS="authorizations";

    public String mExpiration;
    public int mRefBlockNum;
    public String mRefBlockPrefix;
    public int mMaxNetUsageWords;
    public int mMaxCpuUsageMS;
    public int mDelaySec;
    public List<Action> mActions;
    public String[] mScopes=new String[0];
    public List<ContextFreeAction> mContextFreeActions=new ArrayList<ContextFreeAction>();
    public List<String> mTransactionExtensions=new ArrayList<String>();
    public List<String> mSignatures=new ArrayList<String>();
    public List<String> mContextFreeData=new ArrayList<String>();
    public List<String> mAuthorizations=new ArrayList<>();

    public Transaction(String expiration,int refBlockNum,String refBlockPrefix,List<Action>actions){
        mExpiration=expiration;
        mRefBlockNum=refBlockNum;
        mRefBlockPrefix=refBlockPrefix;
        mActions=actions;
        mMaxNetUsageWords=0;
        mMaxCpuUsageMS=0;
        mDelaySec=0;
    }

    public void setScopes(String[] scopes){
        mScopes=scopes;
    }
    public void setMaxNetUsageWords(int net){
        mMaxNetUsageWords=net;
    }
    public void setMaxCpuUsageMS(int cpu){
        mMaxCpuUsageMS=cpu;
    }
    public void setDelaySec(int delay){
        mDelaySec=delay;
    }
    public void setmContextFreeActions(List<ContextFreeAction> contextFreeActions){
        mContextFreeActions=contextFreeActions;
    }
    public void setTransactionExtensions(List<String>extensions){
        mTransactionExtensions=extensions;
    }
    public void setSignatures(List<String>sig){
        mSignatures=sig;
    }
    public void setContextFreeData(List<String>contextFreeData){
        mContextFreeData=contextFreeData;
    }
    public void setAuthorizations(List<String>authorizations){
        mAuthorizations=authorizations;
    }
    @Override
    public String toString(){
        JSONObject json=toJson();
        return json.toString();
    }

    public JSONObject toJson(){
        JSONObject json=new JSONObject();
        try {
            json.put(REF_BLOCK_NUM, mRefBlockNum);
            json.put(REF_BLOCK_PREFIX,mRefBlockPrefix);
            json.put(EXPIRATION,mExpiration);
            if(mScopes.length>0) {
                JSONArray scopes = new JSONArray();
                for (String scope : mScopes) {
                    scopes.put(scope);
                }
                json.put(SCOPE,scopes);
            }
            if(mActions.size()>0){
                JSONArray actions=new JSONArray();
                for(Action action:mActions){
                    JSONObject actionJson=action.toJson();
                    actions.put(actionJson);
                }
                json.put(ACTIONS,actions);
            }
            if(mSignatures.size()>0){
                JSONArray sigs=new JSONArray();
                for(String sig:mSignatures){
                    sigs.put(sig);
                }
                json.put(SIGNATURES,sigs);
            }
            if(mAuthorizations.size()>0){
                JSONArray auths=new JSONArray();
                for(String auth:mAuthorizations){
                    auths.put(auth);
                }
                json.put(AUTHORIZATIONS,auths);
            }
        }catch(JSONException e){
            Log.i(TAG,"Exception in toJson,e:"+e);
            e.printStackTrace();
        }
        return json;
    }
}
