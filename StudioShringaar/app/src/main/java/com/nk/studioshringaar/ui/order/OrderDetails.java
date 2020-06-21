package com.nk.studioshringaar.ui.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.OrderAdapter;
import com.nk.studioshringaar.adapter.OrderItemAdapter;
import com.nk.studioshringaar.adapter.OrderItemData;
import com.nk.studioshringaar.adapter.PaymentData;
import com.nk.studioshringaar.ui.cart.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetails extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Boolean isLoggedin = false;
    private String UID = "";
    private static final String TAG = "OrderDetails";
    private FirebaseFirestore db = null;

    private String orderid = "";

    Chip pendingChip, processingChip, deliveredChip, cancelChip;

    OrderItemAdapter adapter;
    ArrayList<OrderItem> order_item;

    int gTotal = 0;

    RecyclerView item;

    TextView orderID, customer_name, customer_addr, customer_phone;
    LinearLayout listPrice_layout, sellingPrice_layout, discountPrice_layout, shippingPrice_layout, shippingDiscountPrice_layout;
    TextView shipping_fee_discount, shipping_fee, list_price, selling_price, extra_discount_price, totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent i = getIntent();
        try{
            orderid = i.getStringExtra("id");
        } catch (Exception e) {
            Log.e(TAG, "NOT ID GOT FROM INTENT");
            return;
        }

        shipping_fee_discount = findViewById(R.id.shipping_fee_discount);
        shipping_fee = findViewById(R.id.shipping_fee);
        list_price = findViewById(R.id.list_price);
        selling_price = findViewById(R.id.selling_price);
        extra_discount_price = findViewById(R.id.extra_discount_price);
        totalAmount = findViewById(R.id.totalAmount);

        orderID = findViewById(R.id.orderID);
        customer_name = findViewById(R.id.customer_name);
        customer_addr = findViewById(R.id.customer_addr);
        customer_phone = findViewById(R.id.customer_phone);

        listPrice_layout = findViewById(R.id.listPrice_layout);
        sellingPrice_layout = findViewById(R.id.sellingPrice_layout);
        discountPrice_layout = findViewById(R.id.discountPrice_layout);
        shippingPrice_layout = findViewById(R.id.shippingPrice_layout);
        shippingDiscountPrice_layout = findViewById(R.id.shippingDiscountPrice_layout);

        discountPrice_layout.setVisibility(View.GONE);
        shippingPrice_layout.setVisibility(View.GONE);
        shippingDiscountPrice_layout.setVisibility(View.GONE);

        pendingChip = findViewById(R.id.pendingChip);
        processingChip = findViewById(R.id.processingChip);
        deliveredChip = findViewById(R.id.deliveredChip);
        cancelChip = findViewById(R.id.canceledChip);

        pendingChip.setVisibility(View.GONE);
        processingChip.setVisibility(View.GONE);
        deliveredChip.setVisibility(View.GONE);
        cancelChip.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        //setting up firebase
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        item = findViewById(R.id.orderItem);

        //setting layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        item.setLayoutManager(layoutManager);

        //setting adapter to the list
        order_item = new ArrayList<OrderItem>();
        adapter = new OrderItemAdapter(this, order_item);
        item.setAdapter(adapter);

