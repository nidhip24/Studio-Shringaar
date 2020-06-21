package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.cart.OrderItem;
import com.nk.studioshringaar.ui.order.OrderDetails;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderItem> arrayList;

    private static final String TAG = "OrderAdapter";

    public OrderItemAdapter(Context context, ArrayList<OrderItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemAdapter.ViewHolder holder, int position) {
        final OrderItem p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImg())
            .into(holder.img);
//
        holder.title.setText(p.getName());
        holder.cat.setText(p.getCat());
        holder.size.setText("Size -" + p.getSize());

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        long amttemp = Long.parseLong(p.getPrice());
        holder.price.setText("\u20B9"+ formatter.format(amttemp) + " x " + p.getQty() + " qty");
//        holder.delivery.setText(p.getName());
//        holder.price.setText("\u20B9"+p.getPrice());
//
//        holder.title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, OrderDetails.class);
//                i.putExtra("id", p.getId());
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
//            }
//        });
//
//        holder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, OrderDetails.class);
//                i.putExtra("id", p.getId());
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
//            }
//        });

//        holder.title.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, cat, size, price;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.pname);
            cat = itemView.findViewById(R.id.cat);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
        }
    }
}