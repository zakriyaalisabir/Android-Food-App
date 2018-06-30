package com.zakriyaalisabir.f00dstore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Contact contact;
    private Double lati;
    private Double longi;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private ViewFlipper viewFlipper;
    private float initialXPoint;
    int[] image = { R.drawable.food1, R.drawable.food2,R.drawable.food3};

    private ImageView imageViewProfile;
    private TextView tvUName,tvUEmail;

    private String myTitle;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

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
        setContentView(R.layout.activity_profile);


        timeStamp=Calendar.getInstance().getTimeInMillis();
//        Toast.makeText(getApplicationContext(),"timeStamp = "+timeStamp,Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewFlipper=(ViewFlipper)findViewById(R.id.profileViewFilpper);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();//the rootNode of database
        user=mAuth.getCurrentUser();

        progressDialog=new ProgressDialog(Profile.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading your profile.");
        progressDialog.setMessage("Please wait ....");
        progressDialog.show();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);

        imageViewProfile=(ImageView)headerView.findViewById(R.id.imageProfile);
        tvUName=(TextView)headerView.findViewById(R.id.tvUserName);
        tvUEmail=(TextView)headerView.findViewById(R.id.tvUserEmail);


        myRef.child("locations").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contact=dataSnapshot.getValue(Contact.class);

                if(contact!=null){
                    tvUName.setText(contact.getName());
                    tvUEmail.setText(contact.getEmail());
                    myTitle=contact.getTitle();
                    if(!contact.getAccountType().equals("Seller")){
                        startActivity(new Intent(getApplicationContext(),AfterLogin.class));
                        progressDialog.dismiss();
                        finish();
                    }
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this,"Unable to fetch data from server .",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        //setting the carousal images
        for (int i = 0; i < image.length; i++) {
            ImageView imageView = new ImageView(Profile.this);
            imageView.setImageResource(image[i]);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        lati = location.getLatitude();
                        longi = location.getLongitude();

                        Log.d("latitude : ", "" + lati);
                        Log.d("longitude : ", "" + longi);

                        //locationListner is to be stopped
                        if(mAuth.getCurrentUser()==null){
                            locationManager.removeUpdates(locationListener);//stopping locationListner
                            locationManager=null;//stopping locationManager
                        }

                        contact.setLatitude(lati);
                        contact.setLongitude(longi);

                        myRef.child("locations").child(user.getUid()).setValue(contact);

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Toast.makeText(Profile.this,"Turn on location/gps provider.",Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                };
                if (ActivityCompat.checkSelfPermission(Profile.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Profile.this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 10, locationListener);//5000 milliSec and 10 meters
            }
        },4000);


        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                myRef.child("messages").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        myRef.child("messages").child(user.getUid()).child(dataSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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


                                            Intent resultIntent=new Intent(Profile.this,messagesActivity.class);
                                            resultIntent.putExtra("emailFromCA",senderEmail);

                                            PendingIntent pendingIntent=PendingIntent.getActivity(Profile.this,requestCode,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            NotificationManager nM=(NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                            NotificationCompat.Builder builder=new NotificationCompat.Builder(Profile.this)
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            startActivity(new Intent(Profile.this,NavigationSettings.class));
////            return true;
//        }
        if (id == R.id.pingDeliverers) {
            myRef.child("contacts").child("Deliverer").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){
                        Toast.makeText(Profile.this,"Pinging Deliverers...",Toast.LENGTH_LONG).show();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            Contact c=ds.getValue(Contact.class);
                            String uid=c.getUserId();

                            MessageClass m1=new MessageClass("Reach to "+myTitle+" asap,we have an order to deliver.",Calendar.getInstance().getTimeInMillis(), uid,user.getUid());


                            String t=""+Calendar.getInstance().getTimeInMillis();

                            myRef.child("messages").child(uid).child(user.getUid()).child(t).setValue(m1);

                            myRef.child("messages").child(user.getUid()).child(uid).child(t).setValue(m1);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if (id == R.id.action_signout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Profile.this,"Signing out ",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
//            return true;
        }
//        if (id == R.id.action_help) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FirebaseUser user=mAuth.getCurrentUser();
        String email=user.getEmail().replace(".",",");
        String uid=user.getUid();
        final ProgressDialog progressDialog=new ProgressDialog(Profile.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading ...");

        if (id == R.id.add_an_item) {
//            progressDialog.show();
            myRef.child("locations").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contact contact=dataSnapshot.getValue(Contact.class);
                    if(contact!=null){
                        if(contact.getAccountType().equals("Seller")){
                            startActivity(new Intent(Profile.this,AddAn_ItemToList.class));
                            progressDialog.dismiss();
                        }else {
                            Toast.makeText(Profile.this,
                                    "You are a "+contact.getAccountType()+", only sellers can access this place",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Profile.this,"Unable to connect to server.",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } else if (id == R.id.view_my_list) {
//            progressDialog.show();
            myRef.child("locations").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contact contact=dataSnapshot.getValue(Contact.class);
                    if(contact!=null){
                        if(contact.getAccountType().equals("Seller")){
                            startActivity(new Intent(Profile.this,ViewMy_Items.class));
                            progressDialog.dismiss();
                        }else {
                            Toast.makeText(Profile.this,
                                    "You are a "+contact.getAccountType()+", only sellers can access this place",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Profile.this,"Unable to connect to server.",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } else if (id == R.id.update_an_item) {
//            progressDialog.show();
            myRef.child("locations").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contact contact=dataSnapshot.getValue(Contact.class);
                    if(contact!=null){
                        if(contact.getAccountType().equals("Seller")){
                            startActivity(new Intent(Profile.this,AfterUpdate.class));
                            progressDialog.dismiss();
                        }else {
                            Toast.makeText(Profile.this,
                                    "You are a "+contact.getAccountType()+", only sellers can access this place",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Profile.this,"Unable to connect to server.",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } else if (id == R.id.delete_an_item) {
//            progressDialog.show();
            myRef.child("locations").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contact contact=dataSnapshot.getValue(Contact.class);
                    if(contact!=null){
                        if(contact.getAccountType().equals("Seller")){
                            startActivity(new Intent(Profile.this,DeleteAn_Item.class));
                            progressDialog.dismiss();
                        }else {
                            Toast.makeText(Profile.this,
                                    "You are a "+contact.getAccountType()+", only sellers can access this place",
                                    Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Profile.this,"Unable to connect to server.",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        } else if (id == R.id.location_on_map) {
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));

        }
//        else if (id == R.id.view_notifications) {
//
//        }
        else if (id == R.id.my_messenger) {
            startActivity(new Intent(getApplicationContext(),contactsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialXPoint = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalx = event.getX();
                if (initialXPoint > finalx) {
                    if (viewFlipper.getDisplayedChild() == image.length)
                        break;
                    viewFlipper.showNext();
                } else {
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}
