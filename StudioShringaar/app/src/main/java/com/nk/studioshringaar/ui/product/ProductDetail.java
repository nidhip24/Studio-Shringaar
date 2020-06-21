package com.nk.studioshringaar.ui.product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.SliderAdapterExample;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.nk.studioshringaar.ui.cart.Cart;
import com.nk.studioshringaar.ui.login.Login;
import com.nk.studioshringaar.ui.size.SizeChart;
import com.nk.studioshringaar.ui.wishlist.WishList;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.himanshusoni.quantityview.QuantityView;

public class ProductDetail extends AppCompatActivity {

    TextView pd, productName, productCat, productPrice, productOriginalPrice, productDiscount;
    SliderView sliderView;
    List<String> imgList;

    TextView sizeChart;

    String categoryFromIntent,ID;

    private static final String TAG = "ProductDetails";
    private FirebaseFirestore db = null;

    //size buttons
    ArrayList<MaterialButton> buttons = new ArrayList<MaterialButton>();
    ArrayList<String> buttonsText = new ArrayList<String>();
    LinearLayout layout;

    FloatingActionButton w;
    private FirebaseAuth mAuth;
    long discount = -1;
    long maxDiscount = 0;

    Boolean isLoggedin = false;
    String UID = "";

    String selectedSize = "";

    MaterialButton buyNow, addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        sizeChart = findViewById(R.id.sizeChart);

        w = findViewById(R.id.addToWishlist);
        pd = findViewById(R.id.product_details);
        productName = findViewById(R.id.product_name);
        productCat = findViewById(R.id.product_cat);
        productPrice = findViewById(R.id.product_price);
        productOriginalPrice = findViewById(R.id.product_original_price);
        productDiscount = findViewById(R.id.product_discount);
        buyNow = findViewById(R.id.buynow);
        addToCart = findViewById(R.id.addToCart);

        Intent i = getIntent();
        ID = i.getStringExtra("id");
        categoryFromIntent = i.getStringExtra("cat");
//        Toast.makeText(getApplicationContext(),"ID "+ i.getStringExtra("id"), Toast.LENGTH_SHORT).show();

        //getting discount
        DiscountData dd = new DiscountData(getApplicationContext());
        ArrayList<Discount> temp = dd.getDiscount();
        for (int ii=0; ii < temp.size(); ii++) {
            Discount d = temp.get(ii);
//            Toast.makeText(getApplicationContext(),d.getCat()+" af - cate " + categoryFromIntent,Toast.LENGTH_SHORT).show();
            if (categoryFromIntent.equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
                if (discount < d.getDiscount()) {
                    discount = d.getDiscount();
//                    Toast.makeText(getApplicationContext(),"This product is available at discount", Toast.LENGTH_SHORT).show();
                    maxDiscount = d.getMaxDiscount();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        sliderView = findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
//        sliderView.startAutoCycle();
        imgList = new ArrayList<String>();

        layout = findViewById(R.id.sizeButtons);

        w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"fab",Toast.LENGTH_SHORT).show();
                if (isLoggedin) {
                    addToWishlist(1);
                } else {
                    Snackbar.make(view, "Login to add to cart", Snackbar.LENGTH_LONG)
                        .setAction("login", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            }
                        }).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //check if user is logged in
        loginCheck();

        //addtocart onclick listener
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedin){
                    addtocart(1);
                }else{
//                    Toast.makeText(getApplicationContext(),"You\'re not loggeg in",Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Login to add to cart", Snackbar.LENGTH_LONG)
                        .setAction("login", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            }
                        }).show();
                }
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedin){
                    buynow();
                }else{
//                    Toast.makeText(getApplicationContext(),"You\'re not loggeg in",Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "Login to add to cart", Snackbar.LENGTH_LONG)
                            .setAction("login", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                }
                            }).show();
                }
            }
        });

        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SizeChart.class);
                startActivity(i);
            }
        });

        //setup UI component
        setupUI();
        addToWishlist(0);
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
            case R.id.action_wishlist:
//                Toast.makeText(getApplicationContext(),"wish",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), WishList.class);
                startActivity(i);
                break;
            case R.id.action_cart:
