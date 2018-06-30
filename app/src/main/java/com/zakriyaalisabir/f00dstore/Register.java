package com.zakriyaalisabir.f00dstore;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    Button btnSR;
    EditText etN, etE, etP,etTit;

    private LocationManager locationManager;
    private LocationListener locationListener;

    public String name, email, password, phone,title;
    private double lati, longi;
    private String SBD;//seller,buyer,deliverer

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSR = (Button) findViewById(R.id.btnSubmitRegister);
        etN = (EditText) findViewById(R.id.etNameRegister);
        etE = (EditText) findViewById(R.id.etEmailRegister);
        etP = (EditText) findViewById(R.id.etPasswordRegister);
        etTit = (EditText) findViewById(R.id.etTitleRegister);

        SBD=getIntent().getStringExtra("accountType");//fetching from previous activity
        title="";

        if(!SBD.equals("Seller")){
            etTit.setVisibility(View.GONE);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        btnSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etN.getText().toString();
                email = etE.getText().toString();
                password = etP.getText().toString();
                if (etTit.isShown()){
                    title=etTit.getText().toString();
                    if(title.isEmpty() || title.equals("")){
                        Toast.makeText(Register.this, "Invalid information,Title is missing", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Register.this, "Invalid information", Toast.LENGTH_LONG).show();
                } else if (password.length() < 8) {
                    Toast.makeText(Register.this, "Password Length should be minimum of 8 characters", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register.this, "Registering please wait", Toast.LENGTH_LONG).show();
                    final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setTitle("Registering");
                    progressDialog.setMessage("Please wait....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registration Failed,Check Your Internet Connection", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    } else if (task.isSuccessful()) {

                                        locationListener = new LocationListener() {
                                            @Override
                                            public void onLocationChanged(Location location) {
                                                lati = location.getLatitude();
                                                longi = location.getLongitude();

                                                Log.d("latitude : ", "" + lati);
                                                Log.d("longitude : ", "" + longi);

                                                //locationListner is to be stopped after capturing the lat and long
                                                locationManager.removeUpdates(locationListener);//stopping locationListner
                                                locationManager=null;//stopping locationManager

                                                Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_LONG).show();


                                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                                Contact contact=new Contact(name,email,password,lati,longi,SBD,currentUser.getUid(),title);

                                                myRef.child("contacts").child(SBD).child(name).setValue(contact);

                                                myRef.child("locations").child(currentUser.getUid()).setValue(contact);


                                                TitleForNavSettings t=new TitleForNavSettings(contact.getTitle(),currentUser.getEmail(),currentUser.getUid());
                                                myRef.child("titlesWithRespectToEmails").child(currentUser.getEmail().replace(".",",")).setValue(t);

                                                progressDialog.dismiss();

                                                startActivity(new Intent(Register.this,Profile.class));

                                                finish();


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
                                                Toast.makeText(Register.this,"Turn on location/gps provider.",Toast.LENGTH_LONG).show();
                                                startActivity(intent);
                                            }
                                        };
                                        if (ActivityCompat.checkSelfPermission(Register.this,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                                PackageManager.PERMISSION_GRANTED &&
                                                ActivityCompat.checkSelfPermission(Register.this,
                                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                        PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        locationManager.requestLocationUpdates("gps", 0, 0, locationListener);

                                    }
                                }
                            });
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
