package com.nk.studioshringaar.ui.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.Home;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.OrderAdapter;
import com.nk.studioshringaar.adapter.PaymentData;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.adapter.WishlistAdapter;

import java.util.ArrayList;

import static android.widget.GridLayout.HORIZONTAL;

public class Order extends AppCompatActivity {

    RecyclerView rOrder;

    OrderAdapter adapter;
    ArrayList<PaymentData> order_list;

    private FirebaseAuth mAuth;

    Boolean isLoggedin = false;
    String UID = "";

    int flag = -1;

    private static final String TAG = "Order";
    private FirebaseFirestore db = null;

    LinearLayout order_layout,noitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        order_layout = findViewById(R.id.order_layout);
        noitem = findViewById(R.id.noItem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        rOrder = findViewById(R.id.orderRecyclerView);

        //setting layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rOrder.setLayoutManager(layoutManager);

        //setting adapter to the list
        order_list = new ArrayList<PaymentData>();

        //setting up firebase
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        loginCheck();
    }

    public void shopnow(View v){
        startActivity(new Intent(this, Home.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void loginCheck(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            UID = user.getUid();
            isLoggedin = true;
            adapter = new OrderAdapter(this, order_list);
            setupOrder();
        }else{
            isLoggedin = false;
            showNoWishlist();
        }
    }

    void showNoWishlist(){
        order_layout.setVisibility(View.GONE);
        noitem.setVisibility(View.VISIBLE);
    }

    void setupOrder(){
        if (isLoggedin && !UID.equals("")) {
            rOrder.setAdapter(adapter);
            db.collection("user/"+ UID +"/order").whereEqualTo("status", "success")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        if (task.getResult().size() == 0){
                            showNoWishlist();
                            return;
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                order_list.add(new PaymentData(document.getId(), document.getString("orderID"),
                                        document.getString("title"), document.get("amount")+"",
                                        document.getString("status"), document.getString("order_status"),
                                        document.get("timestamp")+"", document.getString("img") ));

                                adapter.notifyDataSetChanged();
//                                Toast.makeText(getApplication(), selectedSize + "  == " + document.getString("docID"), Toast.LENGTH_SHORT).show();

                            } else {
                                //                            Toast.makeText(getApplicationContext(),"no doc",Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"err",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(),"Not loggeg in",Toast.LENGTH_SHORT).show();
        }
    }

}
