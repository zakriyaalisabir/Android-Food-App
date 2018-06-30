package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AfterUpdate extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private ListView listView;

    private TextView tv;

    private ArrayList<imageClassForProductUpload> arrayList;
    private customAdapter cA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_update);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        listView=(ListView)findViewById(R.id.myListViewUpdate);
        tv=(TextView)findViewById(R.id.tvTitleViewUpdateItems);

        arrayList=new ArrayList<>();

        final ProgressDialog progressDialog=new ProgressDialog(AfterUpdate.this);
        progressDialog.setTitle("Populating ListView");
        progressDialog.setMessage("Loading Please Wait ....");
        progressDialog.setCancelable(false);

        cA=new customAdapter(getApplicationContext(),arrayList);
        listView.setAdapter(cA);


        myRef.child("locations").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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


        myRef.child("itemsList").child(user.getUid()).addValueEventListener(new ValueEventListener() {
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
                        Toast.makeText(AfterUpdate.this,"Seller donot have anything in his list.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    Toast.makeText(AfterUpdate.this,"Seller donot have anything in his list.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AfterUpdate.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String u=arrayList.get(i).getUrl();
                String pn=arrayList.get(i).getProductName();
                String pp=arrayList.get(i).getProductPrice();
                String r=arrayList.get(i).getRating();

                Intent intent=new Intent(getApplicationContext(),UpdateAn_Item.class);
                intent.putExtra("u",u);
                intent.putExtra("pn",pn);
                intent.putExtra("pp",pp);
                intent.putExtra("r",r);

                startActivity(intent);

                cA.notifyDataSetInvalidated();
                cA.notifyDataSetChanged();

                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();

                if (!(arrayList.size()>0)){
                    Toast.makeText(AfterUpdate.this,"Seller don't have anything in his list.",Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

    }
}