package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private TextView mContentView;
    private AlertDialog mAlertDialog;
    private EditText mEdit1;
    private Button mBtn;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        mAction=getIntent().getStringExtra(GlobalConstants.EXTRA_KEY_ACTION);

        setContentView(R.layout.eos_info);
        mContentView=(TextView)findViewById(R.id.content);
        mEdit1=(EditText)findViewById(R.id.edit1);
        mBtn=(Button)findViewById(R.id.btn);

        if(mAction.equals(EOSOperations.ACTION_GET_ACCOUNT)){
            mBtn.setVisibility(View.VISIBLE);
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnClicked();
                }
            });
            mEdit1.setVisibility(View.VISIBLE);
            mEdit1.setHint(R.string.input_account_name);
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
        if(mAction.equals(EOSOperations.ACTION_GET_ACCOUNT)){
            mAccountName=mEdit1.getText().toString();
            boolean isAccountNameLegel=isAccountNameLeagle(mAccountName);
            if(!isAccountNameLegel){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage(R.string.eos_account_length_err);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }else {
                //Log.i(TAG,"mAccountName:"+mAccountName);
                startAction();
            }
        }
    }
    private boolean isAccountNameLeagle(String input){
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
                    case EOSOperations.ACTION_GET_ACCOUNT:
                        mContent=EOSOperations.getAccount(mAccountName);
                        //Log.i(TAG,"mContent:"+mContent);
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
