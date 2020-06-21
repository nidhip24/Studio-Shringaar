package com.nk.studioshringaar.ui.register;

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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nk.studioshringaar.Home;
import com.nk.studioshringaar.MainActivity;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.ui.login.Login;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Register";
    Button re;
    EditText email,pass;
    FirebaseAuth firebaseAuth;

    private SignInButton signInButton;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        re = findViewById(R.id.registerButton);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        firebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailV = email.getText().toString();
                String passV = pass.getText().toString();

                if (emailV.equals("") || passV.equals("")){
                    Toast.makeText(getApplicationContext(),"One of the field is empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(emailV,passV).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                            finish();
                            Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            createUser(uid,name,email);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"E-mail or password is wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        if(firebaseAuth.getCurrentUser()!=null){
            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            Toast.makeText(getApplicationContext(),"already",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    public void alreadyAcc(View v){
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"afailed",Toast.LENGTH_SHORT).show();
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
//                Toast.makeText(getApplicationContext(),"1 m",Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(),"failure m",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            createUser(uid,name,email);
//                            startActivity(new Intent(getApplicationContext(), Home.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"failure",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    void createUser(String uid, String name, String email){
        Map<String, Object> user = new HashMap<>();
        if (name != null)
            user.put("name", name);
        if (email != null)
            user.put("email", email);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("user").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Accout created",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startActivity(new Intent(getApplicationContext(), Home.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
