package com.maods.bctest.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maods.bctest.ChainCommonOperations;
import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by MAODS on 2018/7/17.
 */

public class ChainHomeActivity extends Activity {
    private static final String TAG="ChainHomeActivity";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE=1;

    private static final String GET_CHAIN_INFO="get_chain_info";
    private static final String[] BTC_actions=new String[]{};
    private static final String[] ETH_actions=new String[]{};
    private static final String[] EOS_actions=new String[]{
            EOSOperations.ACTION_GET_INFO,
            EOSOperations.ACTION_GET_PRODUCERS,
            EOSOperations.ACTION_GET_AVAILABLE_BP_API_SERVER,
            EOSOperations.ACTION_GET_ACCOUNT,
            EOSOperations.ACTION_GET_BLOCK,
            EOSOperations.ACTION_GET_ABI,
            EOSOperations.ACTION_GET_CODE,
            EOSOperations.ACTION_GET_TABLE_ROWS,
            EOSOperations.ACTION_GET_RAM_PRICE,
            EOSOperations.ACTION_BUYRAM,
            EOSOperations.ACTION_SELLRAM,
            EOSOperations.ACTION_LIST_WALLETS,
            EOSOperations.ACTION_JSON_TO_BIN,
            EOSOperations.ACTION_TRANSFER
    };
    private static final String[] Fabric_actions=new String[]{};

    String mTarget;
    String[] mTargetActions;
    private ChainCommonOperations mCommonOps=null;

    private LinearLayout mContentView;
    private TextView mTitleView;
    private TextView mInfoView;
    private ListView mListView;
    private TextView mEmptyView;
    private ArrayAdapter<String> mAdapter;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chain_home);
        mContentView=(LinearLayout)findViewById(R.id.container);
        mTitleView=(TextView)findViewById(R.id.title);
        mInfoView=(TextView)findViewById(R.id.info);
        mEmptyView=(TextView)findViewById(R.id.empty);
        mListView=(ListView)findViewById(R.id.list);

        Intent intent=getIntent();
        if(intent.hasExtra(GlobalConstants.EXTRA_KEY_CHAIN)) {
            mTarget = intent.getStringExtra(GlobalConstants.EXTRA_KEY_CHAIN);
        }else {
            mTarget = GlobalConstants.EOS;
        }
        switch(mTarget) {
            case GlobalConstants.BTC:
                mTargetActions = BTC_actions;
                break;
            case GlobalConstants.ETH:
                mTargetActions = ETH_actions;
                break;
            case GlobalConstants.EOS:
                mTargetActions = EOS_actions;
                mCommonOps=new EOSOperations();
                break;
            case GlobalConstants.FABRIC:
                mTargetActions = Fabric_actions;
                break;
            default:
                mTargetActions = EOS_actions;
                break;
        }

        if(mTargetActions.length==0){
            mListView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.GONE);
            mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mTargetActions);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    handleOnItemClickInUIThread(view,position);
                }
            });
        }

        if(mCommonOps!=null){
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> serverNodes=mCommonOps.getServerNode();
                    ChainHomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateInfo(serverNodes);
                        }
                    });
                }
            });
            t.start();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){
        return;
    }

    private void updateInfo(List<String>servers){
        if(servers.size()==0){
            mInfoView.setText(R.string.no_server_available);
        }else{
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<servers.size();i++){
                sb.append(servers.get(i)+"\n");
            }
            String serversStr=String.format(getResources().getString(R.string.available_servers),sb.toString());
            mInfoView.setText(serversStr);
        }
    }

    private void handleOnItemClickInUIThread(View view,int position){
        switch(mTarget){
            case GlobalConstants.EOS:{
                switch(mTargetActions[position]){
                    case EOSOperations.ACTION_GET_INFO:
                    case EOSOperations.ACTION_GET_PRODUCERS:
                    case EOSOperations.ACTION_GET_AVAILABLE_BP_API_SERVER:
                    case EOSOperations.ACTION_GET_ACCOUNT:
                    case EOSOperations.ACTION_GET_BLOCK:
                    case EOSOperations.ACTION_GET_ABI:
                    case EOSOperations.ACTION_GET_CODE:
                    case EOSOperations.ACTION_GET_TABLE_ROWS:
                    case EOSOperations.ACTION_GET_RAM_PRICE:
                    case EOSOperations.ACTION_CREATE_WALLET:
                    case EOSOperations.ACTION_JSON_TO_BIN:
                    case EOSOperations.ACTION_TRANSFER:
                    case EOSOperations.ACTION_BUYRAM:
                    case EOSOperations.ACTION_SELLRAM:
                        startEOSGetInfo(mTargetActions[position]);
                        break;
                    case EOSOperations.ACTION_LIST_WALLETS:
                        startEOSList(mTargetActions[position]);
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }
    private void startEOSGetInfo(String action){
        Intent intent=new Intent();
        intent.setClass(this,EOSInfoActivity.class);
        intent.putExtra(GlobalConstants.EXTRA_KEY_ACTION,action);
        startActivity(intent);
    }

    private void startEOSList(String action){
        Intent intent=new Intent();
        intent.setClass(this,EOSListActivity.class);
        intent.putExtra(GlobalConstants.EXTRA_KEY_ACTION,action);
        startActivity(intent);
    }
}
