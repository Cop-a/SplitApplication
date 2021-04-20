package com.example.splitapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment  {

    private static final String TAG = "ProfileActivity";
    myAdapter adapter;
    RecyclerView recyclerView;
    Query query;
    String postTitle;

    TextView username;
    CircleImageView pfp;
    ImageView settings;
    ImageView location;

    private DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    View view;

    private static AppCompatActivity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (AppCompatActivity) context;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.recyclerv_view);

        username = view.findViewById(R.id.txt_profile_username);
        pfp = view.findViewById(R.id.img_profile_pfp);
        settings = view.findViewById(R.id.img_profile_settings);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileSettingFragment()).commit();

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.getUid()).child("profileUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String picUrl = snapshot.getValue(String.class);
                Glide.with(pfp.getContext()).load(picUrl).into(pfp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    username.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });



        initRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }




    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("uID").equalTo(user.getUid());



        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();

        adapter = new myAdapter(options, getActivity());
        recyclerView.setAdapter(adapter);

    }


}
