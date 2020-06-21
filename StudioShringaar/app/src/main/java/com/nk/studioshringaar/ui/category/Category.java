package com.nk.studioshringaar.ui.category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.CustomCommonAdapter;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.ui.cart.Cart;
import com.nk.studioshringaar.ui.product.ProductDetail;
import com.nk.studioshringaar.ui.search.Search;
import com.nk.studioshringaar.ui.search.SearchResult;
import com.nk.studioshringaar.ui.wishlist.WishList;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.nk.studioshringaar.Home.REQUEST_CODE_SEARCH;

public class Category extends AppCompatActivity {

    private String title = "";

    private static final String TAG = "CategoryShow";
    private FirebaseFirestore db = null;

    private ArrayList<Product> product;
    private CustomCommonAdapter adapter;
    private RecyclerView listView;

    private LinearLayout layout, noProduct;

    //filer and sorting button
    private Button filter, sort, clear_filter, clear_sort;

    private TextView min,max;

    private BottomSheetBehavior bottomSheetBehavior, sortBottomSheet;
    RangeSeekBar rangeSeekbar;
    int preMin = 0;
    int preMax = 5000;

    private ImageView close_sheet, close_sort_sheet;

    RadioGroup radioGroup;
    private ArrayList<Product> all_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent i = getIntent();

        layout = findViewById(R.id.layout);
        noProduct = findViewById(R.id.noproduct);

        noProduct.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            title = i.getStringExtra("name");
            toolbar.setTitle(title.substring(0, 1).toUpperCase() + title.substring(1));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        listView =  findViewById(R.id.product_recyclerview);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        product = new ArrayList<Product>();
        all_product = new ArrayList<Product>();
        adapter = new CustomCommonAdapter(this, product,1);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
//        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

        listView.setLayoutManager(layoutManager);


        //filter and sorting
        filter = findViewById(R.id.filter);
        sort = findViewById(R.id.sort);

        min = findViewById(R.id.min);
        max = findViewById(R.id.max);
        close_sheet = findViewById(R.id.close_sheet);
        close_sort_sheet = findViewById(R.id.close_sheet2);

        //setting up bottomsheet for filters
        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setHideable(true);

        // for sorting
        LinearLayout llBottomSheet2 = findViewById(R.id.sort_bottom_sheet);
        sortBottomSheet = BottomSheetBehavior.from(llBottomSheet2);
        sortBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        sortBottomSheet.setPeekHeight(0);
        sortBottomSheet.setHideable(true);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        rangeSeekbar =  findViewById(R.id.rangeSeekbar);

