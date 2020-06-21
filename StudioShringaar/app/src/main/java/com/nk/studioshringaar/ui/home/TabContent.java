package com.nk.studioshringaar.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.CustomCommonAdapter;
import com.nk.studioshringaar.adapter.CustomProductAdapter;
import com.nk.studioshringaar.adapter.PaymentData;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.adapter.ProductsData;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class TabContent extends Fragment {

    String title;
    int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};

    String tabTitle[];

    private static final String TITLE_NAME = "TITLE";

    //customlist adapter
    private CustomProductAdapter adapter;
    private CustomCommonAdapter common_adapter;

    private LinearLayout home_layout, home_recycler, home_spinner;
    private LinearLayout common_layout;

    private List<ProductsData> productList;
    private List<Product> pr;
    private RecyclerView listView;

    private ArrayList<Product> temp_pro;
    private ArrayList<Product> all_product;
    private RecyclerView common_listView;

    private static final String TAG = "TabContent";
    private FirebaseFirestore db = null;
    private FirebaseAuth mAuth;

    Boolean isLoggedin = false;
    String UID = "";

    private PageViewModel pageViewModel;

    //filer and sorting button
    private Button filter, sort, clear_filter, clear_sort;

    private TextView min,max;

    private BottomSheetBehavior bottomSheetBehavior, sortBottomSheet;
    RangeSeekBar rangeSeekbar;
    int preMin = 0;
    int preMax = 5000;

    private ImageView close_sheet, close_sort_sheet;

    RadioGroup radioGroup;

    ArrayList<Discount> discountList;
    long discount = -1;
    long maxDiscount = 0;

    public static TabContent newInstance(int index) {
        TabContent fragment = new TabContent();
        Bundle bundle = new Bundle();
        bundle.putInt(TITLE_NAME, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(TITLE_NAME);
        }
        pageViewModel.setIndex(index);
    }
//    public TabContent(){}
//    public TabContent(int pos, int[ ] titles, Context c){
//
//        this.title = c.getResources().getString(titles[pos]);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_placeholder, container, false);

        //setting firebase
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        common_listView =  view.findViewById(R.id.common_products_rec);
        temp_pro = new ArrayList<Product>();
        //check if user is logged in
        loginCheck();
