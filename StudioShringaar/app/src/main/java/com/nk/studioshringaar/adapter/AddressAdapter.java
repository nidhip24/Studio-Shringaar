package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AddressData> arrayList;
    private String UID;
    private FirebaseFirestore db;
    private View.OnClickListener mOnItemClickListener;
    private static final String TAG = "WishlistAdapter";

    public AddressAdapter(Context context, ArrayList<AddressData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public AddressAdapter(Context context, ArrayList<AddressData> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
    }

    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_list, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(final AddressAdapter.ViewHolder holder, int position) {
        final AddressData p = arrayList.get(position);
//        Glide.with(context)
//            .load(p.getImg())
//            .into(holder.img);

        Log.e("AddressAdapter","in addressadapted");

        holder.phone.setText(p.getPhone());
        holder.t.setText(p.getName());

        if (p.getChecked()){
            holder.t.setChecked(true);
        }else{
            holder.t.setChecked(false);
        }

        holder.addr.setText(p.getAddress());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView phone, addr;
        RadioButton t;

        public ViewHolder(View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.first);
            addr = itemView.findViewById(R.id.addr);
            phone = itemView.findViewById(R.id.phone);
            itemView.setOnClickListener(mOnItemClickListener);

            addr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AddressData p = arrayList.get(getAdapterPosition());
                    p.setChecked(true);
                    for (int i = 0; i<arrayList.size(); i++){
                        AddressData tmp = arrayList.get(i);
                        if ( !(tmp.getId()).equals(p.getId()) ){
                            Log.e("AddressAdapterScartyy",  tmp.getId()+"= "+ p.getId()+" size2");
                            tmp.setChecked(false);
                        }
                    }

                    notifyDataSetChanged();
                }
            });

            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AddressData p = arrayList.get(getAdapterPosition());
                    p.setChecked(true);
                    for (int i = 0; i<arrayList.size(); i++){
                        AddressData tmp = arrayList.get(i);
                        if ( !(tmp.getId()).equals(p.getId()) ){
                            Log.e("AddressAdapterScartyy",  tmp.getId()+"= "+ p.getId()+" size2");
                            tmp.setChecked(false);
                        }
                    }

                    notifyDataSetChanged();
                }
            });

            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AddressData p = arrayList.get(getAdapterPosition());
                    p.setChecked(true);
                    for (int i = 0; i<arrayList.size(); i++){
                        AddressData tmp = arrayList.get(i);
                        if ( !(tmp.getId()).equals(p.getId()) ){
                            Log.e("AddressAdapterScartyy",  tmp.getId()+"= "+ p.getId()+" size2");
                            tmp.setChecked(false);
                        }
                    }

                    notifyDataSetChanged();
                }
            });

        }
    }
}