package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class contactsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private ArrayList<ContactsClassForAdapter> arrayList;
    private customAdapterForContacts cA;

    private ProgressDialog progressDialog;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        final ProgressDialog progressDialog = new ProgressDialog(contactsActivity.this);
        progressDialog.setTitle("Fetching Conversation");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();

        listView=(ListView) findViewById(R.id.myListViewContacts);

        arrayList=new ArrayList<>();
        cA=new customAdapterForContacts(contactsActivity.this,arrayList);
        listView.setAdapter(cA);

        myRef.child("messages").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
//                        Toast.makeText(contactsActivity.this,"uid : "+ds.getKey(),Toast.LENGTH_LONG).show();
                        myRef.child("locations").child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Contact contact=dataSnapshot.getValue(Contact.class);

                                ContactsClassForAdapter ccfa=new ContactsClassForAdapter(contact.getName(),contact.getAccountType(),contact.getEmail());
                                arrayList.add(ccfa);
//                                Toast.makeText(contactsActivity.this,"name : "+contact.getName(),Toast.LENGTH_LONG).show();
                                cA.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    progressDialog.dismiss();

                }else {
                    Toast.makeText(contactsActivity.this,"No conversation.",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(contactsActivity.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent intent=new Intent(contactsActivity.this,messagesActivity.class);
                String email=arrayList.get(pos).getEmail();
                intent.putExtra("emailFromCA",email);
                startActivity(intent);
            }
        });



    }
}
