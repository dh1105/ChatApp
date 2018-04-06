package com.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user on 4/6/2018.
 */

public class NotifBroad extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Broadcast: ", "trif");
        String action = intent.getAction();
        if(action.equals("Notif")){
            context.stopService(new Intent(context, NotifManager.class));
        }
    }
}
