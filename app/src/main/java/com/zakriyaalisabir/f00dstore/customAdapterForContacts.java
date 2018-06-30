package com.zakriyaalisabir.f00dstore;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Zakriya Ali Sabir on 3/25/2018.
 */

public class customAdapterForContacts extends BaseAdapter{

    private Context c;
    private ArrayList<ContactsClassForAdapter> imgClass;

//    LayoutInflater layoutInflater;

    public customAdapterForContacts(Context c, ArrayList<ContactsClassForAdapter> iClass) {
//        super(c,R.layout.custom_list_view,imgClass);
        this.c = c;
        this.imgClass = iClass;
//        this.layoutInflater=LayoutInflater.from(c);
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

        View v=View.inflate(c,R.layout.custom_list_view_contacts,null);
        TextView textViewName=(TextView) v.findViewById(R.id.tvListViewContactsName);
        TextView textViewType=(TextView) v.findViewById(R.id.tvListViewContactsType);
        TextView textViewEmail=(TextView) v.findViewById(R.id.tvListViewContactsEmail);

        textViewName.setText(imgClass.get(i).getName().toString());
        textViewType.setText(imgClass.get(i).getType().toString());
        textViewEmail.setText(imgClass.get(i).getEmail().toString());

        v.setTag(imgClass.get(i));

        return v;
    }

}
