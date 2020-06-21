package com.nk.studioshringaar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.data.Discount;
import com.nk.studioshringaar.data.DiscountData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = null;
    private ArrayList<Discount> discountList;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context c = this;

        discountList = new ArrayList<Discount>();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(getApplicationContext(),Home.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
//                //makerequest(c);
//            }
//        }, 1000);

        getDiscount();
    }

    private void redirect(){
        Intent i = new Intent(getApplicationContext(),Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void getDiscount(){
        Date cDate = new Date();
        final String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        final String[] tt = fDate.split("-");
        final int Y = Integer.parseInt(tt[0]);
        final int M = Integer.parseInt(tt[1]);
        final int D = Integer.parseInt(tt[2]);
        db.collection("discount")
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
                                if ( (D>=SD && M>=SM && Y>=SY) && (D<=ED && M<=EM && Y<=EY) ){
                                    discountList.add(new Discount(document.getId(), document.getString("name"),
                                            document.getString("cat"), document.getString("startDate"),
                                            document.getString("endDate"), (long)document.get("discount"),
                                            (long)document.get("maxDiscount")));
                                }
                                Log.e(TAG, document.getId() + " :/=> " + document.getData());
                            }
                            setup();
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setup() {
        DiscountData dd = new DiscountData(getApplicationContext());
        dd.addDiscount(discountList);
        redirect();
//        ArrayList<Discount> temp = dd.getDiscount();
//        for (int ii=0; ii < temp.size(); ii++) {
//            Discount d = temp.get(ii);
//            Log.e(TAG, " list " + d.getId());
//            Log.e(TAG, " list " + d.getDiscount());
//            Log.e(TAG, " list " + d.getStartDate());
//        }
    }
}