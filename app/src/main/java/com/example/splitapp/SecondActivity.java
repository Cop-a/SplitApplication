package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView nav_username;
    private TextView nav_proname;
    private CircleImageView nav_pfp;
    private TextView test;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.cole));
        }



        firebaseAuth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mDatabase.child("users").child(user.getUid()).child("profileUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            Glide.with(nav_pfp.getContext()).load(task.getResult().getValue()).into(nav_pfp);
                        }
                    }
                });
                mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            nav_username.setText(String.valueOf(task.getResult().getValue()));
                        }
                    }
                });
            }
        });
        //set nav username and email
        View headerView = navigationView.getHeaderView(0);
        nav_username = (TextView) headerView.findViewById(R.id.nav_username);
        nav_pfp = headerView.findViewById(R.id.img_profile_pfp);
        TextView navEmail = (TextView) headerView.findViewById(R.id.nav_proname);
        String nav_u = user.getUid();
        String nav_p = user.getEmail();


        mDatabase = FirebaseDatabase.getInstance().getReference();

/*
        mDatabase.child("users").child(user.getUid()).child("profileUrl").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 Log.d("firebase", String.valueOf(snapshot.getValue().toString()));
                    Glide.with(nav_pfp.getContext()).load(snapshot.getValue().toString()).into(nav_pfp);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
*/


    mDatabase.child("users").child(user.getUid()).child("profileUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
          @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
               if (!task.isSuccessful()) {
                   Log.e("firebase", "Error getting data", task.getException());
               }
               else {
                  Log.d("firebase", String.valueOf(task.getResult().getValue()));
                   Glide.with(nav_pfp.getContext()).load(task.getResult().getValue()).into(nav_pfp);
               }
          }
        });

                mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            nav_username.setText(String.valueOf(task.getResult().getValue()));
                        }
                    }
                });



        navEmail.setText(nav_p);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    nav_username.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });






        switch (item.getItemId()){
            case R.id.nav_feed:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();
                break;
            case R.id.nav_message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                finish();
                Intent intent = new Intent (SecondActivity.this, MainActivity.class);
                startActivity(intent);
                //TODO ADD LOGOUT ACTION
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer((GravityCompat.START));
        } else {
            super.onBackPressed();
        }

    }



    public void FABonClick(View view)
    {
        Intent intent = new Intent (SecondActivity.this, PostActivity.class);
        startActivity(intent);
    }




}