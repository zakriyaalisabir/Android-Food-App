package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class messagesActivity extends AppCompatActivity {

    private ListView lv;

    private ImageButton btnSM;
    private EditText etM;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private ArrayList<MessageClass> arrayList;
    private customAdapterForMessages cA;

    private String myUid;
    private String otherUserUid;
    private String otherUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        final ProgressDialog progressDialog = new ProgressDialog(messagesActivity.this);
        progressDialog.setTitle("Fetching Conversation");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();

        lv=(ListView)findViewById(R.id.listViewMessages);

        etM=(EditText)findViewById(R.id.etMsg);
        btnSM=(ImageButton)findViewById(R.id.btnSendMsg);

        arrayList=new ArrayList<>();
        cA=new customAdapterForMessages(getApplicationContext(),arrayList);
        lv.setAdapter(cA);


        otherUserEmail= getIntent().getStringExtra("emailFromCA").replace(".",",").toString();

        myUid=user.getUid().toString();

        myRef.child("titlesWithRespectToEmails").child(otherUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    TitleForNavSettings tfns=dataSnapshot.getValue(TitleForNavSettings.class);
                    otherUserUid=tfns.getUserId();
                    myRef.child("messages").child(myUid).child(otherUserUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                                arrayList.clear();
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    MessageClass mC=ds.getValue(MessageClass.class);

//                                    Toast.makeText(messagesActivity.this,""+mC.getMsg(),Toast.LENGTH_LONG).show();

                                    arrayList.add(mC);
                                    lv.invalidateViews();
                                    cA.notifyDataSetChanged();
                                }
                                progressDialog.dismiss();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(messagesActivity.this,"No Conversation Found.",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
//                            Toast.makeText(messagesActivity.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(messagesActivity.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
            }
        });


        btnSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=etM.getText().toString();
                if(!msg.isEmpty()){
                    MessageClass m1=new MessageClass(msg, Calendar.getInstance().getTimeInMillis(),otherUserUid,user.getUid());

                    arrayList.clear();


                    String t=""+Calendar.getInstance().getTimeInMillis();

                    myRef.child("messages").child(otherUserUid).child(user.getUid()).child(t).setValue(m1);
                    myRef.child("messages").child(user.getUid()).child(otherUserUid).child(t).setValue(m1);

                    etM.setText("");



                }
            }
        });



    }
}
