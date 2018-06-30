package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
        import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.List;

public class UpdateAn_Item extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private Button btnAdd;
    private EditText etPN,etPP;
    private ImageView imgAI;

    private String prodName,prodPrice;

    private int RESULT_LOAD_IMAGE=1;
    private String imgDecodableString;

    private Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_an__item);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnAdd=(Button)findViewById(R.id.btnUpdate);
        etPN=(EditText)findViewById(R.id.etPNUpdate);
        etPP=(EditText)findViewById(R.id.etPPUpdate);
        imgAI=(ImageView)findViewById(R.id.imgUpdateAnItem);

        final String u=getIntent().getStringExtra("u");
        final String pn=getIntent().getStringExtra("pn");
        final String pp=getIntent().getStringExtra("pp");
        final String r=getIntent().getStringExtra("r");

        etPN.setText(pn);
        etPP.setText(pp);
        Picasso.with(getApplicationContext()).load(u).into(imgAI);
        imgDecodableString=u;

        imgAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent=new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(UpdateAn_Item.this);
                progressDialog.setTitle("Updating Product Info.");
                progressDialog.setMessage("Please wait....");
                progressDialog.setCancelable(false);
                progressDialog.show();


                prodName=etPN.getText().toString();
                prodPrice=etPP.getText().toString();

//                final String d= Calendar.getInstance().getTime().toString();

                if(prodName.isEmpty()||prodPrice.isEmpty()){
                    Toast.makeText(UpdateAn_Item.this,"Invalid info.",Toast.LENGTH_LONG).show();
                }else {
                    if(imgDecodableString!=u){
//                        Uri uri=Uri.fromFile(new File(imgDecodableString));
                        Uri uri=Uri.parse(imgDecodableString);
                        StorageReference storageReference=mStorageRef.child("itemsList").child(user.getUid()).child(prodName);

                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                downloadUrl = taskSnapshot.getDownloadUrl();//private Uri
                                Toast.makeText(UpdateAn_Item.this,"Item Successfully Added.",Toast.LENGTH_LONG).show();

                                imageClassForProductUpload imcfpu=new imageClassForProductUpload(downloadUrl.toString(),prodName,prodPrice,r);
                                myRef.child("itemsList").child(user.getUid()).child(prodName).setValue(imcfpu);
                                if(!prodName.equals(pn)){
                                    myRef.child("itemsList").child(user.getUid()).child(pn).removeValue();
                                }

                                progressDialog.dismiss();
                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(UpdateAn_Item.this,"Uploading Failed.",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });

                    }else {
                        imageClassForProductUpload imcfpu=new imageClassForProductUpload(u,prodName,prodPrice,r);
                        myRef.child("itemsList").child(user.getUid()).child(prodName).setValue(imcfpu);
                        myRef.child("itemsList").child(user.getUid()).child(pn).removeValue();


                        progressDialog.dismiss();
                        finish();
                    }


                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgDecodableString="";
        imgAI.setImageResource(0);
        imgAI.setImageDrawable(null);//removing the pre-loaded image

        imgDecodableString=data.getData().toString();

        Picasso.with(UpdateAn_Item.this).load(data.getData().toString()).into(imgAI);

//        try {
//            // When an Image is picked
//            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//                // Get the Image from data
//
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//                // Get the cursor
//                Cursor cursor = getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                // Move to first row
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                imgDecodableString = cursor.getString(columnIndex);
//                cursor.close();
//                ImageView imgView = (ImageView) findViewById(R.id.imgAddAnItem);
////                imgView.setImageDrawable(null);//removing the pre-loaded image
//
//                // Set the Image in ImageView after decoding the String
//                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
//
////                Picasso.with(getApplicationContext()).load(selectedImage).into(imgView);
//
//            } else {
//                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//        }

    }
}
