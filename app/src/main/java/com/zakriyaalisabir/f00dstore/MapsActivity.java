package com.zakriyaalisabir.f00dstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private double lati, longi;

    private Contact contact;

    private Contact myContact;

    private LatLng myLoc;

    private TextView tvSnippetTit, tvSnippetDes;
    private ImageButton btnS;
    private EditText etS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseUser me = mAuth.getCurrentUser();


        btnS=(ImageButton)findViewById(R.id.btnMapSearch);
        etS=(EditText)findViewById(R.id.etMapSearch);
        btnS.setVisibility(View.GONE);
        etS.setVisibility(View.GONE);


        myRef.child("locations").child(me.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myContact = dataSnapshot.getValue(Contact.class);
                if(!myContact.getAccountType().equals("Seller")){
                    btnS.setVisibility(View.VISIBLE);
                    etS.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);//set the type of map

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater layoutInflater=(LayoutInflater)MapsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view =layoutInflater.inflate(R.layout.custom_snippet_for_map,null);

                tvSnippetTit=(TextView)view.findViewById(R.id.tvSnippetTitle);
                tvSnippetDes=(TextView)view.findViewById(R.id.tvSnippetDescription);

                tvSnippetDes.setText(marker.getSnippet());

                tvSnippetTit.setText(marker.getTitle());

                return view;
            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final String titlesWithRespectToEmails=marker.getSnippet();
//                if(marker.getTitle().equals("")){
//                    Toast.makeText(MapsActivity.this,
//                            "This person '"+titlesWithRespectToEmails+"' is not selling anything.",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Toast.makeText(MapsActivity.this,""+emailOfSeller,Toast.LENGTH_LONG).show();

                final ProgressDialog progressDialog=new ProgressDialog(MapsActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please wait");
                progressDialog.setMessage("Fetching data from server...");
                progressDialog.show();

                myRef.child("titlesWithRespectToEmails").child(titlesWithRespectToEmails.replace(".",","))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TitleForNavSettings t=dataSnapshot.getValue(TitleForNavSettings.class);
                        if(t!=null){
                            if(t.getTitle().equals("")){
                                Toast.makeText(MapsActivity.this,
                                        "This person '"+titlesWithRespectToEmails+"' is not selling anything.",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                ////redirectiong to his shop or list of items
//                                Toast.makeText(MapsActivity.this,"Title : "+t.getTitle(),Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(MapsActivity.this,Order_A_Product.class);
                                intent.putExtra("uid",t.getUserId().toString());
                                startActivity(intent);

                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MapsActivity.this,"Unable to fetch data from server",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

            }
        });

        final ProgressDialog progressDialog=new ProgressDialog(MapsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading Map");
        progressDialog.show();

        user=mAuth.getCurrentUser();

        myRef.child("locations").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for(DataSnapshot myDS:dataSnapshot.getChildren()){
                    contact=myDS.getValue(Contact.class);
                    if (contact != null) {
                        if(contact.getEmail().equals(user.getEmail())){
                            if(contact.getAccountType().equals("Seller")) {
                                myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                MarkerOptions m=new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_loc))
                                        .position(myLoc)
                                        .snippet(contact.getEmail())
                                        .title(contact.getTitle());

                                mMap.addMarker(m).showInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                            }
                            else if(contact.getAccountType().equals("Buyer")) {
                                myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                MarkerOptions m=new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_loc))
                                        .position(myLoc)
                                        .snippet(contact.getEmail())
                                        .title(contact.getTitle());

                                mMap.addMarker(m).hideInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                            }else if(contact.getAccountType().equals("Deliverer")) {
                                myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                MarkerOptions m=new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_loc))
                                        .position(myLoc)
                                        .snippet(contact.getEmail())
                                        .title(contact.getTitle());

                                mMap.addMarker(m).hideInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                            }
                        }else {
                            if(myContact.getAccountType().equals("Seller")) {
                                if(contact.getAccountType().equals("Deliverer")) {
                                    myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                    MarkerOptions m=new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                            .position(myLoc)
                                            .snippet(contact.getEmail())
                                            .title(contact.getTitle());

                                    mMap.addMarker(m);
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
//                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                                }
                            }
                            else if(myContact.getAccountType().equals("Buyer")) {
                                if(contact.getAccountType().equals("Seller")) {
                                    myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                    MarkerOptions m=new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                            .position(myLoc)
                                            .snippet(contact.getEmail())
                                            .title(contact.getTitle());

                                    mMap.addMarker(m);
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
//                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                                }
                            }
                            else if(myContact.getAccountType().equals("Deliverer")) {

                                //////////old else part /////of ////code////////here

                                if(contact.getAccountType().equals("Seller")) {
                                    myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                    MarkerOptions m=new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                            .position(myLoc)
                                            .snippet(contact.getEmail())
                                            .title(contact.getTitle());

                                    mMap.addMarker(m);
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
//                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                                }
                                else if(contact.getAccountType().equals("Buyer")) {
                                    myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());

                                    MarkerOptions m=new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(myLoc)
                                            .snippet(contact.getEmail())
                                            .title(contact.getTitle());

                                    mMap.addMarker(m);
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
//                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
                                }
//                                else if(contact.getAccountType().equals("Deliverer")) {
//                                    myLoc = new LatLng(contact.getLatitude(), contact.getLongitude());
//
//                                    MarkerOptions m=new MarkerOptions()
//                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//                                            .position(myLoc)
//                                            .snippet(contact.getEmail())
//                                            .title(contact.getTitle());
//
//                                    mMap.addMarker(m);
////                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
////                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 17.0f));
//                                }



                            }

                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this,"Unable to UPDATE map...",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });


        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String search=etS.getText().toString().trim().replace("\\s","");
                if(search.isEmpty()){
                    Toast.makeText(getApplicationContext(),"invalid address",Toast.LENGTH_LONG).show();
                }else {
                    myRef.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
//                                final ProgressDialog pd=new ProgressDialog(MapsActivity.this);
//                                pd.setCancelable(true);
//                                pd.setTitle("Searching For Address/Title");
//                                pd.setMessage("Please wait...");
//                                pd.show();

                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    Contact c=ds.getValue(Contact.class);
                                    Log.d("title",c.getTitle());
                                    if(search.equalsIgnoreCase(c.getTitle().trim().replace("\\s",""))){
                                        LatLng ll=new LatLng(c.getLatitude(),c.getLongitude());
                                        MarkerOptions m=new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                .position(ll)
                                                .snippet(c.getEmail())
                                                .title(c.getTitle());

                                        mMap.addMarker(m).showInfoWindow();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 17.0f));
//                                        progressDialog.dismiss();
                                        break;
                                    }
//                                    progressDialog.dismiss();
                                }
//                                progressDialog.dismiss();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}