//                Toast.makeText(getApplicationContext(),"cart",Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getApplicationContext(), Cart.class);
                startActivity(ii);
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
        }else{
            isLoggedin = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        addToWishlist(0);
    }

    void addToWishlist(final int code){
        //checking if product exist in the wishlist
        if (isLoggedin && !UID.equals("")){
            //user is logged in and we have UID
            db.document("user/"+ UID +"/wishlist/"+ ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if (code == 1){
                                deleteItem();
                            }else{
                                wishlisted();
                            }
                        } else {
//                            Toast.makeText(getApplicationContext(),"no doc",Toast.LENGTH_SHORT).show();
                            if (code == 1) {
                                createWishlist();
                            }
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"err",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"User is not logged in",Toast.LENGTH_SHORT).show();
//            Snackbar.make(new View(getApplicationContext()), "Login to add to cart", Snackbar.LENGTH_LONG)
//                    .setAction("login?", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startActivity(new Intent(getApplicationContext(), Login.class));
//                        }
//                    });
        }
    }

    void wishlisted(){
        w.setImageResource(R.drawable.ic_wishlist_i);
    }

    void deleteItem(){
        db.document("user/"+ UID +"/wishlist/"+ ID)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    w.setImageResource(R.drawable.ic_wishlist_no);
                    Toast.makeText(getApplicationContext(),"Removed from the Wishlist",Toast.LENGTH_SHORT).show();
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

    void createWishlist(){
        Map<String, Object> p = new HashMap<>();
        p.put("cat", categoryFromIntent.toLowerCase());

        db.document("user/"+ UID +"/wishlist/"+ ID)
                .set(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Added to wishlist",Toast.LENGTH_SHORT).show();
                        w.setImageResource(R.drawable.ic_wishlist_i);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    void setupUI(){
        db.document("product/category/"+ categoryFromIntent.toLowerCase() + "/"+ID)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
//                                    Toast.makeText(getApplicationContext(),"success", Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"result :" + task.getResult());
                        try{
                            productName.setText(document.getString("pname"));
                            productCat.setText(categoryFromIntent);
                            if (discount!=-1){
                                productDiscount.setVisibility(View.VISIBLE);
                                productOriginalPrice.setVisibility(View.VISIBLE);
                                int p = Integer.parseInt(document.getString("price"));

                                int d = (p*(int)discount)/100;
                                if (d>maxDiscount){
                                    d= (int) maxDiscount;
                                }
//                                Toast.makeText(getApplicationContext(),"discount " + d,Toast.LENGTH_SHORT).show();
                                productPrice.setText("\u20B9"+ (p-d));
                                productOriginalPrice.setText("\u20B9"+document.getString("price"));
                                productDiscount.setText("("+discount+"% OFF)");
                            }else{
                                productPrice.setText("\u20B9"+document.getString("price"));
                                productDiscount.setVisibility(View.GONE);
                                productOriginalPrice.setVisibility(View.GONE);
                            }

                            imgList.add(document.getString("image"));
                            if (document.getString("imagetwo") != null){
                                imgList.add(document.getString("imagetwo"));
                            }
                            if (document.getString("imagethree") != null){
                                imgList.add(document.getString("imagethree"));
                            }
                            if (document.getString("imagefour") != null){
                                imgList.add(document.getString("imagefour"));
                            }
                            try{
                                Log.e(TAG,document.get("size") + " m");
                                List<String> temp = (List<String>) document.get("size");
                                for (int i=0; i<temp.size(); i++){
                                    addButton(temp.get(i));
                                }
                            }catch (Exception e) {
                                Log.e(TAG, "Cannot retrieve size");
                            }
                            pd.setText(document.getString("description"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                pd.setText(Html.fromHtml("<p style='font-size:14sp;font-weight: 200;white-space: pre-wrap;'>"+ document.getString("description") +"</p>", Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                pd.setText(Html.fromHtml("<p style='font-size:14sp;font-weight: 200;white-space: pre-wrap;'>"+ document.getString("description") +"</p>"));
                            }
                            sliderView.setSliderAdapter(new SliderAdapterExample(getApplication(),imgList));
//                                        Toast.makeText(getApplicationContext(), "img2 "+ document.getString("imagetwo"), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"err "+ task.getException(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        //getting size
//        db.collection("product/category/"+ categoryFromIntent.toLowerCase() + "/"+ID+"/size").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "listen:error", e);
//                    return;
//                }
//                for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                    DocumentSnapshot document;
//                    switch (dc.getType()) {
//                        case ADDED:
//                            Log.d(TAG, "NEW SIZE " + dc.getDocument().getData());
//                            document =  dc.getDocument();
//                            if (document.getString("size") != null){
//                                addButton(document.getString("size"));
//                            }
////                            document.getString("pname");
//                            break;
//                        case MODIFIED:
//                            Log.d(TAG, "NEW SIZE" + dc.getDocument().getData());
//                            document =  dc.getDocument();
//                            if (document.getString("size") != null){
//                                addButton(document.getString("size"));
//                            };
//                            break;
//                        case REMOVED:
//                            Log.d(TAG, "Removed city: " + dc.getDocument().getData());
////                            document = dc.getDocument();
//                            break;
//                    }
//                }
//            }
//        });
    }

    void addButton(final String payload){
        final MaterialButton  btnTag = new MaterialButton(this);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(180, 150);
        param.setMargins(8,0,8,0);
        btnTag.setLayoutParams(param);
        btnTag.setText(payload);
        btnTag.setTextColor(Color.WHITE);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),payload,Toast.LENGTH_SHORT).show();
                toggleSizeButton(payload);
                selectedSize = payload;
            }
        });
        layout.addView(btnTag);
        buttons.add(btnTag);
        buttonsText.add(payload);
    }

    void toggleSizeButton(String p){
        for(int i = 0; i < buttonsText.size(); i++){
            if (buttonsText.get(i).equals(p)){
                try {
                    MaterialButton tmp = buttons.get(i);
//                    Toast.makeText(getApplicationContext(), p + " okay", Toast.LENGTH_SHORT).show();
                    tmp.setBackgroundColor(Color.WHITE);
                    tmp.setTextColor(getResources().getColor(R.color.colorPrimary));
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), p + " ee", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }else{
                try{
                    MaterialButton tmp = buttons.get(i);
//                    Toast.makeText(getApplicationContext(), p + " okay3", Toast.LENGTH_SHORT).show();
                    tmp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tmp.setTextColor(Color.WHITE);
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), p + " ee3", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    void buynow(){
        addtocart(0);
    }

    void addtocart(final int code){
        if (isLoggedin && !UID.equals("")){
            //user is logged in and we have UID
            db.collection("user/"+ UID +"/cart").whereEqualTo("docID", ID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0){
                            if (code == 1) {
                                createCart(1);
                            } else {
                                createCart(0);
                            }
                        }else {
                            int flag = -1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                                    Toast.makeText(getApplication(), selectedSize + "  == " + document.getString("docID"), Toast.LENGTH_SHORT).show();
                                    if( selectedSize.trim().equals(document.getString("size")) ) {
                                        flag = 1;
                                        if (code == 1) {
                                            increamentQty(document.getId(), document.getString("qty"), 1);
                                        } else {
                                            increamentQty(document.getId(), document.getString("qty"), 0);
                                        }
                                        break;
                                    }
                                } else {
                                    flag = 1;
                                    Toast.makeText(getApplicationContext(),"no doc",Toast.LENGTH_SHORT).show();
                                    if (code == 1) {
                                        createCart(1);
                                    } else {
                                        createCart(0);
                                    }
                                    Log.d(TAG, "No such document");
                                }
                            }
                            if (flag == -1){
                                if (code == 1) {
                                    createCart(1);
                                } else {
                                    createCart(0);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"err",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"User is not logged in",Toast.LENGTH_SHORT).show();
        }
    }

    void createCart(final int code){
        if (!selectedSize.equals("") && selectedSize != null) {
//            Toast.makeText(getApplicationContext(), "adding to cart", Toast.LENGTH_SHORT).show();
            Map<String, Object> p = new HashMap<>();
            p.put("cat", categoryFromIntent.toLowerCase());
            p.put("qty", "1");
            p.put("size", selectedSize);
            p.put("docID", ID);

            db.collection("user/" + UID + "/cart")
                    .add(p)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            if (code == 0){
                                startActivity(new Intent(getApplicationContext(), Cart.class));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select size", Toast.LENGTH_SHORT).show();
        }
    }

    void increamentQty(String docID, String q, int code){
        if (QuantityView.isValidNumber(q) ){
            int tmp = Integer.parseInt(q);
            tmp++;
            Map<String, Object> data = new HashMap<>();
            data.put("qty", tmp+"");

            db.document("user/" + UID + "/cart/" + docID)
                    .set(data, SetOptions.merge());
            Toast.makeText(getApplicationContext(), "Product quantity incremented", Toast.LENGTH_SHORT).show();

            if (code == 0){
                startActivity(new Intent(this, Cart.class));
            }
        }
    }

}
