package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.R;

import java.io.File;
import java.util.ArrayList;

import io.plactal.eoscommander.data.wallet.EosWallet;
import io.plactal.eoscommander.data.wallet.EosWalletManager;

/**
 * Created by MAODS on 2018/8/10.
 */

public class EOSListActivity extends Activity {
    private static final String TAG="EOSListActivity";
    private static final String WALLET_UNLOCKED=" *";

    private LinearLayout mContainerView;
    private TextView mTitleView;
    private TextView mInfoView;
    private ListView mListView;
    private CheckBox mCheckBox;
    private Button mBtnView;
    private TextView mEmptyView;
    private ArrayAdapter<String> mAdapter;
    private AlertDialog mAlertDialog;

    private String mAction;
    private String[] mListContents;
    private ArrayList<EosWallet.Status> mWalletStatus;
    private EosWalletManager mManager;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.eos_list);
        mContainerView=(LinearLayout)findViewById(R.id.container);
        mTitleView=(TextView)findViewById(R.id.title);
        mInfoView=(TextView)findViewById(R.id.info);
        mEmptyView=(TextView)findViewById(R.id.empty);
        mListView=(ListView)findViewById(R.id.list);
        mCheckBox=(CheckBox)findViewById(R.id.checkbox);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleRememberPswd();
            }
        });
        handleRememberPswd();

        mBtnView=(Button)findViewById(R.id.button);
        mBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickHandler();
            }
        });

        mAction= getIntent().getStringExtra(GlobalConstants.EXTRA_KEY_ACTION);
        switch(mAction){
            case EOSOperations.FUNCTION_LIST_WALLETS:{
                mTitleView.setText(R.string.list_wallets);
                mInfoView.setText(R.string.wallet_list_info);
                mEmptyView.setText(R.string.wallet_empty);
                mBtnView.setText(R.string.create_wallet);
            }
        }

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

        mManager= EosWalletManager.getInstance(this);
        handleRememberPswd();
        startLoadContents();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
    }

    private void handleRememberPswd(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        if(mCheckBox.isChecked()){
            pref.edit().putBoolean(EOSUtils.REMEMBER_WALLET_PSWD,true).commit();
        }else{
            pref.edit().putBoolean(EOSUtils.REMEMBER_WALLET_PSWD,false).commit();
        }
    }

    private void onButtonClickHandler(){
        switch(mAction){
            case EOSOperations.FUNCTION_LIST_WALLETS:
                startCreateWalletActivity();
                break;
            default:
                break;
        }
    }

    private void startCreateWalletActivity(){
        Intent intent=new Intent();
        intent.setClass(this,EOSInfoActivity.class);
        intent.putExtra(GlobalConstants.EXTRA_KEY_ACTION,EOSOperations.FUNCTION_CREATE_WALLET);
        startActivity(intent);
        finish();
    }

    private void startLoadContents(){
        Thread t=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        switch(mAction){
                            case EOSOperations.FUNCTION_LIST_WALLETS:{
                                //openAllWallets();
                                mWalletStatus= mManager.listWallets(null);
                                int size=mWalletStatus.size();
                                if(size==0){
                                    mListContents=new String[0];
                                }else {
                                    mListContents=new String[size];
                                    StringBuilder sb=new StringBuilder();
                                    for(int i=0;i<size;i++){
                                        sb.setLength(0);
                                        EosWallet.Status status=mWalletStatus.get(i);
                                        if(status.locked){
                                            mListContents[i]=status.walletName;
                                        }else{
                                            sb.append(status.walletName);
                                            sb.append(WALLET_UNLOCKED);
                                            mListContents[i]=sb.toString();
                                        }
                                    }
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        EOSListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateList();
                            }
                        });
                    }
                }
        );
        t.start();
    }

    /*private void openAllWallets(){
        String pathStr=mManager.getDir();
        File path=new File(pathStr);
        File[] files=path.listFiles();
        for(File file:files){
            mManager.open(file.getName());
        }
    }*/
    private void updateList(){
        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog=null;
        }
        if(mListContents.length==0){
            mContainerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
            mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mListContents);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    handleOnItemClickInUIThread(view,position);
                }
            });
            mListView.invalidate();
        }
    }

    private void handleOnItemClickInUIThread(View view,int position){
        EosWallet.Status status=mWalletStatus.get(position);
        if(status==null){
            return;
        }
        if(status.locked){
            showUnlockDialog(status.walletName);
        }else{
            openWallet(status.walletName);
        }
    }

    private void showUnlockDialog(final String name){
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.input_pswd_to_unlock_wallet))
                .setView(inputServer)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pswd = inputServer.getText().toString();
                        mManager.unlock(name,pswd);
                        SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(EOSListActivity.this);
                        boolean rememberPswd=pref.getBoolean(EOSUtils.REMEMBER_WALLET_PSWD,false);
                        if(rememberPswd){
                            pref.edit().putString(name,pswd).commit();
                        }
                        startLoadContents();
                    }
                });
        builder.show();
    }

    private void openWallet(String name){
        Intent intent=new Intent();
        intent.setClass(this,EOSWalletManagerActivity.class);
        intent.putExtra(GlobalConstants.EXTRA_KEY_NAME,name);
        startActivity(intent);
    }
}
