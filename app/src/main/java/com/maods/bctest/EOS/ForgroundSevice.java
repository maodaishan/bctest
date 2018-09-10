package com.maods.bctest.EOS;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.maods.bctest.R;

/**
 * Created by MAODS on 2018/9/10.
 */

public class ForgroundSevice extends Service {
    public void onCreate(){
        super.onCreate();
        Notification.Builder builder=new Notification.Builder(this);
        PendingIntent p_intent = PendingIntent.getActivity(this, 0,
                new Intent(this, ForgroundSevice.class), 0);
        builder.setContentIntent(p_intent)
                .setContentTitle("bctest_foreservice")
                .setContentText("bctest running for ram trade");
        Notification notification=builder.build();
        startForeground(1, notification);   // notification ID: 0x1982, you can name it as you will.
    }

    public IBinder onBind(Intent intent){
        return null;
    }
}
