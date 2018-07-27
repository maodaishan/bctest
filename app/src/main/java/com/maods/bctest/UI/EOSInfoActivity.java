package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.R;

import java.util.regex.Pattern;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSInfoActivity extends Activity {
    private static final String TAG="EOSInfoActivity";

    private String mAction;
    private String mContent;
    private boolean mNeedInput=false;
    private String mAccountName=null;
    private String mBlockNum=null;
    private String mContractName=null;
    private String mTableName=null;

    private TextView mContentView;
    private AlertDialog mAlertDialog;
    private EditText mEdit1;
    private EditText mEdit2;
    private EditText mEdit3;
    private Button mBtn;
    private Button mBtnCopy;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        mAction=getIntent().getStringExtra(GlobalConstants.EXTRA_KEY_ACTION);

        setContentView(R.layout.eos_info);
        mContentView=(TextView)findViewById(R.id.content);
        mEdit1=(EditText)findViewById(R.id.edit1);
        mEdit2=(EditText)findViewById(R.id.edit2);
        mEdit3=(EditText)findViewById(R.id.edit3);
        mBtn=(Button)findViewById(R.id.btn);
        mBtnCopy=(Button)findViewById(R.id.copy);
        mBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=mContentView.getText().toString();
                if(!TextUtils.isEmpty(content)) {
                    ClipboardManager cMgr = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("content",content);
                    cMgr.setPrimaryClip(data);
                    Toast.makeText(EOSInfoActivity.this,R.string.content_copyed_to_clipboard,Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(mAction.equals(EOSOperations.ACTION_GET_ACCOUNT)
                ||mAction.equals(EOSOperations.ACTION_GET_BLOCK)
                ||mAction.equals(EOSOperations.ACTION_GET_ABI)
                ||mAction.equals(EOSOperations.ACTION_GET_CODE)
                ||mAction.equals(EOSOperations.ACTION_GET_TABLE_ROWS)){
            mBtn.setVisibility(View.VISIBLE);
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnClicked();
                }
            });
            mEdit1.setVisibility(View.VISIBLE);
            int hint=0;
            switch(mAction){
                case EOSOperations.ACTION_GET_ACCOUNT:
                case EOSOperations.ACTION_GET_ABI:
                case EOSOperations.ACTION_GET_CODE:
                    hint=R.string.input_account_name;
                    break;
                case EOSOperations.ACTION_GET_BLOCK:
                    mEdit1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    hint=R.string.input_block_num;
                    break;
                case EOSOperations.ACTION_GET_TABLE_ROWS:
                    hint=R.string.input_account_name;
                    mEdit2.setVisibility(View.VISIBLE);
                    mEdit2.setHint(R.string.input_contract_name);
                    mEdit3.setVisibility(View.VISIBLE);
                    mEdit3.setHint(R.string.input_table_name);
                    break;
                default:
                    break;
            }
            mEdit1.setHint(hint);
            mNeedInput=true;
        }

        if(!mNeedInput) {
            startAction();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
    }

    private void onBtnClicked(){
        switch(mAction){
            case EOSOperations.ACTION_GET_ACCOUNT:
                mAccountName=mEdit1.getText().toString();
                boolean isAccountNameLegel=isAccountNameLeagle(mAccountName);
                if(!isAccountNameLegel){
                    showAlertMsg(R.string.eos_account_length_err);
                }else {
                    //Log.i(TAG,"mAccountName:"+mAccountName);
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_BLOCK:
                mBlockNum=mEdit1.getText().toString();
                int num=Integer.valueOf(mBlockNum);
                if(num<=0){
                    showAlertMsg(R.string.block_num_error);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_ABI:
                mAccountName=mEdit1.getText().toString();
                if(TextUtils.isEmpty(mAccountName)){
                    showAlertMsg(R.string.get_abi_account_err);
                }else {
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_CODE:
                mAccountName=mEdit1.getText().toString();
                if(TextUtils.isEmpty(mAccountName)){
                    showAlertMsg(R.string.get_abi_account_err);
                }else {
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_TABLE_ROWS:
                mAccountName=mEdit1.getText().toString();
                mContractName=mEdit2.getText().toString();
                mTableName=mEdit3.getText().toString();
                if(TextUtils.isEmpty(mAccountName)
                        ||TextUtils.isEmpty(mContractName)
                        ||TextUtils.isEmpty(mTableName)){
                    showAlertMsg(R.string.get_table_row_input_err);
                }else{
                    startAction();
                }
            default:
                break;
        }
    }
    private boolean isAccountNameLeagle(String input){
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

    private void startAction(){
        //show processing dialog
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.processing);
        mAlertDialog=builder.create();
        mAlertDialog.show();

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                switch(mAction){
                    case EOSOperations.ACTION_GET_INFO:
                        mContent = EOSOperations.getInfo();
                        break;
                    case EOSOperations.ACTION_GET_PRODUCERS:
                        mContent=EOSOperations.getProducers();
                        break;
                    case EOSOperations.ACTION_GET_ACCOUNT:
                        mContent=EOSOperations.getAccount(mAccountName);
                        //Log.i(TAG,"mContent:"+mContent);
                        break;
                    case EOSOperations.ACTION_GET_BLOCK:
                        mContent=EOSOperations.getBlock(mBlockNum);
                        break;
                    case EOSOperations.ACTION_GET_ABI:
                        mContent=EOSOperations.getABI(mAccountName);
                        break;
                    case EOSOperations.ACTION_GET_CODE:
                        mContent=EOSOperations.getCode(mAccountName);
                        break;
                    case EOSOperations.ACTION_GET_TABLE_ROWS:
                        mContent=EOSOperations.getTableRows(mAccountName,mContractName,mTableName);
                        break;
                    case EOSOperations.ACTION_GET_RAM_PRICE:
                        mContent=EOSOperations.getRamPrice();
                        break;
                    default:
                        mContent=null;
                        break;
                }
                EOSInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateContent();
                    }
                });
            }
        });
        t.start();
    }

    private void updateContent(){
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
        mBtnCopy.setEnabled(true);
        if(!TextUtils.isEmpty(mContent)){
            StringBuilder sb=new StringBuilder();
            String[]items=mContent.split(",");
            for(int i=0;i<items.length;i++){
                sb.append(items[i]+"\n");
            }
            mContentView.setText(sb.toString());
        }else{
            mContentView.setText(R.string.get_info_error);
        }
    }
    private void showAlertMsg(int msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
