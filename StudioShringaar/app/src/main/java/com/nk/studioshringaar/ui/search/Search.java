package com.nk.studioshringaar.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {

    MaterialSearchView searchView;

    private ArrayList<Product> searchList;
    private String[] searchArray;

    private FirebaseFirestore db = null;

    private static final String TAG = "HOME";
    private String[] title;
    Boolean isSubmit = false;
    private String sugg[];

    ListView l;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        searchList = new ArrayList<Product>();
        l = findViewById(R.id.search_sugg);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSubmit = true;
//                Toast.makeText(getApplicationContext(),"Your " + query,Toast.LENGTH_SHORT).show();
                search2(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getApplicationContext(),"w " + searchView.isSearchOpen(),Toast.LENGTH_SHORT).show();
                search(newText);
                if (!isSubmit)
                     isSubmit = false;
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
//                Toast.makeText(getApplicationContext(),"closing",Toast.LENGTH_SHORT).show();
                if (!isSubmit){
//                    Toast.makeText(getApplicationContext(),"closing" + isSubmit,Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("error", true);
                    resultIntent.putExtra("usercancelled", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value= (String) adapter.getItem(position);
                Toast.makeText(getApplicationContext(),value,Toast.LENGTH_SHORT).show();
                for (int i=0; i<searchList.size(); i++) {
                    Product p = searchList.get(i);
                    if (p.getName().equalsIgnoreCase(value)){
        //                Toast.makeText(getApplicationContext(),"found " +p.getId(), Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("error", false);
                        resultIntent.putExtra("id",p.getId());
                        resultIntent.putExtra("cat",p.getCategory());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            }
        });

        getCategory();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.showSearch();
        return true;
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
        initSearch();
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

    private void initSearch() {

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
                                    searchList.add(new Product(document.getId(), document.getString("pname"), temptitle));
//                                    Log.e(TAG, document.getId() + " :/=> " + document.getData());
                                }
                                setupSuggestions();
                            } else {
                                Log.e(TAG, "fucked up ");
                                Log.e(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }//end initSearch

    void setupSuggestions(){
        try{
            title = new String[searchList.size()];
            int index = 0;
            for (int i=0; i<searchList.size(); i++) {
                Product p = searchList.get(i);
                title[index] = p.getName();
                index++;
            }
//            searchView.setSuggestions(title);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Getting results", Toast.LENGTH_SHORT).show();
        }
    }

    void search2(String query) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("error", true);
        resultIntent.putExtra("query", query);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    void search(String query){
        int flag=-1;
        String temp[] = new String[searchList.size()];
        int index=0;
        for (int i=0; i<searchList.size(); i++) {
            Product p = searchList.get(i);
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                temp[index] = p.getName();
                index++;
            }
        }
        if (index!=0){
//            Toast.makeText(getApplicationContext(),"tot " + index, Toast.LENGTH_SHORT).show();
            sugg = new String[index];
            for (int i=0;i<index; i++) {
                sugg[i] = temp[i];
            }
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, sugg);
            adapter.notifyDataSetChanged();
            l.setAdapter(adapter);
        }
//        for (int i=0; i<searchList.size(); i++) {
//            Product p = searchList.get(i);
//            if (p.getName().equalsIgnoreCase(query)){
////                Toast.makeText(getApplicationContext(),"found " +p.getId(), Toast.LENGTH_SHORT).show();
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("error", false);
//                resultIntent.putExtra("id",p.getId());
//                resultIntent.putExtra("cat",p.getCategory());
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//                flag=1;
//            }
//        }
//        if (flag==-1){
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("error", true);
//            resultIntent.putExtra("query", query);
//            setResult(Activity.RESULT_OK, resultIntent);
//            finish();
////            Toast.makeText(getApplicationContext(),"No product found", Toast.LENGTH_SHORT).show();
//        }
    }
}
