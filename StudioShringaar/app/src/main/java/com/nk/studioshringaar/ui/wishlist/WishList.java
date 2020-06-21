
package com.nk.studioshringaar.ui.wishlist;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.Home;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.adapter.WishlistAdapter;
import com.nk.studioshringaar.ui.cart.Cart;
import com.nk.studioshringaar.ui.product.ProductDetail;
import com.nk.studioshringaar.ui.search.Search;
import com.nk.studioshringaar.ui.search.SearchResult;

import java.util.ArrayList;

import static android.widget.GridLayout.HORIZONTAL;
import static com.nk.studioshringaar.Home.REQUEST_CODE_SEARCH;

public class WishList extends AppCompatActivity {

    RecyclerView rWishlist;

    WishlistAdapter adapter;
    ArrayList<Product> product_list;

    private FirebaseAuth mAuth;

    Boolean isLoggedin = false;
    String UID = "";

    int flag = -1;

    private static final String TAG = "Wishlist";
    private FirebaseFirestore db = null;

    LinearLayout wishlist,nowishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        wishlist = findViewById(R.id.wishlist);
        nowishlist = findViewById(R.id.noItem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        rWishlist = findViewById(R.id.wishlistRecyclerView);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), HORIZONTAL);
        rWishlist.addItemDecoration(itemDecor);

        //setting layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rWishlist.setLayoutManager(layoutManager);

        //setting adapter to the list
        product_list = new ArrayList<Product>();
//        adapter = new WishlistAdapter(getApplicationContext(), product_list);
//        product_list.add(new Product("1", "Dress", "500", "m", "dress"));



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent ii = new Intent(getApplicationContext(), Cart.class);
                startActivity(ii);
                break;
            case R.id.action_search:
                Intent d = new Intent(getApplicationContext(), Search.class);
                startActivityForResult(d, REQUEST_CODE_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void loginCheck(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            UID = user.getUid();
            isLoggedin = true;
            adapter = new WishlistAdapter(this, product_list, UID, db);
            setupWishlist();
        }else{
            isLoggedin = false;
            showNoWishlist();
        }
    }

    void showNoWishlist(){
        wishlist.setVisibility(View.GONE);
        nowishlist.setVisibility(View.VISIBLE);
    }

    void setupWishlist(){
        if (isLoggedin && !UID.equals("")){
            rWishlist.setAdapter(adapter);
            db.collection("user/"+ UID +"/wishlist").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    if (snapshots.getDocumentChanges().size() == 0){
                        showNoWishlist();
                    }
//                    Toast.makeText(getApplicationContext(),snapshots.getDocumentChanges().size()+"size",Toast.LENGTH_SHORT).show();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        DocumentSnapshot document;
                        switch (dc.getType()) {
                            case ADDED:
                                flag++;
                                Log.d(TAG, "New WishList: " + dc.getDocument().getData());
                                document =  dc.getDocument();
                                if (document.getString("cat") != null){
                                    getDocument(document.getId(),document.getString("cat"));
                                }
//                                product_list.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), "category",document.getString("description")) );
//                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified WishList: " + dc.getDocument().getData());
                                document = dc.getDocument();
                                flag++;
                                for(int i = 0; i < product_list.size(); i++){
                                    Product p = product_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
                                        product_list.remove(i);
                                        Log.d(TAG, "Deleted found document " + p.getName());
                                        break;
                                    }
                                }
                                if (document.getString("cat") != null){
                                    getDocument(document.getId(),document.getString("cat"));
                                }
//                                product_list.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), "Category",document.getString("description")) );
//                                adapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                document = dc.getDocument();
                                flag--;
                                for(int i = 0; i < product_list.size(); i++){
                                    Product p = product_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
                                        product_list.remove(i);
                                        Log.d(TAG, "Deleted found document " + p.getName());
                                        break;
                                    }
                                }
                                Toast.makeText(getApplicationContext(),product_list.size()+" size",Toast.LENGTH_SHORT).show();
                                if (product_list.size()<=0){
                                    showNoWishlist();
                                }
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"Not loggeg in",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        product_list.clear();
//        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"onresume",Toast.LENGTH_SHORT).show();
        setupWishlist();
    }

    void getDocument(String ID, final String cat){
        DocumentReference docRef = db.document("product/category/"+ cat.toLowerCase() + "/"+ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        product_list.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), cat, document.getString("description")) );
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document " +cat);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH) {//you just got back from activity B - deal with resultCode
            //use data.getExtra(...) to retrieve the returned data
            try {
                Boolean err = data.getBooleanExtra("error", false);
                if (!err) {
                    try {
                        String id = data.getStringExtra("id");
                        String cat = data.getStringExtra("cat");
                        Toast.makeText(getApplicationContext(), "id" + id, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), ProductDetail.class);
                        i.putExtra("id", id);
                        i.putExtra("cat", cat);
                        startActivity(i);
                    } catch (Exception e) {
                        Log.e(TAG, "cant " + e);
                    }
                } else if (data.getBooleanExtra("usercancelled", false)) {
                    Log.e(TAG, "User cancelled the action");
                } else {
                    String query = data.getStringExtra("query");
                    Intent i = new Intent(getApplicationContext(), SearchResult.class);
                    i.putExtra("q", query);
                    startActivity(i);
                }
            } catch (Exception e) {
                Log.e(TAG, "Probaly back pressd and didnt get the code :D");
            }
        }
    }

}