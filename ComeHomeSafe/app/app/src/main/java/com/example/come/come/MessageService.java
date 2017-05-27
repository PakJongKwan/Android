package com.example.come.come;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Message;

/**
 * Created by dsm on 2017-05-18.
 */

public class MessageService extends Service {

    public static final String ALERT_TIME = "alert_time";

    private long alertTime = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alertTime = intent.getLongExtra(ALERT_TIME, 0);

        Log.e("TAG", String.valueOf(alertTime));

        if (alertTime != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    String phone = Datas.getEmergencyPhone(MessageService.this);

                    Message.sendSMS(MessageService.this, phone, "아직 귀가하지 않았습니다!");
                }
            }, alertTime - System.currentTimeMillis());

            Log.e("TAG", String.valueOf(alertTime));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
