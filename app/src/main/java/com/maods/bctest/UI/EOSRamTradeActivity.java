package com.maods.bctest.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.EOS.EOSUtils;
import com.maods.bctest.EOS.ForgroundSevice;
import com.maods.bctest.GlobalUtils;
import com.maods.bctest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MAODS on 2018/9/7.
 */

public class EOSRamTradeActivity extends Activity {
    private static final String TAG="EOSRamTradeActivity";

    private EditText mCheckAccountView;
    private Button mCheckBtnView;
    private TextView mCheckResultView;
    private EditText mBuyAccountPayView;
    private EditText mBuyAccountReceiveView;
    private EditText mBuyEosAmountView;
    private EditText mBuyPriceView;
    private EditText mBuyTimeView;
    private Button mBuyBtnView;
    private TextView mBuyTaskInfoView;
    private Button mCancelBuyBtnView;
    private EditText mSellAccountView;
    private EditText mSellAmountView;
    private EditText mSellPriceView;
    private EditText mSellTimeView;
    private Button mSellBtnView;
    private TextView mSellTaskInfoView;
    private Button mCancelSellBtnView;

    private String mBuyAccountPay;
    private String mBuyAccountRcv;
    private String mBuyEosAmount;//by EOS,should be "xx.xxxx"
    private float mBuyPrice;
    private int mBuyTime;
    private String mSellAccount;
    private float mSellPrice;//kB
    private float mSellAmount;//kB
    private int mSellTime;
    private Timer mBuyTimer;
    private Timer mSellTimer;
    private BuyTimerTask mBuyTimerTask;
    private SellTimerTask mSellTimerTask;
    private boolean mHasBuyTask=false;
    private boolean mHasSellTask=false;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        //start a foreground service prevent be killed.because this needs continues retry price.
        Intent intent=new Intent(this, ForgroundSevice.class);
        startService(intent);

