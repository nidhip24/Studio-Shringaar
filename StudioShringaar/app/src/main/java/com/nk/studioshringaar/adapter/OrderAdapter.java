package com.nk.studioshringaar.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.order.OrderDetails;
import com.nk.studioshringaar.ui.order.Review;
import com.nk.studioshringaar.ui.product.ProductDetail;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PaymentData> arrayList;
    private String UID;
    private FirebaseFirestore db;

    private static final String TAG = "OrderAdapter";

    public OrderAdapter(Context context, ArrayList<PaymentData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public OrderAdapter(Context context, ArrayList<PaymentData> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderAdapter.ViewHolder holder, int position) {
        final PaymentData p = arrayList.get(position);
        Glide.with(context)
            .load(p.getImg())
            .into(holder.img);

        holder.delivery.setText("Will be delivered within 7-8 working days");
        if (p.getStatus().equalsIgnoreCase("success")){
            if (p.getOrder_status().equalsIgnoreCase("delivered")){
                holder.green_circle.setVisibility(View.VISIBLE);
                holder.blue_circle.setVisibility(View.GONE);

                holder.delivery.setText("Delivered");
                holder.mRatingBar.setVisibility(View.VISIBLE);
                holder.writeReview.setVisibility(View.VISIBLE);
//                holder.mRatingBar.setRating(5);

            }else{
                holder.blue_circle.setVisibility(View.VISIBLE);
            }
        }else{
            holder.delivery.setText("Payment Failed");
            holder.red_circle.setVisibility(View.VISIBLE);
        }

        holder.title.setText(p.getTitle());
        try {
            String time = p.getTimestamp();
            int st = time.indexOf("seconds") + "seconds=".length();
            int end = time.indexOf(",");
            long sec = Long.parseLong(time.substring(st,end));
            st = time.indexOf("nanoseconds") + "nanoseconds=".length()+1;
            end = time.indexOf(")");
            int nano = Integer.parseInt(time.substring(st,end));
            Log.e("TTTTTTTTTTIMESMATP", sec +" seecc");
            Timestamp t = new Timestamp(sec,nano);
            Date date = t.toDate();
            DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            holder.orderDate.setText(df.format(date));
        }catch (Exception e){
            e.printStackTrace();
        }
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        long amttemp = Long.parseLong(p.getAmount());
        holder.orderAmount.setText("\u20B9"+ formatter.format((amttemp/100)));
//        holder.delivery.setText(p.getName());
//        holder.price.setText("\u20B9"+p.getPrice());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, OrderDetails.class);
                i.putExtra("id", p.getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Review.class);
                i.putExtra("id", p.getId());
                i.putExtra("img", p.getImg());
                i.putExtra("name", p.getTitle());
                i.putExtra("rating", holder.mRatingBar.getRating());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

//        holder.title.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, delivery, orderDate, orderAmount;
        ImageView img;
        CardView card_view;
        Button writeReview;

        RatingBar mRatingBar;

        TextView blue_circle, red_circle, green_circle;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.pname);
            delivery = itemView.findViewById(R.id.deliveryDate);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            card_view = itemView.findViewById(R.id.card_view);

            green_circle = itemView.findViewById(R.id.green_circle);
            red_circle = itemView.findViewById(R.id.red_circle);
            blue_circle = itemView.findViewById(R.id.blue_circle);

            mRatingBar = itemView.findViewById(R.id.rating);
            writeReview = itemView.findViewById(R.id.writeReview);
        }
    }
}