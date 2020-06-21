package com.nk.studioshringaar.ui.account;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.nk.studioshringaar.Home;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.account.address.ShowAddress;
import com.nk.studioshringaar.ui.account.edit.AccountEdit;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";
    private FirebaseFirestore db = null;
    private FirebaseAuth mAuth;

    private Boolean isLoggedin = false;
    private String UID = "";

    private AccountViewModel mViewModel;

    private TextView name, email, phone, myAddress, logout, accountSetting;
    private ImageView edit;

    private String mName, mEmail, mPhone;

    private Button viewAddr, viewOrder;

    private int totalAdd = 0;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.account_fragment, container, false);

        mName = "";
        mPhone = "";
        mEmail = "";

        viewAddr = root.findViewById(R.id.viewAddress);
        viewOrder = root.findViewById(R.id.viewOrder);

        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.mobile);
        myAddress = root.findViewById(R.id.myAddress);

        logout = root.findViewById(R.id.logout);
        accountSetting = root.findViewById(R.id.account_setting);

        edit = root.findViewById(R.id.edit);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        loginCheck();

        accountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i = new Intent(getActivity(), Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        viewAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ShowAddress.class));
            }
        });

        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), ShowAddress.class));
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        loginCheck();
    }

    private void edit(){
        Intent i = new Intent(getActivity(), AccountEdit.class);
        i.putExtra("name", mName);
        i.putExtra("email", mEmail);
        i.putExtra("phone", mPhone);
        startActivity(i);
    }

    private void loginCheck () {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UID = user.getUid();
            isLoggedin = true;
            getUserDetails();
            getAddress();
        }else{
            Toast.makeText(getActivity(), "Please login to continue!!", Toast.LENGTH_SHORT).show();
            isLoggedin = false;
        }
    }

    private void getAddress(){
        db.collection("user/" + UID + "/address")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalAdd=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                try {
                                    if (totalAdd == 0) {
                                        myAddress.setText(document.getString("address"));
                                    }
                                }catch (Exception e) {
                                    Log.e(TAG, "Failed");
                                }
                                totalAdd++;
                            }
                            if (totalAdd-1 == 0){
                                viewAddr.setText("View " + totalAdd + " more");
                            }else {
                                viewAddr.setText("View " + (totalAdd - 1) + " more");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void getUserDetails () {

        db.collection("user").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "DocumentSnapshot data: " + document.getData());
                        try{
                            mName = document.getString("name");
                            name.setText(mName);
                        }catch (Exception e){
                            name.setVisibility(View.GONE);
                            Log.e(TAG, "no name found");
                        }

                        try{
                            mEmail = document.getString("email");
                            email.setText(mEmail);
                        }catch (Exception e){
                            email.setVisibility(View.GONE);
                            Log.e(TAG, "no name found");
                        }

                        try{
                            mPhone = document.getString("phone");
                            phone.setText(mPhone);
                        }catch (Exception e){
                            phone.setVisibility(View.GONE);
                            Log.e(TAG, "no name found");
                        }
                    } else {
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.e(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