        setContentView(R.layout.ram_trade);
        //check balance
        mCheckAccountView=(EditText)findViewById(R.id.check_account);
        mCheckBtnView=(Button)findViewById(R.id.check_btn);
        mCheckResultView=findViewById(R.id.check_result);
        mCheckBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBtnClicked();
            }
        });
        //buy
        mBuyAccountPayView=findViewById(R.id.buy_payer);
        mBuyAccountReceiveView=findViewById(R.id.buy_receiver);
        mBuyEosAmountView=findViewById(R.id.buy_amount);
        mBuyPriceView=findViewById(R.id.buy_price);
        mBuyTimeView=findViewById(R.id.buy_time);
        //mBuyTimeView.setText("5");
        mBuyBtnView=findViewById(R.id.buy_btn);
        mBuyTaskInfoView=findViewById(R.id.buy_task);
        mCancelBuyBtnView=findViewById(R.id.cancel_buy);
        mBuyBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuyBtnClicked();
            }
        });
        mCancelBuyBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHasBuyTask=false;
                mBuyTimer.cancel();
                mBuyTimer=null;
                updateBuyTaskInfoView();
            }
        });
        //sell
        mSellAccountView=findViewById(R.id.sell_account);
        mSellAmountView=findViewById(R.id.sell_amount);
        mSellPriceView=findViewById(R.id.sell_price);
        mSellTimeView=findViewById(R.id.sell_time);
        //mSellTimeView.setText("5");
        mSellTaskInfoView=findViewById(R.id.sell_task);
        mSellBtnView=findViewById(R.id.sell_btn);
        mSellBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSellBtnClicked();
            }
        });
        mCancelSellBtnView=findViewById(R.id.cancel_sell);
        mCancelSellBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHasSellTask=false;
                mSellTimer.cancel();
                mSellTimer=null;
                updateSellTaskInfoView();
            }
        });

        mBuyTimerTask=new BuyTimerTask();
        mSellTimerTask=new SellTimerTask();
    }

    private void onCheckBtnClicked(){
        String account=mCheckAccountView.getText().toString();
        if(!EOSUtils.isAccountNameLeagle(account)){
            GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
            return;
        }
        //auto set accounts for easy input.
        mBuyAccountPayView.setText(account);
        mBuyAccountReceiveView.setText(account);
        mSellAccountView.setText(account);
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                String accountInfo= EOSOperations.getAccount(account);
                if(TextUtils.isEmpty(accountInfo)){
                    return;
                }
                try {
                    JSONObject accountJson=new JSONObject(accountInfo);
                    if(accountJson==null){
                        return;
                    }
                    String liquidBalanceStrRaw=accountJson.getString("core_liquid_balance");
                    //String liquidBalanceStr=liquidBalanceStrRaw.substring(0,liquidBalanceStrRaw.length()-4);
                    double liquidBalance=EOSUtils.getDoubleFromAsset(liquidBalanceStrRaw);//Double.parseDouble(liquidBalanceStr);
                    int ramRaw=accountJson.getInt("ram_quota");
                    double netWeight=accountJson.getDouble("net_weight");
                    double cpuWeight=accountJson.getDouble("cpu_weight");
                    netWeight=netWeight/10000;
                    cpuWeight=cpuWeight/10000;
                    float ramKB=ramRaw/1024;
                    final String checkResult=EOSRamTradeActivity.this.getString(R.string.check_balance_result,liquidBalance,ramKB,cpuWeight,netWeight);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCheckResultView.setText(checkResult);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void onBuyBtnClicked(){
        mBuyAccountPay=mBuyAccountPayView.getText().toString();
        mBuyAccountRcv=mBuyAccountReceiveView.getText().toString();
        mBuyEosAmount=mBuyEosAmountView.getText().toString();
        mBuyPrice=Float.parseFloat(mBuyPriceView.getText().toString());
        mBuyTime=Integer.parseInt(mBuyTimeView.getText().toString());
        if(!EOSUtils.isAccountNameLeagle(mBuyAccountPay) || !EOSUtils.isAccountNameLeagle(mBuyAccountRcv)){
            GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
        }else if(mBuyTime<=0){
            GlobalUtils.showAlertMsg(this,R.string.check_time_alert);
        }else{
            if(mHasBuyTask){
                mBuyTimer.cancel();
                mBuyTimer=null;
            }
            mHasBuyTask=true;
            mCancelBuyBtnView.setEnabled(true);
            mBuyTimer=new Timer();
            mBuyTimer.schedule(mBuyTimerTask,0,mBuyTime*60*1000);
            updateBuyTaskInfoView();
        }
    }

    /**
     * true: no buy task, or price is ok, and finished buy action
     * false:price is not ok, or failed buy action.
     * mCancelBuyBtnView will be disabled while executing
     */
    private boolean checkAndBuyRamSuccess(){
        String buyResult;
        //disable cancel btn when action starts;
        //mCancelBuyBtnView.setEnabled(false);
        if(!mHasBuyTask){
            return true;
        }
        float price=EOSOperations.getRawRamPrice();//by byte;
        price=price*1024;
        Log.i(TAG,"ram price per kByte:"+price);
        if(price<=mBuyPrice) {
            buyResult=EOSOperations.buyRamEos(this, mBuyAccountPay, mBuyAccountRcv, mBuyEosAmount);
            if(!TextUtils.isEmpty(buyResult) && !buyResult.startsWith("err")){
                mHasBuyTask=false;
                //mCancelBuyBtnView.setEnabled(false);
                return true;
            }
        }
        //mCancelBuyBtnView.setEnabled(true);
        return false;
    }

    class BuyTimerTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCancelBuyBtnView.setEnabled(false);
                }
            });
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean result=checkAndBuyRamSuccess();
                    if(result){
                        mBuyTimer.cancel();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCancelBuyBtnView.setEnabled(!result);
                            updateBuyTaskInfoView();
                        }
                    });
                }
            });
            t.start();
        }
    }
    private void updateBuyTaskInfoView(){
        String info;
        if(mHasBuyTask){
            info=getString(R.string.buy_task_info,mBuyEosAmount,mBuyPrice,mBuyTime);
        }else{
            info=null;
        }
        mBuyTaskInfoView.setText(info);
        mBuyTaskInfoView.invalidate();
    }

    private void onSellBtnClicked(){
        mSellAccount=mSellAccountView.getText().toString();
        mSellAmount=Float.parseFloat(mSellAmountView.getText().toString());
        mSellPrice=Float.parseFloat(mSellPriceView.getText().toString());
        mSellTime=Integer.parseInt(mSellTimeView.getText().toString());
        if(!EOSUtils.isAccountNameLeagle(mSellAccount)){
            GlobalUtils.showAlertMsg(this,R.string.eos_account_length_err);
        }else{
            if(mHasSellTask){
                mSellTimer.cancel();
                mSellTimer=null;
            }
            mSellTimer=new Timer();
            mSellTimer.schedule(mSellTimerTask,0,(mSellTime*60*1000));
            mHasSellTask=true;
            mCancelSellBtnView.setEnabled(true);
            updateSellTaskInfoView();
        }
    }


    /**
     * true: no sell task, or price is ok, and finished sell action
     * false:price is not ok, or failed sell action.
     * mCancelSellBtnView will be disabled while executing
     */
    private boolean checkAndSellRamSuccess(){
        String sellResult;
        //disable cancel btn when action starts;
        //mCancelSellBtnView.setEnabled(false);
        if(!mHasSellTask){
            return true;
        }
        float price=EOSOperations.getRawRamPrice();//by byte;
        price=price*1024;
        Log.i(TAG,"ram price per kByte:"+price);
        if(price>=mSellPrice) {
            sellResult=EOSOperations.sellRam(this, mSellAccount, (int)mSellAmount*1024);
            if(!TextUtils.isEmpty(sellResult) && !sellResult.startsWith("err")){
                mHasSellTask=false;
                //mCancelSellBtnView.setEnabled(false);
                return true;
            }
        }
        //mCancelSellBtnView.setEnabled(true);
        return false;
    }

    class SellTimerTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCancelSellBtnView.setEnabled(false);
                }
            });
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean result=checkAndSellRamSuccess();
                    if(result){
                        mSellTimer.cancel();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCancelSellBtnView.setEnabled(!result);
                            updateSellTaskInfoView();
                        }
                    });
                }
            });
            t.start();
        }
    }
    private void updateSellTaskInfoView(){
        String info;
        if(mHasSellTask){
            info=getString(R.string.sell_task_info,mSellAmount,mSellPrice,mSellTime);
        }else{
            info=null;
        }
        mSellTaskInfoView.setText(info);
        mSellTaskInfoView.invalidate();
    }
}
