package com.weebkun.nap_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsManager;

/**
 * BroadcastReceiver class for receiving sms messages
 */
public class SmsReceiver extends BroadcastReceiver {
    
    private SmsManager manager = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        // get phone num to send to
        String phone = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0].getOriginatingAddress();
        manager.sendTextMessage(phone, null, "I'm currently napping. I will check your message later.", null, null);
    }
}
