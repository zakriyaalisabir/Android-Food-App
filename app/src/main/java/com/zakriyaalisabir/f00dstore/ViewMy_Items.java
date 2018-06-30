package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewMy_Items extends AppCompatActivity {

    private ListView listView;

    private TextView tv;

    private ArrayList<imageClassForProductUpload> arrayList;
    private customAdapter cA;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my__items);

        listView=(ListView) findViewById(R.id.myListView);

        tv=(TextView)findViewById(R.id.tvTitleViewItems);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        arrayList=new ArrayList<>();

//
//        final ProgressDialog progressDialog=new ProgressDialog(ViewMy_Items.this);
//        progressDialog.setTitle("Population ListView");
//        progressDialog.setMessage("Loading Please Wait ....");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

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
                        Toast.makeText(ViewMy_Items.this,"Seller don't have anything in his list.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    Toast.makeText(ViewMy_Items.this,"Seller don't have anything in his list.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewMy_Items.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
            }

        });


//        progressDialog.dismiss();

    }
}
