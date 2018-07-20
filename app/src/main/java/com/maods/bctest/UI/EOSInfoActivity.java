package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.R;

/**
 * Created by MAODS on 2018/7/19.
 */

public class EOSInfoActivity extends Activity {
    private static final String TAG="EOSInfoActivity";

    private String mAction;
    private String mContent;

    private TextView mContentView;
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        mAction=getIntent().getStringExtra(GlobalConstants.EXTRA_KEY_ACTION);

        setContentView(R.layout.eos_info);
        mContentView=(TextView)findViewById(R.id.content);

        startAction();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
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
        mAlertDialog.show();

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                switch(mAction){
                    case EOSOperations.ACTION_GET_INFO:
                        mContent = EOSOperations.getInfo();
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
