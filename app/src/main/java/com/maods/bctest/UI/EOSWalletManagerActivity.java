package com.maods.bctest.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.maods.bctest.GlobalConstants;
import com.maods.bctest.GlobalUtils;
import com.maods.bctest.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.plactal.eoscommander.crypto.ec.EosPrivateKey;
import io.plactal.eoscommander.crypto.ec.EosPublicKey;
import io.plactal.eoscommander.data.remote.model.types.EosByteReader;
import io.plactal.eoscommander.data.remote.model.types.EosByteWriter;
import io.plactal.eoscommander.data.remote.model.types.EosType;
import io.plactal.eoscommander.data.wallet.EosWallet;
import io.plactal.eoscommander.data.wallet.EosWalletManager;

/**
 * Created by MAODS on 2018/8/10.
 */

public class EOSWalletManagerActivity extends Activity {
    private static final String TAG="EOSWalletManagerActivity";

    private static final String PRIV="priv";
    private static final String PUB="pub";

    private LinearLayout mContainerView;
    private TextView mTitleView;
    private TextView mInfoView;
    private ListView mListView;
    private Button mBtnCreatePrivate;
    private Button mBtnImport;
    private SimpleAdapter mAdapter;

    private String mName;
    private EosWallet mWallet;
    private Map<EosPublicKey,String> mKeys;
    private List<Map<String,Object>> mListItems=new ArrayList<Map<String,Object>>();

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.manage_wallet);
        mContainerView=(LinearLayout)findViewById(R.id.container);
        mTitleView=(TextView)findViewById(R.id.title);
        mInfoView=(TextView)findViewById(R.id.info);
        mListView=(ListView)findViewById(R.id.list);
        mBtnCreatePrivate=(Button)findViewById(R.id.create_private);
        mBtnCreatePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPrivateKeyAndImport();
            }
        });
        mBtnImport=(Button)findViewById(R.id.import_private);
        mBtnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
            }
        });

        mName= getIntent().getStringExtra(GlobalConstants.EXTRA_KEY_NAME);
        mTitleView.setText(mName);

        mWallet = EosWalletManager.getInstance(this).getWallet(mName);
        refreshKeyList();
    }

    private void createPrivateKeyAndImport(){
        EosPrivateKey privKey=new EosPrivateKey();
        final String priv=privKey.toWif();
        final String pub=privKey.getPublicKey().toString();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        String keys=String.format(getResources().getString(R.string.generated_keys),priv,pub);
        builder.setMessage(keys);
        builder.setPositiveButton(R.string.import_key, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mWallet.importKey(priv);
                EosByteWriter writer = new EosByteWriter(256) ;
                mWallet.pack(writer);
                mWallet.saveFile(mWallet.getWalletFilePath());
                dialog.dismiss();
                refreshKeyList();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void showImportDialog(){
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.input_private_for_import))
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
                        String priv = inputServer.getText().toString();
                        try {
                            mWallet.importKey(priv);
                        }catch (IllegalArgumentException e){
                            GlobalUtils.showAlertMsg(EOSWalletManagerActivity.this,R.string.private_key_import_fail);
                            return;
                        }
                        EosByteWriter writer = new EosByteWriter(256) ;
                        mWallet.pack(writer);
                        mWallet.saveFile(mWallet.getWalletFilePath());
                        refreshKeyList();
                    }
                });
        builder.show();
    }

    private void refreshKeyList(){
        mKeys=mWallet.listKeys();
        if(mKeys.size()>0){
            mListItems.clear();
            Set<Map.Entry<EosPublicKey,String>> keys= mKeys.entrySet();
            for(Map.Entry<EosPublicKey,String> entry:keys){
                Map<String,Object> item=new HashMap<String,Object>();
                item.put(PRIV,"Private Key:"+entry.getValue());
                item.put(PUB,"Public Key:"+entry.getKey().toString());
                mListItems.add(item);
            }
            mAdapter=new SimpleAdapter(this,
                    mListItems,
                    android.R.layout.simple_list_item_2,
                    new String[]{PRIV,PUB},
                    new int[]{android.R.id.text1,android.R.id.text2});
            mListView.setAdapter(mAdapter);
            mListView.invalidate();
        }
    }
}
