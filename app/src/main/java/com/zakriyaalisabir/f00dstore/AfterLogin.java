package com.zakriyaalisabir.f00dstore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AfterLogin extends AppCompatActivity {

    private Button btnLOM,btnMM,btnL;
    private TextView tvN,tvE;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private Contact contact;

    private String senderName;
    private String senderEmail;
    private String to;
    private String senderUid;
    private String msg;
    private long msgTime;

    private long timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        timeStamp= Calendar.getInstance().getTimeInMillis();

        btnLOM=(Button)findViewById(R.id.btnLocationOnMap);
        btnMM=(Button)findViewById(R.id.btnMyMessenger);
        btnL=(Button)findViewById(R.id.btnLogout);
        tvN=(TextView)findViewById(R.id.name);
        tvE=(TextView)findViewById(R.id.email);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();
        user=mAuth.getCurrentUser();

        myRef.child("locations").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contact=dataSnapshot.getValue(Contact.class);
                if(contact!=null){
                    tvN.setText(contact.getName());
                    tvE.setText(contact.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnLOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });

        btnMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),contactsActivity.class));
            }
        });

        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(AfterLogin.this,"Signing out ",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                myRef.child("messages").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//                        Toast.makeText(getApplicationContext(),"child added to db"+dataSnapshot.getKey(),Toast.LENGTH_LONG).show();
                        myRef.child("messages").child(user.getUid()).child(dataSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//                                Toast.makeText(getApplicationContext(),"child added to db"+dataSnapshot.getKey(),Toast.LENGTH_LONG).show();
                                if(dataSnapshot==null){
                                    return;
                                }
                                MessageClass m=dataSnapshot.getValue(MessageClass.class);

                                senderUid=m.getFrom().toString();
                                msg=m.getMsg().toString();
                                to=m.getTo().toString();
                                msgTime=m.getTime();

                                myRef.child("locations").child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot==null){
                                            return;
                                        }

                                        Contact c=dataSnapshot.getValue(Contact.class);
                                        senderName=c.getName().toString();
                                        senderEmail=c.getEmail().toString();

//                                        Toast.makeText(getApplicationContext(),"to"+to,Toast.LENGTH_LONG).show();

                                        if(to.equals(user.getUid().toString())){

                                            if(senderUid.equals(user.getUid().toString())){
                                                return;
                                            }

                                            int requestCode=(senderEmail.length())/(senderName.length());

                                            Intent resultIntent=new Intent(AfterLogin.this,messagesActivity.class);
                                            resultIntent.putExtra("emailFromCA",senderEmail);

                                            PendingIntent pendingIntent=PendingIntent.getActivity(AfterLogin.this,requestCode,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager nM=(NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder=new NotificationCompat.Builder(AfterLogin.this)
                                                    .setSmallIcon(R.drawable.view_notifications)
                                                    .setContentTitle(senderName)
                                                    .setContentText(msg)
                                                    .setAutoCancel(true)
                                                    .setContentIntent(pendingIntent)
                                                    .setDefaults(Notification.DEFAULT_SOUND);//for notification style

                                            if(msgTime<timeStamp){//msgTime<timeStamp (time captured while entering in the profile)
                                                return;
                                            }
                                            else {
//                                                      Random random=new Random();
//                                                      int ii=random.nextInt();//'ii' is the notification id

//                                                       int id=(senderEmail.length())/(senderName.length());

                                                int id=requestCode;
                                                nM.notify(id,builder.build());
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                        Toast.makeText(getApplicationContext(),"child updated in db"+dataSnapshot.getKey(),Toast.LENGTH_LONG).show();

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
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Toast.makeText(getApplicationContext(),"child updated in db"+dataSnapshot.getKey(),Toast.LENGTH_LONG).show();

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
                });
            }
        });
        thread.start();
    }
}
