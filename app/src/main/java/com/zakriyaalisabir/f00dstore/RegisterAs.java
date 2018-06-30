package com.zakriyaalisabir.f00dstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterAs extends AppCompatActivity {

    private Button btnRAS,btnRAB,btnRAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as);

        btnRAB=(Button)findViewById(R.id.registerAsBuyer);
        btnRAS=(Button)findViewById(R.id.registerAsSeller);
        btnRAD=(Button)findViewById(R.id.registerAsDeliverer);

        btnRAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                intent.putExtra("accountType","Buyer");
                startActivity(intent);
                finish();
            }
        });

        btnRAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                intent.putExtra("accountType","Seller");
                startActivity(intent);
                finish();
            }
        });

        btnRAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                intent.putExtra("accountType","Deliverer");
                startActivity(intent);
                finish();
            }
        });

    }
}
