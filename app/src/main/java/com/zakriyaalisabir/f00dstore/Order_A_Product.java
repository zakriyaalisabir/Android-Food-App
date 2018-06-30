package com.zakriyaalisabir.f00dstore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Order_A_Product extends AppCompatActivity {

    private String uidFromMaps;

    private TextView tv;

    private String amount;
    private int rateing;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private ArrayList<imageClassForProductUpload> arrayList;
    private customAdapter cA;

    private StorageReference mStorageRef;

    private ProgressDialog progressDialog;

    private ListView listView;

    private String key,subKey;
    private imageClassForProductUpload img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__a__product);

        uidFromMaps=getIntent().getExtras().getString("uid");

//        Toast.makeText(Order_A_Product.this,"uid : "+uidFromMaps,Toast.LENGTH_LONG).show();


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();

        listView=(ListView) findViewById(R.id.myListViewOrder);

        tv=(TextView)findViewById(R.id.tvTitleOrder);

        myRef.child("locations").child(uidFromMaps).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                if(contact!=null){
                    tv.setText(contact.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ProgressDialog progressDialog=new ProgressDialog(Order_A_Product.this);
        progressDialog.setTitle("Population ListView");
        progressDialog.setMessage("Loading Please Wait ....");
        progressDialog.setCancelable(false);

        arrayList=new ArrayList<>();

        cA=new customAdapter(getApplicationContext(),arrayList);
        listView.setAdapter(cA);

        myRef.child("itemsList").child(uidFromMaps).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null){
                    arrayList.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        imageClassForProductUpload imgClass=ds.getValue(imageClassForProductUpload.class);
                        arrayList.add(imgClass);

                        cA.notifyDataSetChanged();

//                            Toast.makeText(DeleteAn_Item.this,"name :"+imgClass.getProductName()+
//                            "\nprice : "+imgClass.getProductPrice(),Toast.LENGTH_LONG).show();

                    }


                    //sorting an array list in ASC w.r.t rating of products
                    int l=arrayList.size();
                    for (int i=0;i<l;i++){
                        int r= Integer.parseInt(arrayList.get(i).getRating());
                        for (int j=0;j<l;j++){
                            int ra=Integer.parseInt(arrayList.get(j).getRating());
                            if(r<=ra){
                                continue;
                            }else  if(r>ra){
                                imageClassForProductUpload a=arrayList.get(i);
                                imageClassForProductUpload b=arrayList.get(j);
                                arrayList.set(i,b);
                                arrayList.set(j,a);
                                r=Integer.parseInt(b.getRating());
                            }

                        }
                    }

                    if (!(arrayList.size()>0)){
                        Toast.makeText(Order_A_Product.this,"Seller don't have anything in his list.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    Toast.makeText(Order_A_Product.this,"Seller don't have anything in his list.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Order_A_Product.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int ii, long l) {

                myRef.child("itemsList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot!=null){
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(ds!=null){
                                    for(DataSnapshot d:ds.getChildren()){
                                        if(d.getKey().equals(""+arrayList.get(ii).getProductName())){
                                            img=d.getValue(imageClassForProductUpload.class);
                                            key=ds.getKey();
                                            rateing=Integer.parseInt(img.getRating());
//                                            Toast.makeText(getApplicationContext(),"key = "+key,Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final AlertDialog.Builder builder=new AlertDialog.Builder(Order_A_Product.this);
                builder.setCancelable(true);

                builder.setTitle("You Want To Rate The Product Or Buy ? ");
                builder.setIcon(R.drawable.add_an_item);

                builder.setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder builderAmount=new AlertDialog.Builder(Order_A_Product.this);
                        final EditText input=new EditText(Order_A_Product.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builderAmount.setView(input);
                        builderAmount.setCancelable(true);
                        builderAmount.setTitle("Amount Of Item / kg");
                        builderAmount.setIcon(R.drawable.add_an_item);
                        builderAmount.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                amount=input.getText().toString();
                                Toast.makeText(Order_A_Product.this,"Amount = "+amount,Toast.LENGTH_LONG).show();

                                MessageClass m1=new MessageClass("i want "+arrayList.get(ii).getProductName().toUpperCase().toString()+
                                        " of QUANTITY = "+amount.toString(),Calendar.getInstance().getTimeInMillis(), uidFromMaps,user.getUid());


                                String t=""+Calendar.getInstance().getTimeInMillis();

                                myRef.child("messages").child(uidFromMaps).child(user.getUid()).child(t).setValue(m1);

                                myRef.child("messages").child(user.getUid()).child(uidFromMaps).child(t).setValue(m1);

                            }
                        });
                        builderAmount.show();

                    }
                });
                builder.setNegativeButton("Rate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder builderRate=new AlertDialog.Builder(Order_A_Product.this);
                        final EditText inputRate=new EditText(Order_A_Product.this);
                        inputRate.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builderRate.setView(inputRate);
                        builderRate.setCancelable(true);
                        builderRate.setTitle("Rate the product between 1 to food5");
                        builderRate.setIcon(R.drawable.add_an_item);
                        builderRate.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rateing=rateing+Integer.parseInt(inputRate.getText().toString());
                                rateing=rateing/2;


                                Toast.makeText(Order_A_Product.this,"Rating = "+rateing,Toast.LENGTH_LONG).show();

                                MessageClass m1=new MessageClass("i rated your product "+arrayList.get(ii).getProductName().toUpperCase().toString()+
                                        " to "+rateing+" stars",Calendar.getInstance().getTimeInMillis(), uidFromMaps,user.getUid());

                                img.setRating(""+rateing);
                                myRef.child("itemsList").child(key).child(img.getProductName()).setValue(img);

                                String t=""+Calendar.getInstance().getTimeInMillis();

                                myRef.child("messages").child(uidFromMaps).child(user.getUid()).child(t).setValue(m1);

                                myRef.child("messages").child(user.getUid()).child(uidFromMaps).child(t).setValue(m1);

                            }
                        });
                        builderRate.show();
                    }
                });
                builder.show();

            }
        });

    }
}
