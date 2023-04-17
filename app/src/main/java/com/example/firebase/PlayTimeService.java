package com.example.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PlayTimeService extends Service {
    private static final String TAG = "PlayTimeService";
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        // Post a delayed runnable to display the long toast after 5 minutes
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "You have been playing for a long time, maybe you should take a break!", Toast.LENGTH_LONG).show();
                stopSelf();
            }
        }, 600000); // 10 minutes in milliseconds

        // Return START_STICKY to restart the service if it is killed by the system
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't need to bind to this service, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}

