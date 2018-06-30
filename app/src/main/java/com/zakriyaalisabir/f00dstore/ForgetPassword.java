package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnS;
    private EditText et;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth=FirebaseAuth.getInstance();

        btnS=(Button)findViewById(R.id.btnForgetPassword);
        et=(EditText)findViewById(R.id.etEmailForgetPassword);

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog=new ProgressDialog(ForgetPassword.this);
                progressDialog.setTitle("Requesting password reset");
                progressDialog.setMessage("Please wait ....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                email=et.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(getApplicationContext(),"invalid email",Toast.LENGTH_LONG).show();
                }else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"A password reset email is sent to your entered email,kindly check your mailbox ",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"Request for password reset email failed.",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}
