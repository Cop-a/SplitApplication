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

import java.util.ArrayList;


public class FeedFragment extends Fragment {

    private static final String TAG = "MainActivity";
    myAdapter adapter;
    RecyclerView recyclerView;

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrlsLeft = new ArrayList<>();
    private ArrayList<String> mImageUrlsRight = new ArrayList<>();
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


    /*private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");

        mImageUrlsLeft.add("https://firebasestorage.googleapis.com/v0/b/splitapp-8aa7f.appspot.com/o/UserPosts%2Fp3MzUA6WvuQnM1fhBHqTom4TrVz1%2FPosts%2F1615325402-Sponge%20or%20Loli%2F0a16d2a1-dae4-411b-88ef-e28ae2812a9c?alt=media&token=94e31d69-bbca-459e-976a-1dc8435f6bb6");
        mNames.add("spung or loli?? ");

        mImageUrlsRight.add("https://firebasestorage.googleapis.com/v0/b/splitapp-8aa7f.appspot.com/o/UserPosts%2Fp3MzUA6WvuQnM1fhBHqTom4TrVz1%2FPosts%2F1615325402-Sponge%20or%20Loli%2Fb59a98ad-9362-4652-aef3-dee907d5ca84?alt=media&token=96d42aa7-9ad4-4c15-965b-43908959aff6");
        //mNames.add("inugami_korone ");

        mImageUrlsLeft.add("https://img1.gelbooru.com//samples/70/bf/sample_70bf011f96a3542698f046deb7637405.jpg");
        mNames.add("ashley_(warioware) ");

        mImageUrlsRight.add("https://img3.gelbooru.com/images/35/60/3560e75c074921deffcf0fc949ae5e7c.png");
        //mNames.add("senko_(sewayaki_kitsune_no_senko-san) ");

        mImageUrlsLeft.add("https://img3.gelbooru.com/images/a0/d1/a0d175faf465763e79322854395700d0.jpg");
        mNames.add("shirogane_noel ");

        mImageUrlsRight.add("https://img3.gelbooru.com/images/29/3e/293e15a46cda95ece8ce0a6b7b20e089.jpg");
        //mNames.add("hakurei_reimu");

        mImageUrlsLeft.add("https://img3.gelbooru.com/images/2e/05/2e0522f59a5a4ad8721e90eb9aaa763d.png");
        mNames.add("ikamusume ");

        mImageUrlsRight.add("https://img3.gelbooru.com/images/ed/1c/ed1ce9ae707369d74dc7c18e3fece298.jpg");
        //mNames.add("rem_(re:zero) ");

        initRecyclerView();
    }*/

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView");
        recyclerView = view.findViewById(R.id.recyclerv_view);//(R.id.recyclerv_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(FirebaseDatabase.getInstance().getReference().child("posts"), post.class).build();

        adapter = new myAdapter(options);
        recyclerView.setAdapter(adapter);


    }



}
