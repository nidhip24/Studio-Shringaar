package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Product> arrayList;
    private String UID;
    private FirebaseFirestore db;
    ArrayList<Discount> temp;

    private static final String TAG = "CartAdapter";

    public CartAdapter(Context context, ArrayList<Product> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public CartAdapter(Context context, ArrayList<Product> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
        //getting discount
        DiscountData dd = new DiscountData(context);
        temp = dd.getDiscount();
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_two, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        final Product p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImg())
            .into(holder.img);

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
        if (discount!=-1 && p.getDiscount()){
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
//        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        long amttemp = Long.parseLong(p.getPrice());
//        holder.price.setText("\u20B9"+ formatter.format(amttemp) );
        holder.cat.setText(p.getCategory());
        if (QuantityView.isValidNumber(p.getQty())) {
            holder.quantity.setQuantity(Integer.parseInt(p.getQty()));
        }

        holder.size.setText("Size: "+ p.getSize());

        holder.quantity.setQuantityClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.quantity.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically) {
                p.setQty(newQuantity+"");
                try{
                    Map<String, Object> data = new HashMap<>();
                    data.put("qty", newQuantity+"");
                    db.document("user/" + UID + "/cart/" + p.getId())
                            .set(data, SetOptions.merge());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onLimitReached() {

            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("id", p.getoID());
                i.putExtra("cat", p.getCategory());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("id", p.getoID());
                i.putExtra("cat", p.getCategory());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document("user/"+ UID +"/cart/"+ p.getId())
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
        });

//        holder.title.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, cat, size, product_original_price;
        ImageView img;
        QuantityView quantity;
        Button remove;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            remove = itemView.findViewById(R.id.remove);
            quantity = itemView.findViewById(R.id.quantityView_default);
            title = itemView.findViewById(R.id.pname);
            price = itemView.findViewById(R.id.price);
            product_original_price = itemView.findViewById(R.id.product_original_price);
            size = itemView.findViewById(R.id.size);
            cat = itemView.findViewById(R.id.cat);
        }
    }
}