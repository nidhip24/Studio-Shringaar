package com.nk.studioshringaar.ui.cart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.nk.studioshringaar.Home;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.AddressAdapter;
import com.nk.studioshringaar.adapter.AddressData;
import com.nk.studioshringaar.adapter.CartAdapter;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.adapter.WishlistAdapter;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.nk.studioshringaar.data.Promocode;
import com.nk.studioshringaar.ui.product.ProductDetail;
import com.nk.studioshringaar.ui.search.Search;
import com.nk.studioshringaar.ui.search.SearchResult;
import com.nk.studioshringaar.ui.test.Test;
import com.nk.studioshringaar.ui.wishlist.WishList;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.tiper.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nk.studioshringaar.Home.REQUEST_CODE_SEARCH;

public class Cart extends AppCompatActivity  implements PaymentResultListener {

    LinearLayout cartItemLayout,noitem;

    private static final String TAG = "Cart";
    private FirebaseFirestore db = null;

    Boolean isLoggedin = false;
    String UID = "";

    private FirebaseAuth mAuth;

    RecyclerView rCart;

    CartAdapter adapter;
    ArrayList<Product> product_list;

    TextView item, all_price, g_total,f_price;
    float mAllprice, mGTotal;
    int mItem, mDeliveryFee, totalDiscount;

    int flag = -1;

    Button place_order;
    private FirebaseFunctions mFunctions;

    String docRefID = "";
    String prodList = "";

    //address data
    BottomSheetBehavior bottomSheetBehavior;

    LinearLayout no_add,addr_layout;
    String add_selected = "";
    RecyclerView rAddr;

    AddressAdapter addr_adapter;
    ArrayList<AddressData> addr_list;

    EditText aName, aAddress, aPhone, aCity, aState;
    Button addAddressButton;
    private MaterialSpinner country_spinner;
    private List<String> country_list;

    ArrayAdapter<String> country_adapter;

    private CountryCodePicker ccp;

    String addressID;

    ArrayList<Discount> discountList;
    ArrayList<Promocode> promoList;

    Boolean isDiscount = false;
    Boolean isPromocode = false;

    String discountName = "Nil";
    String promocodeName = "Nil";


    private LinearLayout shippingDiscountPrice_layout, shippingPrice_layout, discountPrice_layout;
    private TextView shipping_fee_discount, shipping_fee, extra_discount_price;

    private TextView promocode_applied_text;
    private EditText promocode_text;
    private Button promocode_apply;
    private String email;
    private String oid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mItem =  0;
        mAllprice = (float) 0.0;
        mDeliveryFee = 40;
        mGTotal = (float) 0.0;

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        // layout textview for price details
        extra_discount_price = findViewById(R.id.extra_discount_price);
        shipping_fee = findViewById(R.id.shipping_fee);
        shipping_fee_discount = findViewById(R.id.shipping_fee_discount);

        // the layout for price
        shippingDiscountPrice_layout = findViewById(R.id.shippingDiscountPrice_layout);
        shippingPrice_layout = findViewById(R.id.shippingPrice_layout);
        discountPrice_layout = findViewById(R.id.discountPrice_layout);

        discountPrice_layout.setVisibility(View.GONE);

        promocode_apply = findViewById(R.id.promocode_apply);
        promocode_text = findViewById(R.id.promocode_text);
        promocode_applied_text = findViewById(R.id.promocode_applied_text);

        place_order = findViewById(R.id.place_order);

        cartItemLayout = findViewById(R.id.cart);
        noitem = findViewById(R.id.noitem);

        item = findViewById(R.id.total_item);
        all_price = findViewById(R.id.all_price);
//        delivery_fee = findViewById(R.id.delivery_fee);
        g_total = findViewById(R.id.grand_total);
        f_price = findViewById(R.id.final_price);

        rCart = findViewById(R.id.cartRecyclerView);

        no_add = findViewById(R.id.no_addr);
        addr_layout = findViewById(R.id.address_layout);
        rAddr = findViewById(R.id.addressRecyclerView);

        aName = findViewById(R.id.name);
        aAddress = findViewById(R.id.address);
        aPhone = findViewById(R.id.mobile_no);
        addAddressButton = findViewById(R.id.add);

