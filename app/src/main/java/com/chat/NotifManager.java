package com.chat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by user on 4/6/2018.
 */

public class NotifManager extends Service implements ChildEventListener{

    FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference mDatabase;
    public static final String TAG = "NotifManager";
    private static final int ON_GOING = 1;
    ArrayList<String> unread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Notif: ", "started");
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("messages").addChildEventListener(this);
        Query lastQuery = mDatabase.child("messages").orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(this);
        int n=0;
        unread = new ArrayList<>();
        //Toast.makeText(getApplicationContext(), "Service onStartCommand called", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onChildAdded(DataSnapshot ds, String s) {
        Message m = ds.getValue(Message.class);
        Log.d("MESS: ", m.toString());
        String fromName = ds.getValue(Message.class).getFromName();
        Log.d("From: ", fromName);
        String me = ds.getValue(Message.class).getMessage();
        Log.d("Mess: ", me);
        //String date = ds.getValue(Message.class).getDate();
        String time = ds.getValue(Message.class).getTime();
        String type = ds.getValue(Message.class).getType();
//        m.setFromName(fromName);
//        m.setMessage(me);
//        m.setDate(date);
//        m.setTime(time);
//        m.setType(type);
        String to = ds.getValue(Message.class).getTo();
        boolean b = Boolean.parseBoolean(to);
        Log.d("User: ", user.getEmail());
        if (!m.getFromName().equals(user.getEmail()) && !b && !Helper.isAppRunning(getApplicationContext())){
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            unread.add(me);
            Log.d("VaL: ", "false");
            NotificationManager manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = sendNotif(fromName, me, time, uri);
            manager.notify(ON_GOING, notification);
            m.setSelf(false);
        }
        else {
            Log.d("VaL: ", "true");
            m.setSelf(true);
        }
    }

    @Override
    public void onDestroy() {
        //stopSelf();
        Log.d("Notif service ", "onDestroy()");
        //Toast.makeText(getApplicationContext(), "Service onDestroy called", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        //stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

//        Toast.makeText(getApplicationContext(), "Service onTaskRemoved called", Toast.LENGTH_SHORT).show();
//
//        PendingIntent service = PendingIntent.getService(
//                getApplicationContext(),
//                1001,
//                new Intent(getApplicationContext(), NotifManager.class),
//                PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification sendNotif(String fromName, String me, String time, Uri uri) {
        Log.d("Uri ", uri.toString());
        long[] v = {250,500};

        String mes;
        int n = unread.size();
        if(n==1)
            mes=n+" new message";
        else
            mes=n+" new messages";

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for(int i=0; i<n; i++){
            inboxStyle.addLine(unread.get(i));
        }

        inboxStyle.setSummaryText(mes)
                .setBigContentTitle(fromName);

        NotificationCompat.Builder notif=new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(fromName)
                .setContentText(me)
                .setSubText(time)
                .setSound(uri)
                .setVibrate(v)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(inboxStyle);

        Intent res=new Intent(getApplicationContext(), Chat.class);

        TaskStackBuilder stackBuilder=TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(Chat.class);
        stackBuilder.addNextIntent(res);

//        Intent i=new Intent(getApplicationContext(), NotifBroad.class);
//        i.setAction("Notif");

        PendingIntent in=stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent in = PendingIntent.getBroadcast(getApplicationContext(), 1, i, 0);

        notif.setContentIntent(in);

        return notif.getNotification();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
