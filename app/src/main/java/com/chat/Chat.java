package com.chat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private StorageReference imageRef;
    ProgressBar data;
    private final int PERMISSIONS_ALL=1;
    private final int GALLERY = 2;
    private final int CAMERA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        data=(ProgressBar) findViewById(R.id.loadmess);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        uid=user.getUid();
        imageRef= FirebaseStorage.getInstance().getReference();
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
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        processStopService();
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
        System.out.println("Notif service running onCreate "+ isMyServiceRunning(NotifManager.class)+"");
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void processStartService() {
        Intent intent = new Intent(getApplicationContext(), NotifManager.class);
        startService(intent);
    }

    private void processStopService() {
        Intent intent = new Intent(getApplicationContext(), NotifManager.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Notif service running onResume "+ isMyServiceRunning(NotifManager.class)+"");
        //processStopService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Log.d("Stop: ", "service");
//                in=new Intent(this, NotifManager.class);
//                stopService(in);
                processStopService();
                Intent i=new Intent(this, LogIn.class);
                startActivity(i);
                finish();

            case R.id.gallery:
                startGallery();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startGallery() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select image"), GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY){
            if(resultCode == RESULT_OK){
                if(data!=null){
                    Uri imageUri = data.getData();

                    StorageReference filepath = imageRef.child("message_images").child(imageUri.toString()+".jpg");

                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                String down = task.getResult().getDownloadUrl().toString();
                                Message message = new Message(user.getEmail(), down, getDate(), getTime(), String.valueOf(false), "image");
                                message.setSelf(true);
                                mDatabase.child("messages").push().setValue(message);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error sending image", Toast.LENGTH_LONG).show();
                        }
                    });
                    messageList.scrollToPosition(messages.size() - 1);
                }
            }
        }
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
                Message message = new Message(user.getEmail(), mes, getDate(), getTime(), String.valueOf(false), "text");
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
//            in=new Intent(this, NotifManager.class);
//            Log.d("Service onDestroy(): ", "started");
//            startService(in);
            processStartService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth.getCurrentUser()!=null){
//            in=new Intent(this, NotifManager.class);
//            Log.d("Service onStop(): ", "started");
//            startService(in);
            processStartService();
        }
    }

    @Override
    public void onChildAdded(DataSnapshot ds, String s) {
        System.out.println("DATA: "+ ds.toString() + " " + s);
        Message m = ds.getValue(Message.class);
//        Log.d("MESS: ", m.toString());
//        Log.d("UID: ", uid);
//        String fromName = ds.getValue(Message.class).getFromName();
//        Log.d("From: ", fromName);
//        String me = ds.getValue(Message.class).getMessage();
//        Log.d("Mess: ", me);
//        String date = ds.getValue(Message.class).getDate();
//        String time = ds.getValue(Message.class).getTime();
        String to = ds.getValue(Message.class).getTo();
//        String type = ds.getValue(Message.class).getType();
//        Log.d("Type: ", type);
        Boolean b = Boolean.parseBoolean(to);
//        m.setFromName(fromName);
//        m.setMessage(me);
//        m.setDate(date);
//        m.setTime(time);
//        m.setType(type);
        if (m.getFromName().equals(user.getEmail())){
            Log.d("VaL: ", "true");
            m.setSelf(true);
        }
        else {
            Log.d("VaL: ", "false");
            b = true;
            m.setSelf(false);
        }
        //m.setTo(String.valueOf(b));
        mDatabase.child("messages").child(ds.getKey()).child("to").setValue(String.valueOf(b));
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
                //Log.i ("Notif ServiceRunning", true+"");
                return true;
            }
        }
        //Log.i ("Notif ServiceRunning", false+"");
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
}
