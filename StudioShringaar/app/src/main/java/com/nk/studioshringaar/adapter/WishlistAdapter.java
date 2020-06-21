package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Product> arrayList;
    private String UID;
    private FirebaseFirestore db;

    private static final String TAG = "WishlistAdapter";

    public WishlistAdapter(Context context, ArrayList<Product> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public WishlistAdapter(Context context, ArrayList<Product> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
    }

    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wislist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishlistAdapter.ViewHolder holder, int position) {
        final Product p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImg())
            .into(holder.img);

        holder.title.setText(p.getName());
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        long amttemp = Long.parseLong(p.getPrice());
        holder.price.setText("\u20B9"+ formatter.format(amttemp) );
//        holder.price.setText("\u20B9"+p.getPrice());
        holder.cat.setText(p.getCategory());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("id", p.getId());
                i.putExtra("cat", p.getCategory());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("id", p.getId());
                i.putExtra("cat", p.getCategory());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document("user/"+ UID +"/wishlist/"+ p.getId())
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
        TextView title, price, cat;
        ImageView img, d;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            d = itemView.findViewById(R.id.deleteItem);
            title = itemView.findViewById(R.id.pname);
            price = itemView.findViewById(R.id.price);
            cat = itemView.findViewById(R.id.cat);
        }
    }
}