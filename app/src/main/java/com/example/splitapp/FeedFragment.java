package com.example.splitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class FeedFragment extends Fragment {

    private static final String TAG = "MainActivity";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");

        view = inflater.inflate(R.layout.fragment_feed, container, false);

        initImageBitmaps();

        return view;
    }


    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");

        mImageUrls.add("https://danbooru.donmai.us/data/__mitsumine_mashiro_mikakunin_de_shinkoukei_drawn_by_shika_s1ka__2254f28067c4a7e13702550d43bbeebf.jpg");
        mNames.add("mitsumine_mashiro ");

        mImageUrls.add("https://img3.gelbooru.com//samples/2c/7d/sample_2c7de9c26f25847ebee803e20e63d493.jpg");
        mNames.add("inugami_korone ");

        mImageUrls.add("https://img1.gelbooru.com//samples/70/bf/sample_70bf011f96a3542698f046deb7637405.jpg");
        mNames.add("ashley_(warioware) ");

        mImageUrls.add("https://img3.gelbooru.com/images/35/60/3560e75c074921deffcf0fc949ae5e7c.png");
        mNames.add("senko_(sewayaki_kitsune_no_senko-san) ");

        mImageUrls.add("https://img3.gelbooru.com/images/a0/d1/a0d175faf465763e79322854395700d0.jpg");
        mNames.add("shirogane_noel ");

        mImageUrls.add("https://img3.gelbooru.com/images/29/3e/293e15a46cda95ece8ce0a6b7b20e089.jpg");
        mNames.add("hakurei_reimu");

        mImageUrls.add("https://img3.gelbooru.com/images/2e/05/2e0522f59a5a4ad8721e90eb9aaa763d.png");
        mNames.add("ikamusume ");

        mImageUrls.add("https://img3.gelbooru.com/images/ed/1c/ed1ce9ae707369d74dc7c18e3fece298.jpg");
        mNames.add("rem_(re:zero) ");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerv_view);//(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter( mNames, mImageUrls,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


}
