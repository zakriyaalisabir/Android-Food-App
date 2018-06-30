package com.zakriyaalisabir.f00dstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by Zakriya Ali Sabir on 3/25/2018.
 */

public class customAdapterForMessages extends BaseAdapter{

    private Context c;
    private ArrayList<MessageClass> imgClass;

    public customAdapterForMessages(Context c, ArrayList<MessageClass> iClass) {
        this.c = c;
        this.imgClass = iClass;
    }

    @Override
    public int getCount() {
        return imgClass.size();
    }

    @Override
    public Object getItem(int pos) {
        return imgClass.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


//        View v=view;

        View v;
        TextView textViewName;

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();

        if(imgClass.get(i).getFrom().toString().equals(user.getUid().toString())){

            if(imgClass.get(i).getTo().toString().equals(imgClass.get(i).getFrom().toString())){
                v=View.inflate(c,R.layout.custom_list_view_message_row_me,null);
                textViewName=(TextView)v.findViewById(R.id.tvListViewMyMsg);

            }else {

                v=View.inflate(c,R.layout.custom_list_view_message_row_me,null);
                textViewName=(TextView)v.findViewById(R.id.tvListViewMyMsg);
            }

        }
        else {
            v=View.inflate(c,R.layout.custom_list_view_message_row_other,null);
            textViewName=(TextView)v.findViewById(R.id.tvListViewYouMsg);
        }


        textViewName.setText(imgClass.get(i).getMsg());

        v.setTag(imgClass.get(i));

        return v;
    }
}
