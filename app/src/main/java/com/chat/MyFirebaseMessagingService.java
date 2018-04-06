package com.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by user on 1/24/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String image=remoteMessage.getNotification().getIcon();
        String title=remoteMessage.getNotification().getTitle();
        String text=remoteMessage.getNotification().getBody();
        String sound=remoteMessage.getNotification().getSound();
        Log.d("Title: ", title);

        int id=0;
        Object obj=remoteMessage.getData().get("id");
        if(obj!=null)
            id=Integer.valueOf(obj.toString());

        this.sendNotification(new NotificationData(title, image, text, sound, id));
    }

    private void sendNotification(NotificationData notificationData) {
        Intent in=new Intent(this, MainActivity.class);
        in.putExtra(NotificationData.TEXT, notificationData.getText());

        NotificationCompat.Builder m=null;

        try {
            m=new NotificationCompat.Builder(this).
                    setSmallIcon(R.mipmap.ic_launcher).
                    //setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8")).
                    setContentText(URLDecoder.decode(notificationData.getText(), "UTF-8")).
                    setAutoCancel(true).
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        TaskStackBuilder t=TaskStackBuilder.create(getApplicationContext());
        t.addParentStack(MainActivity.class);
        t.addNextIntent(in);
        PendingIntent p=t.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        if (m != null) {
            m.setContentIntent(p);
            NotificationManager n=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.notify(notificationData.getId(), m.build());
        }
    }

}
