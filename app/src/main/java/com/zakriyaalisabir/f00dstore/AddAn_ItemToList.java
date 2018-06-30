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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class AddAn_ItemToList extends AppCompatActivity {


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
        setContentView(R.layout.activity_add_an__item_to_list);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnAdd=(Button)findViewById(R.id.btnAddAnItem);
        etPN=(EditText)findViewById(R.id.etProductNameAdd);
        etPP=(EditText)findViewById(R.id.etProductPriceAdd);
        imgAI=(ImageView)findViewById(R.id.imgAddAnItem);

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
                final ProgressDialog progressDialog = new ProgressDialog(AddAn_ItemToList.this);
                progressDialog.setTitle("Uploading Product Info");
                progressDialog.setMessage("Please wait....");
                progressDialog.setCancelable(false);
                progressDialog.show();


                prodName=etPN.getText().toString();
                prodPrice=etPP.getText().toString();

                final String d= Calendar.getInstance().getTime().toString();

                if(prodName.isEmpty()||prodPrice.isEmpty()){
                    Toast.makeText(AddAn_ItemToList.this,"Invalid info.",Toast.LENGTH_LONG).show();
                }else {
//                    Uri uri=Uri.fromFile(new File(imgDecodableString));
                    Uri uri=Uri.parse(imgDecodableString);
                    StorageReference storageReference=mStorageRef
                            .child("itemsList")
                            .child(user.getUid())
                            .child(prodName);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            downloadUrl = taskSnapshot.getDownloadUrl();//private Uri
                            Toast.makeText(AddAn_ItemToList.this,"Item Successfully Added.",Toast.LENGTH_LONG).show();

                            imageClassForProductUpload imcfpu=new imageClassForProductUpload(downloadUrl.toString(),prodName,prodPrice,""+5);
                            myRef.child("itemsList").child(user.getUid()).child(prodName).setValue(imcfpu);

                            progressDialog.dismiss();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(AddAn_ItemToList.this,"Uploaded Failed.",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
//                    Toast.makeText(AddAn_ItemToList.this,"Time = "+d,Toast.LENGTH_LONG).show();
//                    imageClassForProductUpload imcfpu=new imageClassForProductUpload(downloadUrl.toString());
//                    myRef.child("itemsList").child(user.getUid()).child(d).setValue(imcfpu);
                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imgDecodableString=data.getData().toString();

        Picasso.with(AddAn_ItemToList.this).load(data.getData().toString()).into(imgAI);
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
//
//                // Set the Image in ImageView after decoding the String
//                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
//
//            } else {
//                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//        }

    }
}