//        gettin titles
        getTitle();

        home_layout = view.findViewById(R.id.home_layout);
        home_recycler = view.findViewById(R.id.home_recycler);
        home_spinner = view.findViewById(R.id.home_spinner);

        filter = view.findViewById(R.id.filter);
        sort = view.findViewById(R.id.sort);

        min = view.findViewById(R.id.min);
        max = view.findViewById(R.id.max);
        close_sheet = view.findViewById(R.id.close_sheet);
        close_sort_sheet = view.findViewById(R.id.close_sheet2);

        common_layout = view.findViewById(R.id.common_layout);

        productList = new ArrayList<ProductsData>();
        pr = new ArrayList<Product>();
        listView =  view.findViewById(R.id.products_list);


        all_product = new ArrayList<Product>();

        adapter = new CustomProductAdapter(getActivity(), productList);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);

        //setting up bottomsheet for filters
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setHideable(true);

        // for sorting
        LinearLayout llBottomSheet2 = view.findViewById(R.id.sort_bottom_sheet);
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

        rangeSeekbar =  view.findViewById(R.id.rangeSeekbar);

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
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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

        final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(getActivity());
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

        clear_sort = view.findViewById(R.id.sortClear);
        clear_filter = view.findViewById(R.id.filterClear);

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

        radioGroup = view.findViewById(R.id.radioGroup);
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

        //getting discount
        DiscountData dd = new DiscountData(getActivity());
        discountList = dd.getDiscount();

        return view;
    }

    void loginCheck(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            UID = user.getUid();
            isLoggedin = true;
            common_adapter = new CustomCommonAdapter(getActivity(), temp_pro, UID, db);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setAlignItems(AlignItems.STRETCH);
//        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

            common_listView.setLayoutManager(layoutManager);

            common_listView.setAdapter(common_adapter);
        }else{
            isLoggedin = false;

            common_adapter = new CustomCommonAdapter(getActivity(), temp_pro);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setAlignItems(AlignItems.STRETCH);
//        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

            common_listView.setLayoutManager(layoutManager);

            common_listView.setAdapter(common_adapter);
        }
    }

    private void listenToChange(){
        pageViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                int temp = Integer.parseInt(s);
                if (tabTitle != null){
                    title = tabTitle[temp];
//                    Toast.makeText(getActivity(),"in listen "+title,Toast.LENGTH_SHORT).show();
                    setupTab();
                }
            }
        });
    }

    private void getTitle() {
        DocumentReference docRef = db.document("product/category");
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
        tabTitle = new String[l.size()+1];
        tabTitle[0] = "Home";
        for (int i=0; i<l.size(); i++){
            Map<String, String> temp = l.get(i);
            for (Map.Entry<String,String> entry : temp.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("name")) {
                    tabTitle[i + 1] = value;
                }
            }
        }
        listenToChange();
    }

    private void setupTab(){
        if (title!=null) {
            if (title.equals("Home")) {
                Log.e("HomeSweetHome", "again");
                home_layout.setVisibility(View.VISIBLE);
                common_layout.setVisibility(View.GONE);

                home_recycler.setVisibility(View.GONE);
                home_spinner.setVisibility(View.VISIBLE);

//                pr.add(new Product("1", "Dress", "500", "m", "dress"));
//                pr.add(new Product("1", "Kurti", "600", "m", "dress"));
//                pr.add(new Product("1", "Demp", "700", "m", "dress"));
//
//                productList.add(new ProductsData(pr, "Slider", 2));
//                productList.add(new ProductsData(pr, "Top selection", 0));
//                productList.add(new ProductsData(pr, "Poster", 3));
//                productList.add(new ProductsData(pr, "Dress", 1));
//
//                List<Product> kur = new ArrayList<Product>();
//
//                kur.add(new Product("1", "Kurti one", "500", "m", "kurtis"));
//                kur.add(new Product("2", "Kurti two", "500", "m", "kurtis"));
//                kur.add(new Product("3", "Kurti t", "500", "m", "kurtis"));

//                productList.add(new ProductsData(kur, "Kurtis", 1));
                setupHome();

//                kur2.add(new Product("1", "Kurti one", "500", "m", "kurtis"));
//                productList.add(new ProductsData(kur2, "noo", 2 ));



//                listView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                home_recycler.setVisibility(View.VISIBLE);
                home_spinner.setVisibility(View.GONE);

            }
            else {
                home_layout.setVisibility(View.GONE);
                common_layout.setVisibility(View.VISIBLE);

                common_listView.setAdapter(common_adapter);

                for (int ii=0; ii < discountList.size(); ii++) {
                    Discount d = discountList.get(ii);
//                    Log.e("DISOCUNTFINDING", title.toLowerCase()+" before  accct   " + d.getCat());
                    if (title.toLowerCase().equalsIgnoreCase(d.getCat()) || d.getCat().equalsIgnoreCase("all")) {
//                        Log.e("DISOCUNTFINDING", title.toLowerCase()+"  accct   " + d.getCat());
                        if (discount < d.getDiscount()) {
                            discount = d.getDiscount();
                            maxDiscount = d.getMaxDiscount();
                        }
                    }
                }

                db.collection("product/category/"+ title.toLowerCase())
                        .whereEqualTo("status","live")
                        .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document;
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New : "+ title + dc.getDocument().getData());
                                    document =  dc.getDocument();
                                    temp_pro.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                                    all_product.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                                    common_adapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
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
                                    common_adapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    document = dc.getDocument();
                                    for(int i = 0; i < all_product.size(); i++){
                                        Product p = all_product.get(i);
                                        if (p.getId().equals(document.getId()) ){
                                            all_product.remove(i);
                                            Log.d(TAG, "Deleted found document " + p.getName());
                                            break;
                                        }
                                    }
                                    common_adapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
            }
        }//end if

    }//end setupTab

    private void setupHome() {
        db.collection("home").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot document;
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New home: " + dc.getDocument().getData());
                            document =  dc.getDocument();
                            long code = (long) document.get("code");
                            if (code == 0){
                                //code 0 product carousel
                                long total = 10;
                                try{
                                    total = (long) document.get("total");
                                } catch (Exception ee){
                                    Log.e(TAG, "TOTAL WAS STRING " +ee);
                                }
                                List<Product> kur = new ArrayList<Product>();
                                productList.add(new ProductsData(document.getId(), kur, "Best of "+document.getString("cat"),
                                        0, document.get("timestamp")+"") );
                                adapter.notifyDataSetChanged();
//                                String total = (String) document.get("total");
                                getProduct(document.getString("cat"), total+"", document.getId());
                            } else if(code == 1){
                                List<Product> kur = new ArrayList<Product>();
                                productList.add(new ProductsData(document.getId(), kur, document.getString("cat"),
                                        1, document.get("timestamp")+"" ));
                                adapter.notifyDataSetChanged();
                                //code 1 three product layout
                                getProduct(document.getString("cat"), "3", document.getId());
                            } else if(code == 2){
                                //code 2 slider
                                List<Product> kur = new ArrayList<Product>();
                                productList.add(new ProductsData(document.getId(), kur, "Slider", 2, document.getString("img"),
                                        document.getString("img2"), document.getString("img3"),
                                        document.getString("img4"), document.get("timestamp")+"" ));
                                adapter.notifyDataSetChanged();
                            } else if(code == 3){
                                //code 3 poster
                                List<Product> kur = new ArrayList<Product>();
                                productList.add(new ProductsData(document.getId(), kur, "Poster", 3, document.getString("img"),
                                        document.getString("img"),document.getString("img"),
                                        document.getString("img") , document.get("timestamp")+"" ));
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case MODIFIED:
//                            Log.d(TAG, "Modified home: " + dc.getDocument().getData());
//                            document = dc.getDocument();
//                            for(int i = 0; i < temp_pro.size(); i++){
//                                Product p = temp_pro.get(i);
//                                if (p.getId().equals(document.getId()) ){
//                                    temp_pro.remove(i);
//                                    Log.d(TAG, "Deleted found document " + p.getName());
//                                    break;
//                                }
//                            }
//                            temp_pro.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), document.getString("imagetwo"), document.getString("imagethree"), document.getString("imagefour"), title.toLowerCase(),document.getString("description")) );
                            common_adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed home: " + dc.getDocument().getData());
                            document = dc.getDocument();
                            for(int i = 0; i < temp_pro.size(); i++){
                                Product p = temp_pro.get(i);
                                if (p.getId().equals(document.getId()) ){
                                    temp_pro.remove(i);
                                    Log.d(TAG, "Deleted found home document " + p.getName());
                                    break;
                                }
                            }
                            common_adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

    }//end setupHome

    private void getProduct(final String cat, final String limit, final String id){
        long li = Long.parseLong(limit);
        final List<Product> kur = new ArrayList<Product>();
        db.collection("product/category/"+cat)
                .whereEqualTo("status","live")
                .limit(li)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().size() == 0){
//                        showNoWishlist();
                        return;
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            kur.add(new Product(document.getId(), document.getString("pname"), document.getString("price"), document.getString("image"), cat));
//                                Toast.makeText(getApplication(), selectedSize + "  == " + document.getString("docID"), Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                    for(int i=0; i<productList.size(); i++) {
                        ProductsData p = productList.get(i);
                        if (p.getId().equals(id)){
                            productList.get(i).setP(kur);
//                            Toast.makeText(getActivity(), "list updated "+ p.getId() + kur.size(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(),"err",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //clear filter
    private void clearFilter(){
        temp_pro.clear();
        temp_pro.addAll(all_product);
        common_adapter.notifyDataSetChanged();
    }

    //clear sort
    private void clearSort(){
        temp_pro.clear();
        temp_pro.addAll(all_product);
        common_adapter.notifyDataSetChanged();
    }

    //filter product
    private void filterProduct(int start, int end){
//        Toast.makeText(getActivity(), "tag "+start + "  ned " + end , Toast.LENGTH_SHORT).show();
        temp_pro.clear();
//        temp_pro.addAll(all_product);

        Log.e(TAG, "NEWWWWWWWWWWWWWWWWWW");
        for (int i = 0; i < all_product.size(); i++) {
            Product p = all_product.get(i);
            int price = Integer.parseInt(p.getPrice());
            if (discount!=-1) {
                int d = (price * (int) discount) / 100;
                if (d > maxDiscount) {
                    d = (int) maxDiscount;
                }
                price = price - d;
            }
            if (price > start && price < end) {
                Log.e(TAG, p.getName() + price + " " + start);
                temp_pro.add(p);
            }
        }
        common_adapter.notifyDataSetChanged();
    }

    //sort product
    private void sortProduct(final String tag) {
//
        temp_pro.clear();
        temp_pro.addAll(all_product);
        Collections.sort(temp_pro, new Comparator<Product>() {

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

        for (int i = 0; i < temp_pro.size(); i++) {
            Log.e(TAG ,"HTL:--" + temp_pro.get(i).getPrice());
        }
        common_adapter.notifyDataSetChanged();
    }

}
