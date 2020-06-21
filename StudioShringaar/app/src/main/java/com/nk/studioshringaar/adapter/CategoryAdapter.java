package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.category.Category;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryData> arrayList;
    private String UID;
    private FirebaseFirestore db;

    private static final String TAG = "WishlistAdapter";

    public CategoryAdapter(Context context, ArrayList<CategoryData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public CategoryAdapter(Context context, ArrayList<CategoryData> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        final CategoryData p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImage())
            .into(holder.img);

        holder.title.setText(p.getName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Category.class);
                i.putExtra("name", p.getName());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.title);
        }
    }
}