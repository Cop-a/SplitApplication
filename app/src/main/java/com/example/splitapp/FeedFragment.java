package com.example.splitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class FeedFragment extends Fragment {

    private static final String TAG = "MainActivity";
    myAdapter adapter;
    RecyclerView recyclerView;


    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");

        view = inflater.inflate(R.layout.fragment_feed, container, false);

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
        recyclerView = view.findViewById(R.id.recyclerv_view);//(R.id.recyclerv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("unixTimestamp");

        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();

        adapter = new myAdapter(options, getActivity());
        recyclerView.setAdapter(adapter);
    }



}
