package com.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by user on 4/6/2018.
 */

public class NotifBroad extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast: ", "trif");
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            context.startService(new Intent(context, NotifManager.class));
        }
        else if(intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)){
            if(intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                context.startService(new Intent(context, NotifManager.class));
            }
        }
        else{
            context.startService(new Intent(context, NotifManager.class));
        }
    }
}
