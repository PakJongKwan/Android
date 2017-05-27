package com.example.come.come;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.come.come.network.Datas;
import com.example.come.come.network.Message;


/**
 * Created by dsm on 2017-05-20.
 */

public class ShakeService extends Service {
    private SensorManager mSensorManager;
    private static final int SHAKE_THRESHOLD = 50;
    private String phone;
    private int shaked = 0;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];

            double acceleration = Math.sqrt(Math.pow(x, 2) +
                    Math.pow(y, 2) +
                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD) {
                shaked += 1;
            }

            if (shaked == 15) {
                Message.sendSMS(ShakeService.this, phone, "긴급상황입니다!");
                shaked = 0;
            }
        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        phone = Datas.getEmergencyPhone(this);

        Log.e("ShakeService", "On!!");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
