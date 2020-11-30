package com.weebkun.nap_app.telephony;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * broadcast receiver for incoming calls
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private SmsManager manager = SmsManager.getDefault();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            System.out.println("ringing");
            // incoming call
            System.out.println(intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
            if(intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                System.out.println(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
                manager.sendTextMessage(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER), null, "I'm currently napping. I will check your message later.", null, null);
                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                telecomManager.endCall();
            }
        }
    }
}
