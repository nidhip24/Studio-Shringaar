package com.nk.studioshringaar.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomCommonAdapter extends RecyclerView.Adapter<CustomCommonAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Product> arrayList;
    ArrayList<Discount> temp;

    private String UID;
    private FirebaseFirestore db;
    int code = -1;

    private String TAG = "CustomCommonAdapter";

    public CustomCommonAdapter(Context context, ArrayList<Product> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        //getting discount
        DiscountData dd = new DiscountData(context);
        temp = dd.getDiscount();
    }

    public CustomCommonAdapter(Context context, ArrayList<Product> arrayList, int code) {
        this.context = context;
        this.arrayList = arrayList;
        this.code = code;
        //getting discount
        DiscountData dd = new DiscountData(context);
        temp = dd.getDiscount();
    }

    public CustomCommonAdapter(Context context, ArrayList<Product> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
        //getting discount
        DiscountData dd = new DiscountData(context);
        temp = dd.getDiscount();
    }

    @Override
    public CustomCommonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (code == 1){
            view = LayoutInflater.from(context).inflate(R.layout.horizontal_card2, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.horizontal_card, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomCommonAdapter.ViewHolder holder, int position) {
        final Product p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImg())
            .into(holder.img);

        if (db != null) {
            db.document("user/" + UID + "/wishlist/" + p.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            holder.icon.setChecked(true);
                        } else {
                            holder.icon.setChecked(false);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            if (code != 1)
                holder.icon.setVisibility(View.VISIBLE);
        }

        long discount = -1;
        long maxDiscount = 0;
        for (int ii=0; ii < temp.size(); ii++) {
            Discount d = temp.get(ii);
            Log.e("DISOCUNTFINDING", p.getCategory()+" before  accct   " + d.getCat());
            if (p.getCategory().equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
                Log.e("DISOCUNTFINDING", p.getCategory()+"  accct   " + d.getCat());
                if (discount < d.getDiscount()) {
                    discount = d.getDiscount();
                    maxDiscount = d.getMaxDiscount();
                }
            }
        }
        if (discount!=-1){
            int pr = Integer.parseInt(p.getPrice());
            int d = (pr*(int)discount)/100;
            if (d>maxDiscount){
                d= (int) maxDiscount;
            }
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            long amttemp = Long.parseLong(p.getPrice());
            holder.price.setText("\u20B9"+ formatter.format(amttemp-d) );
            holder.product_original_price.setText("\u20B9"+ p.getPrice());
            holder.product_original_price.setVisibility(View.VISIBLE);
        }else{
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            long amttemp = Long.parseLong(p.getPrice());
            holder.price.setText("\u20B9"+ formatter.format(amttemp) );
            holder.product_original_price.setVisibility(View.GONE);
        }
        holder.title.setText(p.getName());
//        holder.price.setText("\u20B9"+p.getPrice());
        holder.c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("id", p.getId());
                i.putExtra("cat", p.getCategory());
                context.startActivity(i);
            }
        });


        try {
            holder.icon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b && UID == null) {
                        holder.icon.setChecked(false);
                        Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (b) {
                        //wishlisted
                        Map<String, Object> pp = new HashMap<>();
                        pp.put("cat", p.getCategory().toLowerCase());
                        db.document("user/" + UID + "/wishlist/" + p.getId())
                                .set(pp)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    } else {
                        //remove wishlisted
                        db.document("user/" + UID + "/wishlist/" + p.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

//        holder.title.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,price, product_original_price;
        CardView c;
        ImageView img;
        CheckBox icon;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            product_original_price = itemView.findViewById(R.id.product_original_price);
            c = itemView.findViewById(R.id.card_view);
            icon = itemView.findViewById(R.id.icon);

        }
    }
}