        rangeSeekbar.setRangeValues(500, 4000);
        // Get  noticed while dragging
        rangeSeekbar.setNotifyWhileDragging(true);
        rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Now you have the minValue and maxValue of your RangeSeekbar
                int diff = maxValue - minValue;
                if (diff < 750) {
                    filterProduct(minValue, maxValue);
                    min.setText(""+minValue);
                    max.setText(maxValue+"");
                    bar.setEnabled(false);
                    if(minValue != preMin){
                        rangeSeekbar.setSelectedMinValue(preMin);
                    }
                    else if(maxValue != preMax){
                        rangeSeekbar.setSelectedMaxValue(preMax);
                    }
//                    rangeSeekbar.setEnabled(true);
//                    Snackbar.make(view, "Difference can't be less than 750", Snackbar.LENGTH_SHORT);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                    alert.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            rangeSeekbar.setEnabled(true);
                        }
                    });
                    alert.setCancelable(false);
                    alert.setMessage(Html.fromHtml("Difference can't be less than 750 ")).show();

                } else {
                    filterProduct(minValue, maxValue);
                    preMin = minValue;
                    preMax = maxValue;
                    min.setText(""+minValue);
                    max.setText(maxValue+"");
                }
            }
        });

        final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(getApplicationContext());
        seekBar.setRangeValues(0, 100);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
            }
        });

        seekBar.setNotifyWhileDragging(true);

        close_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        close_sort_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        clear_sort =  findViewById(R.id.sortClear);
        clear_filter = findViewById(R.id.filterClear);

        //clearing filters
        clear_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rangeSeekbar.setSelectedMinValue(500);
                rangeSeekbar.setSelectedMaxValue(4000);
                min.setText(""+500);
                max.setText(4000+"");
                clearFilter();
            }
        });

        //clearing sort
        clear_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.clearCheck();
                clearSort();
            }
        });

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.LtoH:
                        sortProduct("L");
                        break;
                    case R.id.HtoL:
                        sortProduct("H");
                        break;
                }
            }
        });

        //&&&&& end of sorting and filter
        listView.setAdapter(adapter);
        if (!title.equals("")) {
            setup();
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showNoProduct() {
        layout.setVisibility(View.GONE);
        noProduct.setVisibility(View.VISIBLE);
    }

    private void showProduct() {
        layout.setVisibility(View.VISIBLE);
        noProduct.setVisibility(View.GONE);
    }



    private void setup() {
        db.collection("product/category/"+ title.toLowerCase())
                .whereEqualTo("status","live")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                if (snapshots.getDocumentChanges().size() == 0){
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    showNoProduct();
                }else{
                    showProduct();
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot document;
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New product: " + dc.getDocument().getData());
                            document =  dc.getDocument();
                            product.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                            all_product.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                            adapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified product: " + dc.getDocument().getData());
                            document = dc.getDocument();
                            for(int i = 0; i < all_product.size(); i++){
                                Product p = all_product.get(i);
                                if (p.getId().equals(document.getId()) ){
                                    all_product.remove(i);
                                    Log.d(TAG, "Deleted found document " + p.getName());
                                    break;
                                }
                            }
                            all_product.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed product: " + dc.getDocument().getData());
                            document = dc.getDocument();
                            for(int i = 0; i < all_product.size(); i++){
                                Product p = all_product.get(i);
                                if (p.getId().equals(document.getId()) ){
                                    all_product.remove(i);
                                    Log.d(TAG, "Deleted found document " + p.getName());
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }


    //clear filter
    private void clearFilter(){
        product.clear();
        product.addAll(all_product);
        adapter.notifyDataSetChanged();
    }

    //clear sort
    private void clearSort(){
        product.clear();
        product.addAll(all_product);
        adapter.notifyDataSetChanged();
    }

    //filter product
    private void filterProduct(int start, int end){
//        Toast.makeText(getActivity(), "tag "+start + "  ned " + end , Toast.LENGTH_SHORT).show();
        product.clear();
//        temp_pro.addAll(all_product);

        Log.e(TAG, "NEWWWWWWWWWWWWWWWWWW");
        for (int i = 0; i < all_product.size(); i++) {
            Product p = all_product.get(i);
            int price = Integer.parseInt(p.getPrice());
            if (price > start && price < end) {
                Log.e(TAG, p.getName() + price + " " + start);
                product.add(p);
            }
        }
        if (product.size()==0){
//            Toast.makeText(getApplicationContext(), "No product found in that price range", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    //sort product
    private void sortProduct(final String tag) {
//
        product.clear();
        product.addAll(all_product);
        Collections.sort(product, new Comparator<Product>() {

            @Override
            public int compare(Product arg1, Product arg2) {
                // TODO Auto-generated method stub
                Integer obj1 = new Integer(arg1.getPrice().replace(",", ""));
                Integer obj2 = new Integer(arg2.getPrice().replace(",", ""));
                if (tag.equals("H"))
                    return (obj2).compareTo(obj1);
                else
                    return (obj1).compareTo(obj2);
            }
        });

        for (int i = 0; i < product.size(); i++) {
            Log.e(TAG ,"HTL:--" + product.get(i).getPrice());
        }
        adapter.notifyDataSetChanged();
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
            case R.id.action_wishlist:
                Intent i = new Intent(getApplicationContext(), WishList.class);
                startActivity(i);
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

}
