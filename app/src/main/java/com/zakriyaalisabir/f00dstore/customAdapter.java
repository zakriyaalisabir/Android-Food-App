package com.zakriyaalisabir.f00dstore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Zakriya Ali Sabir on 3/25/2018.
 */

public class customAdapter extends BaseAdapter{

    private Context c;
    private ArrayList<imageClassForProductUpload> imgClass;


    public customAdapter(Context c, ArrayList<imageClassForProductUpload> iClass) {
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

        View v=View.inflate(c,R.layout.custom_list_view,null);


        ImageView imageView=(ImageView) v.findViewById(R.id.imgListView);
        TextView textViewName=(TextView) v.findViewById(R.id.tvListViewProdName);
        TextView textViewPrice=(TextView) v.findViewById(R.id.tvListViewProdPrice);
        TextView textViewRating=(TextView)v.findViewById(R.id.tvListViewProdRating);

        textViewName.setText(imgClass.get(i).getProductName());
        textViewPrice.setText(imgClass.get(i).getProductPrice()+" PKR");
        textViewRating.setText(imgClass.get(i).getRating());
        Picasso.with(c).load(imgClass.get(i).getUrl()).centerCrop().fit().into(imageView);



        v.setTag(imgClass.get(i));

        return v;
    }

}
