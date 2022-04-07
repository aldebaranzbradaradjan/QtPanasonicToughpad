package org.alde.sample.panasonictoughpad;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;

import com.panasonic.toughpad.android.api.appbtn.AppButtonManager;

public class ButtonService extends Service {

    private static native void buttonPressed(String button, boolean state);

    private static final String TAG = "Toughpad";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ButtonService : Create service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ButtonService receive command");

        if (intent.getAction() == null || !intent.getAction().equals(AppButtonManager.ACTION_APPBUTTON)) {
            Log.d(TAG, "ButtonService ignore");
            return START_STICKY;
        }

        if (intent.getAction().equals(AppButtonManager.ACTION_APPBUTTON)) {
            int buttonId = intent.getIntExtra(AppButtonManager.EXTRA_APPBUTTON_BUTTON, 0);
            boolean down = intent.getIntExtra(AppButtonManager.EXTRA_APPBUTTON_STATE, 0) == AppButtonManager.EXTRA_APPBUTTON_STATE_DOWN;

            switch (buttonId) {
                case AppButtonManager.BUTTON_A1:
                    Log.d(TAG, "A1 " + down);
                    buttonPressed("A1 ", down);
                    break;
                case AppButtonManager.BUTTON_A2:
                    Log.d(TAG, "A2 " + down);
                    buttonPressed("A2", down);
                    break;
                case AppButtonManager.BUTTON_A3:
                    Log.d(TAG, "A3 " + down);
                    buttonPressed("A3", down);
                    break;
                case AppButtonManager.BUTTON_USER:
                    Log.d(TAG, "USER " + down);
                    buttonPressed("USER", down);
                    break;
                case AppButtonManager.BUTTON_SIDE:
                    Log.d(TAG, "SIDE " + down);
                    buttonPressed("SIDE", down);
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
