package com.nk.studioshringaar;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nk.studioshringaar.adapter.PaymentData;
import com.nk.studioshringaar.adapter.Product;
import com.nk.studioshringaar.ui.cart.Cart;
import com.nk.studioshringaar.ui.order.Order;
import com.nk.studioshringaar.ui.product.ProductDetail;
import com.nk.studioshringaar.ui.search.Search;
import com.nk.studioshringaar.ui.search.SearchResult;
import com.nk.studioshringaar.ui.wishlist.WishList;
import com.razorpay.Checkout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String UID;
    private AppBarConfiguration mAppBarConfiguration;
    public DrawerLayout drawerLayout;
    public NavController navController;
    View navHeader;

    private FirebaseAuth mAuth;
    NavigationView navigationView;

    private FirebaseFirestore db = null;

    private static final String TAG = "HOME";

    public final static int REQUEST_CODE_SEARCH = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Checkout.preload(getApplicationContext());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

//        searchView = findViewById(R.id.search_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account)
                .setDrawerLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        navController.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        navHeader = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        loginCheck();
    }

    void loginCheck(){
//        showCustomDialog();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            UID = user.getUid();
            checkIfRating();
            try{
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.nav_login).setVisible(false);
                nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                Log.e("Home", "user found"+ name);

                TextView ut = navHeader.findViewById(R.id.title);
                if (name!=null){
                    ut.setText("Hello, "+name);
                } else if (email!=null){
                    ut.setText("Hello, "+email);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // No user is signed in
            Log.e("Home", "No use found");
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);

        MenuItem item = menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);
//        Toast.makeText(getApplicationContext(),"menu added " + searchView.isSearchOpen(),Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_wishlist:
//                Toast.makeText(getApplicationContext(),"wish",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), WishList.class);
                startActivity(i);
                break;
            case R.id.action_cart:
//                Toast.makeText(getApplicationContext(),"cart",Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(getApplicationContext(), Cart.class);
                startActivity(ii);
                break;
            case R.id.action_search:
                Intent d = new Intent(getApplicationContext(), Search.class);
                startActivityForResult(d, REQUEST_CODE_SEARCH);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();
        Log.e("HOOOOOOOOOMEEEEE","item "+id);
        switch (id) {
            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_account:
                navController.navigate(R.id.nav_account);
                break;
            case R.id.nav_wishlist:
                navController.navigate(R.id.wishList);
                break;
            case R.id.nav_login:
                navController.navigate(R.id.login);
                break;
            case R.id.nav_cat:
                navController.navigate(R.id.nav_shopbycategory);
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_order:
                navController.navigate(R.id.order);
                break;
        }
        return true;
    }

    void logout(){
        FirebaseAuth.getInstance().signOut();

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);

        nav_Menu.findItem(R.id.nav_login).setVisible(true);

        TextView ut = navHeader.findViewById(R.id.title);
        ut.setText("Hello,");

    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.popup, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH) {//you just got back from activity B - deal with resultCode
            //use data.getExtra(...) to retrieve the returned data
            try {
                Boolean err = data.getBooleanExtra("error", false);
                if (!err) {
                    try {
                        String id = data.getStringExtra("id");
                        String cat = data.getStringExtra("cat");
                        Toast.makeText(getApplicationContext(), "id" + id, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), ProductDetail.class);
                        i.putExtra("id", id);
                        i.putExtra("cat", cat);
                        startActivity(i);
                    } catch (Exception e) {
                        Log.e(TAG, "cant " + e);
                    }
                } else if (data.getBooleanExtra("usercancelled", false)) {
                    Log.e(TAG, "User cancelled the action");
                } else {
                    String query = data.getStringExtra("query");
                    Intent i = new Intent(getApplicationContext(), SearchResult.class);
                    i.putExtra("q", query);
                    startActivity(i);
                }
            } catch (Exception e) {
                Log.e(TAG, "Probaly back pressd and didnt get the code :D");
            }
        }
    }

    private void checkIfRating(){
        db.collection("user/"+ UID +"/order").whereEqualTo("status", "success")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().size() == 0){
                        Toast.makeText(getApplicationContext(),"0",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            String rated = "no";
                            try{
                                rated = document.getString("rated");
                            } catch (Exception e){
                                goRate();
                                break;
                            }
                            String order_stat = document.getString("order_status");

                            if (order_stat.equalsIgnoreCase("delivered") && rated == null){
                                goRate();
                                break;
                            }
                        } else {
                            //                            Toast.makeText(getApplicationContext(),"no doc",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void goRate(){
        startActivity(new Intent(this, Order.class));
    }

}
