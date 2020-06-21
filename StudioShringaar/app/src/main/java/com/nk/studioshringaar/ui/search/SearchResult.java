package com.nk.studioshringaar.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.CustomCommonAdapter;
import com.nk.studioshringaar.adapter.Product;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SearchResult extends AppCompatActivity {

    String q = "";
    private static final String TAG = "SearchResult";
    private FirebaseFirestore db = null;
    private String[] title;
    private ArrayList<Product> searchList;
    private ArrayList<Product> allProduct;


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
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        layout = findViewById(R.id.layout);
        noProduct = findViewById(R.id.noproduct);

        noProduct.setVisibility(View.GONE);

        listView =  findViewById(R.id.product_recyclerview);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        product = new ArrayList<Product>();
        adapter = new CustomCommonAdapter(this, product);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);

        searchList = new ArrayList<Product>();
        allProduct = new ArrayList<Product>();
        all_product = new ArrayList<Product>();

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


        try {
            Intent i = getIntent();
            q = i .getStringExtra("q");
            Log.e(TAG, "THE QUERY " + q);
            getCategory();
        } catch (Exception e){
            Log.e(TAG, "CANNOT RETRIVE QUERY " + e);
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

    private void getCategory(){
        DocumentReference docRef = db.document("product/category");
        final String temp;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<HashMap> group = (ArrayList<HashMap>) document.get("cat");
                        setupString(group);
                    } else {
//                        Toast.makeText(getActivity(),"onHome nodoc ",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setupString(ArrayList<HashMap> l){
        title = new String[l.size()];
        for (int i=0; i<l.size(); i++){
            Map<String, String> temp = l.get(i);
            for (Map.Entry<String,String> entry : temp.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("name")) {
                    title[i] = value;
                }
            }
        }
        getProducts();
    }

    private void getProducts() {

        for (int i=0; i<title.length; i++) {
            Log.e(TAG, "init search for " + title[i]);
            final String temptitle = title[i];
            db.collection("product/category/" + title[i])
                    .whereEqualTo("status","live")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), task.getResult().size()+"",Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "search size " + task.getResult().size());
                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    searchList.add(new Product(document.getId(), document.getString("pname"),
//                                            document.getString("price"), document.getString("image"),
//                                            document.getString("imagetwo"), document.getString("imagethree"),
//                                            document.getString("imagefour"), temptitle,
//                                            document.getString("description")));
                                    allProduct.add(new Product(document.getId(), document.getString("pname"),
                                            document.getString("price"), document.getString("image"),
                                            document.getString("imagetwo"), document.getString("imagethree"),
                                            document.getString("imagefour"), temptitle,
                                            document.getString("description")));
//                                    Log.e(TAG, document.getId() + " :/=> " + document.getData());
                                }
                                setup();
                            } else {
                                Log.e(TAG, "fucked up ");
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }//end initSearch

    void setup(){
        if (!q.equals("")){
            showProduct();
            for (int i=0; i<allProduct.size(); i++){
                int flag = -1;
                Product p = allProduct.get(i);
                for (int j=0;j<product.size();j++){
                    Product temp = product.get(j);
                    if (temp.getId().equals(p.getId())){
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1){
                    continue;
                }
                if(p.getName().toLowerCase().contains(q.toLowerCase())){
                    Log.e(TAG, "fucked up2 "+ product.size());
                    product.add(new Product(p.getId(), p.getName(), p.getPrice(), p.getImg(), p.getImg2(),
                            p.getImg3(), p.getImg4(), p.getCategory(), p.getDescription()));
                    all_product.add(new Product(p.getId(), p.getName(), p.getPrice(), p.getImg(), p.getImg2(),
                            p.getImg3(), p.getImg4(), p.getCategory(), p.getDescription()));
                    adapter.notifyDataSetChanged();
                }
            }
            if (product.size()==0){
                showNoProduct();
            }
        }else {
            showNoProduct();
        }
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


}
