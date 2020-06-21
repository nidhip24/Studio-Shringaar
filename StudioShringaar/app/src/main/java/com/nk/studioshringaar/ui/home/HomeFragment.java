package com.nk.studioshringaar.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    TabLayout tabs;
    ViewPager viewPager;
    SectionsPagerAdapter sectionsPagerAdapter;

    LinearLayout home_content, home_spinner;

    private FirebaseAuth mAuth;
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db = null;
    private View root;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        tabs = root.findViewById(R.id.tabs);
        viewPager = root.findViewById(R.id.view_pager);

        home_content = root.findViewById(R.id.home_content);
        home_spinner = root.findViewById(R.id.home_spinner);
//        tabs.setupWithViewPager(viewPager);

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//
//            }
//        });

        //setting up firebase
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        home_content.setVisibility(View.GONE);
        home_spinner.setVisibility(View.VISIBLE);

        getTitle();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getActivity(),"onstart",Toast.LENGTH_SHORT).show();
//        viewPager.setAdapter(sectionsPagerAdapter);
//        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(getActivity(),"onStop",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity(),"onResume",Toast.LENGTH_SHORT).show();
//        viewPager.setAdapter(sectionsPagerAdapter);
//        tabs.setupWithViewPager(viewPager);
    }

    void getTitle() {
        DocumentReference docRef = db.document("product/category");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, document.get("cat") + "");
                        ArrayList<HashMap> group = (ArrayList<HashMap>) document.get("cat");
                        setupTabs(group);
//                        Toast.makeText(getActivity(),"onHome " + document.getData(),Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "Category Document data: " + document.getData() + group.size());
                    } else {
                        Toast.makeText(getActivity(),"onHome nodoc ",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void setupTabs(ArrayList<HashMap> l){
//        String[] arr = new String[l.size()+1];
        String[] arr = new String[l.size()+1];
        arr[0] = "Home";
        for(int i = 0; i<l.size();i++){
            Log.e(TAG,"i " + l.size());
            Map<String, String> temp = l.get(i);
            for (Map.Entry<String,String> entry : temp.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("name")){
                    arr[i+1] = value;
                    Log.e(TAG,"key " +key + "    val " +value);
                }
                // do stuff
            }
        }

//        int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
        sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getChildFragmentManager(), l.size()+1, arr);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        home_content.setVisibility(View.VISIBLE);
        home_spinner.setVisibility(View.GONE);
    }
}