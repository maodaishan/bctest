package com.maods.bctest.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
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
import com.maods.bctest.GlobalUtils;
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
    private String mWalletName=null;
    private String mArgs=null;
    private String mAccount2Name=null;
    private String mAmount=null;
    private String mMemo=null;
    private int mRamBytes=0;
    private int mEosForCpu=0;
    private int mEosForNet=0;

    private TextView mContentView;
    private AlertDialog mAlertDialog;
    private EditText mEdit1;
    private EditText mEdit2;
    private EditText mEdit3;
    private EditText mEdit4;
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
        mEdit4=(EditText)findViewById(R.id.edit4);
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
                ||mAction.equals(EOSOperations.ACTION_GET_TABLE_ROWS)
                ||mAction.equals(EOSOperations.FUNCTION_CREATE_WALLET)
                ||mAction.equals(EOSOperations.ACTION_JSON_TO_BIN)
                ||mAction.equals(EOSOperations.ACTION_TRANSFER)
                ||mAction.equals(EOSOperations.ACTION_BUYRAM)
                ||mAction.equals(EOSOperations.ACTION_SELLRAM)
                ||mAction.equals(EOSOperations.ACTION_DELEGATEBW)
                ||mAction.equals(EOSOperations.ACTION_UNDELEGATEBW)){
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
                case EOSOperations.FUNCTION_CREATE_WALLET:
                    hint=R.string.input_wallet_name;
                    break;
                case EOSOperations.ACTION_JSON_TO_BIN:
                    hint=R.string.input_account_name;
                    mEdit2.setHint(R.string.input_contract_name);
                    mEdit2.setVisibility(View.VISIBLE);
                    mEdit3.setHint(R.string.input_args);
                    mEdit3.setVisibility(View.VISIBLE);
                    break;
                case EOSOperations.ACTION_TRANSFER:
                    hint=R.string.from;
                    mEdit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setHint(R.string.to);
                    mEdit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setVisibility(View.VISIBLE);
                    mEdit3.setHint(R.string.amount);
                    mEdit3.setVisibility(View.VISIBLE);
                    mEdit4.setHint(R.string.memo);
                    mEdit4.setVisibility(View.VISIBLE);
                    break;
                case EOSOperations.ACTION_BUYRAM:
                    hint=R.string.account_pay_for_ram;
                    mEdit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setHint(R.string.account_receive_ram);
                    mEdit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setVisibility(View.VISIBLE);
                    mEdit3.setHint(R.string.how_much_ram_to_buy);
                    mEdit3.setVisibility(View.VISIBLE);
                    break;
                case EOSOperations.ACTION_SELLRAM:
                    hint=R.string.account_sell_ram;
                    mEdit2.setHint(R.string.bytes_to_sell);
                    mEdit2.setVisibility(View.VISIBLE);
                    break;
                case EOSOperations.ACTION_DELEGATEBW:
                case EOSOperations.ACTION_UNDELEGATEBW:
                    hint=R.string.account_pay_for_res;
                    mEdit1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setHint(R.string.account_receive_res);
                    mEdit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    mEdit2.setVisibility(View.VISIBLE);
                    mEdit3.setHint(R.string.eos_for_cpu);
                    mEdit3.setVisibility(View.VISIBLE);
                    mEdit4.setHint(R.string.eos_for_net);
                    mEdit4.setVisibility(View.VISIBLE);
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
                boolean isAccountNameLegel=EOSUtils.isAccountNameLeagle(mAccountName);
                if(!isAccountNameLegel){
                    GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
                }else {
                    //Log.i(TAG,"mAccountName:"+mAccountName);
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_BLOCK:
                mBlockNum=mEdit1.getText().toString();
                int num=Integer.valueOf(mBlockNum);
                if(num<=0){
                    GlobalUtils.showAlertMsg(this,R.string.block_num_error);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_ABI:
                mAccountName=mEdit1.getText().toString();
                if(TextUtils.isEmpty(mAccountName)){
                    GlobalUtils.showAlertMsg(this,R.string.get_abi_account_err);
                }else {
                    startAction();
                }
                break;
            case EOSOperations.ACTION_GET_CODE:
                mAccountName=mEdit1.getText().toString();
                if(TextUtils.isEmpty(mAccountName)){
                    GlobalUtils.showAlertMsg(this,R.string.get_abi_account_err);
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
                    GlobalUtils.showAlertMsg(this,R.string.get_table_row_input_err);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.FUNCTION_CREATE_WALLET:
                mWalletName=mEdit1.getText().toString();
                if(!TextUtils.isEmpty(mWalletName)) {
                    startAction();
                }
                break;
            case EOSOperations.ACTION_JSON_TO_BIN:
                mAccountName=mEdit1.getText().toString();
                mContractName=mEdit2.getText().toString();
                mArgs=mEdit3.getText().toString();
                startAction();
                break;
            case EOSOperations.ACTION_TRANSFER:
                mAccountName=mEdit1.getText().toString();
                mAccount2Name=mEdit2.getText().toString();
                mAmount=mEdit3.getText().toString()+" EOS";
                mMemo=mEdit4.getText().toString();
                if(!EOSUtils.isAccountNameLeagle(mAccountName) || !EOSUtils.isAccountNameLeagle(mAccount2Name)){
                    GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.ACTION_BUYRAM:
                mAccountName=mEdit1.getText().toString();
                mAccount2Name=mEdit2.getText().toString();
                mRamBytes=Integer.valueOf(mEdit3.getText().toString());
                if(!EOSUtils.isAccountNameLeagle(mAccountName) || !EOSUtils.isAccountNameLeagle(mAccount2Name)){
                    GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
                }else if(mRamBytes<=0){
                    GlobalUtils.showAlertMsg(this,R.string.illegal_ram_input);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.ACTION_SELLRAM:
                mAccountName=mEdit1.getText().toString();
                mRamBytes=Integer.valueOf(mEdit2.getText().toString());
                if(!EOSUtils.isAccountNameLeagle(mAccountName)){
                    GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
                }else if(mRamBytes<=0){
                    GlobalUtils.showAlertMsg(this,R.string.illegal_ram_input);
                }else{
                    startAction();
                }
                break;
            case EOSOperations.ACTION_DELEGATEBW:
            case EOSOperations.ACTION_UNDELEGATEBW:
                mAccountName=mEdit1.getText().toString();
                mAccount2Name=mEdit2.getText().toString();
                mEosForCpu=new Integer(String.valueOf(mEdit3.getText().toString()));
                mEosForNet=new Integer(String.valueOf(mEdit4.getText().toString()));
                if(!EOSUtils.isAccountNameLeagle(mAccountName) || !EOSUtils.isAccountNameLeagle(mAccount2Name)){
                    GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
                }else if(mEosForCpu<=0 || mEosForNet<=0){
                    GlobalUtils.showAlertMsg(this,R.string.illegal_cpu_net);
                }else{
                    startAction();
                }
                break;
            default:
                break;
        }
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
        mAlertDialog.setCanceledOnTouchOutside(false);
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
                    case EOSOperations.FUNCTION_GET_AVAILABLE_BP_API_SERVER:
                        mContent=EOSOperations.getAvailableAPIServer();
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
                    case EOSOperations.FUNCTION_CREATE_WALLET:
                        String pswd=EOSOperations.createWallet(EOSInfoActivity.this,mWalletName);
                        if(!TextUtils.isEmpty(pswd)){
                            SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(EOSInfoActivity.this);
                            boolean rememberWallet=pref.getBoolean(EOSUtils.REMEMBER_WALLET_PSWD,false);
                            if(rememberWallet){
                                pref.edit().putString(mWalletName,pswd).commit();
                            }
                            mContent= String.format(EOSInfoActivity.this.getResources().getString(R.string.wallet_pswd_notify),pswd);
                        }else{
                            mContent= EOSInfoActivity.this.getString(R.string.wallet_create_failed);
                        }
                        break;
                    case EOSOperations.ACTION_JSON_TO_BIN:
                        mContent=EOSOperations.jsonToBin(true,mAccountName,mContractName,mArgs);
                        break;
                    case EOSOperations.ACTION_TRANSFER:
                        mContent=EOSOperations.transfer(EOSInfoActivity.this,mAccountName,mAccount2Name,mAmount,mMemo);
                        break;
                    case EOSOperations.ACTION_BUYRAM:
                        mContent=EOSOperations.buyRam(EOSInfoActivity.this,mAccountName,mAccount2Name,mRamBytes);
                        break;
                    case EOSOperations.ACTION_SELLRAM:
                        mContent=EOSOperations.sellRam(EOSInfoActivity.this,mAccountName,mRamBytes);
                        break;
                    case EOSOperations.ACTION_DELEGATEBW:
                        mContent=EOSOperations.delegatebw(EOSInfoActivity.this,mAccountName,mAccount2Name,mEosForCpu,mEosForNet);
                        break;
                    case EOSOperations.ACTION_UNDELEGATEBW:
                        mContent=EOSOperations.undelegatebw(EOSInfoActivity.this,mAccountName,mAccount2Name,mEosForCpu,mEosForNet);
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
}