        aCity = findViewById(R.id.city);
        aState = findViewById(R.id.state);
        country_spinner = findViewById(R.id.country_spinner);

        country_list = new ArrayList<String>();
        country_list.add("india");
        country_list.add("india2");
        country_list.add("india3");

        country_adapter  = new ArrayAdapter<String>(this, R.layout.country_list_item, country_list);
        country_spinner.setAdapter(country_adapter);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                aPhone.setHint("");
            }
        });

        ccp.registerPhoneNumberTextView(aPhone);
        aPhone.setHint("");

        //setting layout
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
//        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        rCart.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rAddr.setLayoutManager(layoutManager2);

        //setting adapter to the list
        product_list = new ArrayList<Product>();
        addr_list = new ArrayList<AddressData>();

        //setting up firebase
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        promoList = new ArrayList<Promocode>();

        //getting discount
        DiscountData dd = new DiscountData(getApplicationContext());
        discountList = dd.getDiscount();

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedin){
                    String tempList = getProdList();
                    if (tempList.equals("")){
                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addressID = getSelectedAddress();
                    if (addressID.equals("")){
                        Toast.makeText(getApplicationContext(),"Please select address or add one",Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Toast.makeText(getApplicationContext(),tempList,Toast.LENGTH_SHORT).show();
                    mFunctions = FirebaseFunctions.getInstance();
                    createOrder((int) mGTotal*100, tempList);
                    place_order.setEnabled(false);
                }
            }
        });

        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // change the state of the bottom sheet
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set the peek height
        bottomSheetBehavior.setPeekHeight(340);

        // set hideable or not
        bottomSheetBehavior.setHideable(true);

        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mName = aName.getText().toString();
                String mAddress = aAddress.getText().toString();
                String mPhone = aPhone.getText().toString();
                String mCity = aCity.getText().toString();
                String mState = aState.getText().toString();
                String mCountry = "";
                try {
                    mCountry = (String) country_spinner.getSelectedItem();
                }catch (Exception e){
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(),"CCP " + ccp.getSelectedCountryCodeWithPlus(),Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),"CCP " + ccp.getFullNumberWithPlus(),Toast.LENGTH_SHORT).show();
                if ( mName.equals("") || mPhone.equals("") || mAddress.equals("") || mCity.equals("") || mState.equals("") || mCountry == null){

                    Toast.makeText(getApplicationContext(),"Some of the fields are empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                mPhone = ccp.getFullNumberWithPlus();
                reset();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


                addAddress(mName, mAddress, mPhone, mCity, mState, mCountry);
            }
        });

        promocode_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedin){
                    String promo = promocode_text.getText().toString();
                    if (!promo.equalsIgnoreCase("")){
                        checkpromocode(promo);
                    } else {
                        Toast.makeText(getApplicationContext(),"Please enter promocode",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please wait...",Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginCheck();
    }

    private void checkpromocode(final String promocodeName){

        Date cDate = new Date();
        final String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        final String[] tt = fDate.split("-");
        final int Y = Integer.parseInt(tt[0]);
        final int M = Integer.parseInt(tt[1]);
        final int D = Integer.parseInt(tt[2]);
        db.collection("promocode")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), task.getResult().size()+"",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "search size " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String[] st = document.getString("startDate").split("-");
                                int SY = Integer.parseInt(st[0]);
                                int SM = Integer.parseInt(st[1]);
                                int SD = Integer.parseInt(st[2]);
                                String[] et = document.getString("endDate").split("-");
                                int EY = Integer.parseInt(et[0]);
                                int EM = Integer.parseInt(et[1]);
                                int ED = Integer.parseInt(et[2]);
                                try {
                                    if ((D >= SD && M >= SM && Y >= SY) && (D <= ED && M <= EM && Y <= EY) && document.getString("name").equals(promocodeName)) {
                                        if (document.getString("name").equals(promocodeName)) {
                                            promoList.add(new Promocode(document.getId(), document.getString("name"), document.getString("cat"),
                                                    document.getString("startDate"), document.getString("endDate"),
                                                    (long) document.get("discount"), (long) document.get("maxDiscount")));
                                            isPromocode = true;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "GOT ERROR WHILE CHECKING NAME >>> ");
                                    e.printStackTrace();
                                }
                                Log.e(TAG, document.getId() + " :/=> " + document.getData());
                            }
                            setupPromocode(promocodeName);
                        } else {
                            Log.e(TAG, "fucked up ");
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void setupPromocode (String str) {
        if (isDiscount){
            int flag = -1;
            for (int i = 0; i<promoList.size(); i++){
                final Promocode p = promoList.get(i);
                if (p.getName().equals(str)){
                    flag = 1;
                    new MaterialAlertDialogBuilder(Cart.this, R.style.AlertDialogTheme)
                        .setTitle("Alert")
                        .setMessage("Do you want to remove discount and apply promocode?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"Promocode is not applied",Toast.LENGTH_SHORT).show();
                                setupPriceDetails();
                                isPromocode = false;
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isPromocode = true;
                                isDiscount = false;
                                promocodeName = p.getName();
                                setupPriceDetails();
                                Toast.makeText(getApplicationContext(),"Promocode is applied",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                    break;
                }
            }
            if (flag == -1) {
                Toast.makeText(getApplicationContext(),"Invalid promocode or expired",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Promocode is applied",Toast.LENGTH_SHORT).show();
            setupPriceDetails();
        }
    }

    String getSelectedAddress(){
        String tempID = "";
        for (int i=0; i<addr_list.size(); i++){
            AddressData aa = addr_list.get(i);
            if (aa.getChecked()){
                tempID = aa.getId();
                break;
            }
        }
        return tempID;
    }

    void reset(){
        aName.setText("");
        aAddress.setText("");
        aPhone.setText("");
        aCity.setText("");
        aState.setText("");
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        country_list = new ArrayList<String>();
        country_list.add("india");
        country_list.add("india2");
        country_list.add("india3");

        country_adapter  = new ArrayAdapter<String>(this, R.layout.country_list_item, country_list);
        country_spinner.setAdapter(country_adapter);

    }

    void addAddress(String n, String a, String p, String c, String s, String country){
        Map<String, Object> pp = new HashMap<>();
        pp.put("name", n);
        pp.put("address", a);
        pp.put("phone", p);
        pp.put("city", c);
        pp.put("state", s);
        pp.put("country", country);

        db.collection("user/" + UID + "/address")
                .add(pp)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Address added", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        View view = getCurrentFocus();
                        if (view != null) {
                            view.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    }

    public void shopnow(View v){
        startActivity(new Intent(this, Home.class));
    }

    public void add_addr(View v){
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if (isLoggedin && !UID.equals("")) {
//            startActivity(new Intent(this, Test.class));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT).show();
        }
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
                Intent ii = new Intent(getApplicationContext(), WishList.class);
                startActivity(ii);
                break;
            case R.id.action_search:
                Intent d = new Intent(getApplicationContext(), Search.class);
                startActivityForResult(d, REQUEST_CODE_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
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

    String getProdList(){
        String list = "";
        for(int i = 0; i < product_list.size(); i++) {
            Product p = product_list.get(i);
            if (i==0){
                list+=p.getId()+"_"+p.getQty();
            }else{
                list+="~"+p.getId()+"_"+p.getQty();
            }
        }
        return list;
    }

    void enablePlaceOrder(){
        place_order.setEnabled(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //clearing product list
        product_list.clear();
        adapter.notifyDataSetChanged();

        //clearing address list
        addr_list.clear();
        addr_adapter.notifyDataSetChanged();

//        Toast.makeText(getApplicationContext(),"onresume",Toast.LENGTH_SHORT).show();
        setupCart();
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
            email = user.getEmail();
            UID = user.getUid();
            isLoggedin = true;
            adapter = new CartAdapter(this, product_list, UID, db);
            addr_adapter = new AddressAdapter(this, addr_list, UID, db);
            rAddr.setAdapter(addr_adapter);
            setupCart();
        }else{
            isLoggedin = false;
            showNoItem();
        }
    }

    void showNoAddress(){
        addr_layout.setVisibility(View.GONE);
        no_add.setVisibility(View.VISIBLE);
    }

    void setupAddress(){
        if (isLoggedin && !UID.equals("")) {
            rAddr.setAdapter(addr_adapter);
            db.collection("user/"+ UID +"/address").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }
                    if (snapshots.getDocumentChanges().size() == 0){
                        showNoAddress();
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        DocumentSnapshot document;
                        switch (dc.getType()) {
                            case ADDED:
//                                Log.d(TAG, "New addr item: " + dc.getDocument().getData());
                                document =  dc.getDocument();
                                addr_list.add( new AddressData( document.getId(),document.getString("name"),
                                        document.getString("address"), document.get("phone")+"",
                                        document.getString("city"), document.getString("state"), document.getString("country")) );
                                Log.d(TAG, "New addr item: " +addr_list.size()+ dc.getDocument().getData());
                                addr_adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified addr item: " + dc.getDocument().getData());
                                document = dc.getDocument();
                                flag++;
                                for(int i = 0; i < addr_list.size(); i++){
                                    AddressData p = addr_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
//                                            product_list.remove(i);
                                        p.setName(document.getString("name"));
                                        p.setAddress(document.getString("address"));
                                        p.setPhone(document.get("phone")+"");
                                        p.setCity(document.getString("city"));
                                        p.setState(document.getString("state"));
                                        p.setCountry(document.getString("country"));

                                        addr_list.set(i,p);
                                        addr_adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed addr item: " + dc.getDocument().getData());
                                document = dc.getDocument();
                                for(int i = 0; i < addr_list.size(); i++){
                                    AddressData p = addr_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
                                        addr_list.remove(i);
                                        Log.d(TAG, "Deleted found address document " + p.getName());
                                        break;
                                    }
                                }
                                if (addr_list.size()<=0){
                                    showNoAddress();
                                }
                                addr_adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Not logged in",Toast.LENGTH_SHORT).show();
        }
    }

    void showNoItem(){
        cartItemLayout.setVisibility(View.GONE);
        noitem.setVisibility(View.VISIBLE);
    }

    void setupCart(){
        if (isLoggedin && !UID.equals("")) {
            rCart.setAdapter(adapter);
            setupAddress();
            db.collection("user/"+ UID +"/cart").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        if (snapshots.getDocumentChanges().size() == 0){
                            showNoItem();
                        }
        //                    Toast.makeText(getApplicationContext(),snapshots.getDocumentChanges().size()+"size",Toast.LENGTH_SHORT).show();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document;
                            switch (dc.getType()) {
                                case ADDED:
                                    flag++;
                                    Log.d(TAG, "New cart item: " + dc.getDocument().getData());
                                    document =  dc.getDocument();
                                    if (document.getString("cat") != null){
                                        getDocument(document.getId(), document.getString("docID"),document.getString("cat"), document.getString("size"), document.getString("qty"));
                                    }
        //                                product_list.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), "category",document.getString("description")) );
        //                                adapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified cart item: " + dc.getDocument().getData());
                                    document = dc.getDocument();
                                    flag++;
                                    for(int i = 0; i < product_list.size(); i++){
                                        Product p = product_list.get(i);
                                        if (p.getId().equals(document.getId()) ){
//                                            product_list.remove(i);
                                            p.setQty(document.getString("qty"));
                                            p.setCategory(document.getString("cat"));
                                            p.setSize(document.getString("size"));

                                            product_list.set(i,p);
                                            break;
                                        }
                                    }
                                    if (document.getString("cat") != null){
                                        setupPriceDetails();
//                                        getDocument(document.getId(), document.getString("docID"),document.getString("cat"), document.getString("size"), document.getString("qty"));
                                    }
        //                                product_list.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), "Category",document.getString("description")) );
        //                                adapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed cart item: " + dc.getDocument().getData());
                                    document = dc.getDocument();
                                    flag--;
                                    for(int i = 0; i < product_list.size(); i++){
                                        Product p = product_list.get(i);
                                        if (p.getId().equals(document.getId()) ){
                                            product_list.remove(i);
                                            setupPriceDetails();
                                            Log.d(TAG, "Deleted found document " + p.getName());
                                            break;
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(),product_list.size()+" size",Toast.LENGTH_SHORT).show();
                                    if (product_list.size()<=0){
                                        showNoItem();
                                    }
                                    adapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
            });
        }
    }

    void applyPromocode(){
        long discount = -1;
        long maxDiscount = 0;
        if(isPromocode) {
            for (int j = 0; j < promoList.size(); j++) {
                Promocode p = promoList.get(j);
                discount = p.getDiscount();
                maxDiscount = p.getMaxDiscount();
                promocode_applied_text.setVisibility(View.VISIBLE);
                break;
            }
            if (discount!=-1){
                int d = (int) ((mGTotal*(int)discount)/100);
                if (d>maxDiscount){
                    d= (int) maxDiscount;
                }
                mGTotal = mGTotal - d;
                discountPrice_layout.setVisibility(View.VISIBLE);
                extra_discount_price.setText("-" + d);
                totalDiscount = d;
                for (int i=0; i<product_list.size(); i++){
                    Product p = product_list.get(i);
                    p.setDiscount(false);
                    product_list.set(i, p);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    void setupPriceDetails(){
        mAllprice = 0;
        mGTotal = 0;
        mItem = 0;
        totalDiscount = 0;
        for(int i = 0; i < product_list.size(); i++) {
            long discount = -1;
            long maxDiscount = 0;
            Product Ptmp = product_list.get(i);
            if (!isPromocode) {
                for (int ii = 0; ii < discountList.size(); ii++) {
                    Discount d = discountList.get(ii);
                    if (Ptmp.getCategory().equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
                        if (discount < d.getDiscount()) {
                            discount = d.getDiscount();
                            maxDiscount = d.getMaxDiscount();
                            discountName = d.getName();
                            isDiscount = true;
                        }
                    }
                }
            }
            long price = Long.parseLong(Ptmp.getPrice());
            long price2 = Long.parseLong(Ptmp.getPrice());
            if (discount!=-1){
                int d = (int) ((price*(int)discount)/100);
                if (d>maxDiscount){
                    d= (int) maxDiscount;
                }
                totalDiscount += d * Integer.parseInt(Ptmp.getQty());
                price = price - d;
            }
            Log.e(TAG, "Gtotal befor " + mGTotal);
            float de = price * Float.parseFloat(Ptmp.getQty());
            mAllprice += price2 * Integer.parseInt(Ptmp.getQty());
            mGTotal += de;
            mItem += Integer.parseInt(Ptmp.getQty());

            Log.e(TAG, "Gtotal after1 " + mGTotal);
            Log.e(TAG, "DE " + de);
            Log.e(TAG, "price " + price*Float.parseFloat(Ptmp.getQty()));
        }
        if (isPromocode){
            Log.e(TAG, "calling function");
            applyPromocode();
        }
        int temp = Math.round(mGTotal);
        int temp2 = Math.round(mAllprice);
        item.setText("Price ("+mItem+" items)");
        all_price.setText(temp2 + "");
        g_total.setText("\u20B9"+temp + "");
        f_price.setText("\u20B9"+temp + "");

        /*
        *
        *
        * mDeliveryFee -> represents delivery charges
        * mGTotal      -> represents total after applying discount or promocode
        * totalDiscount -> represents total discount applied to cart or from discount or promocode
        * mAllprice    -> represents total of cart
        *
        *
        */

        shipping_fee_discount.setText("-" + mDeliveryFee);
        shipping_fee.setText("+" + mDeliveryFee);
        if (totalDiscount != 0) {
            Log.e(TAG, "not hidden");
            discountPrice_layout.setVisibility(View.VISIBLE);
            extra_discount_price.setText("-" + totalDiscount);
        }else{
            Log.e(TAG, "hidden");
            discountPrice_layout.setVisibility(View.GONE);
        }
    }


    void getDocument(final String docID, final String ID, final String cat, final String size, final String qty){
        DocumentReference docRef = db.document("product/category/"+ cat.toLowerCase() + "/"+ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("status").equals("live")) {
                            product_list.add(new Product(docID, ID, document.getString("pname"),
                                    document.getString("price"), document.getString("image"),
                                    cat, qty, size));
                        }else{
                            Toast.makeText(getApplicationContext(),"Some of the product in cart were unavailable and removed", Toast.LENGTH_SHORT).show();
                            deleteCartItem(docID);
                        }
                        adapter.notifyDataSetChanged();
                        setupPriceDetails();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private Task<String> onCreateOrder(int text, String list) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amt", text);
        data.put("prodList", list);

        return mFunctions
                .getHttpsCallable("createOrder")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        Map<String, String> map = new HashMap<String, String>();
                        map= (Map<String, String>) task.getResult().getData();
//                    String result = json.toString();
                        try{
                            Log.w(TAG, map + "");
                            Log.w(TAG,task.getResult().getData() + "");
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        return String.valueOf(map);
                    }
                });
    }

    void createOrder(int total, String list){
        onCreateOrder(total, list)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // [START_EXCLUDE]
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "onCreate:onFailure", e);
                            enablePlaceOrder();
//                            showSnackbar("An error occurred.");
                            return;
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        String result = task.getResult();
                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            String err = jsonObject.optString("error");


                            if (err.equals("")){
//                            Toast.makeText(getApplicationContext(), "err-"+err,Toast.LENGTH_SHORT).show();
                                int amount = jsonObject.getInt("amount");
                                oid = jsonObject.getString("id");
                                int attempts = jsonObject.getInt("attempts");
                                String currency = jsonObject.getString("currency");
                                String receipt = jsonObject.getString("receipt");
                                String key = jsonObject.getString("key");
                                prodList = jsonObject.getString("prod");
                                docRefID = jsonObject.getString("docRefID");
//                                Toast.makeText(getApplicationContext(), "id-"+id+" am-"+amount,Toast.LENGTH_SHORT).show();
                                if (attempts == 0){
                                    initPayment(oid,key,amount+"");
                                }else {
                                    Toast.makeText(getApplicationContext(),  "Multiple attempts detected! Please try again",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                enablePlaceOrder();
                                Toast.makeText(getApplicationContext(),  "err-"+err,Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            enablePlaceOrder();
                            e.printStackTrace();
                        }
                    }
                });
    }


    void initPayment(String orderid, String key, String amt){

        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID(key);
        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Studio Shringaar");

            options.put("description", "Go digital");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderid);
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            int temp = Integer.parseInt(amt);
            temp*=100;
//            Toast.makeText(getApplicationContext(),"Total payable "+temp,Toast.LENGTH_SHORT).show();
            options.put("amount", temp);

            checkout.open(activity, options);

        } catch(Exception e) {
            enablePlaceOrder();
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(final String s) {
        enablePlaceOrder();
        Product pp = product_list.get(0);
        int size = product_list.size();
        String title = pp.getName();
        if (size>1){
            title += " ( +"+(product_list.size()-1)+ " more)";
        }
        Map<String, Object> data = new HashMap<>();
        data.put("payment_id", s);
        data.put("status", "success");
        data.put("addressID", addressID);
        data.put("title", title);
        data.put("img", pp.getImg());

//        data.put("deliveryFee", mDeliveryFee);
        data.put("GTotal", mGTotal);
        data.put("totalDiscount", totalDiscount);
        data.put("allPrice", mAllprice);

        data.put("discountName", discountName);
        data.put("promocodeName", promocodeName);
        data.put("shippingFee", mDeliveryFee);
        data.put("shippingFeeDiscount", mDeliveryFee);

        db.document("user/" + UID + "/order/" + docRefID)
                .set(data, SetOptions.merge());
        Toast.makeText(getApplicationContext(),"Order Placed" ,Toast.LENGTH_SHORT).show();

        deleteFromCart();
    }

    @Override
    public void onPaymentError(int i, String s) {
        enablePlaceOrder();
        Map<String, Object> data = new HashMap<>();
        data.put("status", s);
        db.document("user/" + UID + "/order/" + docRefID)
                .set(data, SetOptions.merge());
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    void deleteFromCart(){
        String[] split = prodList.split("~");
        Log.e(TAG,split.length+" length " + prodList);
        ArrayList<OrderItem> orderItem = new ArrayList<OrderItem>();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < split.length; i++) {
            Log.e(TAG,split[i]);
            String[] p = split[i].split("_");

            for(int ii = 0; ii < product_list.size(); ii++) {
                Product pp = product_list.get(ii);
                if ( pp.getId().equals(p[0]) ) {
                    Toast.makeText(getApplicationContext(),split[i]+ " creating prod",Toast.LENGTH_SHORT).show();
                    createOrderItem(pp.getoID(),p[1],pp.getSize(),pp.getCategory());
                    orderItem.add(new OrderItem(pp.getoID(),p[1],pp.getSize(),pp.getCategory(),pp.getName(), pp.getPrice(), pp.getImg()) );
                    JSONObject formDetailsJson = new JSONObject();
                    try {
                        formDetailsJson.put("name",pp.getName());
                        formDetailsJson.put("price",pp.getPrice());
                        formDetailsJson.put("qty", p[1]);
                        formDetailsJson.put("size",pp.getSize());
                        formDetailsJson.put("category",pp.getCategory());
                        formDetailsJson.put("img",pp.getImg());
                        jsonArray.put(formDetailsJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    deleteCartItem(pp.getId());
                    break;
                }
            }
        }
        //send mail
        sendMail(jsonArray);
        createOrderItem2(orderItem);
    }

    void deleteCartItem(String id){
        db.document("user/" + UID + "/cart/"+ id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        w.setImageResource(R.drawable.ic_wishlist_no);
//                        Toast.makeText(getApplicationContext(),"Removed from the cart while order",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully deleted! cart");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    void createOrderItem( String dID, String qty, String size, String cat ){
        Map<String, Object> p = new HashMap<>();
//        p.put("cat", categoryFromIntent.toLowerCase());
        p.put("pid", dID);
        p.put("qty", qty);
        p.put("size", size);
        p.put("cat", cat);

        db.collection("user/" + UID + "/order/" + docRefID + "/product")
                .add(p)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(getApplicationContext(), "product added", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    void createOrderItem2(ArrayList<OrderItem> o){
        Map<String, Object> p = new HashMap<>();
//        p.put("cat", categoryFromIntent.toLowerCase());
        p.put("product_list", o);

        db.document("user/" + UID + "/order/" + docRefID)
                .update(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(getApplicationContext(), "product added", Toast.LENGTH_SHORT).show();
//                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private Task<String> onSendEmail(JSONArray o) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("oid", oid);
        data.put("product_list", o);

        data.put("GTotal", mGTotal);
        data.put("totalDiscount", totalDiscount);
        data.put("allPrice", mAllprice);

        data.put("shippingFee", mDeliveryFee);
        data.put("shippingFeeDiscount", mDeliveryFee);

        //getting address
        String addr = "";
        String cn = "";
        for (int i=0; i<addr_list.size(); i++){
            AddressData aa = addr_list.get(i);
            if (aa.getChecked()){
                cn += aa.getName();
                addr += aa.getAddress() + ", " + aa.getCity() + ", " + aa.getState() + ", " + aa.getCountry();
//                tempID = aa.getId();
                break;
            }
        }

        data.put("cus_name", cn);
        data.put("cus_addr", addr);


        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        return mFunctions
                .getHttpsCallable("sendMail")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        Map<String, String> map = new HashMap<String, String>();
                        map= (Map<String, String>) task.getResult().getData();
                        try{
                            Log.w(TAG, map + "");
                            Log.w(TAG,task.getResult().getData() + "");
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        return String.valueOf(map);
                    }
                });
    }

    void sendMail(JSONArray o){
        onSendEmail(o)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            // [START_EXCLUDE]
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "createOrder:onFailure", e);
                            return;
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        String result = task.getResult();
                        Log.e(TAG, result + " <= result");
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String err = jsonObject.optString("error");
                            if (err.equals("")){
                                String id = jsonObject.getString("error");
                                String message = jsonObject.getString("message");
                            } else {
                                Toast.makeText(getApplicationContext(),  "err-"+err,Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

}
