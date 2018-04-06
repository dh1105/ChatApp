package com.chat;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity implements View.OnClickListener, ChildEventListener {

    FirebaseAuth mAuth;
    EditText mymsg;
    FloatingActionButton send;
    RecyclerView.Adapter mAdapter=null;
    ArrayList<Message> messages=null;
    RecyclerView messageList;
    String uid;
    FirebaseUser user;
    private DatabaseReference mDatabase;
    ProgressBar data;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        data=(ProgressBar) findViewById(R.id.loadmess);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        mymsg=(EditText) findViewById(R.id.messageText);
        send=(FloatingActionButton) findViewById(R.id.sendButton);
        send.setOnClickListener(this);
        messages=new ArrayList<>();
        mAdapter=new MyAdapter(this, messages);
        messageList = (RecyclerView) findViewById(R.id.messageList);
        messageList.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setOrientation(LinearLayoutManager.VERTICAL);
        ln.setStackFromEnd(true);
        messageList.setLayoutManager(ln);
        messageList.setAdapter(mAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        DownloadMessage downloadMessage=new DownloadMessage();
//        downloadMessage.execute();
        //DisplayMessage();
        String k;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count=dataSnapshot.getChildrenCount();
                if(count==0)
                    UpdateUI();
                mDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("messages").addChildEventListener(this);
        //UpdateUI();
        if(isMyServiceRunning(NotifManager.class)) {
            Log.d("Stop: ", "service");
            in = new Intent(this, NotifManager.class);
            stopService(in);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isMyServiceRunning(NotifManager.class)) {
            Log.d("Stop: ", "service");
            in = new Intent(this, NotifManager.class);
            stopService(in);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Log.d("Stop: ", "service");
                in=new Intent(this, NotifManager.class);
                stopService(in);
                Intent i=new Intent(this, LogIn.class);
                startActivity(i);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateEntry(){
        boolean valid=true;

        String mes=mymsg.getText().toString();
        if(TextUtils.isEmpty(mes)){
            valid=false;
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:
                if(!validateEntry()){
                    break;
                }
                String mes=mymsg.getText().toString();
                Message message = new Message(user.getEmail(), mes, getDate(), getTime(), String.valueOf(false));
                message.setSelf(true);
                mDatabase.child("messages").push().setValue(message);
//                messages.add(message);
//                mAdapter.notifyDataSetChanged();
//                message = null;
                mymsg.setText("");
//                messageList.scrollToPosition(messages.size() - 1);
        }
    }

    public String getDate() {

        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        return sdf.format(date);

    }

    public String getTime() {

        Date date=new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

        return sdf.format(date);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuth.getCurrentUser()!=null){
            in=new Intent(this, NotifManager.class);
            Log.d("Service onDestroy(): ", "started");
            startService(in);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser()!=null){
            in=new Intent(this, NotifManager.class);
            Log.d("Service onStop(): ", "started");
            startService(in);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot ds, String s) {
        System.out.println("DATA: "+ ds.toString() + " " + s);
        Message m = ds.getValue(Message.class);
        Log.d("MESS: ", m.toString());
        Log.d("UID: ", uid);
        String fromName = ds.getValue(Message.class).getFromName();
        Log.d("From: ", fromName);
        String me = ds.getValue(Message.class).getMessage();
        Log.d("Mess: ", me);
        String date = ds.getValue(Message.class).getDate();
        String time = ds.getValue(Message.class).getTime();
        String to = ds.getValue(Message.class).getTo();
        Boolean b = Boolean.parseBoolean(to);
        m.setFromName(fromName);
        m.setMessage(me);
        m.setDate(date);
        m.setTime(time);
        if (m.getFromName().equals(user.getEmail())){
            Log.d("VaL: ", "true");
            m.setSelf(true);
        }
        else {
            Log.d("VaL: ", "false");
            b = true;
            m.setSelf(false);
        }
        m.setTo(String.valueOf(b));
        System.out.printf("TO MSG: "+b);
        mDatabase.child("messages").child(ds.getKey()).setValue(m);
        messages.add(m);
        UpdateUI();
        mAdapter.notifyDataSetChanged();
        messageList.scrollToPosition(messages.size() - 1);
    }

    private void UpdateUI() {
        data.setVisibility(View.GONE);
        LinearLayout ipDisp, msdDisp;
        ipDisp=(LinearLayout) findViewById(R.id.ipDisp);
        msdDisp=(LinearLayout) findViewById(R.id.msgDisp);
        ipDisp.setVisibility(View.VISIBLE);
        msdDisp.setVisibility(View.VISIBLE);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Notif ServiceRunning", true+"");
                return true;
            }
        }
        Log.i ("Notif ServiceRunning", false+"");
        return false;
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

//    private class DownloadMessage extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            p=new ProgressDialog(Chat.this);
//            p.setIndeterminate(true);
//            p.setMessage("Loading..");
//            p.setCancelable(false);
//            p.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(dataSnapshot!=null) {
//                        DataSnapshot d=dataSnapshot.child("messages");
//                        for (DataSnapshot ds : d.getChildren()) {
//                            Message m = ds.getValue(Message.class);
//                            Log.d("MESS: ", m.toString());
//                            Log.d("UID: ", uid);
//                            String fromName = ds.getValue(Message.class).getFromName();
//                            Log.d("From: ", fromName);
//                            String me = ds.getValue(Message.class).getMessage();
//                            Log.d("Mess: ", me);
//                            String date = ds.getValue(Message.class).getDate();
//                            String time = ds.getValue(Message.class).getTime();
//                            m.setFromName(fromName);
//                            m.setMessage(me);
//                            m.setDate(date);
//                            m.setTime(time);
//                            if (m.getFromName().equals(user.getEmail())){
//                                Log.d("VaL: ", "true");
//                                m.setSelf(true);
//                            }
//                            else {
//                                Log.d("VaL: ", "false");
//                                m.setSelf(false);
//                            }
//                            messages.add(m);
//                        }
//                        mAdapter.notifyDataSetChanged();
//                        messageList.scrollToPosition(messages.size() - 1);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            //updateUI();
//            p.dismiss();
//            //mAdapter.notifyDataSetChanged();
//        }
////
////        private void updateUI() {
////            LinearLayout msgDisp, ipDisp;
////            msgDisp=(LinearLayout) findViewById(R.id.msgDisp);
////            ipDisp=(LinearLayout) findViewById(R.id.ipDisp);
////            msgDisp.setVisibility(View.VISIBLE);
////            ipDisp.setVisibility(View.VISIBLE);
////        }
//    }
}
