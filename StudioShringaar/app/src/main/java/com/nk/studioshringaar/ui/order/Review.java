package com.nk.studioshringaar.ui.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nk.studioshringaar.R;

public class Review extends AppCompatActivity {

    private Boolean isActive = false;
    private Button next;
    private LinearLayout firstLayout, secondLayout;
    private FirebaseFirestore db = null;

    private TextView pname, pname2;
    private RatingBar mRatingBar;
    private EditText reviewText;
    private ImageView img, img2;

    private static final String TAG = "Review";
    private String orderid = "", name = "", imgURL = "";
    private float pRating = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent i = getIntent();
        try{
            orderid = i.getStringExtra("id");
            name = i.getStringExtra("name");
            imgURL = i.getStringExtra("img");
            pRating = i.getFloatExtra("rating", 1f);

//            Toast.makeText(getApplicationContext(), "i = " + orderid+ " n="+ name + " u=" + imgURL+
//                    " r="+ pRating, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "NOT ID GOT FROM INTENT");
            return;
        }

        //setting up firebase
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        next = findViewById(R.id.next);
        firstLayout = findViewById(R.id.firstLayout);
        secondLayout = findViewById(R.id.secondLayout);

        pname = findViewById(R.id.pname);
        pname2 = findViewById(R.id.pname2);

        img = findViewById(R.id.img);
        img2 = findViewById(R.id.img2);

        mRatingBar = findViewById(R.id.rating);

        reviewText = findViewById(R.id.review_text);

        // next on click
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isActive = true;
                checkIsNext();
            }
        });

        checkIsNext();
        setup();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isActive){
            isActive = false;
            checkIsNext();
            return false;
        }else {
            onBackPressed();
            return false;
        }
    }

    private void checkIsNext(){
        if (isActive){
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
        } else {
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
        }
    }

    private void setup(){
        if (!name.equals("")){
            pname.setText(name);
            pname2.setText(name);

            Glide.with(getApplicationContext())
                    .load(imgURL)
                    .into(img);

            Glide.with(getApplicationContext())
                    .load(imgURL)
                    .into(img2);

            mRatingBar.setRating(pRating);
        }
    }

}