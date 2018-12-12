package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.maods.bctest.EOS.EOSAbiHelper;
import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.GlobalUtils;
import com.maods.bctest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by MAODS on 2018/8/23.
 */

public class EOSBrowser extends Activity {
    private static final String TAG="EOSBrowser";
    private static final String VALUE="eos_broswer_execute_action_value";

    private LinearLayout mTotalView;
    private LinearLayout mBrowseContainer;
    private EditText mAccountView;
    private Button mQueryButton;
    private ListView mActionList;
    private ArrayAdapter<String> mActionAdapter;
    private LinearLayout mActionContainer;
    private TextView mActionInfo;
    private ListView mActionArgsList;
    private SimpleAdapter mExecuteAdapter;
    private EditText mAuthAccount;
    private EditText mAuthPermission;
    private Button mExecAction;
    private TextView mResultView;
    private AlertDialog mAlertDialog;

    private String mAccount;
    private EOSAbiHelper mAbiHelper;
    private JSONArray mActions;
    private JSONObject mAction;
    private List<Map<String,JSONObject>> mExecuteActionItems= new ArrayList<>();
    private String mExecuteResult;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.eos_browser);
        mTotalView=(LinearLayout)findViewById(R.id.total);
        mBrowseContainer=(LinearLayout)findViewById(R.id.browse_container);
        mAccountView=(EditText)findViewById(R.id.account);
        mQueryButton=(Button)findViewById(R.id.button_query);
        mActionList=(ListView)findViewById(R.id.action_list);
        mQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccount=mAccountView.getText().toString();
                if(!EOSUtils.isAccountNameLeagle(mAccount)){
                    GlobalUtils.showAlertMsg(EOSBrowser.this,R.string.eos_account_length_err);
                }else{
                    if(mAlertDialog!=null){
                        mAlertDialog.dismiss();
                        mAlertDialog=null;
                    }
                    showProcessing();
                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String abi= EOSOperations.getABI(mAccount);
                            try {
                                mAbiHelper=new EOSAbiHelper(abi);
                            } catch (JSONException e) {
                                EOSBrowser.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GlobalUtils.showAlertMsg(EOSBrowser.this,R.string.get_abi_err);
                                        if(mAlertDialog!=null){
                                            mAlertDialog.dismiss();
                                            mAlertDialog=null;
                                        }
                                    }
                                });
                                e.printStackTrace();
                                return;
                            }
                            EOSBrowser.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //mAuthAccount.setText(mAccount);
                                    mAuthPermission.setText("active");
                                    updateActionsList();
                                }
                            });
                        }
                    });
                    t.start();
                }
            }
        });
        mActionContainer=(LinearLayout)findViewById(R.id.action_container);
        mActionInfo=(TextView)findViewById(R.id.action_info);
        mActionArgsList=(ListView)findViewById(R.id.action_args_list);
        mAuthAccount=(EditText)findViewById(R.id.auth_account);
        mAuthPermission=(EditText)findViewById(R.id.auth_permission);
        mExecAction=(Button)findViewById(R.id.execute);
        mResultView=(TextView)findViewById(R.id.execute_result);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
    }
    private void updateActionsList(){
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
        if(mAbiHelper==null){
            return;
        }
        mActions=mAbiHelper.getActions();
        if(mActions==null){
            return;
        }
        int len=mActions.length();
        String[] actionsLabel=new String[len];
        try {
            for(int i=0;i<len;i++) {
                JSONObject action = mActions.getJSONObject(i);
                actionsLabel[i]=action.getString("name");
            }
        } catch (JSONException e) {
            Log.i(TAG,"get action list error,e:"+e);
            e.printStackTrace();
        }
        mActionAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,actionsLabel);
        mActionList.setAdapter(mActionAdapter);
        mActionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject json=mActions.getJSONObject(position);
                    if(json!=null){
                        onActionItemClicked(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //set listview height
        int height = 0;
        int count = mActionAdapter.getCount();
        for(int i=0;i<count;i++){
            View temp = mActionAdapter.getView(i,null,mActionList);
            temp.measure(0,0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mActionList.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = height;
        mActionList.setLayoutParams(params);
        mActionList.invalidate();
    }

    private void onActionItemClicked(JSONObject action){
        String ricardianContract=null;
        String type=null;
        mActionContainer.setVisibility(View.VISIBLE);
        mAction=action;
        try {
            ricardianContract=action.getString("ricardian_contract");
            type=action.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(ricardianContract)){
            mActionInfo.setText(ricardianContract);
        }
        if(!TextUtils.isEmpty(type)){
            updateExecuteActionList(type);
        }
        mActionContainer.invalidate();
        mTotalView.invalidate();
        mExecAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog!=null){
                    mAlertDialog.dismiss();
                    mAlertDialog=null;
                }
                showProcessing();
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onExecuteActionClick();
                    }
                });
                t.start();
            }
        });
    }

    private void updateExecuteActionList(String type){
        mExecuteActionItems.clear();
        if(mExecuteAdapter!=null) {
            mActionArgsList.invalidate();
        }
        JSONObject struct=mAbiHelper.getStructByName(type);
        if(struct==null){
            return;
        }
        try {
            JSONArray fields=struct.getJSONArray("fields");
            if(fields==null){
                return;
            }
            for(int i=0;i<fields.length();i++){
                JSONObject field=fields.getJSONObject(i);
                HashMap<String,JSONObject> item=new HashMap<>();
                item.put("item",field);
                mExecuteActionItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mExecuteAdapter=new ExecuteActionAdapter(this,mExecuteActionItems,R.layout.eos_execute_item,new String[]{"item"},new int[]{R.id.text1});
        mActionArgsList.setAdapter(mExecuteAdapter);
        //set listview height
        int height = 0;
        int count = mExecuteAdapter.getCount();
        for(int i=0;i<count;i++){
            View temp = mExecuteAdapter.getView(i,null,mActionArgsList);
            temp.measure(0,0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mActionArgsList.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = height;
        mActionArgsList.setLayoutParams(params);
        mActionArgsList.invalidate();
    }

    private void onExecuteActionClick(){
        Map<String,String>args=new HashMap<>();
        for(int i=0;i<mExecuteActionItems.size();i++){
            JSONObject item= (JSONObject) mExecuteActionItems.get(i).get("item");
            try {
                String key=item.getString("name");
                String value=item.getString(VALUE);
                if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
                    Toast.makeText(this,R.string.empty_input,Toast.LENGTH_SHORT).show();
                    return;
                }
                args.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String contract=null;
        try {
            contract=mAction.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG,"get contract name fail,e:"+e);
            return;
        }
        List<Map<String,String>>auths=new ArrayList<Map<String,String>>();
        Map<String,String>auth=new HashMap<String,String>();
        auth.put(EOSOperations.ACTOR,mAuthAccount.getText().toString());
        auth.put(EOSOperations.PERMISSION,mAuthPermission.getText().toString());
        auths.add(auth);
        mExecuteResult=EOSOperations.executeAction(this,true,mAccount,contract,args,auths);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mAlertDialog!=null){
                    mAlertDialog.dismiss();
                    mAlertDialog=null;
                }
                mActionInfo.setText(mExecuteResult);
                mActionInfo.invalidate();
            }
        });
    }

    private void saveInputForActionArgs(int position,String input){
        JSONObject item= (JSONObject) mExecuteActionItems.get(position).get("item");
        try {
            item.put(VALUE,input);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ExecuteActionAdapter extends SimpleAdapter {

        public ExecuteActionAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

        }

        @Override
        public int getCount(){
            return mExecuteActionItems.size();
        }

        public Object getItem(int position) {
            Map<String,JSONObject>item= mExecuteActionItems.get(position);
            return item.get("item");
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView){
                convertView = EOSBrowser.this.getLayoutInflater().inflate(R.layout.eos_execute_item,null);
                holder =new ViewHolder(convertView,position);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            JSONObject field= (JSONObject) getItem(position);
            try {
                holder.name.setText("name:"+field.getString("name")+"    type:"+field.getString("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
    class ViewHolder{
        TextView name;
        EditText editText;
        public ViewHolder(View view,int position){
            name = (TextView) view.findViewById(R.id.text1);
            editText= (EditText) view.findViewById(R.id.edit);
            editText.setTag(position);
            editText.addTextChangedListener(new TextSwitcher(this));
        }
    }

    class TextSwitcher implements TextWatcher {
        private ViewHolder mHolder;

        public TextSwitcher(ViewHolder mHolder) {
            this.mHolder = mHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int position = (int) mHolder.editText.getTag();
            EOSBrowser.this.saveInputForActionArgs(position, s.toString());
        }
    }

    private void showProcessing(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.processing);
        mAlertDialog=builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
    }
}