//        order_item.add(new OrderItem("id","9", "23","kurta","asdadad","993","https://firebasestorage.googleapis.com/v0/b/studioshringaar-nk.appspot.com/o/productImages%2F1Lsv0hRDVtyvb1gdYF8c_1.jpg?alt=media&token=321f9769-ee6e-4470-83c6-2a5839dc0379"));
//        order_item.add(new OrderItem("pid","12", "123","category","no name","1223","https://firebasestorage.googleapis.com/v0/b/studioshringaar-nk.appspot.com/o/productImages%2F1Lsv0hRDVtyvb1gdYF8c_1.jpg?alt=media&token=321f9769-ee6e-4470-83c6-2a5839dc0379"));
        adapter.notifyDataSetChanged();

        loginCheck();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginCheck(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UID = user.getUid();
            isLoggedin = true;
            setup();
        }else{
            isLoggedin = false;
        }
    }

    private void setup() {
        if (isLoggedin){
            db.document("user/"+ UID +"/order/"+orderid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.e(TAG, document.get("cat") + "");
                            try {
                                ArrayList<HashMap> group = (ArrayList<HashMap>) document.get("product_list");
                                for (int i=0; i<group.size(); i++){
                                    Map<String, String> temp = group.get(i);
                                    try {
                                        order_item.add(new OrderItem(temp));
                                        adapter.notifyDataSetChanged();
                                    }catch (Exception e){
                                        Log.e(TAG, "OOPS");
                                    }
                                }
                                String order_stat = document.getString("order_status");
                                String stat = document.getString("status");

                                String promocodeName = document.getString("promocodeName");
                                String discountName = document.getString("discountName");

                                if (!discountName.equals("Nil") || !promocodeName.equals("Nil")){
                                    discountPrice_layout.setVisibility(View.VISIBLE);
                                }

                                String mShippingFee = "0";
                                String mShippingFeeDiscount = "0";
                                String mGTotal = "0";
                                String mAllPrice = "0";
                                String mTotalDiscount = "0";
                                try {
                                    mShippingFee =  document.get("shippingFee") + "";
                                    mShippingFeeDiscount = document.get("shippingFeeDiscount") + "";
                                    mGTotal = document.get("GTotal") + "";
                                    mAllPrice =  document.get("allPrice") + "";
                                    mTotalDiscount = document.get("totalDiscount") + "";

                                    shippingPrice_layout.setVisibility(View.VISIBLE);
                                    shippingDiscountPrice_layout.setVisibility(View.VISIBLE);
                                }catch (Exception e){
                                    Log.e(TAG, "ERROR getting shipping price");
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplication(),"Payment status : " + stat, Toast.LENGTH_SHORT).show();

                                if (order_stat.equalsIgnoreCase("pending")){
                                    pendingChip.setVisibility(View.VISIBLE);
                                }else if(order_stat.equalsIgnoreCase("delivered")){
                                    deliveredChip.setVisibility(View.VISIBLE);
                                }else if(order_stat.equalsIgnoreCase("in-transit")){
                                    processingChip.setVisibility(View.VISIBLE);
                                    processingChip.setText("In-transit");
                                }else if(order_stat.equalsIgnoreCase("processed")){
                                    processingChip.setVisibility(View.VISIBLE);
                                }else if(order_stat.equalsIgnoreCase("cancelled")){
                                    cancelChip.setVisibility(View.VISIBLE);
                                }
                                orderID.setText(document.getString("orderID"));
//                                orderID.setText("Order ID - " + document.getString("orderID"));
                                setupTotal(mShippingFee, mShippingFeeDiscount, mGTotal, mAllPrice, mTotalDiscount);
                                setupAddress(document.getString("addressID"));
                                Log.e(TAG, "p list" + group);
                            }catch (Exception e){
                                Log.e(TAG, "OLD ORDER DATa");
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplication(),"onHome nodoc ",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"User is not logged in",Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTotal(String mShippingFee, String mShippingFeeDiscount, String mGTotal, String mAllPrice, String mTotalDiscount){
        for (int i=0;i<order_item.size(); i++){
            OrderItem o = order_item.get(i);
            try {
                int price = Integer.parseInt(o.getPrice());
                int qty = Integer.parseInt(o.getQty());
                gTotal += price*qty;
            }catch (Exception e){
                Log.e(TAG, "price crash! may-day may-day");
            }
        }
        selling_price.setText(mGTotal+"");
        list_price.setText(mAllPrice+"");
        totalAmount.setText(gTotal+"");

        shipping_fee.setText("+"+ mShippingFee);
        shipping_fee_discount.setText("-"+ mShippingFeeDiscount);
        if (Long.parseLong(mTotalDiscount) != 0){
            extra_discount_price.setText("-" + mTotalDiscount);
        }
    }

    private void setupAddress(String id){
        db.document("user/"+ UID +"/address/"+id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        customer_name.setText(document.getString("name"));
                        customer_addr.setText(document.getString("address"));
                        customer_phone.setText("Phone number - " + document.getString("phone"));
                    } else {
                        Toast.makeText(getApplication(),"onHome nodoc ",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
