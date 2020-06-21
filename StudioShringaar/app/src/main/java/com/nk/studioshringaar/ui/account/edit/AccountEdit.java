package com.nk.studioshringaar.ui.account.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nk.studioshringaar.R;

import java.util.HashMap;
import java.util.Map;

public class AccountEdit extends AppCompatActivity {

    private static final String TAG = "AccountEdit";
    private FirebaseFirestore db = null;
    private FirebaseAuth mAuth;

    private Boolean isLoggedin = false;
    private String UID = "";

    private String mName, mEmail, mPhone;
    private EditText name, email, phone;
    private Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        mName = "";
        mEmail = "";
        mPhone = "";

        Intent i = getIntent();

        mName = i.getStringExtra("name");
        mEmail = i.getStringExtra("email");
        mPhone = i.getStringExtra("phone");

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        apply = findViewById(R.id.apply);

        if (mName != null){
            name.setText(mName);
        }

        if (mEmail!= null){
            email.setText(mEmail);
        }

        if (mPhone!= null){
            phone.setText(mPhone);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
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
        mAuth = FirebaseAuth.getInstance();

        loginCheck();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String e = email.getText().toString();
                String p = phone.getText().toString();

                if (!isLoggedin){
                    Toast.makeText(getApplicationContext(), "Not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (e.equals("")){
                    Toast.makeText(getApplicationContext(), "Please dont leave email id empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                update(n,e,p);
            }
        });
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
        }else{
            Toast.makeText(getApplicationContext(), "Please login to continue!!", Toast.LENGTH_SHORT).show();
            isLoggedin = false;
        }
    }

    private void update(String name, String email, String phone){

        if (isLoggedin) {
            Map<String,Object> updates = new HashMap<>();
            updates.put("email", email);
            if (!name.equals("")){
                updates.put("name", name);
            }else{
                updates.put("name", "");
            }
            if (!phone.equals("")){
                updates.put("phone", phone);
            }else{
                updates.put("phone", "");
            }
            db.collection("user").document(UID).update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Applied changes", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Please login to continue!!", Toast.LENGTH_SHORT).show();
        }
    }

}
