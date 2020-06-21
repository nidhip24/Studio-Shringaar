package com.nk.studioshringaar.ui.account.address;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.AddressData;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.adapter.WishlistAdapter;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class ShowAddress extends AppCompatActivity {

    private static final String TAG = "ShowAddress";
    private FirebaseFirestore db = null;
    private FirebaseAuth mAuth;

    private Boolean isLoggedin = false;
    private String UID = "";

    RecyclerView rAddress;

    ShowAddressAdapter adapter;
    ArrayList<AddressData> addr_list;

    private int totalAdd=0;

    private TextView totalSavedAddr;

    //address data
    private BottomSheetBehavior addressBottom;

    private Button add_address;

    private EditText aName, aAddress, aPhone, aCity, aState;
    private Button addAddressButton;
    private MaterialSpinner country_spinner;
    private List<String> country_list;

    ArrayAdapter<String> country_adapter;

    private CountryCodePicker ccp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        totalSavedAddr = findViewById(R.id.totalSavedAddr);
        add_address = findViewById(R.id.add_address);

        // add address feild
        aName = findViewById(R.id.name);
        aAddress = findViewById(R.id.address);
        aCity = findViewById(R.id.city);
        aState = findViewById(R.id.state);
        aPhone = findViewById(R.id.mobile_no);
        addAddressButton = findViewById(R.id.add);
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

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        rAddress = findViewById(R.id.addressRecycler);

        //setting layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rAddress.setLayoutManager(layoutManager);

        //setting adapter to the list
        addr_list = new ArrayList<AddressData>();

        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);


        // init the bottom sheet behavior
        addressBottom = BottomSheetBehavior.from(llBottomSheet);

        // change the state of the bottom sheet
        addressBottom.setState(BottomSheetBehavior.STATE_HIDDEN);

        // set the peek height
//        addressBottom.setPeekHeight(340);

        // set hideable or not
        addressBottom.setHideable(true);

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedin) {
                    addressBottom.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
                addressBottom.setState(BottomSheetBehavior.STATE_HIDDEN);


                addAddress(mName, mAddress, mPhone, mCity, mState, mCountry);
            }
        });

        loginCheck();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginCheck () {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UID = user.getUid();
            isLoggedin = true;
            adapter = new ShowAddressAdapter(this, addr_list, UID, db);
            rAddress.setAdapter(adapter);
            setup();
        }else{
            Toast.makeText(getApplicationContext(), "Please login to continue!!", Toast.LENGTH_SHORT).show();
            isLoggedin = false;
        }
    }

    private void setup(){
        if (isLoggedin) {
            totalAdd = 0;
            db.collection("user/" + UID + "/address").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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
                                totalAdd++;
                                totalSavedAddr.setText(totalAdd + " SAVED ADDRESS");
                                document =  dc.getDocument();
                                addr_list.add(new AddressData(document.getId(), document.getString("name"),
                                        document.getString("address"), document.getString("phone")));
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                document = dc.getDocument();
                                for(int i = 0; i < addr_list.size(); i++){
                                    AddressData p = addr_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
                                        addr_list.remove(i);
                                        Log.d(TAG, "Deleted found document " + p.getName());
                                        break;
                                    }
                                }
                                addr_list.add(new AddressData(document.getId(), document.getString("name"),
                                        document.getString("address"), document.getString("phone")));
                                adapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                totalAdd--;
                                totalSavedAddr.setText(totalAdd + " SAVED ADDRESS");
                                document = dc.getDocument();
                                for(int i = 0; i < addr_list.size(); i++){
                                    AddressData p = addr_list.get(i);
                                    if (p.getId().equals(document.getId()) ){
                                        addr_list.remove(i);
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

    void reset(){
        aName.setText("");
        aAddress.setText("");
        aPhone.setText("");
        aCity.setText("");
        aState.setText("");
        addressBottom.setPeekHeight(0);
        addressBottom.setState(BottomSheetBehavior.STATE_HIDDEN);

        country_list = new ArrayList<String>();
        country_list.add("india");
        country_list.add("india2");
        country_list.add("india3");

        country_adapter  = new ArrayAdapter<String>(this, R.layout.country_list_item, country_list);
        country_spinner.setAdapter(country_adapter);
    }

}
