package com.zakriyaalisabir.f00dstore;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    private Contact contact;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();//the rootNode of database

        if(currentUser!=null){

            startActivity(new Intent(getApplicationContext(),Profile.class));
            finish();

//            myRef.child("locations").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    contact=dataSnapshot.getValue(Contact.class);
//
//                    if(contact!=null){
//                        if(!contact.getAccountType().equals("Seller")){
//                            startActivity(new Intent(getApplicationContext(),AfterLogin.class));
//                            finish();
//                        }
//                        else {
//                            startActivity(new Intent(getApplicationContext(),Profile.class));
//                            finish();
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Toast.makeText(MainActivity.this,"Unable to fetch data from server .",Toast.LENGTH_LONG).show();
//                }
//            });
        }else {

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            },2000);
        }
    }
}
