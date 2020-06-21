package com.nk.studioshringaar.ui.shopbycategory;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.CategoryAdapter;
import com.nk.studioshringaar.adapter.CategoryData;
import com.nk.studioshringaar.adapter.CustomProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopByCategory extends Fragment {

    private CategoryAdapter adapter;
    private LinearLayout categoryList, noCat;
    private ArrayList<CategoryData> category;
    private RecyclerView listView;

    private ShopByCategoryViewModel mViewModel;

    private static final String TAG = "ShopByCategory";
    private FirebaseFirestore db = null;

    public static ShopByCategory newInstance() {
        return new ShopByCategory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ShopByCategoryViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.shop_by_category_fragment, container, false);

        categoryList = root.findViewById(R.id.categoryList);
        noCat = root.findViewById(R.id.nocat);
        listView = root.findViewById(R.id.category_list);

        noCat.setVisibility(View.GONE);

        category = new ArrayList<CategoryData>();
        adapter = new CategoryAdapter(getContext(), category);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        //getting categories
        getTitle();

        return root;
    }

    private void getTitle() {
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

    private void setupTabs(ArrayList<HashMap> l) {
        if (l.size() == 0) {
            hide();
        }
        for (int i = 0; i < l.size(); i++) {
            Map<String, String> temp = l.get(i);
            String name = "";
            String link = "";
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("name")) {
                    name = value.substring(0, 1).toUpperCase() + value.substring(1);
                }
                if (key.equals("link")) {
                    link = value;
                }
                // do stuff
            }
            category.add(new CategoryData(name, link));
            adapter.notifyDataSetChanged();
        }
    }

    private void hide(){
        noCat.setVisibility(View.VISIBLE);
        categoryList.setVisibility(View.GONE);
    }

}
