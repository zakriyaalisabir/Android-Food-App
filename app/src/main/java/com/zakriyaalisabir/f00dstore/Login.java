package com.zakriyaalisabir.f00dstore;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    Button btnL,btnR;
    EditText etE,etP;
    TextView tvFP;

    public String email;
    public String password;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private final int PERMISSION_CODE=1;

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Toast.makeText(Login.this,"Enable All Permissions In Order To Use Our App Properly",Toast.LENGTH_LONG).show();

        String[] permissions={
                android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if(!hasPermissions(Login.this,permissions)){
            ActivityCompat.requestPermissions(Login.this,permissions,PERMISSION_CODE);
        }

        mAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();//the rootNode of database
        user=mAuth.getCurrentUser();

        btnL=(Button)findViewById(R.id.btnLogin);
        btnR=(Button)findViewById(R.id.btnRegister);
        etE=(EditText)findViewById(R.id.etEmailLogin);
        etP=(EditText)findViewById(R.id.etPasswordLogin);
        tvFP=(TextView) findViewById(R.id.tvForgetPassword);

        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=etE.getText().toString();
                password=etP.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this,"Invalid email or password",Toast.LENGTH_LONG).show();
                }else if(password.length()<8){
                    Toast.makeText(Login.this,"Password Length should be greater than 8 letters",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Login.this,"Logging in please wait",Toast.LENGTH_LONG).show();
                    final ProgressDialog progressDialog=new ProgressDialog(Login.this);
                    progressDialog.setTitle("Signing in");
                    progressDialog.setMessage("Please wait ....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }

                                    else if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        startActivity(new Intent(Login.this,Profile.class));
                                        Toast.makeText(Login.this,"Successfully Logged in",Toast.LENGTH_LONG).show();
                                        finish();
//                                        progressDialog.dismiss();

//                                        myRef.child("locations").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                contact=dataSnapshot.getValue(Contact.class);
//
//                                                if(contact!=null){
//                                                    if(!contact.getAccountType().equals("Seller")){
//                                                        startActivity(new Intent(getApplicationContext(),AfterLogin.class));
//                                                        progressDialog.dismiss();
//                                                        finish();
//                                                    }
//                                                    else {
//                                                        startActivity(new Intent(getApplicationContext(),Profile.class));
//                                                        progressDialog.dismiss();
//                                                        finish();
//                                                    }
//                                                }
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//                                                Toast.makeText(Login.this,"Unable to fetch data from server .",Toast.LENGTH_LONG).show();
//                                            }
//                                        });
                                    }
                                }
                            });

                }

            }
        });

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,RegisterAs.class));
                finish();

            }
        });

        tvFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this,"You clicked on forget password",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this,ForgetPassword.class));
            }
        });

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null){
////            startActivity(new Intent(Login.this,Profile.class));
//        }
//    }

    private boolean hasPermissions(Context context, String[] permissions) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission:permissions){
                if(ActivityCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Login.this,"Permissions Granted!",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Login.this,"Permissions Denied!",Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }
}


